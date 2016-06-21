package com.xiakee.xkxsns.receiver;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.xiakee.xkxsns.ui.activity.MainActivity;
import com.xiakee.xkxsns.ui.activity.SplashActivity;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p/>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

            //获取ActivityManager
            ActivityManager mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            //获得当前运行的task
            List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo rti : taskList) {
                //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
                if (rti.topActivity.getPackageName().equals(context.getPackageName())) {
                    try {
                        Intent resultIntent = new Intent(context, Class.forName(rti.topActivity.getClassName()));
                        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(resultIntent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
            //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
            Intent resultIntent = new Intent(context, SplashActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(resultIntent);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    private NotificationManager mManager;

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        if (mManager == null) {
            mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);


        Intent msgIntent = new Intent(context, SplashActivity.class);
        msgIntent.setAction(MainActivity.MESSAGE_RECEIVED_ACTION);

        //msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
        //msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
        context.sendBroadcast(msgIntent);

//        if (SettingUtils.isPushMe()) {
//            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
//                    msgIntent, 0);
//
//            Notification notification = new Notification.Builder(context)
//                    .setContentTitle("这是一个标题")
//                    .setContentText("这是文本")
//                    .setLargeIcon(
//                            BitmapFactory.decodeResource(context.getResources(),
//                                    R.drawable.icon))
//                    .setContentIntent(contentIntent)
//                    .getNotification();
//
//            mManager.notify(0, notification);
//        }

    }
}
