package Device;

import org.json.JSONException;
import org.json.JSONObject;

public class Device {
    private String hostName;
    private String mac;
    private String lastSeen;
    private boolean	status;

    public Device(String _hostName, String _mac, String _lastSeen, boolean _status){
        hostName = _hostName;
        mac = _mac;
        lastSeen = _lastSeen;
        status = _status;
    }

    public Device(JSONObject jsonObject) throws JSONException {
            hostName = jsonObject.getString("host");
            mac = jsonObject.getString("macaddress");
            lastSeen = jsonObject.getString("lastseen");
            if (jsonObject.getString("status").equals("online")) {
                status = true;
            } else {
                status = false;
            }
    }

    public String getHostName() {
        return hostName;
    }
    public String getMac() {
        return mac;
    }
    public String getSeen() {
        return lastSeen;
    }
    public boolean getStatus() {
        return status;
    }
}
