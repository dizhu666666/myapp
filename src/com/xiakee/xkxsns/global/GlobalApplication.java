package com.xiakee.xkxsns.global;

import org.xutils.x;

import com.android.photo.util.Res;
import com.baidu.location.LocationClient;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

public class GlobalApplication extends Application {

	private static Handler mHandler;
	private static Context mContext;

	/* baidu map location service */

	public LocationClient mLocationClient;

	public String tempcoor = "gcj02";// 国家测绘局标准
	// tempcoor="bd09ll";//百度经纬度标准
	// tempcoor="bd09";//百度墨卡托标准
	// tempMode = LocationMode.Hight_Accuracy;
	// tempMode = LocationMode.Battery_Saving;
	// tempMode = LocationMode.Device_Sensors;

	public TextView mLocationResult, logMsg;
	public TextView trigger, exit;
	public Vibrator mVibrator;

	private String mStrAddr;
	// private LocationMode tempMode = LocationMode.Hight_Accuracy;
	// private String tempcoor = "gcj02";
	private Double lo;
	private Double la;
	private String city;
	private String county;

	@Override
	public void onCreate() {
		super.onCreate();
		mHandler = new Handler();
		mContext = this;
		x.Ext.init(this);

		mLocationClient = new LocationClient(this.getApplicationContext());
		// mMyLocationListener = new MyLocationListener();
		// mLocationClient.registerLocationListener(mMyLocationListener);
		mVibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		Res.init(mContext);
		initPush();
		addWXPlatform();
	}

	private void initPush() {
		JPushInterface.setDebugMode(false); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
	}

	public static Handler getHandler() {
		return mHandler;
	}

	public static Context getContext() {
		return mContext;
	}

	/**
	 * @return
	 * @功能描述 : 添加微信平台分享
	 */
	private void addWXPlatform() {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wxd5a78f99af7eef5b";
		String appSecret = "118affc8dec6d60007980e0ba67824d3";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this, appId, appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}
}
