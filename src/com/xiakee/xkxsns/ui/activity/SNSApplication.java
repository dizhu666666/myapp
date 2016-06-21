package com.xiakee.xkxsns.ui.activity;

import org.xutils.x;

import android.app.Application;

public class SNSApplication extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		x.Ext.init(this);
	}

}
