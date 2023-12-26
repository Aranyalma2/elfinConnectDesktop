package GUI;

import javax.swing.*;
import java.awt.*;

public class ServerConnectionStatusPanel extends JPanel {
    private JLabel statusLabel;

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

    public void setConnectionStatus(ConnectionStatus status) {
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

    public enum ConnectionStatus {
        CONNECTED, CONNECTING, NOT_CONNECTED
    }

}
