package User;

import GUI.MainFrame;

import Device.Device;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class User {
    private transient static User singelton;
    private transient static final String configFile = "user.conf";

    private static String uuid;

    public static String remoteServerIp;

    public static int remoteServerPort;

    private transient ArrayList<Device> DeviceList = new ArrayList<>();

    // Constructor
    private User() {
        loadUser();
    }

    private User(String user, String ip, int port) throws IOException {
        uuid = user;
        remoteServerIp = ip;
        remoteServerPort = port;
        saveUser();
    }

    // Singelton getter
    public static User getInstance() {
        if (singelton == null) {
            singelton = new User();
            loadUser();
        }
        return singelton;
    }

    // Get UUID
    public static String getUUID() {
        return uuid;
    }

    public static String getAddress() {
        return remoteServerIp + ":" + Integer.toString(remoteServerPort);
    }

    // Get DeviceList
    public ArrayList<Device> getDevices() {
        return DeviceList;
    }

    // Get Device by MAC
    public Device getDeviceByMAC(String macAddr) {
        for (Device i : DeviceList) {
            if (i.getMac().equals(macAddr)) {
                return i;
            }
        }
        return null;
    }

    // Overwrite DeviceList to a newer one
    synchronized public void updateDeviceList(ArrayList<Device> newList) {
        DeviceList = newList;
        notifyAll();
    }

    // Update User uuid and server address
    public static void updateUser(String uuidField, String serverField) throws IOException {
        String ip;
        int port;
        //Process serverField
        String[] addr = serverField.split(":");
        if(addr.length != 2) {
            throw new IllegalArgumentException (serverField);
        }
        try {
            ip = addr[0];
            port = Integer.parseInt(addr[1]);
        }catch(IllegalArgumentException parse) {
            throw new IllegalArgumentException (serverField);
        }

        singelton = new User(uuidField, ip, port);
    }

    // Read User from file
    private static void loadUser() {
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

        } catch (Exception ex) {
            //Load default

            uuid = "unset";
            remoteServerIp = "unset";

        }
    }

    // Save User to file
    private static void saveUser() throws IOException {
            FileOutputStream file = new FileOutputStream(configFile);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(uuid);
            out.writeObject(remoteServerIp);
            out.writeObject(remoteServerPort);

            out.close();
            file.close();
    }
}
