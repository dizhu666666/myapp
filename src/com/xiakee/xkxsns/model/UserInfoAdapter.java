package com.xiakee.xkxsns.model;

import com.android.util.DeviceInfo;
import com.android.util.ImageUtil;
import com.android.util.SNSAPI;
import com.android.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.TopicList;
import com.xiakee.xkxsns.bean.UserSpaceInfo;
import com.xiakee.xkxsns.model.ImageLoader.ImageLoaderListener;
import com.xiakee.xkxsns.ui.activity.UserSpaceActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserInfoAdapter extends IonRequest {
	// private Context mContext;

	private ImageView mAvatar;
	private View mParentView;
	private TextView nickName;
	private TextView userLv;
	private TextView numFollow;
	private TextView beFollow;

	private ImageLoader mImgLoader;
	private TextView numReply;
	private TextView numTopics;
	private TextView numPraised;
	private UserSpaceInfo userSpaceInfo;

	public UserInfoAdapter(Context context, View parent, int userId) {
		super(context);
		// mContext = context;
		mImgLoader = ImageLoader.Instance(mContext);
		calcAvatar(parent);
		calcBackground(parent);
		initBackButton(parent);
		adjustPosition(parent);
		initFollowButton(parent);
		initUserData(parent);
		initTopicsData(parent);
		getUserInfo(userId);
	}

	private void getUserInfo(int userId) {
		httpGet(SNSAPI.getUserSpaceInfoUrl(1898, userId));
	}

	private void updateAvatar(final ImageView imgView, final String url) {
		final int width = DeviceInfo.scrrenWidth(mContext);
		Bitmap bitmap = mImgLoader.loadBitmap(SNSAPI.Test_BASE_URL + url, new ImageLoaderListener() {

			@Override
			public void onImageLoaded(Bitmap bitmap, String url) {
				// TODO Auto-generated method stub
				imgView.setImageBitmap(ImageUtil.getRoundImage(mContext, bitmap, width / 4));
				blur(mParentView, bitmap, 100);
			}
		}, width / 4, width / 4);
		if (bitmap != null) {
			imgView.setImageBitmap(ImageUtil.getRoundImage(mContext, bitmap, width / 4));
			blur(mParentView, bitmap, 100);
		}
	}

	private void calcAvatar(View parent) {
		final int width = DeviceInfo.scrrenWidth(mContext);
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(width / 4, width / 4);
		mAvatar = (ImageView) parent.findViewById(R.id.user_info_avatar);
		mAvatar.setLayoutParams(llp);
	}

	private void calcBackground(View parent) {
		mParentView = parent.findViewById(R.id.user_info);
		final int height = DeviceInfo.scrrenHeight(mContext);
		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, (int) (height * 0.4));
		mParentView.setLayoutParams(rl);
	}

	private void adjustPosition(View parent) {
		final View adJustview = parent.findViewById(R.id.back_follow_control);
		RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		llp.setMargins(0, DeviceInfo.getStatusHeight(mContext), 0, 0);
		adJustview.setLayoutParams(llp);
	}

	private void initBackButton(View parent) {
		ImageView backBtn = (ImageView) parent.findViewById(R.id.back_btn);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((UserSpaceActivity) mContext).onBackPressed();
			}
		});
	}

	private void initFollowButton(View parent) {

		ImageView followBtn = (ImageView) parent.findViewById(R.id.follow_btn);
		followBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void blur(View view, Bitmap bm, int radius) {
		Bitmap blurBm = null;

		if (StringUtils.getAPIVersion() >= 17) {
			blurBm = ImageUtil.blurBitmap(mContext, bm);
		} else {
			blurBm = ImageUtil.doBlur(bm, 50, false);
		}
		view.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blurBm));
	}

	private void initUserData(View parent) {
		nickName = (TextView) parent.findViewById(R.id.user_info_nickname);
		userLv = (TextView) parent.findViewById(R.id.user_info_level);
		numFollow = (TextView) parent.findViewById(R.id.num_follow);
		beFollow = (TextView) parent.findViewById(R.id.num_be_follow);
	}

	private void initTopicsData(View parent) {

		final LinearLayout Topics = (LinearLayout) parent.findViewById(R.id.user_info_tab_topics);
		final LinearLayout Reply = (LinearLayout) parent.findViewById(R.id.user_info_tab_reply);
		final LinearLayout Praised = (LinearLayout) parent.findViewById(R.id.user_info_praised);
		/*******************************************************/

		numTopics = (TextView) parent.findViewById(R.id.user_info_tab_topics_num);
		numTopics.setSelected(true);
		numReply = (TextView) parent.findViewById(R.id.user_info_tab_reply_num);
		numPraised = (TextView) parent.findViewById(R.id.user_info_praised_num);

		/*******************************************************/
		final TextView titleTopics = (TextView) parent.findViewById(R.id.user_info_tab_topics_tilte);
		titleTopics.setSelected(true);
		final TextView titleReply = (TextView) parent.findViewById(R.id.user_info_tab_reply_title);
		final TextView titlePraised = (TextView) parent.findViewById(R.id.user_info_praised_title);
		/*****************************************************************/
		Topics.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				numTopics.setSelected(true);
				titleTopics.setSelected(true);
				numReply.setSelected(false);
				titleReply.setSelected(false);
				numPraised.setSelected(false);
				titlePraised.setSelected(false);
			}
		});

		Reply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				numTopics.setSelected(false);
				titleTopics.setSelected(false);
				numReply.setSelected(true);
				titleReply.setSelected(true);
				numPraised.setSelected(false);
				titlePraised.setSelected(false);
			}
		});

		Praised.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				numTopics.setSelected(false);
				titleTopics.setSelected(false);
				numReply.setSelected(false);
				titleReply.setSelected(false);
				numPraised.setSelected(true);
				titlePraised.setSelected(true);
			}
		});
	}

	private class UserAvatar extends IonRequest {
		private ImageView avatar;
		public UserAvatar(Context c, ImageView imgView) {
			super(c);
			avatar = imgView;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void handleError(String errmsg) {
			handleError(errmsg, false);
			// onLoad_error();
		}

		@Override
		protected void handleResult(JsonObject result) {
			Gson gson = new Gson();
			// final TopicList topicList = new TopicList();
			final String bbsListString = result.toString();
			TopicList topicList = gson.fromJson(bbsListString, TopicList.class);
		}
	}

	private class FollowPeople extends IonRequest {

		public FollowPeople(Context c) {
			super(c);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void handleError(String errmsg) {
			handleError(errmsg, false);
			// onLoad_error();
		}

		@Override
		protected void handleResult(JsonObject result) {
			Gson gson = new Gson();
			// final TopicList topicList = new TopicList();
			final String bbsListString = result.toString();
			TopicList topicList = gson.fromJson(bbsListString, TopicList.class);
		}
	}

	@Override
	protected void handleError(String errmsg) {
		handleError(errmsg, false);
		// onLoad_error();
	}

	@Override
	protected void handleResult(JsonObject result) {
		Gson gson = new Gson();
		// final TopicList topicList = new TopicList();
		final String userspace = result.toString();
		userSpaceInfo = gson.fromJson(userspace, UserSpaceInfo.class);
		update(userSpaceInfo);
	}

	private void update(UserSpaceInfo info) {
		updateAvatar(mAvatar, info.photo);
		nickName.setText(info.userName);
		userLv.setText("LV " + info.lv);
		numFollow.setText("" + info.focusCount);
		beFollow.setText("" + info.fansCount);
		numTopics.setText("" + info.topicCount);
		//numReply.setText("" + info.commentCount);
		numPraised.setText("" + info.goodCount);
	}

	public void update() {
		updateAvatar(mAvatar, userSpaceInfo.photo);
		nickName.setText(userSpaceInfo.userName);
		userLv.setText("LV " + userSpaceInfo.lv);
		numFollow.setText("" + userSpaceInfo.focusCount);
		beFollow.setText("" + userSpaceInfo.fansCount);
		numTopics.setText("" + userSpaceInfo.topicCount);
		//numReply.setText("" + userSpaceInfo.commentCount);
		numPraised.setText("" + userSpaceInfo.goodCount);
	}

}
