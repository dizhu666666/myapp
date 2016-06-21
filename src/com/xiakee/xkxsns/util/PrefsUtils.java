package com.xiakee.xkxsns.util;

import com.xiakee.xkxsns.global.GlobalApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefsUtils {
    private static final String CONFIG_NAME = "userInfo";

    public static boolean putInt(String key, int value) {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static int getInt(String key, int def) {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        int value = preferences.getInt(key, def);
        return value;
    }

    public static boolean putString(String key, String value) {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getString(String key, String def) {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        String value = preferences.getString(key, def);
        return value;
    }

    public static boolean putBoolean(String key, boolean value) {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean getBoolean(String key, boolean def) {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        boolean value = preferences.getBoolean(key, def);
        return value;
    }

    public static boolean putFloat(String key, float value) {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public static float getFloat(String key, float def) {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        float value = preferences.getFloat(key, def);
        return value;
    }

    public static boolean putLong(String key, long value) {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static long getLong(String key, long def) {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        long value = preferences.getLong(key, def);
        return value;
    }

    public static boolean clear() {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        return preferences.edit().clear().commit();
    }
}
