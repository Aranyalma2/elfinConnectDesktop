package User;

import Device.Device;
import SW.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The User class store the user's uuid, and remote server configuration persistence.
 * Model object for user, store devices, ports, and manage server query thread.
 */
public class User {
    private transient static User singleton;
    private transient static final String configFile = "user.conf";

    // Stores the UUID of the user
    private static String uuid;

    // Stores the IP address of the remote server
    public static String remoteServerIp;

    // Stores the port number of the remote server
    public static int remoteServerPort;

    // Manages the device query thread for fetching device updates
    private transient static DeviceQueryThread deviceQueryThread;

    // Stores the list of devices fetched from the remote server
    private transient static ArrayList<Device> deviceList = new ArrayList<>();

    // Stores the list of ports associated with devices
    private transient static ArrayList<Integer> portList = new ArrayList<>();

    /**
     * Default constructor. Recreates the user object and initializes the device query thread.
     */
    private User() {
        // Log that a user object is being recreated
        Log.logger.fine("Recreating user object");
        // Attempt to load user data from the configuration file
        if (loadUser()) {
            // If successful, initialize and start the device query thread
            deviceQueryThread = new DeviceQueryThread(uuid, remoteServerIp, remoteServerPort);
            deviceQueryThread.start();
        }
    }

    /**
     * Constructor with parameters. Creates a new user object with provided values.
     *
     * @param user The UUID of the user.
     * @param ip   The IP address of the remote server.
     * @param port The port number of the remote server.
     * @throws IOException If an I/O error occurs during user creation.
     */
    private User(String user, String ip, int port) throws IOException {
        // Log that a new user object is being created
        Log.logger.fine("Creating a new user object");
        // Initialize user properties with provided values
        uuid = user;
        remoteServerIp = ip;
        remoteServerPort = port;
        // Save the user data to the configuration file
        saveUser();
        // Initialize and start the device query thread
        deviceQueryThread = new DeviceQueryThread(uuid, remoteServerIp, remoteServerPort);
        deviceQueryThread.start();
    }

    /**
     * Gets the singleton instance of the User class.
     *
     * @return The singleton instance of the User class.
     */
    public static User getInstance() {
        // If the singleton instance is null, create a new user object
        if (singleton == null) {
            singleton = new User();
        }
        // Return the singleton instance
        return singleton;
    }

    /**
     * Gets the UUID of the user.
     *
     * @return The UUID of the user.
     */
    public static String getUUID() {
        // Return the UUID of the user
        return uuid;
    }

    /**
     * Gets the formatted address string (IP:Port).
     *
     * @return The formatted address string (IP:Port).
     */
    public static String getAddress() {
        // If either the remote server IP or port is not set, return an empty string
        if (remoteServerIp.isEmpty() || remoteServerPort == 0) {
            return "";
        }
        // Return the formatted address string
        return remoteServerIp + ":" + Integer.toString(remoteServerPort);
    }

    /**
     * Gets the connection status of the remote server.
     *
     * @return True if connected; false otherwise.
     */
    public boolean getRemoteServerStatus() {
        // If the device query thread is null, return false; otherwise, return the connection status
        if (deviceQueryThread == null)
            return false;
        return deviceQueryThread.getConnectionStatus();
    }

    /**
     * Start the remote server connection work only if deviceQueryThread terminated
     */
    public void restartTerminatedRemoteServerConnection() {
        if (deviceQueryThread == null)
            return;
        if(deviceQueryThread.getState() != Thread.State.TERMINATED){
            stopRemoteServerConnection();
        }
        deviceQueryThread = new DeviceQueryThread(uuid, remoteServerIp, remoteServerPort);
        deviceQueryThread.start();
    }

    /**
     * Stops the remote server connection by interrupting the sleep and kill the thread.
     */
    public void stopRemoteServerConnection() {
        if (deviceQueryThread == null)
            return;
        deviceQueryThread.interruptSleep();
        deviceQueryThread.interruptKill();
    }

    /**
     * Manually triggers a reconnect to the remote server by interrupting the sleep.
     */
    public void manualFetchRemoteServer() {
        if (deviceQueryThread == null)
            return;
        deviceQueryThread.interruptSleep();
    }

    /**
     * Get deviceList
     *
     * @return deviceList
     */
    public ArrayList<Device> getDevices() {
        return deviceList;
    }
    /**
     * Get ports
     *
     * @return portList
     */
    public ArrayList<Integer> getPorts() {
        return portList;
    }

    /**
     * Gets a device by its MAC address.
     *
     * @param macAddr The MAC address of the device.
     * @return The device with the specified MAC address, or null if not found.
     */
    private Device getDeviceByMAC(String macAddr) {
        for (Device i : deviceList) {
            if (i.getMac().equals(macAddr)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Updates the port associated with a specific MAC address in the portList.
     *
     * @param mac  The MAC address of the device.
     * @param port The new port number.
     */
    public void updatePort(String mac, Integer port) {

        for (int i = 0; i < deviceList.size(); i++) {
            if (deviceList.get(i).getMac().equals(mac)) {
                portList.set(i, port);
            }
        }

    }

    /**
     * Updates the device list and port list based on the received JSON string.
     *
     * @param list The JSON string representing the device list.
     */
    public void updateDeviceList(String list) {

        ArrayList<Device> newDeviceList = DataFromJson.convertJsonToDevs(list);

        ArrayList<Integer> newPortList = new ArrayList<>();

        int new_devIdx = 0;
        for (Device new_device : newDeviceList) {
            int old_devIdx = 0;
            newPortList.add(0);
            for (Device old_device : deviceList) {
                if (Objects.equals(old_device.getMac(), new_device.getMac())) {

                    int oldPort = portList.get(old_devIdx);
                    // Device was in an active bridge but has now gone offline
                    if (oldPort != 0 && !newDeviceList.get(new_devIdx).getStatus()) {
                        oldPort = 0;
                    }
                    newPortList.set(new_devIdx, oldPort);
                    break;
                }
                old_devIdx++;
            }
            new_devIdx++;
        }

        deviceList = newDeviceList;
        portList = newPortList;
    }

    /**
     * Updates the user's UUID and server address. Stops the existing device query thread and creates a new one.
     *
     * @param uuidField   The new UUID.
     * @param serverField The new server address.
     * @throws IOException If an I/O error occurs while updating the user.
     */
    public static void updateUser(String uuidField, String serverField) throws IOException {
        String ip;
        int port;
        // Process serverField
        String[] addr = serverField.split(":");
        if (addr.length != 2) {
            throw new IllegalArgumentException(serverField);
        }
        try {
            ip = addr[0];
            port = Integer.parseInt(addr[1]);
        } catch (IllegalArgumentException parse) {
            throw new IllegalArgumentException(serverField);
        }
        if (deviceQueryThread != null) {
            deviceQueryThread.interruptSleep();
            deviceQueryThread.interruptKill();
        }

        singleton = new User(uuidField, ip, port);
    }

    /**
     * Reads the user's configuration from the file and initializes the user.
     *
     * @return True if the user was successfully loaded from the file, false otherwise.
     */
    private static boolean loadUser() {
        try {
            // Reading the object from a file
            FileInputStream file = new FileInputStream(configFile);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            uuid = (String) in.readObject();
            remoteServerIp = (String) in.readObject();
            remoteServerPort = (int) in.readObject();

            in.close();
            file.close();
            Log.logger.fine("User data loaded");
            return true;

        } catch (Exception ex) {
            // Unable to load user file, load default
            Log.logger.warning("Unable to load the user file, load default");

            uuid = "";
            remoteServerIp = "";
            remoteServerPort = 0;
            return false;

        }
    }

    /**
     * Saves the user's configuration to the file.
     *
     * @throws IOException If an I/O error occurs while saving the user.
     */
    private static void saveUser() throws IOException {
        FileOutputStream file = new FileOutputStream(configFile);
        ObjectOutputStream out = new ObjectOutputStream(file);

        out.writeObject(uuid);
        out.writeObject(remoteServerIp);
        out.writeObject(remoteServerPort);

        out.close();
        file.close();

        Log.logger.fine("User data saved");
    }
}
