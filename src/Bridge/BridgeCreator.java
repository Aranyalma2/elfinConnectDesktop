package Bridge;

import GUI.DeviceTable;
import GUI.MainFrame;
import SW.Log;
import User.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * The BridgeCreator class is responsible for managing bridge, controlling connections
 * status between users` devices and server sockets,
 * and updating the user interface based on user interactions.
 */

public class BridgeCreator {
    //Device view table
    DeviceTable table;
    //Selected row by user
    int selectedRow = -1;
    //Open connection control button
    JButton openConnection;
    //Close connection control button
    JButton closeConnection;
    //Open bridges list
    HashMap<String, TCPBridge> activeBridges = new HashMap<String, TCPBridge>();

    /**
     * Constructor for the BridgeCreator class.
     * Bridge creator object, depends on the view table and control buttons
     *
     * @param _table        The device table to be associated with the Devices table.
     * @param open          The button for opening a connection.
     * @param close         The button for closing a connection.
     */
    public BridgeCreator(DeviceTable _table, JButton open, JButton close) {
        table = _table;
        openConnection = open;
        closeConnection = close;
        addTableSelectionListener(table);

        //Add action listener to "openConnection" button
        openConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mac = (String) table.getValueAt(selectedRow, 2);
                startBridge(mac);
            }
        });

        //Add action listener to "closeConnection" button
        closeConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mac = (String) table.getValueAt(selectedRow, 2);
                stopBridge(mac);
            }
        });

    }

    /**
     * Starts a bridge for the specified device MAC address.
     *
     * @param mac The MAC address of the device.
     */
    private void startBridge(String mac) {

        Log.logger.fine("Attempt to start a bridge for: " + mac);

        //Check if "activeBridges" already has known TCPBridge object, if it has, stop and start a new one
        if(activeBridges.containsKey(mac)){
            stopBridge(mac);
        }

        //Start connection to server

        int localServerPort = 0;

        try {
            boolean localServerPort_Good = false;
            while (!localServerPort_Good) {
                try {
                    activeBridges.put(mac, new TCPBridge(localServerPort, User.getUUID(), mac));
                    localServerPort_Good = true;
                    localServerPort = activeBridges.get(mac).getLocalPort();

                    Log.logger.info("Bridge started for: (" + mac + ") at local port: " + localServerPort);

                } catch (RuntimeException runtimeException) {
                    Log.logger.warning(runtimeException.getMessage());
                    Log.logger.warning("Unable to open local server for: (" + mac + ") at port: " + localServerPort);
                    Log.logger.warning("Generate ports manually");
                    localServerPort = generatePort(localServerPort);
                }
            }

            //Disable open button and enable close
            openConnection.setEnabled(false);
            closeConnection.setEnabled(true);

        } catch (IOException exception) {
            Log.logger.warning(exception.getMessage());
            Log.logger.warning("Unable to create bridge for: (" + mac + ")");

            MainFrame.getInstance().bridgeErrorDialog(exception.getMessage());

            localServerPort = 0;

            openConnection.setEnabled(true);
            closeConnection.setEnabled(false);
        } finally {
            //Update port list and refresh table content
            User.getInstance().updatePort(mac, localServerPort);
            table.refreshTable();
        }
    }

    /**
     * Stops the bridge for the specified device MAC address.
     *
     * @param mac The MAC address of the device.
     */
    private void stopBridge(String mac) {

        Log.logger.fine("Stop bridge for: " + mac);

        // Close the active connection
        TCPBridge forRemove = activeBridges.remove(mac);
        forRemove.stopBridge();

        //Update port list and refresh table content
        User.getInstance().updatePort(mac, 0);
        table.refreshTable();

        openConnection.setEnabled(true);
        closeConnection.setEnabled(false);
    }

    /**
     * Adds a Selection Listener to the provided JTable.
     *
     * @param table The JTable to which the listener is added.
     */
    private void addTableSelectionListener(JTable table) {
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && e.getSource() == selectionModel && !selectionModel.isSelectionEmpty()) {

                    //If user select a row by clicking on it, get:
                    // -> row count
                    // -> the status of the row (device)
                    // -> the port of the row (active connection)
                    // By these values control button`s states updates

                    selectedRow = (int) table.getValueAt(table.getSelectedRow(), 0) - 1;
                    Object statusValue = table.getValueAt(selectedRow, 4);
                    Object connectionValue = table.getValueAt(selectedRow, 5);

                    if ("Online".equals(statusValue)) {
                        openConnection.setEnabled("none".equals(connectionValue));
                        closeConnection.setEnabled(!"none".equals(connectionValue));
                    } else {
                        if(!"none".equals(connectionValue)){
                            stopBridge((String) table.getValueAt(selectedRow, 2));
                        }
                        openConnection.setEnabled(false);
                        closeConnection.setEnabled(false);
                    }

                }
            }
        });
    }

    //
    /**
     * Deprecated method to generate a port number manually by
     * ports in the 'ArrayList<Integer> ports' list.
     *
     * @param reference The reference port number.
     * @return The generated port number.
     */
    @Deprecated
    private int generatePort(int reference) {
        ArrayList<Integer> ports = new ArrayList<>(User.getInstance().getPorts());
        ports.removeIf(n -> n == 0);

        int possiblePort;

        if (reference != 0 && !ports.contains(reference)) {
            possiblePort = reference;
        } else {
            if (ports.isEmpty()) {
                //Default
                return 50000;
            }
            possiblePort = Collections.max(ports);
        }

        if (possiblePort <= 65534) {
            return possiblePort + 1;
        } else {
            return Collections.min(ports) - 1;
        }
    }

    /**
     * Stops all active bridges, maintaining the object, and resets the control buttons' state.
     */
    public void stopAllActiveBridge() {
        for (String mac : activeBridges.keySet()){
            stopBridge(mac);
        }
        openConnection.setEnabled(false);
        closeConnection.setEnabled(false);
    }
}
