package com.xiakee.xkxsns.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.ui.adapter.SplashPagerAdapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class SplashViewPager implements OnPageChangeListener {

	private ImageView imageView = null;
	private List<View> mAdvPics;
	private Context mContext;
	private AtomicInteger mCurrentIndex = new AtomicInteger(0);
	private ImageView[] mImgdots = null;
	private ViewPager mViewPager = null;

	public SplashViewPager(Context context, final View view) {
		super();
		mContext = context;
		initViewPager(view);
	}

	private void initViewPager(final View viewParent) {
		mViewPager = (ViewPager) viewParent.findViewById(R.id.view_pager_adv);
		ViewGroup group = (ViewGroup) viewParent.findViewById(R.id.viewGroup);
		// 这里存放的是四张广告背景
		// WindowManager wm = (WindowManager) mContext
		// .getSystemService(Context.WINDOW_SERVICE);
		// int height = wm.getDefaultDisplay().getHeight();//屏幕高度
		// LayoutParams LayoutParams = mViewPager.getLayoutParams();
		// LayoutParams.height = height;
		// mViewPager.setLayoutParams(LayoutParams);
		TypedArray drawable = mContext.getResources().obtainTypedArray(	R.array.splash);
		mAdvPics = new ArrayList<View>();

		for (int i = 0; i < drawable.length(); ++i) {
			if (i == drawable.length() - 1) {
				LayoutInflater inflater = LayoutInflater.from(mContext);
				final View view = inflater.inflate(R.layout.guider_last, null);
				mAdvPics.add(view);
				// return;
			} else {
				ImageView imgView = new ImageView(mContext);
				mAdvPics.add(imgView);
			}
		}
		drawable.recycle();

		// 对imageviews进行填充
		mImgdots = new ImageView[mAdvPics.size()];
		// 小图标
		for (int i = 0; i < mAdvPics.size(); i++) {
			imageView = new ImageView(mContext);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					25, 25);
			layoutParams.setMargins(5, 0, 5, 0);
			imageView.setLayoutParams(layoutParams);
			mImgdots[i] = imageView;
			if (i == 0) {
				mImgdots[i].setBackgroundResource(R.drawable.dot_selected);
			} else {
				mImgdots[i].setBackgroundResource(R.drawable.dot_unselected);
			}
			group.addView(mImgdots[i]);
		}
		mViewPager.setAdapter(new SplashPagerAdapter(mContext, mAdvPics));
		mViewPager.addOnPageChangeListener(this);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		mCurrentIndex.getAndSet(position);
		setSelected(position);
	}

	private void setSelected(int position) {
		for (int i = 0; i < mImgdots.length; i++) {
			mImgdots[position].setBackgroundResource(R.drawable.dot_selected);
			if (position != i) {
				mImgdots[i].setBackgroundResource(R.drawable.dot_unselected);
			}
		}
	}

}
