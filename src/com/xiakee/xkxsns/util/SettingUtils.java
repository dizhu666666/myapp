package com.xiakee.xkxsns.util;

import com.xiakee.xkxsns.global.GlobalApplication;
import com.xiakee.xkxsns.global.PrefsConfig;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by William on 2015/11/11.
 */
public class SettingUtils {

    public static void isDownloadImage(boolean isDownload) {
        putBoolean(PrefsConfig.LOAD_IMAGE, isDownload);
    }

    public static boolean isDownloadImage() {
        return getBoolean(PrefsConfig.LOAD_IMAGE, true);
    }

    public static void isPushMe(boolean isPush) {
        putBoolean(PrefsConfig.PUSH_ME, isPush);
    }

    public static boolean isPushMe() {
        return getBoolean(PrefsConfig.PUSH_ME, true);
    }

    private static final String CONFIG_NAME = "config";

    private static boolean putBoolean(String key, boolean value) {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    private static boolean getBoolean(String key, boolean def) {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        boolean value = preferences.getBoolean(key, def);
        return value;
    }

    public static boolean clear() {
        SharedPreferences preferences = GlobalApplication.getContext()
                .getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        return preferences.edit().clear().commit();
    }
}
