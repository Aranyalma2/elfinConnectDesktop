package GUI;

import User.User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.table.TableColumn;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class MainFrame extends WindowAdapter {
    private static MainFrame singelton = null;

    public JPanel userPanel;

    public JPanel serverPanel;

    public JFrame frame;

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

        userPanel = new UserPanel();
        northPanel.add(userPanel);

        serverPanel = new ServerConnectionStatusPanel();
        northPanel.add(serverPanel);

        north.add(northPanel, BorderLayout.NORTH);
    }

    // paint table
    private void paintTable(Container south) {
        JTable jTable = new JTable(new DevicePanel());
        // size
        jTable.getColumnModel().getColumn(0).setMinWidth(25);
        jTable.getColumnModel().getColumn(1).setMinWidth(150);
        jTable.getColumnModel().getColumn(2).setMinWidth(150);
        jTable.getColumnModel().getColumn(3).setMinWidth(150);
        jTable.getColumnModel().getColumn(4).setMinWidth(75);
        // color
        TableColumn col = jTable.getColumnModel().getColumn(4);
        col.setCellRenderer(new tableColorRenderer(Color.green, Color.red));

        jTable.setFillsViewportHeight(true);
        jTable.setAutoCreateRowSorter(true);

        south.add(new JScrollPane(jTable), BorderLayout.SOUTH);
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

    public void serverErrorDialog() {
        String message = "Unable to connect server:\n" + User.getAddress();
        JOptionPane.showMessageDialog(frame, message, "Server Error", JOptionPane.ERROR_MESSAGE);
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
        String message = "Server connection lost:\n" + User.getAddress();
        String[] buttons = { "Reconnect", "Cancel" };
        int selected = JOptionPane.showOptionDialog(frame, message, "Server Error", JOptionPane.ERROR_MESSAGE, 0, null, buttons, buttons[0]);
        if(selected != -1 && buttons[selected].equals(buttons[0])) {
            //HERE SHOULD START LOGIN AND PINGING THREADS
        }
    }



    public void windowClosing(WindowEvent e) {
        int a = JOptionPane.showConfirmDialog(frame, "It will close active connection!\nAre you sure?", "Quit",
                JOptionPane.YES_NO_OPTION);
        if (a == JOptionPane.YES_OPTION) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }
}
