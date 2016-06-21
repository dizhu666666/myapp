package com.android.photo.adapter;

import java.util.List;

import com.android.photo.util.BitmapCache;
import com.android.photo.util.BitmapCache.ImageCallback;
import com.android.photo.util.ImageItem;
import com.android.photo.util.Res;
import com.android.util.DeviceInfo;
import com.xiakee.xkxsns.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 这个是显示一个文件夹里面的所有图片时用的适配器
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:49:35
 */
public class AlbumGridViewAdapter extends BaseAdapter {
	final String TAG = getClass().getSimpleName();
	private Context mContext;
	private List<ImageItem> dataList;
	private List<ImageItem> selectedDataList;
	private DisplayMetrics dm;
	// private int currentPosition;
	BitmapCache cache;
	private RelativeLayout.LayoutParams mLLp;

	public AlbumGridViewAdapter(Context c, List<ImageItem> dataList, List<ImageItem> selectedDataList) {
		mContext = c;
		cache = new BitmapCache();
		this.dataList = dataList;
		this.selectedDataList = selectedDataList;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int targetLen = DeviceInfo.scrrenWidth(mContext) / 4;
		mLLp = new RelativeLayout.LayoutParams(targetLen, targetLen);
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals(imageView.getTag())) {
					imageView.setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};

	/**
	 * 存放列表项控件句柄
	 */
	private class ViewHolder {
		public ImageView imgView;
		public ImageView imgBG;
		public ImageButton selectToggle;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		// currentPosition = position;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.plugin_camera_select_imageview, null);
			viewHolder.imgView = (ImageView) convertView.findViewById(R.id.item_image);
			viewHolder.imgView.setLayoutParams(mLLp);
			viewHolder.imgBG = (ImageView) convertView.findViewById(Res.getWidgetID("item_image_bg"));
			viewHolder.imgBG.setLayoutParams(mLLp);
			viewHolder.selectToggle = (ImageButton) convertView.findViewById(R.id.item_select);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String path;
		if (dataList != null && dataList.size() > position)
			path = dataList.get(position).imagePath;
		else
			path = "camera_default";
		if (path.contains("camera_default")) {
			viewHolder.imgView.setImageResource(Res.getDrawableID("plugin_camera_no_pictures"));
		} else {
			final ImageItem item = dataList.get(position);
			viewHolder.imgView.setTag(item.imagePath);
			cache.displayBmp(viewHolder.imgView, item.thumbnailPath, item.imagePath, callback);
		}
		viewHolder.imgBG.setTag(position);
		// viewHolder.selectToggle.setTag(position);

		viewHolder.imgBG.setOnClickListener(new ToggleClickListener(viewHolder.selectToggle));
		// viewHolder.selectToggle.setOnClickListener(new
		// ToggleClickListener());
		if (selectedDataList.contains(dataList.get(position))) {
			viewHolder.selectToggle.setSelected(true);
			// viewHolder.imgView.setSelected(true);
			// viewHolder.selectToggle.setVisibility(View.VISIBLE);
		} else {
			// viewHolder.imgView.setSelected(false);
			viewHolder.selectToggle.setSelected(false);
		}
		return convertView;
	}

	public int dipToPx(int dip) {
		return (int) (dip * dm.density + 0.5f);
	}

	private class ToggleClickListener implements OnClickListener {
		ImageButton selectToggle;

		public ToggleClickListener(ImageButton selectToggle) {
			this.selectToggle = selectToggle;
		}

		@Override
		public void onClick(View view) {
			if (view instanceof ImageView) {
				ImageView imgView = (ImageView) view;
				selectToggle.setSelected(!selectToggle.isSelected());
				// ImageView imgView = (ImageView) view;
				int position = Integer.valueOf(imgView.getTag().toString());
				// int position = (Integer) selectToggle.getTag();
				if (dataList != null && mOnItemClickListener != null && position < dataList.size()) {
					mOnItemClickListener.onItemClick(position, selectToggle.isSelected(), selectToggle);
				}
			}
		}
	}

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}

	public interface OnItemClickListener {
		public void onItemClick(int position, boolean isSelected, ImageButton selectToggle);
	}

}
