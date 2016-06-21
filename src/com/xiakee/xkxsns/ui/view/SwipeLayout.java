package com.xiakee.xkxsns.ui.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.util.LogUtils;

public class SwipeLayout extends FrameLayout {

    public enum SwipeState {
        Open, Close, Delete
    }

    public enum BgState {
        Pressed, Normal
    }

    private ViewDragHelper viewDragHelper;

    private View contentView;// 内容view
    private View deleteView;// 删除view
    private int contentWidth;// 内容的宽
    private int deleteWidth;// 删除的宽
    private int deleteHeight;// 删除的高度

    private int pressedBg = R.color.lavenderblush;//按下时的背景
    private int normalBg = R.color.white;//默认背景

    private SwipeState mState = SwipeState.Close;//默认是关闭状态
    private BgState mBgState = BgState.Normal;//背景默认是Normal

    public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
        setBackgroundColor(getResources().getColor(normalBg));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        deleteView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        contentWidth = contentView.getMeasuredWidth();
        deleteWidth = deleteView.getMeasuredWidth();
        deleteHeight = deleteView.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
//		super.onLayout(changed, left, top, right, bottom);
        contentView.layout(0, 0, right, bottom);
        deleteView.layout(right, 0, right + deleteWidth, bottom);
//		contentView.layout(0, 0, contentWidth, deleteHeight);
//		deleteView.layout(contentWidth, 0, contentWidth + deleteWidth,
//				deleteHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    private float downX, downY, mDownX, mDownY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mState == SwipeState.Close) {
                    setBackgroundColor(getResources().getColor(pressedBg));
                    mBgState = BgState.Pressed;
                }

                mDownX = event.getX();
                mDownY = event.getY();

                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                float moveX = event.getX();
                float moveY = event.getY();
                float deltaX = moveX - downX;//x方向滑动的距离
                float deltaY = moveY - downY;//y方向滑动的距离

                if (mBgState == BgState.Pressed) {
                    float mDeltaX = moveX - mDownX;
                    float mDeltaY = moveY - mDownY;
                    if (Math.abs(mDeltaX) > 5 || Math.abs(mDeltaY) > 5) {
                        setBackgroundColor(getResources().getColor(normalBg));
                        mBgState = BgState.Normal;
                    }
                }

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    //滑动的方向偏向于水平方向,此时不应该让listView滑动(接收事件)
                    requestDisallowInterceptTouchEvent(true);//请求父view不拦截事件
                }


                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                if (mBgState == BgState.Pressed) {
                    setBackgroundColor(getResources().getColor(normalBg));
                    mBgState = BgState.Normal;
                }
                float upX = event.getX();
                float upY = event.getY();
                if (mState == SwipeState.Open) {
                    if ((upX - mDownX == 0) && (upY - mDownY == 0)) {
                        smoothClose();
                    }
                } else if (mState == SwipeState.Close) {
                    if ((upX - mDownX == 0) && (upY - mDownY == 0)) {
                        if (clickListener != null) {
                            clickListener.onSwipeClick(SwipeLayout.this);
                        }

                    }
                }

                break;
        }
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback = new Callback() {
        @Override
        public boolean tryCaptureView(View child, int arg1) {
            return child == contentView || child == deleteView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return deleteWidth;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                if (left > 0) left = 0;
                if (left < -deleteWidth) left = -deleteWidth;
            } else if (child == deleteView) {
                if (left < (contentWidth - deleteWidth)) left = contentWidth - deleteWidth;
                if (left > contentWidth) left = contentWidth;
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            LogUtils.e("onViewPositionChanged");
            if (changedView == contentView) {
                deleteView.layout(deleteView.getLeft() + dx, deleteView.getTop(), deleteView.getRight() + dx, deleteView.getBottom());
            } else if (changedView == deleteView) {
                contentView.layout(contentView.getLeft() + dx, contentView.getTop(), contentView.getRight() + dx, contentView.getBottom());
            }

            //LogUtils.e("contentView.getLeft()：" + contentView.getLeft());
            //改变状态
            if (contentView.getLeft() == mDelRate * contentWidth && mState != SwipeState.Delete) {
                mState = SwipeState.Delete;
                if (listener != null) {
                    listener.onDel(SwipeLayout.this);
                }
            } else if (contentView.getLeft() == 0 && mState != SwipeState.Close) {
                mState = SwipeState.Close;
                if (listener != null) {
                    listener.onClose(SwipeLayout.this);
                }
            } else if (contentView.getLeft() == -deleteWidth && mState != SwipeState.Open) {
                mState = SwipeState.Open;
                if (listener != null) {
                    listener.onOpen(SwipeLayout.this);
                }
            } else if (contentView.getLeft() > -deleteWidth && contentView.getLeft() < 0) {
                if (mState == SwipeState.Close) {
                    //则表示准备打开
                    if (listener != null) {
                        listener.onStartOpen(SwipeLayout.this);
                    }
                } else if (mState == SwipeState.Open) {
                    //则表示准备关闭
                    if (listener != null) {
                        listener.onStartClose(SwipeLayout.this);
                    }
                }
            }

        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (contentView.getLeft() > -deleteWidth / 2) {
                //close
                smoothClose();
            } else {
                //open
                smoothOpen();
            }
        }

    };

    /**
     * 平滑关闭
     */
    public void smoothClose() {
        viewDragHelper.smoothSlideViewTo(contentView, 0, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    private int mDelRate = 1;

    /***
     * 平滑删除
     *
     * @param delRate 值越大，滑动速度越快
     */
    public void smoothDel(int delRate) {
        mDelRate = delRate;
        viewDragHelper.smoothSlideViewTo(contentView, mDelRate * contentWidth, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    /**
     * 平滑打开
     */
    public void smoothOpen() {
        viewDragHelper.smoothSlideViewTo(contentView, -deleteWidth, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    /**
     * 快速打开
     */
    public void fastOpen() {
        contentView.layout(-deleteWidth, contentView.getTop(), -deleteWidth + contentWidth, contentView.getBottom());
        deleteView.layout(-deleteWidth + contentWidth, deleteView.getTop(), contentWidth, deleteView.getBottom());
        Log.e("tag", "fastOpen : " + contentView.getLeft());
    }

    /**
     * 快速关闭
     */
    public void fastClose() {
        contentView.layout(0, 0, contentWidth, deleteHeight);
        deleteView.layout(contentWidth, 0, contentWidth + deleteWidth,
                deleteHeight);
    }


    @Override
	public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        }
    }

    private OnSwipeStateChangeListener listener;

    public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener listener) {
        this.listener = listener;
    }

    private OnSwipeClickListener clickListener;

    public void setOnSwipeClickListener(OnSwipeClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnSwipeClickListener {
        void onSwipeClick(SwipeLayout swipeLayout);
    }

    public interface OnSwipeStateChangeListener {
        void onClose(SwipeLayout swipeLayout);

        void onOpen(SwipeLayout swipeLayout);

        void onDel(SwipeLayout swipeLayout);

        /**
         * 准备关闭
         */
        void onStartClose(SwipeLayout swipeLayout);

        /**
         * 准备打开
         */
        void onStartOpen(SwipeLayout swipeLayout);
    }
}
