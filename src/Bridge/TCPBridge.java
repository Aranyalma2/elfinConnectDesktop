package Bridge;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.MissingResourceException;

public class TCPBridge {
    private ServerSocket localServer = null;

    private Socket clientSocket = null;

    private Socket remoteSocket = null;

    private Thread localConnectionHandlerThread = null;

    private Thread clientToRemoteThread = null;

    private Thread remoteToClientThread = null;

    //Data send head
    String header = "";
    //Connection fabrication message
    String connect = "";

    public TCPBridge(int localServerPort, String remoteHost, int remotePort, String uuid, String endDeviceMAC) throws RuntimeException, IOException {
        String mockMac = "T" +  Integer.toString(localServerPort);
        header = "data;"+uuid+";"+mockMac+";";
        connect = "connme;"+uuid+";"+endDeviceMAC;

        try {

            localServer = new ServerSocket(localServerPort);


        }catch(IOException e){
            e.printStackTrace();
            throw new RuntimeException("Unable to open local server");
        }

        try{
            remoteSocket = new Socket(remoteHost, remotePort);
            remoteSocket.getOutputStream().write(connect.getBytes());
        }catch(IOException e){
            localServer.close();
            throw new IOException("Unable to connect remote host");
        }

        localConnectionHandlerThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    clientSocket = localServer.accept();
                    createConnectionThreads();

                } catch (IOException e) {
                    break;
                }
            }

            stopConnection();
            System.out.println("Bridge demolished");

        });
        localConnectionHandlerThread.start();

    }

    public void stopBridge(){
        try {
            System.out.println("Stop local and remote sockets");
            localServer.close();
            remoteSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close the local/remote sockets");
        }
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
