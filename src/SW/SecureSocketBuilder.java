/**
 * SecureSocketBuilder is a utility class for creating secure SSL sockets.
 */
package SW;

import java.io.*;
import javax.net.ssl.*;

public class SecureSocketBuilder {


    // The default SSL socket factory used for creating SSL sockets.
    private static final SSLSocketFactory sslSocketFactory;

    // The host (IP address or domain) to connect to.
    private static String host;

    // The port number to connect to.
    private static int port = 0;

    /**
     * Static block to initialize the default SSL socket factory.
     */
    static {
        // Get the default SSL server socket factory and cache it for further usage
        sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    /**
     * Sets the server information (host and port) for the SSL connection.
     *
     * @param host The host (IP address or domain) to connect to.
     * @param port The port number to connect to.
     */
    public static void setServer(String host, int port) {
        SecureSocketBuilder.host = host;
        SecureSocketBuilder.port = port;
    }

    /**
     * Retrieves the host (IP address or domain) for the SSL connection.
     *
     * @return The host for the SSL connection.
     */
    public static String getHost() {
        return host;
    }

    /**
     * Retrieves the port number for the SSL connection.
     *
     * @return The port number for the SSL connection.
     */
    public static int getPort() {
        return port;
    }

    /**
     * Creates a new SSL socket and performs the TLS handshake.
     *
     * @return The newly created SSLSocket.
     * @throws IOException If an I/O error occurs during the socket creation.
     */
    public static SSLSocket getNewSocket() throws IOException {
        // Check if host or port is null
        if (host == null || port == 0) {
            throw new IOException("Unable to create SSLSocket: host or port is null");
        }

        // Create an SSLSocket using the SSLSocketFactory
        SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);

        // Enable only TLS 1.3
        sslSocket.setEnabledProtocols(new String[]{"TLSv1.3"});

        // Perform the TLS handshake
        sslSocket.startHandshake();

        return sslSocket;
    }
}
