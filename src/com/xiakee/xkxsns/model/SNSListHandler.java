package com.xiakee.xkxsns.model;

import java.util.ArrayList;
import java.util.List;

import com.android.util.ActivityLauncher;
import com.android.util.DeviceInfo;
import com.android.util.SNSAPI;
import com.android.xlistview.XListView;
import com.android.xlistview.XListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.TopicDetail;
import com.xiakee.xkxsns.bean.TopicList;
import com.xiakee.xkxsns.ui.activity.MainActivity;
import com.xiakee.xkxsns.ui.activity.TopicDetailsActivity;
import com.xiakee.xkxsns.ui.adapter.TopicListAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SNSListHandler extends IonRequest implements IXListViewListener, OnItemClickListener {

	private List<TopicDetail> mTopicList;
	// private TopicList topicList;
	private BaseAdapter mAdapter;
	// private Context mContext;
	private XListView mListView;
	private SNSViewPager mSnsBanner;
	private Handler mHandler;
	// private int start = 0;

	private int refreshIndex = -1;
	private boolean bUp = true;
	private String mLastRefreshTime;

	public SNSListHandler(Context c, View view) {
		super(c);
		// mContext = c;
		initListView(view);
	}

	public void initListView(View view) {
		// topicList = new TopicList();
		mTopicList = new ArrayList<TopicDetail>();
		mListView = (XListView) view.findViewById(R.id.sns_list);
		final View bannerView = inflateBannerView();
		mSnsBanner = SNSViewPager.Instance(mContext, bannerView);
		// mSnsBanner = new SNSViewPager(mContext,bannerView);
		final View topicView = inflateTopicsView();
		initForumTopicsClick(topicView);
		mListView.addHeaderView(bannerView, null, true);
		mListView.addHeaderView(topicView, null, true);
		mListView.setHeaderDividersEnabled(false);
		mAdapter = new TopicListAdapter(mContext, mListView, mTopicList);

		mListView.setPullLoadEnable(true);
		// mListView.setPullLoadEnable(false);
		// mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);
		mHandler = new Handler();

		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		getMoreTopicList();
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
			mSnsBanner.pullRefresh();
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

	private void refresh(List<TopicDetail> topicList) {
		// TODO Auto-generated method stub
		((TopicListAdapter) mAdapter).refresh(topicList);
	}

	private void getMoreTopicList() {
		httpGet(SNSAPI.getTopicsListUrl(refreshIndex > 0 ? mTopicList.get(refreshIndex).getTopicId() : 0,
				SNSAPI.PULL_UP, 1898));
	}

	private void getLatestTopicList() {
		httpGet(SNSAPI.getTopicsListUrl(refreshIndex > 0 ? mTopicList.get(0).getTopicId() : 0, SNSAPI.PULL_DOWN, 1898));
	}

	private View inflateBannerView() {
		final LayoutInflater lif = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return lif.inflate(R.layout.view_banner, null, false);
	}

	private View inflateTopicsView() {
		final LayoutInflater lif = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return lif.inflate(R.layout.view_topics_tab, null, false);
	}

	private void initForumTopicsClick(View topicsView) {
		final TextView tv_equ = (TextView) topicsView.findViewById(R.id.equipment);
		tv_equ.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(mContext, "tv_equ", 1000).show();
				final Intent intent = new Intent();
				intent.putExtra(SNSAPI.KEY_FORUMID, 1);
				intent.putExtra(SNSAPI.KEY_FORUMNAME_RESID, R.string.equipment);
				ActivityLauncher.startCommonForumActivity(mContext, intent);
			}
		});

		/*************************************************/
		final TextView outside = (TextView) topicsView.findViewById(R.id.outside);
		outside.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Intent intent = new Intent();
				intent.putExtra(SNSAPI.KEY_FORUMID, 2);
				intent.putExtra(SNSAPI.KEY_FORUMNAME_RESID, R.string.outside);
				ActivityLauncher.startCommonForumActivity(mContext, intent);
			}
		});
		/********************************************************/

		final TextView tv_knowledge = (TextView) topicsView.findViewById(R.id.knowledge);
		tv_knowledge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Intent intent = new Intent();
				intent.putExtra(SNSAPI.KEY_FORUMID, 3);
				intent.putExtra(SNSAPI.KEY_FORUMNAME_RESID, R.string.knowledge);
				ActivityLauncher.startCommonForumActivity(mContext, intent);
			}
		});
		/**************************************************************/

		final TextView tv_flea_market = (TextView) topicsView.findViewById(R.id.flea_market);
		tv_flea_market.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Intent intent = new Intent();
				intent.putExtra(SNSAPI.KEY_FORUMID, 4);
				intent.putExtra(SNSAPI.KEY_FORUMNAME_RESID, R.string.flea_market);
				ActivityLauncher.startCommonForumActivity(mContext, intent);
			}
		});
	}

	/*
	 * private void geneItems() { for (int i = 0; i != 5; ++i) { items.add(
	 * "refresh cnt " + (++start)); } }
	 */
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
		int index = position - mListView.getHeaderViewsCount();
		((MainActivity) mContext).startActivity(new Intent(mContext, TopicDetailsActivity.class)
				.putExtra(TopicDetailsActivity.TOPIC_ID, ""+mTopicList.get(index).topicId));
	}
}
