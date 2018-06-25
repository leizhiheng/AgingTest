package com.cwtcn.agingtest.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.cwtcn.agingtest.AgingApplication;

public class Preferences {
    private static SharedPreferences sPreferences;
    private Preferences(){}

    public static SharedPreferences getInstance() {
        if (sPreferences == null) {
            synchronized (Preferences.class) {
                if (sPreferences == null) {
                    sPreferences = AgingApplication.sContext.getSharedPreferences(Constants.SHARE_REPFERENCES_NAME, Context.MODE_PRIVATE);
                }
            }
        }
        return sPreferences;
    }

    public static int getInt(String key, int defaultValue) {
        return getInstance().getInt(key, defaultValue);
    }

    public static void setInt(String key, int value) {
        getInstance().edit().putInt(key, value).commit();
    }

    public static void setString(String key, String value) {
        getInstance().edit().putString(key, value).commit();
    }
    public static String getString(String key, String defaultValue) {
        return getInstance().getString(key, defaultValue);
    }

    public static void clear() {
        getInstance().edit().clear().commit();
    }
}
