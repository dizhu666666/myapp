package com.xiakee.xkxsns.ui.activity;

import com.android.util.DeviceInfo;
import com.android.util.ImageUtil;
import com.android.util.SNSAPI;
import com.android.util.StringUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.FocusState;
import com.xiakee.xkxsns.bean.UserSpaceInfo;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.model.ImageLoader;
import com.xiakee.xkxsns.ui.adapter.UserSpacePagerAdapter;
import com.xiakee.xkxsns.ui.view.PagerSlidingTabStrip;
import com.xiakee.xkxsns.util.ToastUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 编辑发帖页面activity
 *
 * @author william
 * @version 2015年10月25日 下午11:48:34
 */
public class UserSpaceActivity extends FragmentActivity implements View.OnClickListener {

	private int mUserId;

	@Bind(R.id.iv_icon)
	ImageView ivIcon;
	@Bind(R.id.tv_nickname)
	TextView tvNickname;
	@Bind(R.id.tv_level)
	TextView tvLevel;
	@Bind(R.id.iv_add_attention)
	ImageView ivAddAttention;
	@Bind(R.id.iv_back)
	ImageView ivBack;

	@Bind(R.id.tabs)
	PagerSlidingTabStrip tabs;
	@Bind(R.id.vp_pager)
	ViewPager vpPager;

	@Bind(R.id.rl_root_user)
	RelativeLayout rlUserRoot;

	@Bind(R.id.outer)
	ScrollView outer;
	@Bind(R.id.fl_tabs)
	FrameLayout flTabs;

	private UserSpacePagerAdapter mPagerAdapter;
	private UserSpaceInfo mData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserId = getIntent().getIntExtra(SNSAPI.USERID, -1);
		if (mUserId == -1) {
			return;
		}
		setContentView(R.layout.activity_userspace);
		ButterKnife.bind(this);

		final View view = outer;
		view.post(new Runnable() {
			@Override
			public void run() {
				ViewGroup.LayoutParams containerparams = vpPager.getLayoutParams();
				containerparams.height = outer.getHeight() - flTabs.getHeight();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					containerparams.height -= DeviceInfo.getStatusHeight(UserSpaceActivity.this);
				}
				vpPager.setLayoutParams(containerparams);
			}
		});

		RelativeLayout.LayoutParams params = (LayoutParams) ivAddAttention.getLayoutParams();
		params.topMargin = DeviceInfo.getStatusHeight(this) + params.topMargin;
		ivAddAttention.setOnClickListener(this);

		RelativeLayout.LayoutParams params1 = (LayoutParams) ivBack.getLayoutParams();
		params1.topMargin = DeviceInfo.getStatusHeight(this) + params1.topMargin;
		ivBack.setOnClickListener(this);

		setTranslucentStatus();

		adjustPosition(rlUserRoot);
		// http://127.0.0.1/comm/person/info?loginUserId=1898&topicUserId=1898
		Ion.with(this).load(HttpUrl.GET_USER_SPACE_INFO).setBodyParameter("loginUserId", UserManager.getLoginUserId())
				.setBodyParameter("topicUserId", String.valueOf(mUserId)).as(UserSpaceInfo.class)
				.setCallback(new FutureCallback<UserSpaceInfo>() {
					@Override
					public void onCompleted(Exception e, UserSpaceInfo userSpaceInfo) {
						if (null != userSpaceInfo) {
							mData = userSpaceInfo;
							final int width = DeviceInfo.scrrenWidth(UserSpaceActivity.this);
							ImageLoader imageLoader = ImageLoader.Instance(UserSpaceActivity.this);
							Bitmap bitmap = imageLoader.loadBitmap(SNSAPI.Test_BASE_URL + mData.photo,
									new ImageLoader.ImageLoaderListener() {
								@Override
								public void onImageLoaded(Bitmap bitmap, String url) {
									ivIcon.setImageBitmap(
											ImageUtil.getRoundImage(UserSpaceActivity.this, bitmap, width / 4));
									blur(rlUserRoot, bitmap, 100);
								}
							}, width / 4, width / 4);

							if (bitmap != null) {
								ivIcon.setImageBitmap(
										ImageUtil.getRoundImage(UserSpaceActivity.this, bitmap, width / 4));
								blur(rlUserRoot, bitmap, 100);
							}
							tvNickname.setText(mData.userName);
							tvLevel.setText(String.format(getString(R.string.level), mData.lv));
							tvLevel.setSelected("1".equals(mData.sex));
							ivAddAttention.setSelected(1 == mData.focusStatus);
							// 用户信息加载完毕，再初始化viewPager
							initViewPager();
						}
					}
				});

	}

	/**
	 * 设置状态栏背景状态
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void setTranslucentStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);

			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			// 激活状态栏设置
			tintManager.setStatusBarTintEnabled(true);
			// 激活导航栏设置
			// tintManager.setStatusBarTintColor(getResources().getColor(R.color.red));
			tintManager.setStatusBarTintResource(0);// 状态栏无背景
			tintManager.setNavigationBarTintEnabled(true);

			// win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		// 创建状态栏的管理实例
		// SystemBarTintManager tintManager = new SystemBarTintManager(this);
		// 激活状态栏设置
		// tintManager.setStatusBarTintEnabled(true);
		// 激活导航栏设置
		// tintManager.setStatusBarTintColor(getResources().getColor(R.color.red));
		// tintManager.setNavigationBarTintEnabled(true);
		// tintManager.setStatusBarTintResource(0);// 状态栏无背景
		// tintManager.setStatusBarTintDrawable(get);
	}

	private void adjustPosition(RelativeLayout root) {
		// final View adJustview =
		// parent.findViewById(R.id.back_follow_control);
		// root.getLayoutParams()
		LinearLayout.LayoutParams llp = (android.widget.LinearLayout.LayoutParams) root.getLayoutParams();

		llp.height = DeviceInfo.scrrenHeight(this) / 2;
		/*
		 * new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.
		 * WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		 */
		llp.setMargins(0, -DeviceInfo.getStatusHeight(this), 0, 0);

		root.setLayoutParams(llp);
	}

	private void initViewPager() {
		mPagerAdapter = new UserSpacePagerAdapter(getSupportFragmentManager(), mData, outer);
		vpPager.setAdapter(mPagerAdapter);
		tabs.setViewPager(vpPager);
		vpPager.setCurrentItem(1);
	}

	/***
	 * 模糊背景
	 *
	 * @param view
	 * @param bm
	 * @param radius
	 */
	private void blur(View view, Bitmap bm, int radius) {

		Bitmap blurBm = null;
		if (StringUtils.getAPIVersion() >= 17) {
			blurBm = ImageUtil.blurBitmap(this, bm);
		} else {
			blurBm = ImageUtil.doBlur(bm, 50, false);
		}
		view.setBackgroundDrawable(new BitmapDrawable(this.getResources(), blurBm));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.iv_icon:
			// 查看大头像
			startActivity(new Intent(this, BigPhotoActivity.class).putExtra("photoUrl", mData.photo));
			break;

		case R.id.iv_back:
			this.finish();
			break;

		case R.id.iv_add_attention:
			if (!UserManager.isLogin()) {
				this.startActivity(new Intent(this, LoginActivity.class));
				return;
			}

			if (UserManager.getLoginUserId().equals(String.valueOf(mUserId))) {
				ToastUtils.showToast("你不能关注自己哦！");
				return;
			}
			ivAddAttention.setSelected(!ivAddAttention.isSelected());
			Ion.with(this).load(HttpUrl.FOCUS_OR_CANCEL).setBodyParameter("loginUserId", UserManager.getLoginUserId())
					.setBodyParameter("token", UserManager.getToken())
					.setBodyParameter("focusUserId", String.valueOf(mUserId)).as(FocusState.class)
					.setCallback(new FutureCallback<FocusState>() {
						@Override
						public void onCompleted(Exception e, FocusState focusState) {
							String msg = "你不能关注自己哦！";
							if (null != focusState) {
								// 1关注成功 0取消关注成功
								if ("0".equals(focusState.focusStatus)) {
									// holder.ivFocusState.setImageResource(R.drawable.add_focus);//图片设置为添加关注
									msg = getString(R.string.cancel_focus_succeed);
								} else if ("1".equals(focusState.focusStatus)) {
									// 图片设置为已关注，如果是双向关注，显示双向关注的图片
									// holder.ivFocusState.setImageResource(R.drawable.selector_focus);
									// holder.ivFocusState.setSelected(data.isMutualFocus());
									msg = getString(R.string.add_focus_succeed);
								}
								ToastUtils.showToast(msg);
							}

						}

					});
		}
	}
}
