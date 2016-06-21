package com.xiakee.xkxsns.ui.fragment;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.model.SNSListHandler;
import com.xiakee.xkxsns.ui.activity.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

public class FragmentSNS extends BaseFragment {

	private View mRootView;
	private Context mContex;
	private double lo;
	private double la;

	private MainActivity mMA;

	// private SNSViewPager mSnsBanner;
	private SNSListHandler mSnsListHandler;
	// private ListView mListView;

	public FragmentSNS() {
		super();
	}

	public FragmentSNS(Context context, double lo, double la) {
		super();
		mContex = context;
	}

	// private void init(View rootView) {
	// mSnsBanner = SNSViewPager.Instance(mContex, rootView);
	// }

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
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		if (mRootView == null) {
//			mRootView = LayoutInflater.from(mContex).inflate(R.layout.fragment_sns, null);
//		}
//		initList(mRootView);
//		ViewGroup parent = (ViewGroup) mRootView.getParent();
//		if (null != parent) {
//			parent.removeView(mRootView);
//		}
//		return mRootView;
//	}

	@Override
	public View onCreateRootView(LayoutInflater inflater) {
		mRootView  = inflater.inflate(R.layout.fragment_sns, null);
		return mRootView;
	}

	@Override
	public void onCreateFinished() {
		initList(mRootView);
	}

	private void initList(View root) {
		if (null == mSnsListHandler)
			mSnsListHandler = new SNSListHandler(mActivity, root);
	}

//	@Override
//	public void onResume() {
//		mMA = (MainActivity) getActivity();
//		mMA.getTitleBar().setTitle(R.string.xiakee_sns);
//	}

}
