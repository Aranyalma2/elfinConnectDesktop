package GUI;

import Device.Device;
import User.User;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class DevicePanel extends AbstractTableModel {

    ArrayList<Device> devices = User.getInstance().getDevices();

    @Override
    public int getRowCount() {
        return devices.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int r, int c) {
        Device dev = devices.get(r);
        return switch (c) {
            case 0 -> r + 1;
            case 1 -> dev.getHostName();
            case 2 -> dev.getMac();
            case 3 -> dev.getSeen();
            default -> dev.getStatus() ? "Online" : "Offline";
        };
    }

    @Override
    public String getColumnName(int c) {
        return switch (c) {
            case 0 -> "No.";
            case 1 -> "Hostname";
            case 2 -> "MAC";
            case 3 -> "Last seen";
            default -> "Status";
        };
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (c == 0)
            return Integer.class;
        return String.class;
    }

    /*
     * @Override public void setValueAt(Object obj, int r, int c) { elfinIoT iot =
     * iots.get(r); if(c>=2 && c<=3) { switch(c) { case 2:
     * student.setSignature((Boolean)obj); break; default:
     * student.setGrade((Integer)obj); break; } students.set(r, student);
     * this.fireTableRowsUpdated(r, r); } }
     */
    @Override
    public boolean isCellEditable(int r, int c) {
        /*
         * if(c < getColumnCount() && c >= 2) { return true; }
         */
        return false;
    }

    public void updateTable() {
        devices = User.getInstance().getDevices();
        fireTableDataChanged();
    }
}

// -----------COLOR RENDERER CLASS-----------
class tableColorRenderer extends DefaultTableCellRenderer {
    Color color1, color2;

    public tableColorRenderer(Color color1, Color color2) {
        super();
        this.color1 = color1;
        this.color2 = color2;
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
