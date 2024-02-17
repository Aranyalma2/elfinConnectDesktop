package User;

import GUI.MainFrame;
import SW.Log;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * The DeviceQueryThread class represents a thread for querying the server to update the device table.
 */
public class DeviceQueryThread extends Thread {
    private String userId;
    private String serverAddress;
    private int serverPort;
    private Socket socket = null;
    private boolean sleepInterrupted = false;
    private boolean killInterrupted = false;
    private boolean remoteServerStatus = false;

    /**
     * Constructs a DeviceQueryThread with the specified user ID, server address, and server port.
     *
     * @param userId        The user ID.
     * @param serverAddress The server address.
     * @param serverPort    The server port.
     */
    public DeviceQueryThread(String userId, String serverAddress, int serverPort) {
        this.userId = userId;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    /**
     * The main run method of the thread. Continuously queries the server to update the device table.
     */
    @Override
    public void run() {
        Log.logger.info("Start server query thread for updating the device table");
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
                        Log.logger.fine("Send query to the server");
                        sendQueryToServer(outputStream);

                        // Read and log the server's response
                        String response = readServerResponse(inputStream);
                        User.getInstance().updateDeviceList(response);

                        remoteServerStatus = true;

                        sleepWithInterruption(30);
                    }
                } catch (IOException e) {
                    Log.logger.warning("Unable to reach the server for data fetch: [" + e.getMessage() + "]");
                    User.getInstance().updateDeviceList("");
                    MainFrame.getInstance().timeoutErrorDialog();

                    // If an IOException occurs while establishing a socket, restart the socket
                    try {
                        remoteServerStatus = false;
                        closeSocket();
                        createSocket();
                    } catch (IOException ee) {
                        // Try to auto-reconnect automatically
                        try {
                            sleepWithInterruption(5);
                        } catch (InterruptedException ex) {
                            Log.logger.warning("Auto-reconnect sleep interrupted: [" + ex.getMessage() + "]");
                        }
                    }
                } catch (InterruptedException ex) {
                    remoteServerStatus = false;
                    Log.logger.warning("Server query thread interrupted: [" + ex.getMessage() + "]");
                }
            }
            // Close the socket before the thread exits
            closeSocket();
        } catch (IOException e) {
            remoteServerStatus = false;
            Log.logger.warning("Unable to close the existing socket: [" + e.getMessage() + "]");
            Log.logger.severe("May abandoned sockets still open");
        }
        remoteServerStatus = false;
        Log.logger.info("Stop server query thread");
    }

    /**
     * Sleeps for the specified number of seconds with interruption support.
     *
     * @param seconds The number of seconds to sleep.
     * @throws InterruptedException If the sleep is interrupted.
     */
    private void sleepWithInterruption(int seconds) throws InterruptedException {
        for (int i = 0; i < seconds && !sleepInterrupted; i++) {
            TimeUnit.SECONDS.sleep(1);
        }
        sleepInterrupted = false;
    }

    /**
     * Interrupts the sleep operation.
     */
    public void interruptSleep() {
        sleepInterrupted = true;
    }

    /**
     * Interrupts the main loop, terminating the thread.
     */
    public void interruptKill() {
        killInterrupted = true;
    }

    /**
     * Sends a query message to the server through the specified output stream.
     *
     * @param outputStream The output stream to the server.
     * @throws IOException If an I/O error occurs.
     */
    private void sendQueryToServer(OutputStream outputStream) throws IOException {
        String queryMessage = "query;" + userId;
        byte[] messageBytes = queryMessage.getBytes();
        outputStream.write(messageBytes);
        outputStream.flush();
    }

    /**
     * Reads the server's response from the specified input stream.
     *
     * @param inputStream The input stream from the server.
     * @return The server's response as a string.
     * @throws IOException If an I/O error occurs.
     */
    private String readServerResponse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return reader.readLine();
    }

    /**
     * Sets the parameters of the device query thread.
     *
     * @param userId        The user ID.
     * @param serverAddress The server address.
     * @param serverPort    The server port.
     */
    public void setParameters(String userId, String serverAddress, int serverPort) {
        this.userId = userId;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        interruptSleep();
    }

    /**
     * Creates a new socket for the device query thread.
     *
     * @throws IOException If an I/O error occurs while creating the socket.
     */
    private void createSocket() throws IOException {
        socket = new Socket(serverAddress, serverPort);
    }

    /**
     * Closes the existing socket if it is not already closed.
     *
     * @throws IOException If an I/O error occurs while closing the socket.
     */
    private void closeSocket() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    /**
     * Gets the connection status of the device query thread.
     *
     * @return True if the device query thread is connected to the server, false otherwise.
     */
    public boolean getConnectionStatus() {
        return remoteServerStatus;
    }
}
