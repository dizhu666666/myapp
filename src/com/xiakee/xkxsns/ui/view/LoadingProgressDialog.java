package com.xiakee.xkxsns.ui.view;

import org.xutils.common.Callback;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.ui.activity.PostActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * 页面加载动画类
 * 
 * @author WTY
 *
 */

public class LoadingProgressDialog extends Dialog {
	private Context context = null;
	private static LoadingProgressDialog customProgressDialog = null;
	private Handler mHandler;

	public LoadingProgressDialog(Context context) {
		super(context);
		this.context = context;
		setCancelable(false);
	}

	public LoadingProgressDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		setCancelable(false);
	}

	public LoadingProgressDialog(Context context, int theme, Callback.Cancelable cancelable) {
		super(context, theme);
		this.context = context;
//		this.cancelHttp = cancelable;
		setCancelable(false);
	}

	public static LoadingProgressDialog createDialog(Context context) {
		customProgressDialog = new LoadingProgressDialog(context, R.style.myProgressDialog);
		customProgressDialog.setContentView(R.layout.view_loadingprogress);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return customProgressDialog;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		if (customProgressDialog == null) {
			return;
		}
		ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loading_iv);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
		animationDrawable.start();
	}

	/**
	 * 
	 * setTitile 标题
	 * 
	 * @param strTitle
	 * @return
	 * 
	 */
	public LoadingProgressDialog setTitile(String strTitle) {
		return customProgressDialog;
	}

	/**
	 * 
	 * setTitile 标题
	 * 
	 * @param strTitle
	 * @return
	 * 
	 */
	public LoadingProgressDialog setTitile(int res) {
		return customProgressDialog;
	}

	/**
	 * 
	 * setMessage 提示内容
	 * 
	 * @param strMessage
	 * @return
	 * 
	 */
	public LoadingProgressDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressDialog.findViewById(R.id.progress_tv);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
		return customProgressDialog;
	}

	/**
	 * 
	 * setMessage 提示内容
	 * 
	 * @param strMessage
	 * @return
	 * 
	 */
	public LoadingProgressDialog setMessage(int strMessage) {
		TextView tvMsg = (TextView) customProgressDialog.findViewById(R.id.progress_tv);
		if (tvMsg != null) {
			tvMsg.setText(context.getResources().getString(strMessage));
		}
		return customProgressDialog;
	}

	@Override
	public void onBackPressed() {
		if (null != mHandler) {
			Message msg = mHandler.obtainMessage();
			msg.what = PostActivity.CANCEL_UPLOADING;
			mHandler.dispatchMessage(msg);
		}
		dismiss();
	}

	public void setHandler(Handler h) {
		this.mHandler = h;
	}

}