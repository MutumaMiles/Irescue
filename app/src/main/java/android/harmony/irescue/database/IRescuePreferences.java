package android.harmony.irescue.database;

import android.content.Context;
import android.preference.PreferenceManager;

public class IRescuePreferences {
    private static final String DEVICE_TOKEN="DEVICE_TOKEN";
    private static final String PRESS_TIME="PRESS_TIME";

    public static void setPressTime(Context context,long time){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(PRESS_TIME,time)
                .apply();
    }
    public static long getPressTime(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(PRESS_TIME,0);
    }
    public static void setDeviceToken(Context context,String deviceId){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(DEVICE_TOKEN,deviceId)
                .apply();
    }

    public static String getDeviceToken(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(DEVICE_TOKEN,null);

    }
}
