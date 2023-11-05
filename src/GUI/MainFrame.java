package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BoxLayout;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

//import clientIoT.clientIoT;
//import user.User;

public class MainFrame extends WindowAdapter {
    private static MainFrame singelton = null;
/*
    public JPanel userPanel;
    public ConnectionFrame connectionPanel;
    public BridgeFrame bridgePanel;

    public JFrame frame;
    public TableData data;

    private MainFrame() {
        frame = new JFrame("IoT Bridge Client");
        data = new TableData();
    }

    public static MainFrame getInstance() {
        if (singelton == null) {
            singelton = new MainFrame();
        }
        return singelton;
    }

    // paint bridge connection states
    private void paintBridge(Container center) {
        bridgePanel = new BridgeFrame();
        center.add(bridgePanel, BorderLayout.CENTER);

    }

    // paint table
    private void paintTable(Container south) {
        JTable jTable = new JTable(data);
        // size
        jTable.getColumnModel().getColumn(0).setMinWidth(25);
        jTable.getColumnModel().getColumn(1).setMinWidth(150);
        jTable.getColumnModel().getColumn(2).setMinWidth(150);
        jTable.getColumnModel().getColumn(3).setMinWidth(100);
        jTable.getColumnModel().getColumn(4).setMinWidth(150);
        jTable.getColumnModel().getColumn(5).setMinWidth(100);
        jTable.getColumnModel().getColumn(6).setMinWidth(75);
        // color
        TableColumn col = jTable.getColumnModel().getColumn(6);
        col.setCellRenderer(new tableColorRenderer(Color.green, Color.red));

        jTable.setFillsViewportHeight(true);
        jTable.setAutoCreateRowSorter(true);

        south.add(new JScrollPane(jTable), BorderLayout.SOUTH);
    }

    // paint user
    private void paintUser(Container north) {
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.LINE_AXIS));

        connectionPanel = new ConnectionFrame(bridgePanel);
        northPanel.add(connectionPanel);

        userPanel = new UserFrame();
        northPanel.add(userPanel);

        north.add(northPanel, BorderLayout.NORTH);
    }

    private void setupFrame(JFrame frame) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(this);
        // frame.setSize(screen_Width , screen_Height);
        frame.setMinimumSize(new Dimension(800, 600));
    }

    private void paintElementsToFrame(JFrame frame) {

        frame.setLayout(new BorderLayout());
        paintBridge(frame.getContentPane());
        paintUser(frame.getContentPane());
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

    public void serverErrorDialog() {
        String message = "Unable to connect server:\n" + User.edge.getAddress();
        JOptionPane.showMessageDialog(frame, message, "Server Error", JOptionPane.ERROR_MESSAGE);
    }

    public void authErrorDialog() {
        String message = "Unable authenticate to server:\n" + User.getInstance().getUUID();
        String[] buttons = { "Try Again", "Cancel" };
        int selected = JOptionPane.showOptionDialog(frame, message, "Server Error", JOptionPane.ERROR_MESSAGE, 0, null, buttons, buttons[0]);
        if(selected != -1 && buttons[selected].equals(buttons[0])) {
            clientIoT.startServerConnection();
        }
    }
    public void timeoutErrorDialog() {
        String message = "Server connection lost:\n" + User.edge.getAddress();
        String[] buttons = { "Reconnect", "Cancel" };
        int selected = JOptionPane.showOptionDialog(frame, message, "Server Error", JOptionPane.ERROR_MESSAGE, 0, null, buttons, buttons[0]);
        if(selected != -1 && buttons[selected].equals(buttons[0])) {
            clientIoT.startServerConnection();
        }
    }



    public void windowClosing(WindowEvent e) {
        int a = JOptionPane.showConfirmDialog(frame, "It will close active connection!\nAre you sure?", "Quit",
                JOptionPane.YES_NO_OPTION);
        if (a == JOptionPane.YES_OPTION) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }
*/
}
