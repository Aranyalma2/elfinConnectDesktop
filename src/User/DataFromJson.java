// DataFromJson.java
package User;

import SW.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Device.Device;

import java.util.ArrayList;

/**
 * The DataFromJson class provides methods to convert JSON data to Java objects or values.
 */
public class DataFromJson {

    /**
     * Converts a JSON string representing a list of devices into an ArrayList of Device objects.
     *
     * @param listString The JSON string representing the list of devices.
     * @return An ArrayList of Device objects.
     */
    public static ArrayList<Device> convertJsonToDevs(String listString) {

        ArrayList<Device> deviceArrayList = new ArrayList<>();

        try {
            // Convert the JSON string to a JSONArray
            JSONArray arrayJson = new JSONArray(listString);

            // Iterate through the JSONArray and create Device objects
            for (int i = 0; i < arrayJson.length(); i++) {
                deviceArrayList.add(new Device(arrayJson.getJSONObject(i)));
            }
        } catch (NullPointerException | JSONException exception) {
            // Log the error and return an empty list in case of JSON parsing error
            Log.logger.warning("Error in JSON parse, unable to process device json: ["+exception.getMessage()+"]");
            return new ArrayList<>();
        }

        return deviceArrayList;
    }

    /**
     * Converts a JSON string representing a status into a boolean.
     *
     * @param listString The JSON string representing the status.
     * @return A boolean indicating the status (success or failure).
     */
    public static boolean convertJsonToStatus(String listString) {
        try {
            // Convert the JSON string to a JSONObject
            JSONObject jsonObject = new JSONObject(listString);

            // Check if the status in the JSONObject is "success"
            return jsonObject.getString("status").equals("success");
        } catch (NullPointerException | JSONException exception) {
            // Log and return false in case of JSON parsing error
            Log.logger.warning("Error in JSON parse, unable to process status json: ["+exception.getMessage()+"]");
            return false;
        }
    }
}
