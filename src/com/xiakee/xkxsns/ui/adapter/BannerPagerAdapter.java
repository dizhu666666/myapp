package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.android.util.DeviceInfo;
import com.android.util.SNSAPI;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.Banner;
import com.xiakee.xkxsns.model.ImageLoader;
import com.xiakee.xkxsns.model.ImageLoader.ImageLoaderListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BannerPagerAdapter extends PagerAdapter {

	protected Context mContext;
	protected ImageLoader mImgloader;
	protected Banner[] banners;
	/**
	 * 装点点的ImageView数组
	 */
	protected List<View> mViewList;

	public BannerPagerAdapter(Context c, List<View> views) {
		this.mViewList = views;
		this.mContext = c;
		mImgloader = ImageLoader.Instance(mContext);
	}

	public BannerPagerAdapter(Context c, List<View> views, Banner[] banners) {
		this.mViewList = views;
		this.mContext = c;
		this.banners = banners;
		mImgloader = ImageLoader.Instance(mContext);
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
		if (banners == null || banners.length == 0) {
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

		final int maxLength = DeviceInfo.scrrenWidth(mContext);
		final int maxHeight = DeviceInfo.scrrenHeight(mContext);

		// get bitmap
		final String imgUrl = SNSAPI.Test_BASE_URL + banners[realPosition].getImgUrl();
		final Bitmap bm = mImgloader.loadBitmap(imgUrl, new ImageLoaderListener() {
			@Override
			public void onImageLoaded(Bitmap bitmap, String url) {
				// TODO Auto-generated method stub
				if (bitmap != null && !bitmap.isRecycled()) {
					imgview.setImageBitmap(bitmap);
				}
			}
		}, maxLength, maxHeight);
		if (bm != null && !bm.isRecycled()) {
			imgview.setImageBitmap(bm);
		} else {
			imgview.setImageResource(R.drawable.bg_defaut_01);
		}
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

	public void updateData(List<View> views, Banner[] banners) {
		mViewList = views;
		this.banners = banners;
		notifyDataSetChanged();
	}

}
