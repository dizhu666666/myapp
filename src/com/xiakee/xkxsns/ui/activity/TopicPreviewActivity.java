package com.xiakee.xkxsns.ui.activity;

import java.util.List;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.SectionData;
import com.xiakee.xkxsns.bean.TopicUtil;
import com.xiakee.xkxsns.ui.adapter.TopicPreviewAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 帖子预览界面
 *
 * @author william
 * @QQ:52218739
 * @version 2014年10月18日 下午11:47:53
 */
public class TopicPreviewActivity extends BaseActivity {
	private String mTopicTitle;
	private List<SectionData> mSectionDataList;
	// private Context mContext;

	RelativeLayout photo_relativeLayout;
	private ListView mListView;
	private BaseAdapter mAdapter;

	private TextView mPostAction;
	private TextView mPreviewAction;
	private TextView mBackButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_preview);
		Intent intent = getIntent();
		String jsonStr = intent.getStringExtra("topic");
		// TODO
		final TopicUtil topic = TopicUtil.loadTopic(this, jsonStr);
		// mTopicTitle = topic.title;
		// mSectionDataList = topic.contents;
		initListViews(topic);
		initTitleBar();
	}

	private void initListViews(TopicUtil topic) {
		mListView = (ListView) findViewById(R.id.lv_topic_preview);
		mAdapter = new TopicPreviewAdapter(this, mListView, topic);
		mListView.setAdapter(mAdapter);
		final TextView tv = new TextView(this);
		tv.setText(topic.title);
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(25);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 5, 0, 5);
		mListView.addHeaderView(tv);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(PostActivity.PREVIEW_RETURN2EDIT);
		// finish();
	}

	private void initTitleBar() {
		mTitleBar = getTitleBar();
		if (mTitleBar != null) {
			mPostAction = mTitleBar.showRightAction(R.string.post, -1);
			mPostAction.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// uploadContents(mManager.getSecectedPics());
					setResult(PostActivity.RREVIEW_POST);
					TopicPreviewActivity.this.finish();
				}
			});
			mPreviewAction = mTitleBar.showPreRightAction(R.string.preview, -1);
			mPreviewAction.setTextColor(getResources().getColor(R.color.gray));
			mBackButton = mTitleBar.showLeftAction(R.drawable.title_back_arrow, R.string.back);
			mBackButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setResult(PostActivity.PREVIEW_RETURN2EDIT);
					TopicPreviewActivity.this.finish();
				}
			});
		}
	}
}
