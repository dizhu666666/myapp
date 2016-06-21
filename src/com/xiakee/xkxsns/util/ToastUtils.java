package com.xiakee.xkxsns.util;

import com.xiakee.xkxsns.global.GlobalApplication;

import android.view.Gravity;
import android.widget.Toast;


public class ToastUtils {
    private static Toast toast;

    public static void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(GlobalApplication.getContext(), msg,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void showToastNoCache(String msg, int duration) {
        Toast toast = Toast.makeText(GlobalApplication.getContext(), msg,
                duration);
        toast.show();
    }

    private static Toast mCenterToast;

    public static void showCenterToast(String msg) {
        if (toast == null) {
            mCenterToast = Toast.makeText(GlobalApplication.getContext(), msg,
                    Toast.LENGTH_SHORT);
            mCenterToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mCenterToast.setText(msg);
        }
        mCenterToast.show();
    }
}
