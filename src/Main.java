import GUI.MainFrame;
import java.io.*;
import java.net.*;
import java.io.IOException;
import java.util.Enumeration;

public class Main {
    public static void main(String[] args) {
        int localPort = 12345; // Port to host the local TCP server
        String remoteHost = "localhost"; // Remote server hostname or IP
        int remotePort = 8080; // Remote server port


        BridgeServerConnection bs = new BridgeServerConnection();
        try {
            bs.createSockets(localPort,remoteHost,remotePort,"almafaa","Terminal-Test","98D863CC68B1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
