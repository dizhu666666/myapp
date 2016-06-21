package com.xiakee.xkxsns.ui.activity;

import com.android.util.ActivityLauncher;
import com.android.util.SNSAPI;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.model.ForumTopicsListHandler;
import com.xiakee.xkxsns.util.UserManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class CommonForumActivity extends BaseActivity {

	private ForumTopicsListHandler mListHandler;
	private int mTypeId;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final LayoutInflater inflater = LayoutInflater.from(this);
		final View view = inflater.inflate(R.layout.activity_forum_topic_list, null);
		final Intent intent = getIntent();
		mTypeId = intent.getIntExtra(SNSAPI.KEY_FORUMID, -1);
		final int resId = intent.getIntExtra(SNSAPI.KEY_FORUMNAME_RESID, -1);
		initList(view, mTypeId);
		setContentView(view);
		updateTitle(resId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// updateTitle(R.string.discovery);
		super.onResume();
	}

	private void initList(View root, int forumType) {
		if (null == mListHandler)
			mListHandler = new ForumTopicsListHandler(this, root, forumType);
	}

	private void updateTitle(int resId) {
		mTitleBar = getTitleBar();
		if (mTitleBar != null) {
			final TextView btnReady2Post = mTitleBar.showRightAction("", R.drawable.btn_post_topic);
			btnReady2Post.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					if (UserManager.isLogin()) {
						intent.putExtra(SNSAPI.KEY_FORUMID, mTypeId);
						ActivityLauncher.startPostActivity(CommonForumActivity.this, intent);
					} else {
						ActivityLauncher.startLoginActivity(CommonForumActivity.this, intent);
					}

				}
			});
			mTitleBar.setTitle(resId);
			mTitleBar.showLeftAction(R.drawable.title_back_arrow);
		}
	}

}
