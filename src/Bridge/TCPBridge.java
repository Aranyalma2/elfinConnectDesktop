package Bridge;

import SW.SecureSocketBuilder;
import User.DataFromJson;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import SW.Log;

import javax.net.ssl.SSLSocket;

/**
 * The TCPBridge class manages the creation and control of a TCP bridge between a local server and a remote server.
 * It handles data forwarding between the two servers and includes functionality for starting, stopping, and restarting the bridge.
 */
public class TCPBridge {
    //Local server instance
    private ServerSocket localServer = null;
    //Local server`s last incommoded client`s socket
    private Socket clientSocket = null;
    //Remote server connection socket
    private SSLSocket remoteSocket = null;
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

    /**
     * Constructs a TCPBridge object, creating a local TCP server socket and establishing a connection to a remote server.
     * Restream the data from localserver to the remote server socket and add header information to data.
     * Also establish a tcp and application layer connection to remote server.
     *
     * @param localServerPort The port number for the local server.
     * @param uuid            The unique identifier for the user.
     * @param endDeviceMAC    The MAC address of the end device.
     * @throws RuntimeException If there is an issue during the creation process.
     * @throws IOException      If there is an issue with I/O operations (Server/Socket related).
     */
    public TCPBridge(int localServerPort, String uuid, String endDeviceMAC) throws RuntimeException, IOException {
        //Moc mac is unique id for fill important and unused parts of the header
        String mockMac = "T" +  Integer.toString(localServerPort);
        //"data;uuid;mockMac;mockMac;1" => send a data packet to server; user-id; 2x mockID (need while connection active); "1" represented it is a virtual device
        header = "data;"+uuid+";"+mockMac+";"+mockMac+";1;";
        //"conme;uuid;endDeviceMAC" => send a connection request to server, for connect this socket to the "endDeviceMAC`s" socket
        connect = "connme;"+uuid+";"+endDeviceMAC;

        try {

            localServer = new ServerSocket(localServerPort);

            Log.logger.info("A local server started on: (" + this.getLocalPort() + ") port.");


        }catch(IOException e){
            throw new RuntimeException("Local server create error: " + e.getMessage());
        }

        boolean connectionStatus = false;
        try{
            Log.logger.info("Attempt to connect remote server at: (" + SecureSocketBuilder.getHost() + ":" + SecureSocketBuilder.getPort() +")");
            remoteSocket = SecureSocketBuilder.getNewSocket();
            remoteSocket.setSoTimeout(5000);
            remoteSocket.getOutputStream().write(connect.getBytes());
            Log.logger.info("Sent connection request to remote server at: (" + SecureSocketBuilder.getHost() + ":" + SecureSocketBuilder.getPort() +")");

            connectionStatus = DataFromJson.convertJsonToStatus(readServerResponse(remoteSocket.getInputStream()));
            remoteSocket.setSoTimeout(1800000);
        }catch(IOException e){

            localServer.close();
            throw new IOException("Unable to connect remote server at: (" + SecureSocketBuilder.getHost() + ":" + SecureSocketBuilder.getPort() +")");
        }

        //Server rejected bridge creation request
        if(!connectionStatus){
            localServer.close();
            throw new IOException("Server refused the bridge creation request!");
        }

        Log.logger.info("Server connection established and bridge creation accepted for: ("+ endDeviceMAC +")");

        localConnectionHandlerThread = new Thread(() -> {
            Log.logger.info("Local server is listening at: ("+ this.getLocalPort() +")");
            while(!Thread.currentThread().isInterrupted()){
                try {
                    clientSocket = localServer.accept();
                    Log.logger.info("Local server: ("+ this.getLocalPort() +") incoming connection!");
                    createConnectionThreads();

                } catch (IOException e) {
                    break;
                }
            }
            stopConnection();

        });
        localConnectionHandlerThread.start();

    }

    /**
     * Retrieves the local port of the local server for this bridge.
     *
     * @return The local port number.
     */
    public int getLocalPort(){
        return localServer.getLocalPort();
    }

    /**
     * Stops the bridge by closing the local and remote sockets.
     *
     * @throws RuntimeException If there is an issue closing the sockets.
     */
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

    /**
     * Starts the connection threads for data forwarding.
     */
    private void startConnection(){
        clientToRemoteThread.start();
        remoteToClientThread.start();
    }

    /**
     * Stops the connection threads.
     */
    private void stopConnection(){
        if (clientToRemoteThread != null && clientToRemoteThread.getState() == Thread.State.RUNNABLE) {
            clientToRemoteThread.interrupt();
        }
        if (remoteToClientThread != null && remoteToClientThread.getState() == Thread.State.RUNNABLE) {
            remoteToClientThread.interrupt();
        }
    }

    /**
     * Restarts the connection threads.
     */
    private void restartConnection(){
        Log.logger.warning("Restart connection threads. Local server port : (" +this.getLocalPort()+")");

        stopConnection();
        startConnection();
    }

    /**
     * Creates and starts the threads for forwarding data between the client and remote server.
     *
     * @throws IllegalThreadStateException If there is an issue creating the threads.
     */
    private void createConnectionThreads() throws IllegalThreadStateException{

        //STOP Thread, if somehow running
        stopConnection();

        // Create input and output streams for client and remote connections

            // Start a thread to forward data from client to remote server
            clientToRemoteThread = new Thread(() -> {
                try {
                    int bytesRead;
                    byte[] buffer = new byte[1024];

                    while (!Thread.currentThread().isInterrupted() && (bytesRead = clientSocket.getInputStream().read(buffer)) != -1) {
                        Log.logger.finer("Forward content to server. Local server port : (" +this.getLocalPort()+")");
                        // Create a custom header and attach the message

                        String message = new String(buffer, 0, bytesRead);
                        String combinedMessage = header + message;

                        remoteSocket.getOutputStream().write(combinedMessage.getBytes());
                    }
                } catch (IOException e) {
                    Log.logger.warning("Error occurred at client->server thread: [" + e.getMessage() + " Local server port : (" +this.getLocalPort()+")");
                    restartConnection();
                }
            });

            // Start a thread to forward data from remote server to client
            remoteToClientThread = new Thread(() -> {
                try {
                    int bytesRead;
                    byte[] buffer = new byte[1024];
                    while (!Thread.currentThread().isInterrupted() && (bytesRead = remoteSocket.getInputStream().read(buffer)) != -1) {
                        Log.logger.finer("Received content from server. Local server port : (" +this.getLocalPort()+")");
                        clientSocket.getOutputStream().write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    Log.logger.warning("Error occurred at server->client thread: [" + e.getMessage() + " Local server port : (" +this.getLocalPort()+")");
                    restartConnection();

                }
            });

            startConnection();

    }

    /**
     * Reads the server response from the input stream.
     *
     * @param inputStream The input stream from which to read the response.
     * @return The server response as a string.
     * @throws IOException If there is an issue reading from the input stream.
     */
    private String readServerResponse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader.readLine();
    }


}
