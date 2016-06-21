package com.xiakee.xkxsns.ui.activity;

import com.android.photo.adapter.FolderAdapter;
import com.android.photo.util.PublicWay;
import com.android.photo.util.Res;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 这个类主要是用来进行显示包含图片的文件夹
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:48:06
 */
public class FoldersActivity extends Activity {

	private FolderAdapter folderAdapter;
	private Button bt_cancel;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(Res.getLayoutID("plugin_camera_image_file"));
		PublicWay.activityList.add(this);
		mContext = this;
		bt_cancel = (Button) findViewById(Res.getWidgetID("cancel"));
		bt_cancel.setOnClickListener(new CancelListener());
		GridView gridView = (GridView) findViewById(Res.getWidgetID("fileGridView"));
		TextView textView = (TextView) findViewById(Res.getWidgetID("headerTitle"));
		textView.setText(Res.getString("photo"));
		folderAdapter = new FolderAdapter(this);
		gridView.setAdapter(folderAdapter);
	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		@Override
		public void onClick(View v) {
			// 清空选择的图片
			// Bimp.tempSelectBitmap.clear();
			// Bimp.max = 0;
			/*
			 * Intent intent = new Intent(); intent.setClass(mContext,
			 * PostActivity.class); startActivity(intent);
			 */
			finish();
		}
	}

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (keyCode == KeyEvent.KEYCODE_BACK) { Intent intent = new Intent();
	 * intent.setClass(mContext, PostActivity.class); startActivity(intent); }
	 * 
	 * return true; }
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
//		Intent intent = new Intent();
//		intent.setClass(mContext, PostActivity.class);
//		startActivity(intent);
		finish();
	}

}
