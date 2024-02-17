package Bridge;

import User.DataFromJson;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import SW.Log;

public class TCPBridge {
    //Local server instance
    private ServerSocket localServer = null;
    //Local server`s last incommoded client`s socket
    private Socket clientSocket = null;
    //Remote server connection socket
    private Socket remoteSocket = null;
    //Handle all incoming tcp client connection to Local server instance
    private Thread localConnectionHandlerThread = null;

    //Handle data sending to server
    private Thread clientToRemoteThread = null;
    //Handle incoming data from server
    private Thread remoteToClientThread = null;

    //Data send header
    String header = "";
    //Connection fabrication message
    String connect = "";

    //Create a local tcp server socket, and restream the data to a remote server socket and add header information to data.
    //Also establish a tcp and application layer connection to remote server.
    public TCPBridge(int localServerPort, String remoteHost, int remotePort, String uuid, String endDeviceMAC) throws RuntimeException, IOException {
        //Moc mac is unique id for fill important and unused parts of the header
        String mockMac = "T" +  Integer.toString(localServerPort);
        //"data;uuid;mockMac;mockMac;1" => send a data packet to server; user-id; 2x mockID (need while connection active); "1" represented it is a virtual device
        header = "data;"+uuid+";"+mockMac+";"+mockMac+";1;";
        //"conme;uuid;endDeviceMAC" => send a connection request to server, for connect this socket to the "endDeviceMAC`s" socket
        connect = "connme;"+uuid+";"+endDeviceMAC;

        try {

            localServer = new ServerSocket(localServerPort);

            Log.logger.info("A local server started on: (" + localServerPort + ") port.");


        }catch(IOException e){
            throw new RuntimeException("Local server create error: " + e.getMessage());
        }

        boolean connectionStatus = false;
        try{
            Log.logger.info("Attempt to connect remote server at: (" + remoteHost + ":" + remotePort +")");
            remoteSocket = new Socket(remoteHost, remotePort);
            remoteSocket.setSoTimeout(5000);
            remoteSocket.getOutputStream().write(connect.getBytes());
            Log.logger.info("Sent connection request to remote server at: (" + remoteHost + ":" + remotePort +")");

            connectionStatus = DataFromJson.convertJsonToStatus(readServerResponse(remoteSocket.getInputStream()));
            remoteSocket.setSoTimeout(1800000);
        }catch(IOException e){

            localServer.close();
            throw new IOException("Unable to connect remote server at: (" + remoteHost + ":" + remotePort +")");
        }

        //Server rejected bridge creation request
        if(!connectionStatus){
            localServer.close();
            throw new IOException("Server refused the bridge creation request: " + connectionStatus);
        }

        Log.logger.info("Server connection established and bridge creation accepted for: ("+ endDeviceMAC +")");

        localConnectionHandlerThread = new Thread(() -> {
            Log.logger.info("Local server is listening at: ("+ localServerPort +")");
            while(!Thread.currentThread().isInterrupted()){
                try {
                    clientSocket = localServer.accept();
                    Log.logger.info("Local server: ("+ localServerPort +") incoming connection!");
                    createConnectionThreads();

                } catch (IOException e) {
                    break;
                }
            }
            stopConnection();

        });
        localConnectionHandlerThread.start();

    }

    public int getLocalPort(){
        return localServer.getLocalPort();
    }

    private String readServerResponse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader.readLine();
    }

    public void stopBridge() throws RuntimeException{
        try {
            Log.logger.info("Try to stop bridge. Local server port : (" +this.getLocalPort()+")");
            localServer.close();
            remoteSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close the local/remote sockets Local server port : (" +this.getLocalPort()+")");
        }
        Log.logger.info("Bridge threads stopped. Local server port : (" +this.getLocalPort()+")");
    }

    private void startConnection(){
        clientToRemoteThread.start();
        remoteToClientThread.start();
    }

    private void stopConnection(){
        if (clientToRemoteThread != null && clientToRemoteThread.getState() == Thread.State.RUNNABLE) {
            clientToRemoteThread.interrupt();
        }
        if (remoteToClientThread != null && remoteToClientThread.getState() == Thread.State.RUNNABLE) {
            remoteToClientThread.interrupt();
        }
    }

    private void restartConnection(){
        stopConnection();
        startConnection();
    }


    private void createConnectionThreads() throws IllegalThreadStateException{

        //STOP Thread, if somehow running
        stopConnection();

        // Create input and output streams for client and remote connections
        try {
            InputStream clientInput = clientSocket.getInputStream();
            OutputStream clientOutput = clientSocket.getOutputStream();
            InputStream remoteInput = remoteSocket.getInputStream();
            OutputStream remoteOutput = remoteSocket.getOutputStream();


            // Start a thread to forward data from client to remote server
            clientToRemoteThread = new Thread(() -> {
                try {
                    int bytesRead;
                    byte[] buffer = new byte[1024];

                    while (!Thread.currentThread().isInterrupted() && (bytesRead = clientInput.read(buffer)) != -1) {
                        // Create a custom header and attach the message

                        String message = new String(buffer, 0, bytesRead);
                        String combinedMessage = header + message;

                        remoteOutput.write(combinedMessage.getBytes());
                    }
                } catch (IOException e) {
                    restartConnection();
                    throw new IllegalThreadStateException("Remote client->server connection error");
                }
            });

            // Start a thread to forward data from remote server to client
            remoteToClientThread = new Thread(() -> {
                try {
                    int bytesRead;
                    byte[] buffer = new byte[1024];
                    while (!Thread.currentThread().isInterrupted() && (bytesRead = remoteInput.read(buffer)) != -1) {
                        clientOutput.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    restartConnection();
                    throw new IllegalThreadStateException("Remote server->client connection error");
                }
            });

            startConnection();

        }catch(IOException e){
            e.printStackTrace();
            stopConnection();
        }
    }


}
