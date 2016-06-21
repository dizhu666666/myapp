package com.android.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.ScrollView;

public class InnerListView extends ListView {

	ScrollView parentScrollView;

	private int maxHeight;

	public InnerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public ScrollView getParentScrollView() {
		return parentScrollView;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:

			setParentScrollAble(false);
		case MotionEvent.ACTION_MOVE:

			break;
		case MotionEvent.ACTION_UP:

		case MotionEvent.ACTION_CANCEL:
			setParentScrollAble(true);
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub

		if (maxHeight > -1) {

			heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight,
					MeasureSpec.AT_MOST);

		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		System.out.println(getChildAt(0));
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	/**
	 * @param flag
	 */
	private void setParentScrollAble(boolean flag) {

		parentScrollView.requestDisallowInterceptTouchEvent(!flag);
	}

	public void setParentScrollView(ScrollView parentScrollView) {
		this.parentScrollView = parentScrollView;
	}

}
