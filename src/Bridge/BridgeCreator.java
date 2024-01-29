package Bridge;

import GUI.DevicePanel;
import User.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

public class BridgeCreator {

    JTable table;

    DevicePanel tablePanel;
    int selectedRow = -1;

    JButton openConnection;
    JButton closeConnection;

    HashMap<Integer, TCPBridge> activeBridges = new HashMap<Integer, TCPBridge>();

    public BridgeCreator(JTable table, DevicePanel devicePanel, JButton open, JButton close){
        tablePanel = devicePanel;
        openConnection = open;
        closeConnection = close;
        addTableSelectionListener(table);

        openConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Generate a port and start connection to server
                int localServerPort = 12345;
                String mac = "testMac";
                try {

                    table.setValueAt(localServerPort, selectedRow, 5); //NOT WORKS

                    table.getModel().setValueAt("y", 1, 1);

                    tablePanel.setValueAt("x", 2, 2);

                    tablePanel.updateTable();

                    activeBridges.put(selectedRow, new TCPBridge(localServerPort, User.remoteServerIp, User.remoteServerPort, User.getUUID(), mac));
                    openConnection.setEnabled(false);
                    closeConnection.setEnabled(true);
                }catch (IllegalThreadStateException exception){
                    System.out.println("Open bridge ERROR");
                    exception.printStackTrace();
                    openConnection.setEnabled(true);
                    closeConnection.setEnabled(false);
                }
            }
        });

        closeConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the active connection
                TCPBridge forRemove = activeBridges.remove(selectedRow);
                forRemove.stopBridge();
                table.setValueAt("none", selectedRow, 5);
                openConnection.setEnabled(true);
                closeConnection.setEnabled(false);
            }
        });

    }

    private void addTableSelectionListener(JTable _table) {
        table = _table;
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
}
