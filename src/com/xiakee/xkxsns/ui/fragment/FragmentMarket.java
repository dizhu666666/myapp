package com.xiakee.xkxsns.ui.fragment;

import com.android.util.SNSAPI;
import com.xiakee.xkxsns.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("JavascriptInterface")
public class FragmentMarket extends BaseFragment /* WebViewFragment */ {

	private String mUrl;
	// private CustomProgressDialog mDialog;
	private WebView myWebView;
	private View mRootView;
	private Context mContex;

	private class HelloWebViewClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	private String getUrl(Intent intent) {
		return intent.getStringExtra("url");
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loadUrl(View view, String url) {
		// showFramedialog();
		if (myWebView == null) {
			myWebView = (WebView) view.findViewById(R.id.webview);
			myWebView.getSettings().setJavaScriptEnabled(true);
			/*
			 * myWebView.addJavascriptInterface(new JavaforJsObject(this,
			 * myWebView .getSettings().getUserAgentString()), "objAndroid");
			 */
			// 载入js
			// myWebView.loadUrl("file:///android_asset/test.html");
			myWebView.loadUrl(url);
			myWebView.setWebViewClient(new HelloWebViewClient());
			handleBack(myWebView);
		}
	}

	private void handleBack(final WebView wv) {
		wv.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack()) { // 表示按返回键
						// 时的操作
						wv.goBack(); // 后退
						return true; // 已处理
					}
				}
				return false;
			}
		});
	}

	public FragmentMarket() {
		super();
	}

	public FragmentMarket(Context context) {
		super();
		mContex = context;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Activity.RESULT_FIRST_USER && data != null) {
		}
	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		// setView();
//		loadUrl(mRootView, SNSAPI.URL_MARKET);
//		// setLitener();
//		super.onActivityCreated(savedInstanceState);
//
//	}



//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		if (mRootView == null) {
//			mRootView = LayoutInflater.from(mContex).inflate(R.layout.fragement_market, null);
//		}
//		// loadUrl(mRootView, SNSAPI.URL_MARKET);
//		ViewGroup parent = (ViewGroup) mRootView.getParent();
//		if (null != parent) {
//			parent.removeView(mRootView);
//		}
//		return mRootView;
//	}

	@Override
	public View onCreateRootView(LayoutInflater inflater) {
		mRootView = inflater.inflate(R.layout.fragement_market, null);
		return mRootView;
	}

	@Override
	public void onCreateFinished() {
		loadUrl(mRootView, SNSAPI.URL_MARKET);
	}
	/*
	 * @Override // 设置回退 // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	 * public boolean onKeyDown(int keyCode, KeyEvent event) { if ((keyCode ==
	 * KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) { myWebView.goBack(); //
	 * goBack()表示返回WebView的上一页面 return true; } return super.onKeyDown(keyCode,
	 * event); }
	 */

	@Override
	public void onPause() {
		super.onPause();
	};

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	};

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();

	}

	public void setUrl(final String url) {
		mUrl = url;
	}

	/*
	 * public void showFramedialog() { mDialog = new CustomProgressDialog(this,
	 * "正在加载中...", R.anim.frame_waitingdialog); mDialog.setCancelable(false);
	 * mDialog.show(); }
	 */

}
