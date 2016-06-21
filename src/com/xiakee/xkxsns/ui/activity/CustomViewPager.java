package com.xiakee.xkxsns.ui.activity;

import android.content.Context;
import android.support.v4.view.ViewPager;

/**
 * 不可以滑动，但是可以setCurrentItem的ViewPager。
 */
public class CustomViewPager extends ViewPager {

	public CustomViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	/*  private boolean scrollble=true;

	    public CustomViewPager (Context context){
	        super(context);
	    }

	    public CustomViewPager(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }


	    @Override
	    public boolean onTouchEvent(MotionEvent ev) {
	        if (!scrollble) {
	            return true;
	        }
	        return super.onTouchEvent(ev);
	    }


	    public boolean isScrollble() {
	        return scrollble;
	    }

	    public void setScrollble(boolean scrollble) {
	        this.scrollble = scrollble;
	    }*/
}
