package User;
import Device.Device;
import GUI.MainFrame;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DeviceQueryThread extends Thread {
    private String userId;
    private String serverAddress;
    private int serverPort;
    private Socket socket = null;;
    private boolean sleepInterrupted = false;

    private boolean killInterrupted = false;

    private boolean remoteServerStatus = false;

    public DeviceQueryThread(String userId, String serverAddress, int serverPort) {
        this.userId = userId;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }


    @Override
    public void run() {
        System.out.println("USER THREAD STARTED");
        try {
            while (!killInterrupted) {
                try {
                    if (socket == null || !socket.getInetAddress().getHostAddress().equals(serverAddress) || socket.getPort() != serverPort) {
                        // Close existing socket (if any) and create a new one
                        closeSocket();
                        createSocket();
                    }

                    // Set the timeout for socket operations (adjust as needed)
                    if (socket != null) {
                        socket.setSoTimeout(5000);
                        OutputStream outputStream = socket.getOutputStream();
                        InputStream inputStream = socket.getInputStream();

                        // Perform the query task every 30 seconds
                        sendQueryToServer(outputStream);

                        // Read and log the server's response
                        String response = readServerResponse(inputStream);
                       // System.out.println("Server Response: " + response);
                        User.getInstance().updateDeviceList(response);

                        remoteServerStatus = true;

                        sleepWithInterruption(30);
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // Handle connection-related IO exceptions
                    User.getInstance().updateDeviceList("");
                    MainFrame.getInstance().timeoutErrorDialog();

                    // If an IOException occurs while establishing a socket, restart the socket
                    try{
                        remoteServerStatus = false;
                        closeSocket();
                        createSocket();
                    }catch(IOException ee){
                        //Try to auto reconnect automatically
                        try {
                            sleepWithInterruption(5);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                    remoteServerStatus = false;
                    e.printStackTrace();
                }
            }
            // Close the socket before the thread exits
            closeSocket();
        } catch(IOException e){
            remoteServerStatus = false;
            e.printStackTrace(); // Handle connection-related IO exceptions
        }
        remoteServerStatus = false;
        System.out.println("USER THREAD KILLED");
    }


    private void sleepWithInterruption(int seconds) throws InterruptedException {
        for (int i = 0; i < seconds && !sleepInterrupted; i++) {
            TimeUnit.SECONDS.sleep(1);
        }
        sleepInterrupted = false;
    }

    public void interruptSleep() {
        sleepInterrupted = true;
    }

    public void interruptKill() {
        killInterrupted = true;
    }

    private void sendQueryToServer(OutputStream outputStream) throws IOException {
        String queryMessage = "query;" + userId;
        byte[] messageBytes = queryMessage.getBytes();
        outputStream.write(messageBytes);
        outputStream.flush();
    }

    private String readServerResponse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader.readLine();

    }

    public void setParamters(String userId, String serverAddress, int serverPort) {
        this.userId = userId;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        interruptSleep();
    }

    private void createSocket() throws IOException{
        socket = new Socket(serverAddress, serverPort);
    }

    private void closeSocket() throws IOException{
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public boolean getConnectionStatus(){
        return remoteServerStatus;
    }
}