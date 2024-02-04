import Device.Device;
import GUI.MainFrame;
import GUI.ServerConnectionStatusPanel;
import User.User;

public class Main {
    public static void main(String[] args) {

        User.getInstance();

        MainFrame main = MainFrame.getInstance();
        main.createGUI();

        while(true){

            MainFrame.getInstance().deviceTable.refreshTable();
            if(User.getInstance().getRemoteServerStatus()){
                MainFrame.getInstance().serverPanel.setConnectionStatus(ServerConnectionStatusPanel.ConnectionStatus.CONNECTED);
            }
            else{
                MainFrame.getInstance().serverPanel.setConnectionStatus(ServerConnectionStatusPanel.ConnectionStatus.NOT_CONNECTED);
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }
}
