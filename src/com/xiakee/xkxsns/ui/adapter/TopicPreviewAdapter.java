package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.android.util.DeviceInfo;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.SectionData;
import com.xiakee.xkxsns.bean.TopicUtil;
import com.xiakee.xkxsns.model.ImageLoader;
import com.xiakee.xkxsns.model.ImageLoader.ImageLoaderListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
//import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopicPreviewAdapter extends CustomBaseAdapter implements OnScrollListener {

	// private LruCache<String, Bitmap> memCache;
	//
	// private MyHandler mMyHandler;
	private ImageLoader mImgLoader;
	private ListView mListView;
	protected int mScreenHeight = 0;
	protected int mScreenWidth = 0;
	private List<SectionData> mSectionList;
	private TopicUtil mTopic;
	private RelativeLayout.LayoutParams Item_llp;
	private int mFirstVisibleItem;
	private int mVisibleItemCount;
	private boolean isFirstEnterThisActivity = true;

	public TopicPreviewAdapter(Context context, ListView listView, TopicUtil data) {
		super(context);
		mListView = listView;
		mTopic = data;
		mScreenHeight = DeviceInfo.scrrenHeight(context);
		Item_llp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, mScreenHeight / 3);
		mSectionList = data.contents;
		mListView.setOnScrollListener(this);
		mImgLoader = ImageLoader.Instance(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mSectionList != null ? mSectionList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mSectionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	// must add this method,always forget
	@Override
	public int getItemViewType(int position) {
		return mSectionList.get(position).getDataType() == 0 ? 0 : 1;
	}

	// must add this method,always forget
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(final int position, final View convertView, ViewGroup parent, ViewHolder holder) {
		final SectionData current = mSectionList.get(position);
		final String value = mSectionList.get(position).getmRealValue();
		final int type = mSectionList.get(position).getDataType();

		if (1 == type) {
			final ImageView imgItem = holder.obtainView(convertView, R.id.item_img_section);
			imgItem.setTag(value);
			setImageView(imgItem, value);

		} else {
			final TextView textItem = holder.obtainView(convertView, R.id.item_text_section);
			// textItem.setLayoutParams(Item_llp);
			textItem.setText(value);
		}
		return convertView;

	}

	private void setImageView(ImageView imgView, String path) {
		Bitmap bitmap = mImgLoader.loadBitmapFromMemCache(path);
		if (null != bitmap) {
			imgView.setImageBitmap(bitmap);
		} else {
			imgView.setImageResource(R.drawable.bg_defaut_01);
		}
		imgView.setLayoutParams(Item_llp);
	}

	@Override
	public int itemLayoutId(int position) {
		return mSectionList.get(position).getDataType() == 1 ? R.layout.item_img_topic_preview
				: R.layout.item_text_topic_preview;
	}

	/**
	 * 为GridView的item加载图片
	 * 
	 * @param firstVisibleItem
	 *            Listview中可见的第一张图片的下标
	 * @param visibleItemCount
	 *            Listview中可见的图片的数量
	 */
	private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
		final int headViewCount = mListView.getHeaderViewsCount();
		for (int i = firstVisibleItem + headViewCount; i < firstVisibleItem + visibleItemCount; i++) {
			String imagePath = mSectionList.get(i - headViewCount).getmRealValue();
			Bitmap bm = mImgLoader.loadBitmapFromLocal(imagePath, new ImageLoaderListener() {

				@Override
				public void onImageLoaded(Bitmap bitmap, String path) {
					// TODO Auto-generated method stub
					ImageView img = (ImageView) mListView.findViewWithTag(path);
					if (img != null) {
						img.setImageBitmap(bitmap);
					}
					Log.e("onImageLoaded", "path: " + path);
				}
			}, mScreenWidth, mScreenHeight);
			ImageView view = (ImageView) mListView.findViewWithTag(imagePath);
			if (null != view && null != bm) {
				view.setImageBitmap(bm);
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (scrollState == SCROLL_STATE_IDLE) {
			loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		if (isFirstEnterThisActivity && visibleItemCount > 0) {
			loadBitmaps(firstVisibleItem, visibleItemCount);
			isFirstEnterThisActivity = false;
		}
	}
}