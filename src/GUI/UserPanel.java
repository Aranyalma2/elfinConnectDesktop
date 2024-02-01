package GUI;

import User.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class UserPanel extends JPanel {
    JTextField uuidTEXT;
    JTextField serverTEXT;

    public UserPanel(){
        //USER SECTION

        //--LEFT--
        JPanel input = new JPanel();
        input.setLayout(new BoxLayout(input, BoxLayout.Y_AXIS));

        //UUID
        JPanel uuidPanel = new JPanel(new FlowLayout());
        uuidPanel.add(new JLabel("UUID:   "));
        uuidTEXT = (JTextField) uuidPanel.add(new JTextField(23));
        input.add(uuidPanel);

        //SERVER
        JPanel serverPanel = new JPanel(new FlowLayout());
        serverPanel.add(new JLabel("Server:"));
        serverTEXT = (JTextField) serverPanel.add(new JTextField(23));
        input.add(serverPanel);

        update();

        this.add(input);

        //--RIGHT--
        JButton apply = new JButton("<html><center>Apply<br>and<br>Connect</center></html>");
        apply.setPreferredSize(new Dimension(100, 75));
        apply.addActionListener(new ApplyButton());
        this.add(apply);

        //BORDER jPanel
        this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "User and Server"));

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
    }

    private class ApplyButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                User.updateUser(uuidTEXT.getText(), serverTEXT.getText());

            }catch(IllegalArgumentException ie) {
                //INVALID SERVER ADDRESS INPUT
                addressErrorDialog(ie.getMessage());
            } catch (IOException ex) {
                saveUserErrorDialog(ex.getMessage());
            }
        }
    }

    public void addressErrorDialog(String tried) {
        String message = "Invalid server address format: " + tried + "\nFormat: ip/domain:port\nExample: 192.0.2.1:7218 or example.com:7218";
        JOptionPane.showMessageDialog(MainFrame.getInstance().frame, message, "Invalid address", JOptionPane.ERROR_MESSAGE);

    }

    public void saveUserErrorDialog(String tried) {
        String message = "Unable to save user changes, user still usable in this session";
        JOptionPane.showMessageDialog(MainFrame.getInstance().frame, message, "Unable to save user", JOptionPane.WARNING_MESSAGE);

    }

    private void update() {
        uuidTEXT.setText(User.getUUID());
        serverTEXT.setText(User.getAddress());
    }

}
