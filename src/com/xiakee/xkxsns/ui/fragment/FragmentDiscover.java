package com.xiakee.xkxsns.ui.fragment;

import com.xiakee.xkxsns.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

public class FragmentDiscover extends BaseFragment {

	private View mRootView;
	private Context mContex;

	public FragmentDiscover() {
		super();
	}

	public FragmentDiscover(Context context) {
		super();
		mContex = context;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Activity.RESULT_FIRST_USER && data != null) {
		}
	}

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		if (mRootView == null) {
//			mRootView = LayoutInflater.from(mContex).inflate(R.layout.fragement_discover, null);
//		}
//		ViewGroup parent = (ViewGroup) mRootView.getParent();
//		if (null != parent) {
//			parent.removeView(mRootView);
//		}
//		return mRootView;
//	}

	@Override
	public View onCreateRootView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.fragement_discover, null);
	}

	@Override
	public void onCreateFinished() {

	}

}
