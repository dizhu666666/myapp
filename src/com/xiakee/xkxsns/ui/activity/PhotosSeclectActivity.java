package com.xiakee.xkxsns.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.photo.adapter.AlbumGridViewAdapter;
import com.android.photo.util.AlbumHelper;
import com.android.photo.util.Bimp;
import com.android.photo.util.ImageBucket;
import com.android.photo.util.ImageItem;
import com.android.photo.util.PublicWay;
import com.android.photo.util.Res;
import com.android.util.DeviceInfo;
import com.android.util.ImageUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xiakee.xkxsns.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 这个是进入相册显示所有图片的界面
 * 
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:47:15
 */
public class PhotosSeclectActivity extends Activity {
	// 显示手机里的所有图片的列表控件
	private GridView gridView;
	// 当手机里没有图片时，提示用户没有图片的控件
	private TextView tv;
	// gridView的adapter
	private AlbumGridViewAdapter gridImageAdapter;
	// 完成按钮
	private Button okButton;
	// 相册按钮
	private Button album;
	// 取消按钮
	private Button cancel;
	private Intent intent;
	// 预览按钮
	private Button preview;
	private Context mContext;
	private ArrayList<ImageItem> dataList;
	private AlbumHelper helper;
	public static List<ImageBucket> contentList;
	public static Bitmap bitmap;
	// add by wiliam 10-27
	private int index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(Res.getLayoutID("plugin_camera_album"));
		// add by william
		index = getIntent().getIntExtra(PostActivity.CURRENT_INDEX, -1);
//		PublicWay.activityList.add(this);
		mContext = this;
		// 注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
		bitmap = BitmapFactory.decodeResource(getResources(), Res.getDrawableID("plugin_camera_no_pictures"));
		init();
		initListener();
		// 这个函数主要用来控制预览和完成按钮的状态
		isShowOkBt();
		setTranslucentStatus();
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// mContext.unregisterReceiver(this);
			// TODO Auto-generated method stub
			gridImageAdapter.notifyDataSetChanged();
		}
	};
	
	/**
	 * 设置状态栏背景状态
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void setTranslucentStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);
			
//			win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			
			// 创建状态栏的管理实例
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			// 激活状态栏设置
			tintManager.setStatusBarTintEnabled(true);
			// 激活导航栏设置
//			tintManager.setStatusBarTintColor(getResources().getColor(R.color.red));
			tintManager.setNavigationBarTintEnabled(true);
			// tintManager.setStatusBarTintResource(0);// 状态栏无背景
			 tintManager.setStatusBarTintDrawable(getResources().getDrawable(R.drawable.plugin_camera_title_bar));	}
		}


	// 预览按钮的监听
	private class PreviewListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (Bimp.tempSelectBitmap.size() > 0) {
				intent.putExtra("position", 1);
				intent.setClass(PhotosSeclectActivity.this, PreviewActivity.class);
				startActivity(intent);
			}
		}
	}

	// 完成按钮的监听
	private class AlbumSendListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// overridePendingTransition(R.anim.activity_translate_in,
			// R.anim.activity_translate_out);
			// intent.setClass(mContext, PostActivity.class);
			// startActivity(intent);
			/*for (int i = 0; i < Bimp.tempSelectBitmap.size(); ++i) {
				Bimp.tempSelectBitmap.get(i).isUsed = true;
			}*/
			Intent intent = new Intent();
			intent.putExtra(PostActivity.CURRENT_INDEX, index);
			setResult(Activity.RESULT_OK, intent);
			// unregisterReceiver(broadcastReceiver);
			finish();
		}
	}

	// 相册按钮监听
	private class AlbumButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			intent.setClass(PhotosSeclectActivity.this, FoldersActivity.class);
			startActivity(intent);
			// Bimp.tempSelectBitmap.clear();
			// Bimp.max = 0;
			// finish();
		}
	}

	// 取消按钮的监听
	private class CancelListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Bimp.tempSelectBitmap.clear();
			Bimp.max = 0;
			// intent.setClass(mContext, PostActivity.class);
			// startActivity(intent);
			// Intent intent = new Intent();
			// intent.putExtra(PostActivity.CURRENT_INDEX, index);
			setResult(Activity.RESULT_CANCELED);
			// unregisterReceiver(broadcastReceiver);
			finish();
		}
	}

	// 初始化，给一些对象赋值
	private void init() {
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		contentList = helper.getImagesBucketList(false);
		dataList = new ArrayList<ImageItem>();
		for (int i = 0; i < contentList.size(); i++) {
			dataList.addAll(contentList.get(i).imageList);
		}
		RelativeLayout headView= (RelativeLayout) findViewById(Res.getWidgetID("headview"));
		RelativeLayout.LayoutParams titleBarLLP = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ImageUtil.dip2px(mContext, 45));
		titleBarLLP.setMargins(0, DeviceInfo.getStatusHeight(this), 0, 0);
		// Title bar
		headView.setLayoutParams(titleBarLLP);

		album = (Button) findViewById(Res.getWidgetID("album"));
		cancel = (Button) findViewById(Res.getWidgetID("cancel"));
		cancel.setOnClickListener(new CancelListener());
		album.setOnClickListener(new AlbumButtonListener());
		preview = (Button) findViewById(Res.getWidgetID("preview"));
		preview.setOnClickListener(new PreviewListener());
		intent = getIntent();
		// Bundle bundle = intent.getExtras();
		gridView = (GridView) findViewById(Res.getWidgetID("select_gridview"));
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList, Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		tv = (TextView) findViewById(Res.getWidgetID("myText"));
		gridView.setEmptyView(tv);
		okButton = (Button) findViewById(Res.getWidgetID("ok_button"));
		okButton.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
	}

	private void initListener() {

		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(int position, boolean isSelected, ImageButton selectToggle) {
				if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
					selectToggle.setSelected(false);
					// selectToggle.setVisibility(View.GONE);
					if (!removeOneData(dataList.get(position))) {
						Toast.makeText(PhotosSeclectActivity.this, Res.getString("only_choose_num"), 200).show();
					}
					return;
				}
				if (isSelected) {
					selectToggle.setSelected(true);
					Bimp.tempSelectBitmap.add(dataList.get(position));
					okButton.setText(
							Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
				} else {
					Bimp.tempSelectBitmap.remove(dataList.get(position));
					selectToggle.setSelected(false);
					okButton.setText(
							Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
				}
				isShowOkBt();
			}
		});
		okButton.setOnClickListener(new AlbumSendListener());
	}

	private boolean removeOneData(ImageItem imageItem) {
		if (Bimp.tempSelectBitmap.contains(imageItem)) {
			Bimp.tempSelectBitmap.remove(imageItem);
			okButton.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			return true;
		}
		return false;
	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			okButton.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			preview.setPressed(true);
			okButton.setPressed(true);
			preview.setClickable(true);
			okButton.setClickable(true);
			okButton.setTextColor(Color.WHITE);
			preview.setTextColor(Color.WHITE);
		} else {
			okButton.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
			preview.setPressed(false);
			preview.setClickable(false);
			okButton.setPressed(false);
			okButton.setClickable(false);
			okButton.setTextColor(Color.parseColor("#E1E0DE"));
			preview.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (keyCode == KeyEvent.KEYCODE_BACK) {
	 * intent.setClass(PhotosSeclectActivity.this, FoldersActivity.class); //
	 * unregisterReceiver(broadcastReceiver); startActivity(intent); } return
	 * false;
	 * 
	 * }
	 */

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// unregisterReceiver(broadcastReceiver);
		Bimp.tempSelectBitmap.clear();
		Bimp.max = 0;
		finish();
	};

	@Override
	protected void onRestart() {
		isShowOkBt();
		super.onRestart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);

	}
}
