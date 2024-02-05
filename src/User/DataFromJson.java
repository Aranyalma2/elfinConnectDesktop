package User;

import org.json.JSONArray;
import org.json.JSONException;

import Device.Device;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataFromJson {
    public static ArrayList<Device> convertJsonToDevs(String listString){

        ArrayList<Device> deviceArrayList = new ArrayList<>();

        try {
            JSONArray arrayJson = new JSONArray(listString);
            for(int i = 0; i < arrayJson.length(); i++){
                deviceArrayList.add(new Device(arrayJson.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<Device>();
        }

        return deviceArrayList;
    }

    public static boolean convertJsonToStatus(String listString){
        try {
            System.out.println(listString);
            JSONObject jsonObject = new JSONObject(listString);
            return jsonObject.getString("status").equals("success");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
