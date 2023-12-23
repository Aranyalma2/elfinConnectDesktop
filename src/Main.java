import GUI.MainFrame;
import User.User;

import java.io.*;
import java.net.*;
import java.io.IOException;
import java.util.Enumeration;

public class Main {
    public static void main(String[] args) {

        User user = User.getInstance();

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
