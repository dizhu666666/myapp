package com.xiakee.xkxsns.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.android.util.DeviceInfo;
import com.android.util.SNSAPI;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.Banner;
import com.xiakee.xkxsns.ui.adapter.BannerPagerAdapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SNSViewPager extends IonRequest implements OnTouchListener, OnPageChangeListener {

	//TODO 待优化，在后台动态改变banner页书目的时候会有问题
	private ViewPager advPager = null;
	private boolean isPullRefresh = false;
	private Thread mThread;

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// advPager.setCurrentItem(msg.what);
			if (0 == msg.what) {
				mIndex.incrementAndGet();
				advPager.setCurrentItem(mIndex.get(), true);
			}
			super.handleMessage(msg);
		}
	};
	// private ImageView imageView = null;
	private boolean isContinue = true;

	// private Context mContext;
	private TextView[] mTextViews;
	private ImageView[] mDotViews = null;
	private String[] mTextStrings = null;
	private List<View> mViewList;
	// private ImageLoader mImgLoader;
	private AtomicInteger mIndex = new AtomicInteger(0);

	// private FixedSpeedScroller mScroller = null;
	private View mParent;
	private BannerPagerAdapter mAdapter;

	private static volatile SNSViewPager mSelf;

	public static SNSViewPager Instance(Context context, View view) {
		if (mSelf == null) {
			synchronized (SNSViewPager.class) {
				if (mSelf == null) {
					mSelf = new SNSViewPager(context, view);
				}
			}
		}
		return mSelf;
	}

	public SNSViewPager(Context context, View view) {
		super(context);
		mParent = view;
		initViewPager(view, null);
		getRemoteBanneImgs();
	}

	public void pullRefresh() {
		isPullRefresh = true;
		getRemoteBanneImgs();
	}

	/*
	 * private void controlViewPagerSpeed(ViewPager vp) { try { Field mField;
	 * mField = ViewPager.class.getDeclaredField("mScroller");
	 * mField.setAccessible(true); mScroller = new FixedSpeedScroller(mContext,
	 * new AccelerateInterpolator()); mScroller.setmDuration(1500); // 2000ms
	 * mField.set(vp, mScroller); } catch (Exception e) { e.printStackTrace(); }
	 * }
	 */
	@Override
	protected void handleError(String errmsg) {
		handleError(errmsg, false);
	}

	@Override
	protected void handleResult(JsonObject result) {
		Gson gson = new Gson();
		banners banners = new banners();
		final String bannersArray = result.toString();
		banners = gson.fromJson(bannersArray, banners.class);
		if (isPullRefresh) {
			updateValue(banners.getBanners());
			isPullRefresh = false;
		} else {
			refresh(banners.getBanners());
		}
	}

	// @Override
	// public void onImageLoader(Bitmap bitmap, String url) {
	// // TODO Auto-generated method stub
	// int positon = mUrls.indexOf(url);
	// if (null != bitmap)
	// ((ImageView) mImgList.get(positon)).setImageBitmap(bitmap);
	// else
	// ((ImageView) mImgList.get(positon))
	// .setBackgroundResource(R.drawable.app_icon);
	//
	// }

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
		mIndex.getAndSet(position);
		setSelectedBg(position % mViewList.size());
		// todo
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		handler.removeMessages(0);
		return false;
		// TODO Auto-generated method stub
	}

	private void setSelectedBg(int relPosition) {
		for (int i = 0; i < mDotViews.length; i++) {
			mDotViews[relPosition].setBackgroundResource(R.drawable.dot_selected);
			mTextViews[0].setText(mTextStrings[relPosition]);
			if (relPosition != i) {
				mDotViews[i].setBackgroundResource(R.drawable.dot_unselected);
			}
		}
	}

	private void getRemoteBanneImgs() {
		httpGet(SNSAPI.getBannerListUrl());
	}

	private void updateValue(Banner[] banners) {
		if (mDotViews == null) {
			refresh(banners);
			return;
		}
		if (null != banners) {
			// 小图标
			mTextStrings = new String[banners.length];
			for (int i = 0; i < banners.length; i++) {

				mTextStrings[i] = banners[i].getBbsDesc();
				// ViewGroup group = (ViewGroup)
				// mParent.findViewById(R.id.viewGroup);
				// ViewGroup textGroup = (ViewGroup)
				// mParent.findViewById(R.id.textGroup);
				// textGroup.addView(text);
			}

			mAdapter.updateData(mViewList, banners);
		}
	}

	private void refresh(Banner[] banners) {
		if (null != banners) {
			/*
			 * for (int i = 0; i < banners.length; i++) { final ImageView
			 * imgView = new ImageView(mContext);
			 * imgView.setScaleType(ImageView.ScaleType.FIT_XY);
			 * mViewList.add(imgView); }
			 */
			mDotViews = new ImageView[banners.length];
			mTextStrings = new String[banners.length];
			mTextViews = new TextView[banners.length];
			// mViewList.clear();
			// 小图标
			for (int i = 0; i < banners.length; i++) {
				final ImageView imgView = new ImageView(mContext);
				imgView.setScaleType(ImageView.ScaleType.FIT_XY);
				imgView.setTag(i);
				mViewList.add(imgView);
				imgView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Toast.makeText(mContext, "第" + v.getTag().toString() + "张图", 1000).show();
					}
				});
				/***************************************/

				ImageView imageView = new ImageView(mContext);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(15, 15);
				layoutParams.setMargins(5, 0, 5, 0);
				imageView.setLayoutParams(layoutParams);
				mDotViews[i] = imageView;

				/***************************************/

				TextView text = new TextView(mContext);
				text.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				text.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
				text.setMaxLines(1);
				text.setMaxEms(40);
				text.setTextSize(mContext.getResources().getDimension(R.dimen.textsize_content_smallestt));
				text.setTextColor(mContext.getResources().getColor(R.color.unselected_tab));
				mTextViews[i] = text;
				/************************************************/

				if (i == 0) {
					mDotViews[i].setBackgroundResource(R.drawable.dot_selected);
					mTextViews[i].setText(banners[i].getBbsDesc());
				} else {
					mDotViews[i].setBackgroundResource(R.drawable.dot_unselected);
				}
				mTextStrings[i] = banners[i].getBbsDesc();
				ViewGroup group = (ViewGroup) mParent.findViewById(R.id.viewGroup);
				group.addView(imageView);
				ViewGroup textGroup = (ViewGroup) mParent.findViewById(R.id.textGroup);
				textGroup.addView(text);
			}

			mAdapter.updateData(mViewList, banners);
			// mAdapter.notifyDataSetChanged();
		}
	}

	private void initViewPager(View viewParent, Banner[] banners) {
		advPager = (ViewPager) viewParent.findViewById(R.id.view_pager_adv);
		// {
		// controlViewPagerSpeed(advPager);
		// }
		int ScreenWidth = DeviceInfo.scrrenWidth(mContext);
		int screenHeight = DeviceInfo.scrrenHeight(mContext);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ScreenWidth, screenHeight / 3);// 实际图片的比例
		advPager.setLayoutParams(lp);

		// 这里存放的是四张广告背景
		mViewList = new ArrayList<View>();
		/*
		 * int size = 0; if (banners != null) { size = banners.length; } for
		 * (int i = 0; i < size; i++) { final ImageView imgView = new
		 * ImageView(mContext);
		 * imgView.setScaleType(ImageView.ScaleType.FIT_XY);
		 * mViewList.add(imgView); } // 对imageviews进行填充 ViewGroup group =
		 * (ViewGroup) viewParent.findViewById(R.id.viewGroup); mDotViews = new
		 * ImageView[mViewList.size()]; // 小图标 for (int i = 0; i <
		 * mViewList.size()size; i++) { imageView = new ImageView(mContext);
		 * LinearLayout.LayoutParams layoutParams = new
		 * LinearLayout.LayoutParams(15, 15); layoutParams.setMargins(5, 0, 5,
		 * 0); imageView.setLayoutParams(layoutParams); mDotViews[i] =
		 * imageView; if (i == 0) {
		 * mDotViews[i].setBackgroundResource(R.drawable.selected); } else {
		 * mDotViews[i].setBackgroundResource(R.drawable.unselected); }
		 * group.addView(mDotViews[i]); }
		 */

		// mTextViewList = new ArrayList<TextView>();
		advPager.addOnPageChangeListener(this);
		advPager.setOnTouchListener(this);
		mAdapter = new BannerPagerAdapter(mContext, mViewList, banners);
		advPager.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		mThread = new Thread(new MyThread());
		mThread.start();
	}

	class MyThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				if (isContinue) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessageDelayed(0, 1500);
				}
			}
		}

	}

	private class banners {
		private Banner[] banners;

		public Banner[] getBanners() {
			return banners;
		}

		@SuppressWarnings("unused")
		public void setBanners(Banner[] banners) {
			this.banners = banners;
		}
	}
}
