package GUI;

import Device.Device;
import SW.Log;
import User.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;


/**
 * The DeviceTable class extends JTable and represents a table displaying information about user's devices.
 * It includes functionality for refreshing the table with updated device data.
 * Implements custom cell renderers.
 */
public class DeviceTable extends JTable {

    // List of devices
    ArrayList<Device> devices;
    // List of ports
    ArrayList<Integer> ports;
    // Table model
    DefaultTableModel model;

    /**
     * Constructor for the DeviceTable class.
     * Sets up the table with specified column names, properties, and rendering.
     */
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

        Log.logger.fine("Table object created");

        refreshTable();

    }

    /**
     * Function to refresh the content of the table with updated device data.
     */
    public void refreshTable() {
        Log.logger.fine("Refreshing device table...");
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

        Log.logger.fine("Device table refresh successfully.");

    }
}

// -----------COLOR RENDERER CLASS-----------
/**
 * TableCellColorRenderer is a custom cell renderer for coloring cells based on device status.
 */
class TableCellColorRenderer extends DefaultTableCellRenderer {
    Color color1, color2;

    /**
     * Constructor for TableCellColorRenderer.
     *
     * @param color1 The color for "Online" status.
     * @param color2 The color for "Offline" status.
     */
    public TableCellColorRenderer(Color color1, Color color2) {
        super();
        this.color1 = color1;
        this.color2 = color2;
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * {@inheritDoc}
     */
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

/**
 * TableCellCenterRenderer is a custom cell renderer for centering cell content.
 */
class TableCellCenterRenderer extends DefaultTableCellRenderer {

    /**
     * Constructor for TableCellCenterRenderer.
     */
    public TableCellCenterRenderer() {
        super();
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }
}