package com.browser.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


public class BrowserPreferences {

    public static final String IS_ENABLE_DEVELOPER_MODE = "is_enable_developer_mode";
    public static final String IS_ENABLE_INTERNET_ERROR = "is_enable_internet_error";

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getSharedPreferenceObj(Context context){
        if (context != null && sharedPreferences == null )
            sharedPreferences = context.getSharedPreferences(context.getPackageName() , Context.MODE_PRIVATE);

        return sharedPreferences ;
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        if (getSharedPreferenceObj(context) != null) {
            return getSharedPreferenceObj(context).getBoolean(key, defaultValue);
        } else {
            return false;
        }
    }
    public static void setBoolean(Context context, String key, boolean value) {
        if (getSharedPreferenceObj(context) != null && !TextUtils.isEmpty(key)) {
            final SharedPreferences.Editor editor = getSharedPreferenceObj(context).edit();
            if (editor != null) {
                editor.putBoolean(key, value);
                editor.apply();
            }
        }
    }

    /**
     * Clear all preferences.
     */
    public static void clearPreferences(Context context) {
        final SharedPreferences.Editor editor = getSharedPreferenceObj(context).edit();
        editor.clear();
        editor.apply();
    }


    public static void setEnableDeveloperMode(Context context, Boolean isEnable) {
        setBoolean(context, IS_ENABLE_DEVELOPER_MODE, isEnable);
    }

    public static boolean isEnableDeveloperMode(Context context) {
        return getBoolean(context, IS_ENABLE_DEVELOPER_MODE, false);
    }

    public static void setEnableInternetErrorViewOnly(Context context, Boolean isEnable) {
        setBoolean(context, IS_ENABLE_INTERNET_ERROR, isEnable);
    }

    public static boolean isEnableInternetErrorViewOnly(Context context) {
        return getBoolean(context, IS_ENABLE_INTERNET_ERROR, false);
    }
}
