package GUI;


import SW.Log;
import User.User;
import Bridge.BridgeCreator;
import SW.SWdata;

import java.awt.*;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * The MainFrame class represents the main graphical user interface for the Elfin Bridge Client.
 * It includes panels for user information, server connection status, and a table of devices.
 */
public class MainFrame extends WindowAdapter {
    // Singleton instance
    private static MainFrame singleton = null;

    // Panels for user information and server connection status
    public UserPanel userPanel;
    public ServerConnectionStatusPanel serverPanel;

    // Table panel
    public DeviceTable deviceTable;
    public BridgeCreator bridgeCreator;

    // Main frame of the application
    public JFrame frame;

    // Private constructor for singleton pattern
    private MainFrame() {
        frame = new JFrame("Elfin Bridge Client " + SWdata.version);
        BufferedImage image = SWdata.getIcon();
        if (image != null) {
            frame.setIconImage(image);
        }
    }

    /**
     * Gets the singleton instance of the MainFrame class.
     *
     * @return The MainFrame singleton instance.
     */
    public static MainFrame getInstance() {
        if (singleton == null) {
            singleton = new MainFrame();
        }
        return singleton;
    }

    /**
     * Sets up the frame with default settings.
     *
     * @param frame The JFrame to be set up.
     */
    private void setupFrame(JFrame frame) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(this);
        frame.setMinimumSize(new Dimension(800, 600));
    }

    /**
     * Paints user information and server connection status panels to the specified container.
     *
     * @param north The container to paint user information and server connection status panels.
     */
    private void paintUserAndStatus(Container north) {
        Log.logger.info("Create User & Status panels");

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.LINE_AXIS));

        serverPanel = new ServerConnectionStatusPanel();
        userPanel = new UserPanel(serverPanel);
        northPanel.add(userPanel);
        northPanel.add(serverPanel);

        north.add(northPanel, BorderLayout.NORTH);
    }

    /**
     * Paints the table of devices and control buttons to the specified container.
     *
     * @param south The container to paint the table of devices and control buttons.
     */
    private void paintTable(Container south) {
        Log.logger.info("Create DeviceTable & Control panels");

        JLabel tableHeader = new JLabel("Devices");
        tableHeader.setFont(new Font("Ariel", Font.BOLD, 20));

        JPanel bridgeBtns = new JPanel();
        JButton openConnectionButton = new JButton("Open connection");
        openConnectionButton.setEnabled(false);
        JButton closeConnectionButton = new JButton("Close connection");
        closeConnectionButton.setEnabled(false);
        bridgeBtns.add(openConnectionButton);
        bridgeBtns.add(closeConnectionButton);
        bridgeBtns.setLayout(new FlowLayout(FlowLayout.LEFT));

        deviceTable =  new DeviceTable();
        bridgeCreator = new BridgeCreator(deviceTable, openConnectionButton, closeConnectionButton);

        south.add(tableHeader, BorderLayout.CENTER);
        south.add(bridgeBtns,BorderLayout.CENTER);
        south.add(new JScrollPane(deviceTable), BorderLayout.SOUTH);
    }

    /**
     * Paints user information, server connection status, and the table of devices to the frame.
     *
     * @param frame The JFrame to paint the panels.
     */
    private void paintElementsToFrame(JFrame frame) {
        frame.setLayout(new BorderLayout());
        paintUserAndStatus(frame.getContentPane());
        paintTable(frame.getContentPane());
    }

    /**
     * Creates the graphical user interface, sets up the frame, and displays it.
     */
    public void createGUI() {
        Log.logger.info("Setup panels");

        setupFrame(frame);
        paintElementsToFrame(frame);
        frame.setVisible(true);

        Log.logger.info("Rendering panels");
        
        frame.pack();
    }

    /**
     * Displays an error dialog for a local server error.
     *
     * @param port The port number where the error occurred.
     */
    public void localServerErrorDialog(String port) {
        Log.logger.info("Local error dialog shown: [" + port + "]");
        String message = "Unable to create local server:\nlocalhost:" + port;
        JOptionPane.showMessageDialog(frame, message, "Local error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays an error dialog for a connection timeout.
     */
    public void timeoutErrorDialog() {
        MainFrame.getInstance().serverPanel.setConnectionStatus(ServerConnectionStatusPanel.ConnectionStatus.NOT_CONNECTED);
        bridgeCreator.stopAllActiveBridge();

        Log.logger.info("Connection error dialog shown: [TIMEOUT]");

        String message = "Server connection lost:\n" + User.getAddress();
        String[] buttons = {"Ok", "Reconnect"};
        int selected = JOptionPane.showOptionDialog(frame, message, "Connection error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, buttons, buttons[0]);
        if (selected != -1 && buttons[selected].equals(buttons[1])) {
            MainFrame.getInstance().serverPanel.setConnectionStatus(ServerConnectionStatusPanel.ConnectionStatus.CONNECTING);
            User.getInstance().manualReconnectRemoteServer();
        }
    }

    /**
     * Displays an error dialog for a bridge-related error.
     *
     * @param reason The reason for the bridge error.
     */
    public void bridgeErrorDialog(String reason) {
        Log.logger.info("Bridge error dialog shown: [" + reason + "]");
        JOptionPane.showMessageDialog(frame, reason, "Bridge error", JOptionPane.ERROR_MESSAGE);
    }


    // Handles window closing event
    @Override
    public void windowClosing(WindowEvent e) {
        int a = JOptionPane.showConfirmDialog(frame, "It will close active connections!\nAre you sure?", "Quit",
                JOptionPane.YES_NO_OPTION);
        if (a == JOptionPane.YES_OPTION) {
            bridgeCreator.stopAllActiveBridge();
            User.getInstance().stopRemoteServerConnection();
            Log.logger.info("Closing the app");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }
}
