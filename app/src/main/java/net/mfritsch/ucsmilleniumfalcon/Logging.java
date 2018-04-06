package net.mfritsch.ucsmilleniumfalcon;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logging {

    private static String LogString = "";

    public static void writeLog(String TAG, String ownLog) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");

        LogString = mdformat.format(calendar.getTime()) + " - " + ownLog + "\n" + LogString;
        Log.d(TAG, ownLog);
    }

    public static String readLog() {
        return LogString;
    }
}
