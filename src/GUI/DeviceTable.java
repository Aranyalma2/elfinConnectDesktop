package GUI;

import Device.Device;
import User.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class DeviceTable extends JTable {

    ArrayList<Device> devices;
    ArrayList<Integer> ports;
    DefaultTableModel model;

    public DeviceTable(){
        String[] columnNames = {"No.", "Hostname", "MAC", "Last Seen", "Status", "Running Port"};

        model = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create JTable with the model
        this.setModel(model);
        // Disable user-driven column reordering
        this.getTableHeader().setReorderingAllowed(false);
        // Fill all available space to fill
        this.setFillsViewportHeight(true);

        // Set column widths, color, alignment
        int[] columnWidths = {30, 150, 150, 150, 75, 80};
        for (int i = 0; i < columnWidths.length; i++) {
            //width
            this.getColumnModel().getColumn(i).setMinWidth(columnWidths[i]);
            this.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);

            //color&alignment
            this.getColumnModel().getColumn(i).setCellRenderer(i!=4?new TableCellCenterRenderer():new TableCellColorRenderer(Color.green, Color.red));
        }

        refreshTable();

    }

    // Function to refresh the content of the table
    public void refreshTable() {
        // Clear existing rows
        model.setRowCount(0);

        devices = User.getInstance().getDevices();

        ports = User.getInstance().getPorts();

        int deviceNo = 0;

        // Populate the table with data from the updated hostList
        for (Device device : devices) {
            Object[] rowData = {deviceNo +1, device.getHostName(), device.getMac(),
                    device.getSeen(), device.getStatus() ? "Online" : "Offline", ports.get(deviceNo) != 0 ? ports.get(deviceNo) : "none"};
            deviceNo++;
            model.addRow(rowData);
        }

    }
}

// -----------COLOR RENDERER CLASS-----------
class TableCellColorRenderer extends DefaultTableCellRenderer {
    Color color1, color2;

    public TableCellColorRenderer(Color color1, Color color2) {
        super();
        this.color1 = color1;
        this.color2 = color2;
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (table.getModel().getValueAt(row, column).equals("Online"))
            cell.setBackground(color1);
        else
            cell.setBackground(color2);
        return cell;
    }
}

class TableCellCenterRenderer extends DefaultTableCellRenderer {

    public TableCellCenterRenderer() {
        super();
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }
}
