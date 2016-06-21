package com.xiakee.xkxsns.util;

import android.util.Log;

public class LogUtils {
    private static String defTag = LogUtils.class.getSimpleName();
    public static boolean debug = true;//是否处于调试阶段，上线后改为false

    public static void e(String msg) {
        if (debug) {
            Log.e(defTag, msg);
        }
    }
}
