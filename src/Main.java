import SW.Log;
import SW.SWdata;
import GUI.MainFrame;
import GUI.ServerConnectionStatusPanel;
import User.User;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        //Create logger and print app & system properties.
        Log.createLogger();
        Log.logger.info("Start Elfin Connect " + SWdata.version);

        Properties properties = System.getProperties();

        Log.logger.info("Java: " + properties.getProperty("java.version") + " " + properties.getProperty("java.vendor"));
        Log.logger.info("Java VM Name: " + properties.getProperty("java.vm.name"));
        Log.logger.info("Java Class Version: " + properties.getProperty("java.class.version"));
        Log.logger.info("OS: " + properties.getProperty("os.name") + " " + properties.getProperty("os.arch"));

        //Create the User
        User.getInstance();

        //Create GUI
        MainFrame main = MainFrame.getInstance();
        main.createGUI();

        while (true) {
            //Refresh device table
            MainFrame.getInstance().deviceTable.refreshTable();
            //Update server status
            if (User.getInstance().getRemoteServerStatus()) {
                MainFrame.getInstance().serverPanel.setConnectionStatus(ServerConnectionStatusPanel.ConnectionStatus.CONNECTED);
            } else {
                MainFrame.getInstance().serverPanel.setConnectionStatus(ServerConnectionStatusPanel.ConnectionStatus.NOT_CONNECTED);
            }

            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                Log.logger.warning("Main sleep unexpectedly interrupted from outside, close application.");
                Log.logger.severe(e.getMessage());
                return;
            }

        }

    }
}
