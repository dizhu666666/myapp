package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class BasePagerAdapter extends PagerAdapter {

	protected Context mContext;
//	protected ImageLoader mImgloader;
	protected String[] mUrls;
	/**
	 * 装点点的ImageView数组
	 */
	protected List<View> mViewList;

	public BasePagerAdapter(Context c, List<View> views) {
		this.mViewList = views;
		this.mContext = c;
		// mUrls = urls;
//		mImgloader = ImageLoader.Instance(mContext);
	}

	public BasePagerAdapter(Context c, List<View> views, String[] urls) {
		this.mViewList = views;
		this.mContext = c;
		mUrls = urls;
//		mImgloader = ImageLoader.Instance(mContext);
	}

	public BasePagerAdapter(Context c, List<View> views, List<String> urls) {
		this.mViewList = views;
		this.mContext = c;
		mUrls = new String[urls.size()];
		for (int i = 0; i < urls.size(); ++i) {
			mUrls[i] = urls.get(i);
		}
//		mImgloader = ImageLoader.Instance(mContext);
	}

	@Override
	public void destroyItem(View Parent, int position, Object arg2) {

		final int realPosition = position % mViewList.size();
		final View view = mViewList.get(realPosition);
		final ViewGroup parent = (ViewGroup) view.getParent();
		if (null != parent) {
			parent.removeView(view);
		}
		// if(Parent)
		// ((ViewPager) Parent).removeView(mViewList.get(position
		// % mViewList.size()));
	}

	@Override
	public void finishUpdate(View parent) {
	}

	@Override
	public int getCount() {
		if (mUrls == null || mUrls.length == 0) {
			return 0;
		}
		return Integer.MAX_VALUE;
	}

	@Override
	public Object instantiateItem(View Parent, int position) {
		final int realPosition = position % mViewList.size();
		final ImageView imgview = (ImageView) mViewList.get(realPosition);
		ViewGroup parent = (ViewGroup) imgview.getParent();
		if (null != parent) {
			parent.removeView(imgview);
		}
		((ViewPager) Parent).addView(imgview);

		// get bitmap
	/*	final Bitmap bm = mImgloader.getImage(mUrls[realPosition],
				new onImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						// TODO Auto-generated method stub
						if (bitmap != null && !bitmap.isRecycled()) {
							imgview.setImageBitmap(bitmap);
						}
					}
				});
		if (bm != null && !bm.isRecycled()) {
			imgview.setImageBitmap(bm);
		} else {
			imgview.setImageResource(drawable.gym_default);
		}*/
		return imgview;
	}

	@Override
	public boolean isViewFromObject(View parent, Object obj) {
		return (parent == obj);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

	public void updateData(List<View> views, String[] urls) {
		mViewList = views;
		mUrls = urls;
		notifyDataSetChanged();
	}

}
