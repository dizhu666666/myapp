package com.xiakee.xkxsns.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.UserManager;

/**
 * 网络连接状态变化监听
 * Created by William on 2015/12/14.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e("网络变化");
        UserManager.checkLogin(context);
    }
}
