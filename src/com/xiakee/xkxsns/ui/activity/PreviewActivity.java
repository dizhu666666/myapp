package com.xiakee.xkxsns.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.photo.util.Bimp;
import com.android.photo.util.ImageItem;
import com.android.photo.util.PublicWay;
import com.android.photo.util.Res;
import com.android.photo.zoom.PhotoView;
import com.android.photo.zoom.ViewPagerFixed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 这个是用于进行图片浏览时的界面
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:47:53
 */
public class PreviewActivity extends Activity {
	private Intent intent;
	// 返回按钮
	private Button back_bt;
	// 发送按钮
	private Button send_bt;
	// 删除按钮
	private Button del_bt;
	// 顶部显示预览图片位置的textview
	private TextView positionTextView;
	// 获取前一个activity传过来的position
	private int position;
	// 当前的位置
	private int location = 0;

	private List<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();

	// private Context mContext;

	RelativeLayout photo_relativeLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(Res.getLayoutID("plugin_camera_gallery"));// 切屏到主界面
		PublicWay.activityList.add(this);
		// mContext = this;
		back_bt = (Button) findViewById(Res.getWidgetID("gallery_back"));
		send_bt = (Button) findViewById(Res.getWidgetID("send_button"));
		del_bt = (Button) findViewById(Res.getWidgetID("gallery_del"));
		back_bt.setOnClickListener(new BackListener());
		send_bt.setOnClickListener(new GallerySendListener());
		del_bt.setOnClickListener(new DelListener());
		intent = getIntent();
		// Bundle bundle = intent.getExtras();
		position = intent.getIntExtra("position", -1);
		isShowOkBt();
		// 为发送按钮设置文字
		pager = (ViewPagerFixed) findViewById(Res.getWidgetID("gallery01"));
		pager.setOnPageChangeListener(pageChangeListener);
		// for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
		// initListViews(Bimp.tempSelectBitmap.get(i).getBitmap());
		// }
		// william 优化
		initListViews(Bimp.tempSelectBitmap.size());
		adapter = new MyPageAdapter(this, listViews, Bimp.tempSelectBitmap);
		pager.setAdapter(adapter);
		pager.setPageMargin(getResources().getDimensionPixelOffset(Res.getDimenID("ui_10_dip")));
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			location = arg0;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};



	private void initListViews(int size) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		for (int i = 0; i < size; ++i) {
			PhotoView imgview = new PhotoView(this);
			imgview.setBackgroundColor(0xff000000);
			// imgview.setImageBitmap(bm);
			imgview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			listViews.add(imgview);
		}
	}

	// 返回按钮添加的监听器
	private class BackListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			intent.setClass(PreviewActivity.this, FoldersActivity.class);
			startActivity(intent);
		}
	}

	// 删除按钮添加的监听器
	private class DelListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (listViews.size() == 1) {
				Bimp.tempSelectBitmap.clear();
				Bimp.max = 0;
				send_bt.setText(
						Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
				Intent intent = new Intent("data.broadcast.action");
				sendBroadcast(intent);
				finish();
			} else {
				Bimp.tempSelectBitmap.remove(location);
				Bimp.max--;
				pager.removeAllViews();
				listViews.remove(location);
				adapter.setListViews(listViews);
				send_bt.setText(
						Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
				adapter.notifyDataSetChanged();
			}
		}
	}

	// 完成按钮的监听
	private class GallerySendListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			intent.setClass(PreviewActivity.this, PostActivity.class);
			startActivity(intent);
			finish();
		}
	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			send_bt.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			send_bt.setPressed(true);
			send_bt.setClickable(true);
			send_bt.setTextColor(Color.WHITE);
		} else {
			send_bt.setPressed(false);
			send_bt.setClickable(false);
			send_bt.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	};

	/**
	 * 监听返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (position == 1) {
				this.finish();
				intent.setClass(PreviewActivity.this, PhotosSeclectActivity.class);
				startActivity(intent);
			} else if (position == 2) {
				this.finish();
				intent.setClass(PreviewActivity.this, ShowPhotosActivity.class);
				startActivity(intent);
			}
		}
		return true;
	}

	class MyPageAdapter extends PagerAdapter {

		private Context mContext;
		private List<View> listViews;
		private List<ImageItem> mImgList;

		private int size;

		public MyPageAdapter(Context c, List<View> listViews) {
			mContext = c;
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public MyPageAdapter(Context c, List<View> listViews, List<ImageItem> imgList) {
			mContext = c;
			this.listViews = listViews;
			this.mImgList = imgList;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(List<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		@Override
		public int getCount() {
			return size;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public Object instantiateItem(View arg0, int position) {

			// william 优化
			ImageView imgView = null;
			try {
				imgView = (ImageView) listViews.get(position % size);
				((ViewPagerFixed) arg0).addView(imgView, 0);

			} catch (Exception e) {
				Log.e("MyPageAdapter", e.getMessage());
			}
			imgView.setImageBitmap(mImgList.get(position % size).getBitmap(mContext));
			return listViews.get(position % size);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
