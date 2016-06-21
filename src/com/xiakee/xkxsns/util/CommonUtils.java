package com.xiakee.xkxsns.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.global.GlobalApplication;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

public class CommonUtils {

    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    public static String MD5Encoding(String s) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(s.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                String c = Integer.toHexString(0xff & encryption[i]);
                if (c.length() == 1) {
                    strBuf.append("0");
                }
                strBuf.append(c);
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /***
     * 手机号码验证
     *
     * @param number
     * @return
     */
    public static boolean mobileNumberCheck(String number) {
        return Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$").matcher(number).matches();
    }

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    public static String getImgPathByUri(Context context, Uri selectedImage) {
        String imgPath = null;

        String[] filePathColumn = {MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        String st8 = context.getResources().getString(R.string.cant_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(context, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return null;
            }
            imgPath = picturePath;
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(context, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return null;

            }
            imgPath = file.getAbsolutePath();
        }

        return imgPath;

    }

    /***
     * 此方法将会删除整个文件
     *
     * @param rootFile
     */
    public static void cleanFile(File rootFile) {
        if (null != rootFile) {
            LinkedList<File> fileList = new LinkedList<File>();
            fileList.addFirst(rootFile);
            while (!fileList.isEmpty()) {
                File file = fileList.removeFirst();
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File f : files) {
                        if (f.isDirectory()) {
                            fileList.addFirst(f);
                        } else {
                            f.delete();
                        }
                    }
                } else {
                    file.delete();
                }
            }
        }
    }


    /***
     * 使代码在主线程中执行
     *
     * @param r
     */
    public static void runOnUIThread(Runnable r) {
        GlobalApplication.getHandler().post(r);
    }

    /***
     * 从父view中移除自身
     *
     * @param child
     */
    public static void removeSelfFromParent(View child) {
        if (child == null) {
            return;
        }
        ViewParent parent = child.getParent();
        if (parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            group.removeView(child);
        }
    }

    /***
     * 字节流转换为字符串
     *
     * @param inputStream
     * @return
     */
    public static String stream2String(InputStream inputStream) {
        InputStream is = inputStream;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int len = 0;
        byte[] bys = new byte[1024];

        try {
            while ((len = is.read(bys)) != -1) {
                baos.write(bys, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = baos.toString();
        try {
            baos.close();
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;

    }

    /***
     * 由于一些SIM卡的限制，获取到的手机号可能为空
     *
     * @return
     */
    public static String getPhoneNumber() {
        TelephonyManager manager = (TelephonyManager) GlobalApplication
                .getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = manager.getLine1Number();
        return phoneNumber;
    }

    /****
     * 获取设备唯一ID
     *
     * @return
     */
    public static String getDeviceId() {
        TelephonyManager manager = (TelephonyManager) GlobalApplication
                .getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = manager.getDeviceId();
        return deviceId;
    }

    /***
     * 通过进程ID获取appName
     *
     * @param context
     * @param pID
     * @return
     */
    public static String getAppName(Context context, int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningAppProcesses = am
                .getRunningAppProcesses();
        Iterator<RunningAppProcessInfo> iterator = runningAppProcesses
                .iterator();
        while (iterator.hasNext()) {
            RunningAppProcessInfo info = iterator.next();
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
