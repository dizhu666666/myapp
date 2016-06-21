package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.android.util.ActivityLauncher;
import com.android.util.DeviceInfo;
import com.android.util.ImageUtil;
import com.android.util.SNSAPI;
//import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.TopicDetail;
import com.xiakee.xkxsns.model.ImageLoader;
import com.xiakee.xkxsns.model.ImageLoader.ImageLoaderListener;
//import com.xiakee.xkxsns.model.SnsListHandler.TopicList;
import com.xiakee.xkxsns.model.IonRequest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
//import android.widget.BaseAdapter;
//import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 实现对BaseAdapter中ViewHolder相关的简化 Created with IntelliJ IDEA. Author: william
 * email:me.weizh@live.com Date: 15-06-2 Time: 下午5:54
 */
public class TopicListAdapter extends CustomBaseAdapter implements OnScrollListener {

	/**
	 * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
	 * 参考http://blog.csdn.net/guolin_blog/article/details/9526203#comments
	 */
	protected boolean isFirstEnter = true;
	// private Context mContext;
	/**
	 * 一屏中第一个item的位置
	 */
	protected int mFirstVisibleItem;
	protected ImageLoader mImgLoader;

	private ListView mListView;

	protected int mScreenHeight = 0;
	protected int mScreenWidth = 0;
	/**
	 * 一屏中所有item的个数
	 */
	protected int mVisibleItemCount;

	private Handler mHandler;
	// data
	private List<TopicDetail> mTopicList;

	private TextView mTemp;
	// private static final int distance = 4;
	private LayoutParams avatarLLP;
	private int reqWidth;
	private int reqHeight;

	private final int SEX_MAN = 1;
	private final int SEX_WOMAN = 2;

	public TopicListAdapter(Context context, ListView listView, List<TopicDetail> topicList) {
		super(context);
		// mContext = context;
		mHandler = new Handler();
		mListView = listView;
		mTopicList = topicList;
		// mListView.setOnScrollListener(this);
		avatarLLP = calcAvatarLLP();
		mImgLoader = ImageLoader.Instance(mContext);
	}

	/**
	 * 取消下载任务
	 */
	public void cancelTask() {
		mImgLoader.cancelTask();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mTopicList != null ? mTopicList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mTopicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 因此在这里为首次进入程序开启下载任务。
		if (isFirstEnter && visibleItemCount > 0 && mTopicList.size() > 0) {
			loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
			isFirstEnter = false;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
		} else {
			cancelTask();
		}

	}

	public void refresh(List<TopicDetail> topicList) {
		mTopicList = topicList;
		notifyDataSetChanged();
	}

	/**
	 * 为ListView的item加载图片
	 * 
	 * @param firstVisibleItem
	 *            Listview中可见的第一张图片的下标
	 * @param visibleItemCount
	 *            Listview中可见的图片的数量
	 */
	private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {

		final int headViewCount = mListView.getHeaderViewsCount();

		// Log.e("headcount", "headViewCount: " + headViewCount);

		int footViewCount = 0;
		// if (mListView.getFooterVisibitlity() == View.VISIBLE) {
		// footViewCount = 1;
		// }

		// Log.e("check", "check_Visibitlity: " +
		// mListView.getFooterVisibitlity());
		// Log.e("check", "check_visibleItemCount: " + visibleItemCount);

		// Log.e("footViewCount", "headViewCount: " + footViewCount);

		// Log.e("itemsSize", "itemsSize: " + mTopicList.size());
		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount - 1; i++) {
			// final int index = i - headViewCount - 1;

			// if (i >= mTopicList.size())
			// break;
			// Log.e("check", "checkIndex: " + i);
			// Log.e("check", "checkfirstVisibleItem: " + firstVisibleItem);
			final int count = mTopicList.get(i).getImgCount();
			if (1 == count) {
				final String imgUrl = mTopicList.get(i).getImgUrl();
				adapteImg(SNSAPI.Test_BASE_URL + imgUrl);
			} else if (2 == count) {
				final String imgUrl = mTopicList.get(i).getImgUrl();
				adapteImg(SNSAPI.Test_BASE_URL + imgUrl);

				final String imgUrl2 = mTopicList.get(i).getImgUrl2();
				adapteImg(SNSAPI.Test_BASE_URL + imgUrl2);
			} else if (3 == count) {
				final String imgUrl = mTopicList.get(i).getImgUrl();
				adapteImg(SNSAPI.Test_BASE_URL + imgUrl);

				final String imgUrl2 = mTopicList.get(i).getImgUrl2();
				adapteImg(SNSAPI.Test_BASE_URL + imgUrl2);

				final String imgUrl3 = mTopicList.get(i).getImgUrl3();
				adapteImg(SNSAPI.Test_BASE_URL + imgUrl3);
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent, ViewHolder holder) {

		final TopicDetail topicDetail = mTopicList.get(position);
		final int sex = topicDetail.getSex();
		// title part
		final TextView tvTitle = (TextView) holder.obtainView(convertView, R.id.topic_title);
		final TextView tvDes = (TextView) holder.obtainView(convertView, R.id.topic_des);

		/*************************************************************/
		final TextView userName = (TextView) holder.obtainView(convertView, R.id.user_name);
		final TextView userLv = (TextView) holder.obtainView(convertView, R.id.user_lv);
		final TextView userLocation = (TextView) holder.obtainView(convertView, R.id.user_location);
		final TextView postTime = (TextView) holder.obtainView(convertView, R.id.post_time);
		final TextView commentsCount = (TextView) holder.obtainView(convertView, R.id.num_comments);
		final String avatarUrl = topicDetail.getPhoto();
		/******************************************************************/
		userName.setText(topicDetail.getUserName());
		tvTitle.setText(topicDetail.getTitle());
		String des = topicDetail.getTopicDesc();
		if (null == des) {
			tvDes.setVisibility(View.GONE);
		} else {
			tvDes.setVisibility(View.VISIBLE);
			tvDes.setText(topicDetail.getTopicDesc());
		}

		userLv.setSelected(sex == SEX_MAN ? true : false);
		userLv.setText("lv " + topicDetail.getLv());
		userLocation.setText("北京"/* topicDetail.getPostCity() */);
		postTime.setText(topicDetail.getTopicTime());
		commentsCount.setText("" + topicDetail.getCommentCount());
		/******************** Img part ******************************/
		if (0 == topicDetail.getImgCount()) {
			final ImageView imgView = holder.obtainView(convertView, R.id.img_topic);
			imgView.setVisibility(View.GONE);

		} else if (1 == topicDetail.getImgCount()) {
			final ImageView imgView1 = holder.obtainView(convertView, R.id.img_topic);
			adapte1Img(imgView1, SNSAPI.Test_BASE_URL + topicDetail.getImgUrl());
		} else {
			final ImageView imgView1 = (ImageView) holder.obtainView(convertView, R.id.img_1);
			adapte3Img(imgView1, SNSAPI.Test_BASE_URL + topicDetail.getImgUrl());
			final ImageView imgView2 = (ImageView) holder.obtainView(convertView, R.id.img_2);
			adapte3Img(imgView2, SNSAPI.Test_BASE_URL + topicDetail.getImgUrl2());
			final ImageView imgView3 = (ImageView) holder.obtainView(convertView, R.id.img_3);
			adapte3Img(imgView3, SNSAPI.Test_BASE_URL + topicDetail.getImgUrl3());
		}
		// /*************** user actions ******************************/
		// final View actionView = holder.obtainView(convertView,
		// R.id.actions_topic);
		// final MyTextView readersTv = (MyTextView)
		// actionView.findViewById(R.id.tv_readers);
		// readersTv.setText("" + topicDetail.getReadCount());
		//
		// final MyTextView commentTv = (MyTextView)
		// actionView.findViewById(R.id.tv_comment);
		// commentTv.setText("" + topicDetail.getCommentCount());
		// commentTv.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent();
		// ActivityLauncher.startPostActivity(mContext, intent);
		// }
		// });
		// final TextView praiseTv = (MyTextView)
		// actionView.findViewById(R.id.tv_praise);
		// praiseTv.setText("" + topicDetail.getGoodCount());
		// praiseTv.setSelected(topicDetail.getGoodStatus() == 0 ? false :
		// true);
		// // praiseTv.setTag(topicDetail.getTopicId());
		// praiseTv.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// // Toast.makeText(mContext, "praiseTv", 1000).show();
		// // praiseTv.setTag(topicDetail.getTopicId());
		// mTemp = (MyTextView) actionView.findViewById(R.id.tv_praise);
		// // TODO
		// new
		// Http(mContext).httpGet(SNSAPI.getPraiseActionUrl(topicDetail.getTopicId(),
		// 1899));
		// // mTemp.setSelected(true);
		// }
		// });

		return convertView;
	}

	@Override
	public int itemLayoutId(int position) {
		return mTopicList.get(position).getImgCount() <= 1 ? R.layout.item_topic_1pic : R.layout.item_topic_3pics;
	}

	@Override
	public int getItemViewType(int position) {
		return getItemType(position);
	}

	// must add this method,always forget
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	private LinearLayout.LayoutParams calcAvatarLLP() {
		reqWidth = calcAvatarwWidth();
		reqHeight = reqWidth;
		return new LinearLayout.LayoutParams(reqWidth, reqHeight);
	}

	private int getItemType(int position) {
		if (mTopicList.get(position).getImgCount() <= 1) {
			return 0;
		}
		return 1;
	}

	private void adapte1Img(final ImageView currentView, String strUrl) {

		int reqWidth = calcBigImgWidth();
		int reqHeight = calcBigImgHeight();
		currentView.setVisibility(View.VISIBLE);
		currentView.setTag(strUrl);
		final Bitmap bitmap = mImgLoader.loadBitmap(strUrl, new ImageLoaderListener() {
			@Override
			public void onImageLoaded(Bitmap bitmap, String url) {
				// if (bitmap != null) {
				// if (url.equals(currentView.getTag().toString())) {
				// currentView.setImageBitmap(bitmap);
				// } else {
				// currentView.setImageResource(R.drawable.xiakee_small);
				// }
				// }
				final ImageView img = (ImageView) mListView.findViewWithTag(url);
				if (img != null) {
					img.setImageBitmap(bitmap);
				}
			}
		}, reqWidth, reqHeight);

		if (null != bitmap) {
			currentView.setImageBitmap(bitmap);
		} else {
			currentView.setImageResource(R.drawable.xiakee_small);
		}
		LinearLayout.LayoutParams imgLLP = new LinearLayout.LayoutParams(reqWidth, reqHeight);
		currentView.setLayoutParams(imgLLP);
	}

	private void adapte3Img(final ImageView currentView, String fullUrl) {
		int reqWidth = calcSmallImgWidth();
		int reqHeight = reqWidth;
		currentView.setTag(fullUrl);
		final Bitmap bitmap = mImgLoader.loadBitmap(fullUrl, new ImageLoaderListener() {
			@Override
			public void onImageLoaded(Bitmap bitmap, String url) {
				// if (bitmap != null) {
				// if (url.equals(currentView.getTag().toString())) {
				// currentView.setImageBitmap(bitmap);
				// } else {
				// currentView.setImageResource(R.drawable.xiakee_small);
				// }
				// }
				final ImageView img = (ImageView) mListView.findViewWithTag(url);
				if (img != null) {
					img.setImageBitmap(bitmap);
				}
			}
		}, reqWidth, reqHeight);
		if (bitmap != null) {
			currentView.setImageBitmap(bitmap);
		} else {
			currentView.setImageResource(R.drawable.xiakee_small);
		}
		LinearLayout.LayoutParams imgViewLp = new LinearLayout.LayoutParams(reqWidth, reqHeight);
		currentView.setLayoutParams(imgViewLp);
	}

	private void adapteImg(String fullUrl) {
		int reqWidth = calcSmallImgWidth();
		int reqHeight = reqWidth;
		mImgLoader.loadBitmap(fullUrl, new ImageLoaderListener() {
			@Override
			public void onImageLoaded(Bitmap bitmap, String url) {
				final ImageView img = (ImageView) mListView.findViewWithTag(url);
				if (img != null) {
					img.setImageBitmap(bitmap);
				}
			}
		}, reqWidth, reqHeight);
	}

	private void adapteAvatar(final ImageView currentView, String strUrl) {

		currentView.setTag(strUrl);
		Bitmap bitmap = mImgLoader.loadBitmap(strUrl, new ImageLoaderListener() {
			@Override
			public void onImageLoaded(Bitmap bitmap, String url) {
				final ImageView img = (ImageView) mListView.findViewWithTag(url);
				if (img != null) {
					img.setImageBitmap(ImageUtil.getRoundImage(mContext, bitmap, reqWidth));
				}
			}
		}, reqWidth, reqHeight);
		if (bitmap != null) {
			currentView.setImageBitmap(ImageUtil.getRoundImage(mContext, bitmap, reqWidth));
		} else {
			currentView.setImageBitmap(ImageUtil.getRoundImage(mContext, R.drawable.default_avatar, reqWidth));
		}
		currentView.setLayoutParams(avatarLLP);
	}

	private int calcSmallImgWidth() {
		final int screenWidth = DeviceInfo.scrrenWidth(mContext);
		return (screenWidth) / 3 - 9;
	}

	private int calcBigImgWidth() {
		final int screenWidth = DeviceInfo.scrrenWidth(mContext);
		return screenWidth;
	}

	private int calcBigImgHeight() {
		return DeviceInfo.reqLenBaseHeight(mContext, 0.333f);
	}

	private int calcAvatarwWidth() {
		return DeviceInfo.reqLenBaseWidth(mContext, 0.1f);
	}

	private class Http extends IonRequest {

		public Http(Context c) {
			super(c);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void handleError(String errmsg) {
			handleError(errmsg, false);
		}

		@Override
		protected void handleResult(JsonObject result) {
			// Gson gson = new Gson();
			final int goodStatus = result.get("goodStatus").getAsInt();
			final int topicId = result.get("topicId").getAsInt();
			final boolean bPraised = (goodStatus == 0 ? false : true);

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mTemp.setSelected(bPraised);
				}
			});
		}
	}

	private class AvatarClickListener implements OnClickListener {
		private int mUserId;

		public AvatarClickListener(int userId) {
			mUserId = userId;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final Intent intent = new Intent();
			intent.putExtra(SNSAPI.USERID, mUserId);
			ActivityLauncher.startUserSpaceActivity(mContext, intent);
		}

	}

}