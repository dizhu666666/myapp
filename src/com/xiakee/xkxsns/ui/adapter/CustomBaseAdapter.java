package com.xiakee.xkxsns.ui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 实现对BaseAdapter中ViewHolder相关的简化 Created with IntelliJ IDEA. Author: wangjie
 * email:tiantian.china.2@gmail.com Date: 14-4-2 Time: 下午5:54
 */
public abstract class CustomBaseAdapter
		extends BaseAdapter /* implements OnScrollListener */ {
	// added by william

	/**
	 * 各个控件的缓存
	 */
	public class ViewHolder {

		public int mIndex;

//		public ViewHolder() {
//		}

//		public ViewHolder(int index) {
//			mIndex = index;
//		}

		public SparseArray<View> mViews = new SparseArray<View>();

		// public List<View> mViews = new ArrayList<View>();

		/**
		 * 指定resId和类型即可获取到相应的view
		 * 
		 * @param convertView
		 * @param resId
		 * @param <T>
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public <T extends View> T obtainView(View convertView, int sub_resId) {

			// Log.e("obtainView: ",
			// "convertView.getId()--"+convertView.getId()+" sub_resId: " +
			// sub_resId);
			View v = mViews.get(sub_resId);// 如果已經存在，就直接取之使用

			if (null == v) {// 如果第一次創建，就緩存起來。
				v = convertView.findViewById(sub_resId);
				mViews.put(sub_resId, v);
			}
			return (T) v;
		}

	}

	protected Context mContext;

	protected CustomBaseAdapter() {
	}

	protected CustomBaseAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(itemLayoutId(position), null);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		return getView(position, convertView, parent, holder);
	}

	/**
	 * 使用该getView方法替换原来的getView方法，需要子类实现
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @param holder
	 * @return
	 */
	public abstract View getView(int position, View convertView, ViewGroup parent, ViewHolder holder);

	public abstract int itemLayoutId(int position);

}