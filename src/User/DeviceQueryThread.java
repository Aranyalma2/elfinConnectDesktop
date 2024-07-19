package User;

import GUI.MainFrame;
import SW.Log;
import SW.SecureSocketBuilder;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * The DeviceQueryThread class represents a thread for querying the server to update the device table.
 */
public class DeviceQueryThread extends Thread {
    private final String userId;
    private Socket socket = null;
    private boolean sleepInterrupted = false;
    private boolean killInterrupted = false;
    private boolean remoteServerStatus = false;

    /**
     * Constructs a DeviceQueryThread with the specified user ID.
     *
     * @param userId        The user ID.
     */
    public DeviceQueryThread(String userId) {
        this.userId = userId;
    }

    /**
     * The main run method of the thread. Continuously queries the server to update the device table.
     */
    @Override
    public void run() {
        Log.logger.info("Start server query thread for updating the device table");
        try {
            try {
                createSocket();
            } catch (IOException e) {
                remoteServerStatus = false;
                MainFrame.getInstance().connectErrorDialog();
                Log.logger.warning("Unable to create the socket: [" + e.getMessage() + "]");
            }
            while (socket != null && !killInterrupted) {
                try {

                    // Set the timeout for socket operations (adjust as needed)
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

                } catch (IOException e) {
                    remoteServerStatus = false;
                    Log.logger.warning("Unable to reach the server for data fetch: [" + e.getMessage() + "]");
                    User.getInstance().updateDeviceList("");
                    MainFrame.getInstance().timeoutErrorDialog();
                    break;

                } catch (InterruptedException ex) {
                    remoteServerStatus = false;
                    Log.logger.warning("Server query thread interrupted: [" + ex.getMessage() + "]");
                }
            }
            // Close the socket before the thread exits
            closeSocket();
        } catch (IOException e) {
            remoteServerStatus = false;
            User.getInstance().updateDeviceList("");
            MainFrame.getInstance().connectErrorDialog();
            Log.logger.warning("Unable to close the existing socket: [" + e.getMessage() + "]");
            Log.logger.severe("May abandoned sockets still open");

        }
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
     * Creates a new socket for the device query thread.
     *
     * @throws IOException If an I/O error occurs while creating the socket.
     */
    private void createSocket() throws IOException {
        socket = SecureSocketBuilder.getNewSocket();
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
