package com.xiakee.xkxsns.ui.view;

import android.content.Context;
import android.os.AsyncTask;

public class AsyncLoadingProgress extends AsyncTask<String, Integer, Object> {
	private LoadingProgressDialog mProgress;
	private Context mContext;
	private String mTip;

	public AsyncLoadingProgress(Context context, String tip) {
		mContext = context;
		mTip = tip;
	}

	@Override
	protected Object doInBackground(String... params) {
		// 后续代码
		return null;
	}

	protected void onPostExecute(String result) {
		stopProgressDialog();
		// 后续代码
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		startProgressDialog();
	}

	private void startProgressDialog() {
		if (mProgress == null) {
			mProgress = LoadingProgressDialog.createDialog(mContext);
			mProgress.setMessage(mTip);
		}
		mProgress.show();
	}

	private void stopProgressDialog() {
		if (mProgress != null) {
			mProgress.dismiss();
			mProgress = null;
		}
	}

}
