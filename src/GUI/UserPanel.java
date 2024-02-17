package GUI;

import SW.Log;
import User.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * The UserPanel class represents a panel containing user input fields and controls for updating user information.
 * It includes text fields for UUID and server address, an "Apply and Connect" button, and a connection status panel.
 */
public class UserPanel extends JPanel {
    JTextField uuidTEXT;
    JTextField serverTEXT;
    ServerConnectionStatusPanel status;

    /**
     * Constructor for the UserPanel class.
     * Initializes the layout, creates user input fields, and adds controls for updating user information.
     *
     * @param _status The ServerConnectionStatusPanel to display the connection status.
     */
    public UserPanel(ServerConnectionStatusPanel _status) {
        status = _status;

        // USER SECTION
        // --LEFT--
        JPanel input = new JPanel();
        input.setLayout(new BoxLayout(input, BoxLayout.Y_AXIS));

        // UUID
        JPanel uuidPanel = new JPanel(new FlowLayout());
        uuidPanel.add(new JLabel("UUID:   "));
        uuidTEXT = (JTextField) uuidPanel.add(new JTextField(23));
        input.add(uuidPanel);

        // SERVER
        JPanel serverPanel = new JPanel(new FlowLayout());
        serverPanel.add(new JLabel("Server:"));
        serverTEXT = (JTextField) serverPanel.add(new JTextField(23));
        input.add(serverPanel);

        update();

        this.add(input);

        // --RIGHT--
        JButton apply = new JButton("<html><center>Apply<br>and<br>Connect</center></html>");
        apply.setPreferredSize(new Dimension(100, 75));
        apply.addActionListener(new ApplyButton());
        this.add(apply);

        // BORDER jPanel
        this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "User and Server"));

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    /**
     * ActionListener for the "Apply and Connect" button.
     * Updates user information, stops all active bridges, and sets the connection status to connecting.
     */
    private class ApplyButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Log.logger.info("Button pressed");
                User.updateUser(uuidTEXT.getText(), serverTEXT.getText());
                MainFrame.getInstance().bridgeCreator.stopAllActiveBridge();
                status.setConnectionStatus(ServerConnectionStatusPanel.ConnectionStatus.CONNECTING);
            } catch (IllegalArgumentException ie) {
                // INVALID SERVER ADDRESS INPUT
                Log.logger.warning("Invalid server address: [" + ie.getMessage() + "]");
                addressErrorDialog(ie.getMessage());
            } catch (IOException ex) {
                Log.logger.warning("Save error occurred: [" + ex.getMessage() + "]");
                saveUserErrorDialog(ex.getMessage());
            }
        }
    }

    /**
     * Displays an error dialog for invalid server address input.
     *
     * @param tried The invalid server address.
     */
    public void addressErrorDialog(String tried) {
        Log.logger.info("Invalid address dialog shown: [" + tried + "]");
        String message = "Invalid server address format: " + tried + "\nFormat: ip/domain:port\nExample: 192.0.2.1:7218 or example.com:7218";
        JOptionPane.showMessageDialog(MainFrame.getInstance().frame, message, "Invalid address", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays an error dialog for unable to save user changes.
     *
     * @param tried The error message.
     */
    public void saveUserErrorDialog(String tried) {
        Log.logger.info("Unable to save user dialog shown: [" + tried + "]");
        String message = "Unable to save user changes.";
        JOptionPane.showMessageDialog(MainFrame.getInstance().frame, message, "Unable to save user", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Updates the text fields with the current user information.
     */
    private void update() {
        uuidTEXT.setText(User.getUUID());
        serverTEXT.setText(User.getAddress());
    }
}
