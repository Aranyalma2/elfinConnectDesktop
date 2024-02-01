package User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Device.Device;

import java.util.ArrayList;

public class DeviceFromJson {
    public static ArrayList<Device> convert(String listString){

        ArrayList<Device> deviceArrayList = new ArrayList<>();

        try {
            JSONArray arrayJson = new JSONArray(listString);
            for(int i = 0; i < arrayJson.length(); i++){
                deviceArrayList.add(new Device(arrayJson.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deviceArrayList;
    }
}
