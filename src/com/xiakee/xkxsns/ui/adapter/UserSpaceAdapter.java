package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.android.util.DeviceInfo;
import com.android.util.SNSAPI;
import com.android.xlistview.XListView;
//import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.TopicDetail;
import com.xiakee.xkxsns.model.ImageLoader;
import com.xiakee.xkxsns.model.ImageLoader.ImageLoaderListener;
//import com.xiakee.xkxsns.model.SnsListHandler.TopicList;
import com.xiakee.xkxsns.model.IonRequest;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 实现对BaseAdapter中ViewHolder相关的简化 Created with IntelliJ IDEA. Author: william
 * email:me.weizh@live.com Date: 15-06-2 Time: 下午5:54
 */
public class UserSpaceAdapter extends CustomBaseAdapter {

    protected ImageLoader mImgLoader;

    private XListView mListView;

    private Handler mHandler;
    // data
    private List<TopicDetail> mTopicList;

    private TextView mTemp;
    // private static final int distance = 4;
    private int reqWidth;
    private int reqHeight;

    public UserSpaceAdapter(Context context, ListView listView, List<TopicDetail> topicList) {
        super(context);
        mHandler = new Handler();
        mListView = (XListView) listView;
        mTopicList = topicList;
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
        // return mComments.length;
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


    public void refresh(List<TopicDetail> topicList) {
        mTopicList = topicList;
        notifyDataSetChanged();
    }

//	/**
//	 * 为ListView的item加载图片
//	 * 
//	 * @param firstVisibleItem
//	 *            Listview中可见的第一张图片的下标
//	 * @param visibleItemCount
//	 *            Listview中可见的图片的数量
//	 */
//	private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
//
//		final int headViewCount = mListView.getHeaderViewsCount();
//
//		// Log.e("headcount", "headViewCount: " + headViewCount);
//
//		int footViewCount = 0;
//		if (mListView.getFooterVisibitlity() == View.VISIBLE) {
//			footViewCount = 1;
//		}
//
//		Log.e("check", "check_Visibitlity: " + mListView.getFooterVisibitlity());
//		Log.e("check", "check_visibleItemCount: " + visibleItemCount);
//
//		// Log.e("footViewCount", "headViewCount: " + footViewCount);
//
//		// Log.e("itemsSize", "itemsSize: " + mTopicList.size());
//		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount - 1; i++) {
//			// final int index = i - headViewCount - 1;
//
//			// if (i >= mTopicList.size())
//			// break;
//			// Log.e("check", "checkIndex: " + i);
//			// Log.e("check", "checkfirstVisibleItem: " + firstVisibleItem);
//			final int count = mTopicList.get(i).getImgCount();
//			if (1 == count) {
//				final String imgUrl = mTopicList.get(i).getImgUrl();
//				adapteImg(SNSAPI.Test_BASE_URL + imgUrl);
//			} else if (2 == count) {
//				final String imgUrl = mTopicList.get(i).getImgUrl();
//				adapteImg(SNSAPI.Test_BASE_URL + imgUrl);
//
//				final String imgUrl2 = mTopicList.get(i).getImgUrl2();
//				adapteImg(SNSAPI.Test_BASE_URL + imgUrl2);
//			} else if (3 == count) {
//				final String imgUrl = mTopicList.get(i).getImgUrl();
//				adapteImg(SNSAPI.Test_BASE_URL + imgUrl);
//
//				final String imgUrl2 = mTopicList.get(i).getImgUrl2();
//				adapteImg(SNSAPI.Test_BASE_URL + imgUrl2);
//
//				final String imgUrl3 = mTopicList.get(i).getImgUrl3();
//				adapteImg(SNSAPI.Test_BASE_URL + imgUrl3);
//			}
//		}
//	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent, ViewHolder holder) {

        final TopicDetail topicDetail = mTopicList.get(position);
        // title part
//		final TextView time_past = (TextView) holder.obtainView(convertView, R.id.time_pasted);
        final TextView titleView = (TextView) holder.obtainView(convertView, R.id.title_topic);
        /******************************************************************/
        titleView.setText(topicDetail.getTitle());

        TextView tvLv = holder.obtainView(convertView, R.id.user_lv);
        tvLv.setVisibility(View.GONE);

        //post_time
        TextView tvTime = holder.obtainView(convertView, R.id.post_time);
        tvTime.setText(topicDetail.getTopicTime());

        TextView tvComments = holder.obtainView(convertView, R.id.num_comments);
        tvComments.setText(topicDetail.getCommentCount() + "");
//		time_past.setText(topicDetail.getTopicTime());
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
        /*************** user actions ******************************/
//		final View actionView = holder.obtainView(convertView, R.id.actions_topic);
//		final MyTextView readersTv = holder.obtainView(convertView,R.id.tv_readers);
//		readersTv.setText("" + topicDetail.getReadCount());
//
//		final MyTextView commentTv = holder.obtainView(convertView,R.id.tv_comment);
//		commentTv.setText("" + topicDetail.getCommentCount());
//		commentTv.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				ActivityLauncher.startPostActivity(mContext, intent);
//			}
//		});
//		final TextView praiseTv = (MyTextView) actionView.findViewById(R.id.tv_praise);
//		praiseTv.setText("" + topicDetail.getGoodCount());
//		praiseTv.setSelected(topicDetail.getGoodStatus() == 0 ? false : true);
//		// praiseTv.setTag(topicDetail.getTopicId());
//		praiseTv.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				// Toast.makeText(mContext, "praiseTv", 1000).show();
//				// praiseTv.setTag(topicDetail.getTopicId());
//				mTemp = (MyTextView) actionView.findViewById(R.id.tv_praise);
//				// TODO
//				new Http(mContext).httpGet(SNSAPI.getPraiseActionUrl(topicDetail.getTopicId(), 1899));
//				// mTemp.setSelected(true);
//			}
//		});

        return convertView;
    }

    @Override
    public int itemLayoutId(int position) {
        return mTopicList.get(position).getImgCount() <= 1 ? R.layout.item_userspace_topic_1pic : R.layout.item_userspace_topic_3pics;
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
        LinearLayout.LayoutParams imgLLP = (LinearLayout.LayoutParams) currentView.getLayoutParams();
        imgLLP.height = reqHeight;
        currentView.setLayoutParams(imgLLP);
    }

    private void adapte3Img(final ImageView currentView, String fullUrl) {
        int reqWidth = calcSmallImgWidth();
        int reqHeight = reqWidth;
        currentView.setTag(fullUrl);
        final Bitmap bitmap = mImgLoader.loadBitmap(fullUrl, new ImageLoaderListener() {
            @Override
            public void onImageLoaded(Bitmap bitmap, String url) {
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
        LinearLayout.LayoutParams imgViewLp = (LinearLayout.LayoutParams) currentView.getLayoutParams();
        imgViewLp.height = reqHeight;
        currentView.setLayoutParams(imgViewLp);
    }

//	private void adapteImg(String fullUrl) {
//		int reqWidth = calcSmallImgWidth();
//		int reqHeight = reqWidth;
//		 mImgLoader.loadBitmap(fullUrl, new ImageLoaderListener() {
//			@Override
//			public void onImageLoaded(Bitmap bitmap, String url) {
//				final ImageView img = (ImageView) mListView.findViewWithTag(url);
//				if (img != null) {
//					img.setImageBitmap(bitmap);
//				}
//			}
//		}, reqWidth, reqHeight);
//	}


    private int calcSmallImgWidth() {
        final int screenWidth = DeviceInfo.scrrenWidth(mContext);
        return (screenWidth) / 3 - 9;
    }

    private int calcBigImgWidth() {
        final int screenWidth = DeviceInfo.scrrenWidth(mContext);
        return screenWidth - 9;
    }

    private int calcBigImgHeight() {
        return DeviceInfo.reqLenBaseHeight(mContext, 0.333f);
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

}