package User;

import Device.Device;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Objects;


public class User {
    private transient static User singelton;
    private transient static final String configFile = "user.conf";

    private static String uuid;

    public static String remoteServerIp;

    public static int remoteServerPort;

    private transient static DeviceQueryThread deviceQueryThread;

    private transient static ArrayList<Device> deviceList = new ArrayList<>();
    private transient static ArrayList<Integer> portList = new ArrayList<>();

    // Constructor
    private User() {
        if(loadUser()) {
            deviceQueryThread = new DeviceQueryThread(uuid, remoteServerIp, remoteServerPort);
            deviceQueryThread.start();
        }
    }

    private User(String user, String ip, int port) throws IOException {
        uuid = user;
        remoteServerIp = ip;
        remoteServerPort = port;
        saveUser();
        deviceQueryThread = new DeviceQueryThread(uuid, remoteServerIp, remoteServerPort);
        deviceQueryThread.start();
    }

    // Singelton getter
    public static User getInstance() {
        if (singelton == null) {
            singelton = new User();
        }
        return singelton;
    }

    // Get UUID
    public static String getUUID() {
        return uuid;
    }

    public static String getAddress() {

        if(remoteServerIp.isEmpty() || remoteServerPort == 0){
            return "";
        }
        return remoteServerIp + ":" + Integer.toString(remoteServerPort);
    }

    public boolean getRemoteServerStatus(){
        if(deviceQueryThread == null)
            return false;
        return deviceQueryThread.getConnectionStatus();
    }

    // Get deviceList
    public ArrayList<Device> getDevices()  {
        return deviceList;
    }

    public ArrayList<Integer> getPorts()  {
        return portList;
    }

    // Get Device by MAC
    private Device getDeviceByMAC(String macAddr) {
        for (Device i : deviceList) {
            if (i.getMac().equals(macAddr)) {
                return i;
            }
        }
        return null;
    }

    public void updatePort(String mac, Integer port){

        for(int i = 0; i < deviceList.size(); i++){
            if(deviceList.get(i).getMac().equals(mac)){
                portList.set(i, port);
            }
        }

    }

    // Overwrite deviceList to a newer one
    public void updateDeviceList(String list) {

        ArrayList<Device> newDeviceList = DataFromJson.convertJsonToDevs(list);

        ArrayList<Integer> newPortList = new ArrayList<>();

        int new_devIdx = 0;
        for(Device new_device : newDeviceList){
            int old_devIdx = 0;
            newPortList.add(0);
            for(Device old_device : deviceList){
                if (Objects.equals(old_device.getMac(), new_device.getMac())) {

                    int oldPort = portList.get(old_devIdx);
                    //Device was in an active bridge, but no went offline
                    if(oldPort != 0 && !newDeviceList.get(new_devIdx).getStatus()){
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
        if(deviceQueryThread != null) {
            deviceQueryThread.interruptSleep();
            deviceQueryThread.interruptKill();
        }

        singelton = new User(uuidField, ip, port);
    }

    // Read User from file
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

            return true;

        } catch (Exception ex) {
            //Load default

            uuid = "";
            remoteServerIp = "";
            remoteServerPort = 0;
            return false;

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
