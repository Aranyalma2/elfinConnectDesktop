import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BridgeServerConnection {

    //Local tcp server port
    private int localPort = 0;
    //Local tcp server socket
    private ServerSocket localServer = null;
    //Client, connected to localServer
    private Socket clientSocket = null;
    //Remote tcp server socket for data
    private Socket bridgeSocket = null;
    //Temote tcp server socket for heartbeat msg
    private Socket bridgeHBSocket = null;

    private Thread clientToRemoteThread = null;

    private Thread remoteToClientThread = null;
    private Thread hearthbeatThread = null;

    private Thread localConnectionHandlerThread = null;

    //Hearthbeat message
    String beat = "";
    //Data send head
    String header = "";
    //Connection fabrication message
    String connect = "";

    public void stopAll(){

    }

    public void stopBridge(){
        clientToRemoteThread.interrupt();
        remoteToClientThread.interrupt();
        hearthbeatThread.interrupt();
        localConnectionHandlerThread.interrupt();
    }
    public void startBridge(){
        hearthbeatThread.start();
        clientToRemoteThread.start();
        remoteToClientThread.start();
    }

    public void createBridgeThreads() throws IllegalThreadStateException{

        //STOP Thread, if somehow running
        if (clientToRemoteThread != null && clientToRemoteThread.getState() == Thread.State.RUNNABLE) {
            clientToRemoteThread.interrupt();
        }
        if (remoteToClientThread != null && remoteToClientThread.getState() == Thread.State.RUNNABLE) {
            remoteToClientThread.interrupt();
        }
        if (hearthbeatThread != null && hearthbeatThread.getState() == Thread.State.RUNNABLE) {
            hearthbeatThread.interrupt();
        }

        // Create input and output streams for client and remote connections
        try {
            InputStream clientInput = clientSocket.getInputStream();
            OutputStream clientOutput = clientSocket.getOutputStream();
            InputStream remoteInput = bridgeSocket.getInputStream();
            OutputStream remoteOutput = bridgeSocket.getOutputStream();
            OutputStream remoteHBOutput = bridgeHBSocket.getOutputStream();

            //Start the socket heartbeat thread
            hearthbeatThread = new Thread(() -> {
                try {
                    remoteHBOutput.write(beat.getBytes());
                    Thread.sleep(100);
                    remoteHBOutput.write(connect.getBytes());
                    while (!Thread.interrupted()) {
                            remoteHBOutput.write(beat.getBytes());
                            //30SEC heartbeat msg
                            Thread.sleep(30000);
                    }
                } catch (IOException e) {
                    stopBridge();
                    throw new IllegalThreadStateException("Remote Heartbeat connection error");
                } catch (InterruptedException e) {
                    // Exit the thread if interrupted
                    return;
                }
            });

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
                    stopBridge();
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
                    stopBridge();
                    throw new IllegalThreadStateException("Remote server->client connection error");
                }
            });


        }catch(IOException e){
            e.printStackTrace();
            stopBridge();
        }
    }

    public void createSockets(int localServerPort, String remoteHost, int remotePort, String uuid, String virtualName, String endDeviceMAC) throws IOException, IllegalThreadStateException {

        beat = "beat;"+uuid+";"+virtualName+";"+virtualName+";1";
        header = "data;"+uuid+";"+virtualName+";";
        connect = "conn;"+uuid+";"+virtualName+";"+endDeviceMAC;

        bridgeSocket = new Socket(remoteHost, remotePort);
        bridgeHBSocket = new Socket(remoteHost, remotePort);

        localServer = new ServerSocket(localServerPort);


        localConnectionHandlerThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    clientSocket = localServer.accept();
                    createBridgeThreads();
                    startBridge();

                } catch (IOException e) {
                    stopBridge();
                    throw new IllegalThreadStateException("Local connection error");
                }
            }
        });
        localConnectionHandlerThread.start();
    }
}
