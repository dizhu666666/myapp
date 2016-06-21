package com.xiakee.xkxsns.model;

import com.android.util.SNSAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

public abstract class IonRequest {

	public interface IHttpcallBack {
		public void onResultError(int type);

		public void onResultSuccess(int type);
	}

	private static final int ERROR_DIALOG = 1;
	private static final String ERROR_EXIT = "errexit";
	private static final String ERROR_MESSAGE = "errmsg";
	private static final int PROGRESS_DIALOG = 0;
	public static final int TYPE_FAVORITE = 2;

	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_SEPATOR = 1;
	protected Context mContext;

	private String mCurrentRequest;

	public IonRequest(Context c) {
		mContext = c;
	}

	public void cancelAll() {
		Ion.getDefault(mContext).cancelAll();
	}

	protected String getCurrentRequest() {
		return mCurrentRequest;
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
	protected void handleError(String errmsg, boolean exit) {
		Bundle args = new Bundle();
		args.putString(ERROR_MESSAGE, errmsg);
		args.putBoolean(ERROR_EXIT, exit);
		// Fragment dialogFragment = Fragment.newInstance(
		// "Are you sure you want to do this?");
		// dialogFragment.show(getFragmentManager(), "dialog");
		if ("java.util.concurrent.TimeoutException".equalsIgnoreCase(errmsg)) {
			Toast.makeText(mContext, "请检查网络连接！", 1000).show();
		}

		// new AlertDialog.Builder(mContext).setTitle("错误信息").setMessage(errmsg)
		// .setPositiveButton("确定", null).show();
		// ((Activity) mContext).showDialog(ERROR_DIALOG, args);
	}

	private void handleResult(Exception e, JsonObject result) {
		// ((Activity) mContext).dismissDialog(PROGRESS_DIALOG);
		if (e != null) {
			// usually happens due to network issue
			handleError(e.toString());
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

	public void httpGet(String url) {
		if (mCurrentRequest != null) {
			return; // one request a time
		}
		mCurrentRequest = url;
		// mCurrentRequest =
		// "http://dlsvr04.asus.com/pub/ASUS/nb/DriversForWin8.1/VGA/VGA_nVidia_Win81_64_VER918134505.zip";
		// ((Activity) mContext).showDialog(PROGRESS_DIALOG);

		// ProgressDialog bar = new ProgressDialog(mContext, 1);
		// bar.setMessage("请稍后...");
		Ion.with(mContext).load(url).setLogging("ION_LOG", Log.DEBUG).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {
					@Override
					public void onCompleted(Exception e, JsonObject result) {
						handleResult(e, result);
					}
				});
	}

	public void httpPost(String url, String json) {
		if (mCurrentRequest != null) {
			return; // one request a time
		}
		mCurrentRequest = url;
		// showDialog(PROGRESS_DIALOG);
		// Ion.with(this).load(url).addHeader(arg0, arg1);

		Ion.with(mContext).load(url).uploadProgressBar(new ProgressBar(mContext)).setStringBody(json).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {
					@Override
					public void onCompleted(Exception e, JsonObject result) {
						handleResult(e, result);
					}
				});
	}
}
