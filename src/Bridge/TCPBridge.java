package Bridge;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;

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

    public TCPBridge(int localServerPort, String remoteHost, int remotePort, String uuid, String endDeviceMAC) throws IllegalThreadStateException {
        header = "data;"+uuid+";"+endDeviceMAC+";";
        connect = "connme;"+uuid+";"+endDeviceMAC;

        try {

            localServer = new ServerSocket(localServerPort);

            remoteSocket = new Socket(remoteHost, remotePort);
            remoteSocket.getOutputStream().write(connect.getBytes());

        }catch(IOException e){
            throw new IllegalThreadStateException("Local connection error");
        }

        localConnectionHandlerThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    clientSocket = localServer.accept();
                    createConnectionThreads();

                } catch (IOException e) {
                    stopConnection();
                    throw new IllegalThreadStateException("Local connection error");
                }
            }
            stopConnection();
        });
        localConnectionHandlerThread.start();

    }

    public void stopBridge(){
        localConnectionHandlerThread.interrupt();
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

                    remoteOutput.write(connect.getBytes());

                    while (!Thread.currentThread().isInterrupted() && (bytesRead = clientInput.read(buffer)) != -1) {
                        // Create a custom header and attach the message

                        String message = new String(buffer, 0, bytesRead);
                        String combinedMessage = header + message;

                        remoteOutput.write(combinedMessage.getBytes());
                    }
                } catch (IOException e) {
                    stopConnection();
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
                    stopConnection();
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
