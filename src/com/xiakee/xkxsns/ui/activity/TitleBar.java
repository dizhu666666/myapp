package com.xiakee.xkxsns.ui.activity;

import com.xiakee.xkxsns.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TitleBar extends LinearLayout {

	public static final int TYPE_BACKACTION_ACTIVITY = 0;
	public static final int TYPE_BACKACTION_CUSTOM = 2;
	public static final int TYPE_BACKACTION_FRAGMENT = 1;
	private int mBackAction = TYPE_BACKACTION_ACTIVITY;
	private BaseActivity mBaseActivity;
	private TextView mLeftAction; // left
	private TextView mCenterTitle; // Middle
	private TextView mRightAction; // Right
	private TextView mPreRightAction;// Right_pre

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mBaseActivity = (BaseActivity) context;
	}

	public TextView getLeftAction() {
		return mLeftAction;
	}

	public TextView getRCustomAction() {
		return mRightAction;
	}

	public TextView getR_preCustomAction() {
		return mPreRightAction;
	}

	public void hideBackButton() {
		if (mLeftAction != null) {
			mLeftAction.setVisibility(View.GONE);
		}
	}

	public void hideCustomAction() {
		if (mRightAction != null) {
			mRightAction.setVisibility(View.INVISIBLE);
		}
	}

	public void hidePreRightAction() {
		if (mPreRightAction != null) {
			mPreRightAction.setVisibility(View.INVISIBLE);
		}
	}

	public void hideSpinnerButton() {
		hideBackButton();
	}

	public void setBackAction(final int type) {
		mBackAction = type;
	}

	// Title set up
	public void setTitle(final CharSequence charSequence) {
		if (mCenterTitle != null) {
			mCenterTitle.setText(charSequence);
		}
	}

	public void setTitle(int resid) {
		if (mCenterTitle != null) {
			mCenterTitle.setText(resid);
		}
	}

	public void setImageTitle(int resI) {
		if (mCenterTitle != null) {
			mCenterTitle.setBackgroundResource(resI);
		}
	}

	public void initBackAction() {
		if (mLeftAction != null) {
			mLeftAction.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					switch (mBackAction) {
					case TYPE_BACKACTION_ACTIVITY: {
						mBaseActivity.finish();
						break;
					}
					case TYPE_BACKACTION_FRAGMENT: {
						mBaseActivity.getSupportFragmentManager().popBackStack();
						break;
					}
					case TYPE_BACKACTION_CUSTOM: {
						break;
					}
					}
				}
			});
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mLeftAction = (TextView) findViewById(R.id.title_left_actions);
		mCenterTitle = (TextView) findViewById(R.id.title_center);
		mRightAction = (TextView) findViewById(R.id.title_right_actions);
		mPreRightAction = (TextView) findViewById(R.id.title_pre_right_actions);
		initBackAction();
	}

	/*******************************************************************************************/
	// back btn setup
	@SuppressLint("NewApi")
	public TextView showLeftAction(int drawable_id) {

		if (mLeftAction != null) {
			mLeftAction.setVisibility(View.VISIBLE);
			Drawable drawable = null;
			try {
				drawable = getResources().getDrawable(drawable_id);
			} catch (NotFoundException nfe) {
				nfe.printStackTrace();
				drawable = null;
			}
			mLeftAction.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
		}
		return mLeftAction;
	}

	// back btn setup
	@SuppressLint("NewApi")
	public TextView showLeftAction(int drawable_id, int String_id) {

		if (mLeftAction != null) {
			mLeftAction.setVisibility(View.VISIBLE);
			mLeftAction.setText(mBaseActivity.getString(String_id));
			Drawable drawable = null;
			try {
				drawable = getResources().getDrawable(drawable_id);
			} catch (NotFoundException nfe) {
				nfe.printStackTrace();
				drawable = null;
			}
			mLeftAction.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
		}
		return mLeftAction;
	}

	@SuppressLint("NewApi")
	public TextView showLeftAction(int drawable_id, String content) {
		if (mLeftAction != null) {
			mLeftAction.setVisibility(View.VISIBLE);
			mLeftAction.setText(content);
			Drawable drawable = null;
			try {
				drawable = getResources().getDrawable(drawable_id);
			} catch (NotFoundException nfe) {
				nfe.printStackTrace();
				drawable = null;
			}
			mLeftAction.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
		}
		return mLeftAction;
	}

	/*******************************************************************************************/

	@SuppressLint("NewApi")
	public TextView showRightAction(String content, int drawableId) {
		if (mRightAction != null) {
			mRightAction.setVisibility(View.VISIBLE);
			mRightAction.setText(content);
			Drawable drawable = null;
			try {
				drawable = getResources().getDrawable(drawableId);
			} catch (NotFoundException nfe) {
				nfe.printStackTrace();
				drawable = null;
			}
			mRightAction.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
		}
		return mRightAction;
	}

	@SuppressLint("NewApi")
	public TextView showRightAction(int stringId, int drawableId) {
		if (mRightAction != null) {
			mRightAction.setVisibility(View.VISIBLE);
			mRightAction.setText(getResources().getString(stringId));
			Drawable drawable = null;
			try {
				drawable = getResources().getDrawable(drawableId);
			} catch (NotFoundException nfe) {
				drawable = null;
			}
			mRightAction.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
		}
		return mRightAction;
	}

	/*******************************************************************************************/
	@SuppressLint("NewApi")
	public TextView showPreRightAction(String content, int drawableId) {
		if (mPreRightAction != null) {
			mPreRightAction.setVisibility(View.VISIBLE);
			mPreRightAction.setText(content);
			Drawable drawable = null;
			try {
				drawable = getResources().getDrawable(drawableId);
			} catch (NotFoundException nfe) {
				nfe.printStackTrace();
				drawable = null;
			}
			mPreRightAction.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
		}
		return mPreRightAction;
	}

	@SuppressLint("NewApi")
	public TextView showPreRightAction(int stringId, int drawableId) {
		if (mPreRightAction != null) {
			mPreRightAction.setVisibility(View.VISIBLE);
			mPreRightAction.setText(getResources().getString(stringId));
			Drawable drawable = null;
			try {
				drawable = getResources().getDrawable(drawableId);
			} catch (NotFoundException nfe) {
				drawable = null;
			}
			mPreRightAction.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
		}
		return mPreRightAction;
	}

	public void showCustomAction_Text(int String_id) {
		if (mRightAction != null) {
			// mRcustomAction.setVisibility(View.VISIBLE);
			mRightAction.setText(mBaseActivity.getString(String_id));
			// mRcustomAction.setClickable(true);
		}
	}

	// Action btn setup
	public void showCustomAction_Text(String text) {
		if (mRightAction != null) {
			// mRcustomAction.setVisibility(View.VISIBLE);
			mRightAction.setText(text);
			// mRcustomAction.setClickable(true);
		}
	}

	public void setRightActionOnClickListener(OnClickListener listener) {
		if (mRightAction != null) {
			mRightAction.setOnClickListener(listener);
		}
	}

	public void showSpinnerButton(int String_id, int drawable_id) {
		if (mLeftAction != null) {
			// mLcustomAction.setVisibility(View.VISIBLE);
			mLeftAction.setText(mBaseActivity.getString(String_id));
			Drawable drawable = getResources().getDrawable(drawable_id);
			mLeftAction.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
			// mLcustomAction.setClickable(true);
		}
	}

	public void showSpinnerButton(String content, Drawable drawable) {
		if (mLeftAction != null) {
			// mLcustomAction.setVisibility(View.VISIBLE);
			mLeftAction.setText(content);
			mLeftAction.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
			// mLcustomAction.setClickable(true);
		}
	}

	public void showSpinnerButton(String content, int drawable_id) {
		Drawable drawable = getResources().getDrawable(drawable_id);
		showSpinnerButton(content, drawable);
	}

	public void showSpinnerButton(String content, int drawable_id, boolean left) {
		if (mLeftAction != null) {
			// mLcustomAction.setVisibility(View.VISIBLE);
			mLeftAction.setText(content);
			Drawable drawable = getResources().getDrawable(drawable_id);
			mLeftAction.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
			// mLcustomAction.setClickable(true);
		}
	}

}