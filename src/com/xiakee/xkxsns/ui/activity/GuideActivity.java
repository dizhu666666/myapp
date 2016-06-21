package com.xiakee.xkxsns.ui.activity;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.model.SplashViewPager;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * @{# GuideActivity.java Create on 2013-5-2 下午10:59:08
 * 
 * class desc: 引导界面
 * 
 * <p>
 * Copyright: Copyright(c) 2013
 * </p>
 * 
 * @Version 1.0
 * @Author <a href="mailto:me.weizh@live.com">Leo</a>
 * 
 * 
 */
public class GuideActivity extends Activity {

	private SplashViewPager mViewPager;

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.activity_guide, null);
		mViewPager = new SplashViewPager(this, view);
		setContentView(view);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 隐藏应用程序的标题栏，即当前activity的标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.guide);
		initViews();
	}

}
