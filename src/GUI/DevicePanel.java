package GUI;

import Device.Device;
import User.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class DevicePanel extends AbstractTableModel{
    ArrayList<Device> devices = User.getInstance().getDevices();
    ArrayList<String> ports = new ArrayList<>();

    public DevicePanel(){
        super();
        for(int i = 0; i < devices.size(); i++){
            ports.add(i, "none");
        }
    }

    @Override
    public int getRowCount() {
        return devices.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int r, int c) {
        Device dev = devices.get(r);
        switch (c) {
            case 0:
                return r + 1;
            case 1:
                return dev.getHostName();
            case 2:
                return dev.getMac();
            case 3:
                return dev.getSeen();
            case 4:
                return dev.getStatus() ? "Online" : "Offline";
            case 5:
                return ports.get(r);
            default:
                return "none";
        }
    }

    @Override
    public String getColumnName(int c) {
        return switch (c) {
            case 0 -> "No.";
            case 1 -> "Hostname";
            case 2 -> "MAC";
            case 3 -> "Last seen";
            case 4 -> "Status";
            case 5 -> "Running port";
            default -> "None";
        };
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (c == 0)
            return Integer.class;
        return String.class;
    }

    public void updateTable() {
        devices = User.getInstance().getDevices();
        setValueAt("Alma", 3,3);
        fireTableDataChanged();
    }
}

// -----------COLOR RENDERER CLASS-----------
class TableColorRenderer extends DefaultTableCellRenderer {
    Color color1, color2;

    public TableColorRenderer(Color color1, Color color2) {
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

class TableCenterRenderer extends DefaultTableCellRenderer {

    public TableCenterRenderer() {
        super();
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }
}


