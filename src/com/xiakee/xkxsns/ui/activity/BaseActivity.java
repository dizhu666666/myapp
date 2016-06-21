package com.xiakee.xkxsns.ui.activity;

import java.io.File;
import java.util.List;

import com.android.photocropper.CropHandler;
import com.android.photocropper.CropHelper;
import com.android.photocropper.CropParams;
import com.android.util.DeviceInfo;
import com.android.util.SNSAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders.Any.B;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xiakee.xkxsns.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class BaseActivity extends FragmentActivity implements CropHandler {

	public static final int REQUESTCODE = 10086;
	private static final int ERROR_DIALOG = 1;
	private static final String ERROR_EXIT = "errexit";
	private static final String ERROR_MESSAGE = "errmsg";
	private static final int PROGRESS_DIALOG = 0;

	private String mCurrentRequest;
	protected TitleBar mTitleBar;

	protected int mScreenHeight = 0;
	protected int mScreenWidth = 0;

	public void cancelAll() {
		Ion.getDefault(this).cancelAll();
	}

	@Override
	public Activity getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public CropParams getCropParams() {
		// TODO Auto-generated method stub
		return null;
	}

	protected String getCurrentRequest() {
		return mCurrentRequest;
	}

	protected void getScreenInfo() {
		mScreenWidth = DeviceInfo.scrrenWidth(this);
		mScreenHeight = DeviceInfo.scrrenHeight(this);
	}

	public TitleBar getTitleBar() {
		return mTitleBar;
	}

	protected void handleError(String errmsg) {
		// the default error handling will show an alert dialog and
		// finish the activity after the user confirmation
		// subclass may override the method to change behavior
		handleError(errmsg, true);
	}

	/**
	 * HTTP request error handling
	 * 
	 * @param errmsg
	 * @param exit
	 *            if true, exit the activity when error message is confirmed by
	 *            the user
	 */
	@SuppressLint("NewApi")
	protected void handleError(String errmsg, boolean exit) {
		Bundle args = new Bundle();
		args.putString(ERROR_MESSAGE, errmsg);
		args.putBoolean(ERROR_EXIT, exit);
		this.showDialog(ERROR_DIALOG, args);

	}

	private void handleResult(Exception e, JsonObject result) {
		this.dismissDialog(PROGRESS_DIALOG);
		if (e != null) {
			// usually happens due to network issue
			handleError(e.getMessage());
		} else if (result != null && !result.isJsonNull()) {
			JsonElement rescode = result.get(SNSAPI.RESULT_CODE);
			if (SNSAPI.RESULT_CODE_ERROR.equals(rescode.getAsString())) {
				JsonElement resmsg = result.get(SNSAPI.RESULT_MESSAGE);
				handleError(resmsg.getAsString());
			} else {
				handleResult(result);
			}
		}
		mCurrentRequest = null;
	}

	/**
	 * HTTP request result handling
	 * 
	 * @param result
	 */
	protected void handleResult(JsonObject result) {
		// subclass shall override the method and handle the response
	}

	@SuppressWarnings("deprecation")
	public void httpGet(String url) {
		if (mCurrentRequest != null) {
			return; // one request a time
		}
		mCurrentRequest = url;
		showDialog(PROGRESS_DIALOG);
		// Ion.with(this).load(url).addHeader(arg0, arg1);
		Ion.with(this).load(url).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
			@Override
			public void onCompleted(Exception e, JsonObject result) {
				handleResult(e, result);
			}
		});
	}

	@SuppressWarnings("deprecation")
	public void httpPost(String url, String path) {
		if (mCurrentRequest != null) {
			return; // one request a time
		}
		mCurrentRequest = url;
		showDialog(PROGRESS_DIALOG);
		// Ion.with(this).load(url).addHeader(arg0, arg1);
		Ion.with(this).load(url).uploadProgressBar(new ProgressBar(this)).setMultipartFile("null", new File(path))
				.asJsonObject().setCallback(new FutureCallback<JsonObject>() {
					@Override
					public void onCompleted(Exception e, JsonObject result) {
						handleResult(e, result);
					}
				});
	}

	public void httpPost(String url, JsonObject jsonObject, List<String> paths) {
		if (mCurrentRequest != null) {
			return; // one request a time
		}
		mCurrentRequest = url;
		showDialog(PROGRESS_DIALOG);
		// Ion.with(this).load(url).addHeader(arg0, arg1);
		final B ion_b = Ion.with(this).load(url).uploadProgressBar(new ProgressBar(this));
		for (int i = 0; i < paths.size(); ++i) {
			ion_b.setMultipartFile("pics" + i, new File(paths.get(i)));
		}
		ion_b.setJsonObjectBody(jsonObject).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
			@Override
			public void onCompleted(Exception e, JsonObject result) {
				handleResult(e, result);
				// Toast.makeText(BaseActivity.this, e.toString(), 2000).show();
			}
		});
	}

	public void httpPost(String url, String stringJson, List<String> paths) {
		if (mCurrentRequest != null) {
			return; // one request a time
		}
		mCurrentRequest = url;
		showDialog(PROGRESS_DIALOG);
		// Ion.with(this).load(url).addHeader(arg0, arg1);
		final B ion_b = Ion.with(this).load(url).uploadProgressBar(new ProgressBar(this));
		for (int i = 0; i < paths.size(); ++i) {
			ion_b.setMultipartFile("", new File(paths.get(i)));
		}
		// ion_b.addHeader("Content-Type", "application/json");
		// ion_b.setByteArrayBody(arg0)
		// ion_b.addMultipartParts(arg0)
		// ion_b.setMultipartFile(arg0, arg1)
		// ion_b.setMul
		ion_b.setBodyParameter("topicDetail", stringJson).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
			@Override
			public void onCompleted(Exception e, JsonObject result) {
				handleResult(e, result);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PostActivity.REQUEST_PHOTO_SELECT) {
			onMultiPhotoSelected();
		} else if (requestCode == CropHelper.REQUEST_CAMERA || requestCode == CropHelper.REQUEST_CROP) {
			CropHelper.handleResult(this, requestCode, resultCode, data);
		} else if (requestCode == PostActivity.PREVIEW_ACTIVITY_REQUEST_CODE) {
			onTopicPreviewDone(resultCode);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getScreenInfo();
		setTranslucentStatus();
	}

	@Override
	public Dialog onCreateDialog(int id, Bundle args) {
		Dialog dialog = null;
		switch (id) {
		case PROGRESS_DIALOG: {
			ProgressDialog dlg = new ProgressDialog(this);
			dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog = dlg;
			break;
		}
		case ERROR_DIALOG: {
			String errmsg = args.getString(ERROR_MESSAGE);
			boolean exit = args.getBoolean(ERROR_EXIT);
			OnClickListener listener = null;
			if (exit) {
				listener = new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				};
			}
			dialog = new AlertDialog.Builder(this).setMessage(errmsg).setCancelable(false)
					.setPositiveButton(R.string.button_ok, listener).create();
			break;
		}
		}
		return dialog;
	}

	protected void onMultiPhotoSelected() {

	}

	protected void onTopicPreviewDone(int resultCode) {

	}

	@Override
	public void onCropCancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCropFailed(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		cancelAll();
		if (getCropParams() != null)
			CropHelper.clearCachedCropFile(getCropParams().uri);
		super.onDestroy();
	}

	@Override
	public void onPhotoCropped(Uri uri) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();

		// Update the checked tab of the navigation bar
	}

	@Override
	public void setContentView(int layoutResID) {
		View view = getLayoutInflater().inflate(layoutResID, null);
		setContentView(view);
	}

	@Override
	public void setContentView(View view) {
		LinearLayout layout = new LinearLayout(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(params);
		layout.setOrientation(LinearLayout.VERTICAL);

		View titleBar = View.inflate(this, R.layout.view_titlebar, null);
		final int height = getResources().getDimensionPixelSize(
				R.dimen.title_bar_height) /*
											 * +
											 * DeviceInfo.getStatusHeight(this)
											 */;
		// set up the height value for titleBar
		// titleBar.getLayoutParams()
		LinearLayout.LayoutParams titleBarLLP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);

//		titleBar.getLayoutParams().height = height;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			titleBarLLP.setMargins(0, DeviceInfo.getStatusHeight(this), 0, 0);
		}
		titleBar.setLayoutParams(titleBarLLP);
		mTitleBar = (TitleBar) titleBar;

		// set weight so that it leaves space for bottom_Bar（fragment�?
		view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				0/* LayoutParams.MATCH_PARENT */, 1.0f));
		layout.addView(titleBar);
		layout.addView(view);
		super.setContentView(layout);
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		setContentView(view);
	}

	/**
	 * 设置状态栏背景状态
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void setTranslucentStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);

			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			// 激活状态栏设置
			tintManager.setStatusBarTintEnabled(true);
			// 激活导航栏设置
			tintManager.setStatusBarTintColor(getResources().getColor(R.color.white));
			tintManager.setNavigationBarTintEnabled(true);

			// win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		// 创建状态栏的管理实例
		// SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// 激活状态栏设置
		// tintManager.setStatusBarTintEnabled(true);
		// 激活导航栏设置
		// tintManager.setStatusBarTintColor(getResources().getColor(R.color.red));
		// tintManager.setNavigationBarTintEnabled(true);
		// tintManager.setStatusBarTintResource(0);// 状态栏无背景
		// tintManager.setStatusBarTintDrawable(get);
	}

	// public int getStatusBarHeight() {
	// int result = 0;
	// int resourceId = getResources().getIdentifier("status_bar_height",
	// "dimen", "android");
	// if (resourceId > 0) {
	// result = getResources().getDimensionPixelSize(resourceId);
	// }
	// return result;
	// }

}
