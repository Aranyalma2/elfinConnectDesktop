package Bridge;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BridgeCreator {

    int selectedRow = -1;

    JButton openConnection;
    JButton closeConnection;

    public BridgeCreator(JTable table, JButton open, JButton close){
        openConnection = open;
        closeConnection = close;
        addTableSelectionListener(table);

        openConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Generate a port and start connection to server
            }
        });

        closeConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the active connection

            }
        });

    }

    private void addTableSelectionListener(JTable table) {
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && e.getSource() == selectionModel && selectionModel.isSelectionEmpty() == false) {
                    selectedRow = table.getSelectedRow();
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
