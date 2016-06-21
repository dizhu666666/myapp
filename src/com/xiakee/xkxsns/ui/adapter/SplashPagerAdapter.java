package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.android.util.DeviceInfo;
import com.android.util.ImageUtil;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.ui.activity.GuideActivity;
import com.xiakee.xkxsns.ui.activity.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * 
 * @{# ViewPagerAdapter.java Create on 2013-5-2 下午11:03:39
 * 
 * class desc: 引导页面适配器
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
public class SplashPagerAdapter extends BasePagerAdapter {

	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	// 界面列表
	private List<View> views;

	public SplashPagerAdapter(Context c, List<View> views) {
		super(c, views);
		this.views = views;
	}

	// 获得当前界面数
	@Override
	public int getCount() {
		return views != null ? views.size() : 0;
	}

	private void goHome() {
		// 跳转
		Intent intent = new Intent(mContext, MainActivity.class);
		mContext.startActivity(intent);
		// mContext.finish();
	}

	// 初始化arg1位置的界面
	@Override
	public Object instantiateItem(View parent, int position) {
		((ViewPager) parent).addView(views.get(position), 0);
		TypedArray drawable = mContext.getResources().obtainTypedArray(R.array.splash);
		final int res_id = drawable.getResourceId(position, 0);
		final int width = DeviceInfo.scrrenWidth(mContext);
		final int hight = DeviceInfo.scrrenHeight(mContext);

		Bitmap bm = ImageUtil.compressedBitmap(mContext, res_id, width, hight);
		if (position < views.size() - 1) {
			final ImageView view = (ImageView) views.get(position);
			view.setImageBitmap(bm);
			view.setScaleType(ImageView.ScaleType.FIT_XY);
			drawable.recycle();
		}

		// ((ViewPager) parent).addView(views.get(position), 0);
		// final ImageView view = (ImageView) views.get(position);
		// view.setImageBitmap(mImgloader.getResizedBitmapFromLocal(mUrls[position].replaceAll(ImageLoader.regularEx,
		// "")));

		else {
			final View view = views.get(position);
			final ImageView bgView = (ImageView) view.findViewById(R.id.last_guider_pic);
			bgView.setImageBitmap(bm);
			bgView.setScaleType(ImageView.ScaleType.FIT_XY);
			final ImageView enter_home_btn = (ImageView) view.findViewById(R.id.enter_homepage);

			enter_home_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					setGuided();
					goHome();// todo
					((GuideActivity) mContext).finish();

				}
			});
			// ImageView mStartWeiboImageButton = (ImageView) parent
			// .findViewById(R.id.start_weibo);
			// mStartWeiboImageButton.setOnClickListener(new OnClickListener() {

			// @Override
			// public void onClick(View v) {
			// // 设置已经引导
			// setGuided();
			// goHome();// todo
			//
			// }
			//
			// });
		}
		return views.get(position);
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	/**
	 * 
	 * method desc：设置已经引导过了，下次启动不用再次引导
	 */
	private void setGuided() {
		SharedPreferences preferences = mContext.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		// 存入数据
		editor.putBoolean("isFirstIn", false);
		// 提交修改
		editor.commit();
	}

	@Override
	public void startUpdate(View arg0) {
	}

}
