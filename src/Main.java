import Device.Device;
import GUI.MainFrame;
import User.User;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        User user = User.getInstance();

        ArrayList<Device> d = new ArrayList<Device>();
        Device d1 = new Device("host", "mac", "date", true);
        Device d2 = new Device("host2", "mac2", "date2", false);
        Device d3 = new Device("host3", "mac3", "date3", false);
        d.add(d1);
        d.add(d2);
        d.add(d3);

        user.updateDeviceList(d);

        MainFrame main = MainFrame.getInstance();
        main.createGUI();

        int localPort = 12345; // Port to host the local TCP server
        String remoteHost = "localhost"; // Remote server hostname or IP
        int remotePort = 8080; // Remote server port

        BridgeServerConnection bs = new BridgeServerConnection();
        try {
            bs.initBridge(remoteHost,remotePort,"965b963fa1b585df","Terminal-Test","98D863584D0E");
            Thread.sleep(10000);
            bs.buildConnection();
            bs.createBridge(localPort,remoteHost,remotePort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
