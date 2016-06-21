package com.xiakee.xkxsns.model;

import java.util.ArrayList;
import java.util.List;

import com.android.util.DeviceInfo;
import com.android.util.SNSAPI;
import com.android.xlistview.XListView;
import com.android.xlistview.XListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.TopicDetail;
import com.xiakee.xkxsns.bean.TopicList;
import com.xiakee.xkxsns.ui.adapter.UserSpaceAdapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class UserSpaceHandler extends IonRequest implements IXListViewListener, OnItemClickListener {

	private List<TopicDetail> mTopicList;
	private BaseAdapter mAdapter;
	private XListView mListView;
	private Handler mHandler;

	private int refreshIndex = -1;
	private boolean bUp = true;
	private String mLastRefreshTime;
	private int mUserId;
	private UserInfoAdapter mDataAdapter;

	public UserSpaceHandler(Context c, View view, int userId) {
		super(c);
		mUserId = userId;
		mHandler = new Handler();
		mTopicList = new ArrayList<TopicDetail>();
		initViews(view);
		getMoreTopicList();
	}

	public void initViews(View view) {
		// topicList = new TopicList();
		//mListView = (XListView) view.findViewById(R.id.user_space_list);
		final View userInfo = inflateUserInfoView();
		mDataAdapter = new UserInfoAdapter(mContext, userInfo,mUserId);
		mListView.addHeaderView(userInfo, null, true);
		mListView.setHeaderDividersEnabled(false);
		mAdapter = new UserSpaceAdapter(mContext, mListView, mTopicList);

		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);

		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

	}

	@Override
	protected void handleError(String errmsg) {
		handleError(errmsg, false);
		onLoad_error();
	}

	@Override
	protected void handleResult(JsonObject result) {
		Gson gson = new Gson();
		// final TopicList topicList = new TopicList();
		final String bbsListString = result.toString();
		TopicList topicList = gson.fromJson(bbsListString, TopicList.class);
		final int currentSize = topicList.getTopicList().size();
		refreshIndex += currentSize;
		if (bUp) {
			if (currentSize == 0) {
				onLoad(true);
			} else {
				refresh(addMoreTopicList(topicList.getTopicList()));
				onLoad(false);
			}
		} else {
			refresh(addLatestTopicList(topicList.getTopicList()));
			onLoad(false);
		}
	}

	public List<TopicDetail> addMoreTopicList(List<TopicDetail> topicList) {
		mTopicList.addAll(topicList);
		return mTopicList;
	}

	public List<TopicDetail> addLatestTopicList(List<TopicDetail> topicList) {
		mTopicList.addAll(0, topicList);
		return mTopicList;
	}

	// private View initUserView() {
	// final View parent = inflateUserInfoView();
	// final View userView = parent.findViewById(R.id.user_info);
	// final int height = DeviceInfo.scrrenHeight(mContext);
	// RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
	// android.view.ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.4));
	// userView.setLayoutParams(rl);
	//
	// final TextView bgImg = (TextView) parent.findViewById(R.id.user_info_bg);
	//
	// final Bitmap bkg = ImageUtil.getResizedBitmap(mContext,
	// R.drawable.default_avatar, 200, 200);
	// Bitmap blurBm = null;
	// if (StringUtils.getAPIVersion() >= 17) {
	// blurBm = ImageUtil.blurBitmap(mContext, bkg);
	// } else {
	// blurBm = ImageUtil.doBlur(bkg, 50, false);
	// }
	// bgImg.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(),
	// blurBm));
	// return parent;
	// }
	//
	// private ImageView calcAvatar(View parent) {
	// final int width = DeviceInfo.scrrenWidth(mContext);
	// LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(width / 4,
	// width / 4);
	// final ImageView avatar = (ImageView)
	// parent.findViewById(R.id.user_info_avatar);
	// avatar.setLayoutParams(llp);
	// return avatar;
	// }
	//
	// private View calcBackground(View parent) {
	// final View userView = parent.findViewById(R.id.user_info);
	// final int height = DeviceInfo.scrrenHeight(mContext);
	// RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
	// android.view.ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.4));
	// userView.setLayoutParams(rl);
	//
	// return userView;
	//
	// }
	//
	// private void blur(ImageView view, Bitmap bm, int radius) {
	// Bitmap blurBm = null;
	// if (StringUtils.getAPIVersion() >= 17) {
	// blurBm = ImageUtil.blurBitmap(mContext, bm);
	// } else {
	// blurBm = ImageUtil.doBlur(bm, 50, false);
	// }
	// view.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(),
	// blurBm));
	// }

	private void refresh(List<TopicDetail> topicList) {
		// TODO Auto-generated method stub
		((UserSpaceAdapter) mAdapter).refresh(topicList);
	}

	private void getMoreTopicList() {
		httpGet(SNSAPI.getTopicsListUrl(refreshIndex > 0 ? mTopicList.get(refreshIndex).getTopicId() : 0,
				SNSAPI.PULL_UP, 1898));
	}

	private void getLatestTopicList() {
		httpGet(SNSAPI.getTopicsListUrl(refreshIndex > 0 ? mTopicList.get(0).getTopicId() : 0, SNSAPI.PULL_DOWN, 1898));
	}

	private View inflateUserInfoView() {
		final LayoutInflater lif = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return lif.inflate(R.layout.view_user_info, null, false);
	}

	private void onLoad(boolean isEnd) {
		mListView.stopRefresh();
		mListView.stopLoadMore(isEnd);
		mListView.setRefreshTime(DeviceInfo.getCurrentTime());
	}

	private void onLoad_error() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime(mLastRefreshTime);
	}

	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				bUp = false;
				mLastRefreshTime = DeviceInfo.getCurrentTime();
				getLatestTopicList();
			}
		}, 500);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// geneItems();
				bUp = true;
				getMoreTopicList();
			}
		}, 500);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "第" + position + "条", 1000).show();
	}
	
	public void refresh()
	{
		mDataAdapter.update();
	}
}
