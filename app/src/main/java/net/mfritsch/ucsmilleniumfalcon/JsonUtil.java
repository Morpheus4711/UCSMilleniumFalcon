package net.mfritsch.ucsmilleniumfalcon;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {
    private static final String TAG = JsonUtil.class.getSimpleName();

    public static String toJSon(Falcon falcon) {
        try {
            // Here we convert Java Object to JSON
            JSONObject jsonObj = new JSONObject();

            jsonObj.put("SublightDrivers", falcon.getSublightDrivers()); // Set the first pair
            //jsonObj.put("Weapons", jsonObj);

            JSONObject jsonWeapon = new JSONObject(); // we need another object to store the weapons
            jsonWeapon.put("Upper", falcon.getWeaponsUp());
            jsonWeapon.put("Lower", falcon.getWeaponsLow());
            // We add the object to the main object
            jsonObj.put("Weapons", jsonWeapon);

            jsonObj.put("ForwardFloodLights", falcon.getForwardFloodights());

            JSONObject jsonCockpit = new JSONObject(); // we need another object to store the cockpit
            jsonCockpit.put("Fitting", falcon.getCockpitFittings());
            jsonCockpit.put("Lights", falcon.getCockpitLights());
            //We add the object to the main object
            jsonObj.put("Cockpit", jsonCockpit);

            jsonObj.put("MainHold", falcon.getMainHold()); // Set the pair
            jsonObj.put("BoardingRamp", falcon.getBoardingRamp()); // Set the pair

            // and finally we add the phone number
            // In this case we need a json array to hold the java list

            JSONObject jsonLight = new JSONObject(); // we need another object to store the light
            jsonLight.put("LandingGear", falcon.getLandingGear()); // Set the first pair
            jsonLight.put("LandingLight", falcon.getLandingLight()); // Set the second name/pair
            jsonObj.put("Light", jsonLight);

            Logging.writeLog(TAG, "JSON String - " + jsonObj.toString());
            return jsonObj.toString() + "\n";

        } catch (JSONException e) {
            e.printStackTrace();
            Log.v(TAG, "JSON Exception - " + e);
        }
        return null;
    }
}
