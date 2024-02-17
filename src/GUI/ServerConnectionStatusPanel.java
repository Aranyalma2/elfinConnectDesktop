package GUI;

import SW.Log;

import javax.swing.*;
import java.awt.*;

/**
 * The ServerConnectionStatusPanel class represents a panel displaying the connection status to the server.
 * It includes a label to indicate whether the connection is connected, connecting, or not connected.
 */
public class ServerConnectionStatusPanel extends JPanel {
    private JLabel statusLabel;

    /**
     * Constructor for the ServerConnectionStatusPanel class.
     * Initializes the layout manager, creates components, and sets initial styles.
     */
    public ServerConnectionStatusPanel() {
        // Set layout manager
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Create and add components
        statusLabel = new JLabel("Not connected");
        statusLabel.setFont(new Font("Ariel", Font.BOLD, 20));
        statusLabel.setForeground(Color.RED);

        this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Server connection status"));

        add(statusLabel);
    }

    /**
     * Sets the connection status based on the provided ConnectionStatus enum.
     *
     * @param status The connection status to be set.
     */
    public void setConnectionStatus(ConnectionStatus status) {

        Log.logger.fine("Update connection status panel to: (" + status +")");

        switch (status) {
            case CONNECTED:
                statusLabel.setText("Connected");
                statusLabel.setForeground(Color.GREEN);
                break;
            case CONNECTING:
                statusLabel.setText("Connecting");
                statusLabel.setForeground(Color.BLUE);
                break;
            case NOT_CONNECTED:
                statusLabel.setText("Not connected");
                statusLabel.setForeground(Color.RED);
                break;
        }
    }

    /**
     * Enum representing different connection statuses: CONNECTED, CONNECTING, and NOT_CONNECTED.
     */
    public enum ConnectionStatus {
        CONNECTED, CONNECTING, NOT_CONNECTED
    }
}
