package SW;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class SecureSocketBuilder {
    private static final SSLSocketFactory sslSocketFactory;
    private static String host;
    private static int port = 0;

    static {
        // Get the default SSL server socket factory and cache it for further usage
        sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    public static void setServer(String host, int port){
        SecureSocketBuilder.host = host;
        SecureSocketBuilder.port = port;
    }

    public static SSLSocket getNewSocket() throws IOException {

        if(host == null || port == 0){
            throw new IOException("Unable create SSLSocket: host or port is null");
        }

        // Create an SSLSocket using the SSLSocketFactory
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);

        // Enable all the supported cipher suites
        sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());

        // Perform the TLS handshake
        sslSocket.startHandshake();

        return sslSocket;
    }

}
