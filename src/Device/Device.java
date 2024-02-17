package Device;

import org.json.JSONException;
import org.json.JSONObject;
import SW.Log;

public class Device {
    private String hostName;
    private String mac;
    private String lastSeen;
    private boolean	status;

    /**
     * Constructor to create a Device object with specified attributes.
     *
     * @param _hostName The host name of the device.
     * @param _mac      The MAC address of the device.
     * @param _lastSeen The last seen date of the device.
     * @param _status   The status of the device (online or offline).
     */
    public Device(String _hostName, String _mac, String _lastSeen, boolean _status){
        hostName = _hostName;
        mac = _mac;
        lastSeen = _lastSeen;
        status = _status;

        Log.logger.fine("A Device object created: (" + mac + ")");
    }
    /**
     * Constructor to create a Device object from a JSONObject.
     *
     * @param jsonObject The JSONObject containing device information.
     * @throws JSONException If there is an issue parsing the JSON.
     */
    public Device(JSONObject jsonObject) throws JSONException {
        hostName = jsonObject.getString("hostname");
        mac = jsonObject.getString("macaddress");
        lastSeen = jsonObject.getString("lastseendate");
        status = jsonObject.getString("status").equals("online");

        Log.logger.fine("A Device object created: (" + mac + ")");

    }
    /**
     * Gets the host name of the device.
     *
     * @return The host name.
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Gets the MAC address of the device.
     *
     * @return The MAC address.
     */
    public String getMac() {
        return mac;
    }

    /**
     * Gets the last seen date of the device.
     *
     * @return The last seen date.
     */
    public String getSeen() {
        return lastSeen;
    }

    /**
     * Gets the status of the device.
     *
     * @return The status (true if online, false if offline).
     */
    public boolean getStatus() {
        return status;
    }
}
