package GUI;

import User.User;
import Bridge.BridgeCreator;

import java.awt.*;

import javax.swing.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class MainFrame extends WindowAdapter {
    private static MainFrame singelton = null;

    public UserPanel userPanel;

    public ServerConnectionStatusPanel serverPanel;

    public DeviceTable deviceTable;

    public JFrame frame;

    private BridgeCreator bridgeCreator;

    private MainFrame() {
        frame = new JFrame("Elfin Bridge Client");
    }

    public static MainFrame getInstance() {
        if (singelton == null) {
            singelton = new MainFrame();
        }
        return singelton;
    }


    // paint user
    private void paintUserAndStatus(Container north) {
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.LINE_AXIS));

        serverPanel = new ServerConnectionStatusPanel();

        userPanel = new UserPanel(serverPanel);
        northPanel.add(userPanel);

        northPanel.add(serverPanel);

        north.add(northPanel, BorderLayout.NORTH);
    }

    // paint table
    private void paintTable(Container south) {
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

    private void setupFrame(JFrame frame) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(this);
        // frame.setSize(screen_Width , screen_Height);
        frame.setMinimumSize(new Dimension(800, 600));
    }

    private void paintElementsToFrame(JFrame frame) {

        frame.setLayout(new BorderLayout());
        paintUserAndStatus(frame.getContentPane());
        paintTable(frame.getContentPane());

    }

    public void createGUI() {

        // Set up the content pane
        setupFrame(frame);
        paintElementsToFrame(frame);
        // Display the window
        frame.setVisible(true);
        // device.setFullScreenWindow(frame);
        frame.pack();
    }

    public void localServerErrorDialog(String port) {
        String message = "Unable to create local server:\nlocalhost:" + port;
        JOptionPane.showMessageDialog(frame, message, "Local error", JOptionPane.ERROR_MESSAGE);
    }

    public void authErrorDialog() {
        String message = "Unable authenticate to server:\n" + User.getUUID();
        String[] buttons = { "Try Again", "Cancel" };
        int selected = JOptionPane.showOptionDialog(frame, message, "Server Error", JOptionPane.ERROR_MESSAGE, 0, null, buttons, buttons[0]);
        if(selected != -1 && buttons[selected].equals(buttons[0])) {
            //HERE SHOULD START LOGIN AND PINGING THREADS
        }
    }
    public void timeoutErrorDialog() {
        String message = "Server connection lost:\n" + User.getAddress() + "\nReconnecting...";
        String[] buttons = { "Understood" };
        int selected = JOptionPane.showOptionDialog(frame, message, "Connection error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, buttons, buttons[0]);
        MainFrame.getInstance().serverPanel.setConnectionStatus(ServerConnectionStatusPanel.ConnectionStatus.NOT_CONNECTED);
        bridgeCreator.stopAllActiveBridge();
    }

    public void bridgeErrorDialog(String reason) {
        JOptionPane.showMessageDialog(frame, reason, "Bridge error", JOptionPane.ERROR_MESSAGE);
    }



    public void windowClosing(WindowEvent e) {
        int a = JOptionPane.showConfirmDialog(frame, "It will close active connection!\nAre you sure?", "Quit",
                JOptionPane.YES_NO_OPTION);
        if (a == JOptionPane.YES_OPTION) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }
}
