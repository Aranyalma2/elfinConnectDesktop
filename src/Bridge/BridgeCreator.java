package Bridge;

import GUI.DeviceTable;
import User.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
                int localServerPort = 12345;
                int selected = selectedRow;
                try {

                    String mac = (String)table.getValueAt(selected, 2);

                    activeBridges.put(selected, new TCPBridge(localServerPort, User.remoteServerIp, User.remoteServerPort, User.getUUID(), mac));
                    table.setValueAt(localServerPort, selected, 5);
                    openConnection.setEnabled(false);
                    closeConnection.setEnabled(true);
                }catch (IllegalThreadStateException exception){
                    System.out.println("Open bridge ERROR");
                    exception.printStackTrace();

                    table.setValueAt("none", selected, 5);
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
}
