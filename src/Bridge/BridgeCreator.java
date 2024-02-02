package Bridge;

import GUI.DeviceTable;
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

public class BridgeCreator {

    DeviceTable table;

    int selectedRow = -1;

    JButton openConnection;
    JButton closeConnection;

    HashMap<Integer, TCPBridge> activeBridges = new HashMap<Integer, TCPBridge>();

    public BridgeCreator(DeviceTable _table, JButton open, JButton close){
        table = _table;
        openConnection = open;
        closeConnection = close;
        addTableSelectionListener(table);

        openConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Generate a port and start connection to server
                int localServerPort = generatePort(0);
                String mac = (String)table.getValueAt(selectedRow, 2);
                try {
                    boolean localServerPort_Good = false;
                    while(!localServerPort_Good) {
                        try {
                            activeBridges.put(selectedRow, new TCPBridge(localServerPort, User.remoteServerIp, User.remoteServerPort, User.getUUID(), mac));
                            localServerPort_Good = true;
                        } catch (RuntimeException runtimeException) {
                            localServerPort = generatePort(localServerPort);
                        }
                    }

                    openConnection.setEnabled(false);
                    closeConnection.setEnabled(true);
                }catch (IOException exception){
                    System.out.println("Open bridge ERROR");
                    exception.printStackTrace();

                    localServerPort = 0;

                    openConnection.setEnabled(true);
                    closeConnection.setEnabled(false);
                } finally {
                    User.getInstance().updatePort(mac, localServerPort);
                    table.refreshTable();
                }
            }
        });

        closeConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the active connection
                TCPBridge forRemove = activeBridges.remove(selectedRow);
                forRemove.stopBridge();

                String mac = (String)table.getValueAt(selectedRow, 2);
                User.getInstance().updatePort(mac, 0);
                table.refreshTable();

                openConnection.setEnabled(true);
                closeConnection.setEnabled(false);
            }
        });

    }

    private void addTableSelectionListener(JTable table) {
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && e.getSource() == selectionModel && selectionModel.isSelectionEmpty() == false) {
                    selectedRow = (int)table.getValueAt(table.getSelectedRow(),0)-1;
                    System.out.println(selectedRow);
                    Object statusValue = table.getValueAt(selectedRow, 4);
                    Object connectionValue = table.getValueAt(selectedRow, 5);

                    if ("Online".equals(statusValue)) {
                        openConnection.setEnabled("none".equals(connectionValue));
                        closeConnection.setEnabled(!"none".equals(connectionValue));
                    } else {
                        openConnection.setEnabled(false);
                        closeConnection.setEnabled(false);
                    }

                }
            }
        });
    }

    private int generatePort(int reference){
        ArrayList<Integer> ports = new ArrayList<>(User.getInstance().getPorts());
        ports.removeIf(n -> n == 0);

        int possiblePort;

        if(reference != 0 && !ports.contains(reference)){
            possiblePort = reference;
        }
        else{
            if (ports.isEmpty()) {
                //Default
                return 50000;
            }
            possiblePort = Collections.max(ports);
        }

        if (possiblePort <= 65564) {
            return possiblePort + 1;
        } else {
            return Collections.min(ports) - 1;
        }
    }
}
