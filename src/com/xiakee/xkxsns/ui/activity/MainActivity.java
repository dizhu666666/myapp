package com.xiakee.xkxsns.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.popupwindow.AbstractSpinerAdapter;
import com.android.popupwindow.CustemSpinerAdapter;
import com.android.popupwindow.SpinerPopWindow;
import com.android.util.ActivityLauncher;
import com.android.util.DeviceInfo;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.receiver.NetworkChangeReceiver;
import com.xiakee.xkxsns.ui.adapter.MyFMStatePagerAdapter;
import com.xiakee.xkxsns.ui.fragment.FragmentDiscover;
import com.xiakee.xkxsns.ui.fragment.FragmentMarket;
import com.xiakee.xkxsns.ui.fragment.FragmentMy;
import com.xiakee.xkxsns.ui.fragment.FragmentSNS;
import com.xiakee.xkxsns.util.LogUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements AbstractSpinerAdapter.IOnItemSelectListener {
	public static final String MESSAGE_RECEIVED_ACTION = MainActivity.class.getName();
	private static final Map<String, Integer> pushMessages = new HashMap<String, Integer>();// 推送消息集合
	public static final String KEY_MESSAGE = "msgContent";
	public static final String KEY_EXTRAS = "msgExtras";

	private ImageView mTabDiscover;
	private ImageView mTabMarket;
	private ImageView mTabSNS;
	private ImageView mTabMy;
	private MyPushMessageReceiver mPushMessageReceiver;// 自定义消息推送广播接受者
	private NetworkChangeReceiver mNetworkChangeReceiver;
	
	// title part

	private TextView mLeftAction;

	private List<Fragment> mFmList;

	private ViewPager mViewPager;
	private View mTabsView;
	private int currentPage;

	private double gps_lo;
	private double gps_la;
	// popwindow
	private SpinerPopWindow mSpinerPopWindow;
	private String[] ItemsEntry;
	private AbstractSpinerAdapter mAdapter;

	private void handleTabsChnaged(int index) {
		switch (index) {
		case 0: {
			mTabDiscover.setSelected(true);
			mTabMarket.setSelected(false);
			mTabSNS.setSelected(false);
			mTabMy.setSelected(false);
			// updateTitle(R.string.discovery);
			break;
		}
		case 1: {
			mTabDiscover.setSelected(false);
			mTabMarket.setSelected(true);
			mTabSNS.setSelected(false);
			mTabMy.setSelected(false);
			// updateTitle(R.string.market);
			break;
		}
		case 2: {
			mTabDiscover.setSelected(false);
			mTabMarket.setSelected(false);
			mTabSNS.setSelected(true);
			mTabMy.setSelected(false);
			// updateTitle(R.string.xiakee_sns);
			break;
		}
		case 3: {
			mTabDiscover.setSelected(false);
			mTabMarket.setSelected(false);
			mTabSNS.setSelected(false);
			mTabMy.setSelected(true);
			// updateTitle(R.string.my);
			break;
		}
		default: {
			break;
		}
		}
		currentPage = index;
		mViewPager.setCurrentItem(index, false);
	}

	private void initBottomTabs(View view) {
		mTabsView = view.findViewById(R.id.view_bottom_tabs);
		mTabDiscover = (ImageView) mTabsView.findViewById(R.id.discover);
		mTabDiscover.setSelected(true);
		mTabDiscover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				handleTabsChnaged(0);
			}
		});
		/********************************************************************/
		mTabMarket = (ImageView) mTabsView.findViewById(R.id.market);
		mTabMarket.setSelected(false);
		mTabMarket.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				handleTabsChnaged(1);
			}
		});
		/********************************************************************/
		mTabSNS = (ImageView) mTabsView.findViewById(R.id.sns);
		mTabSNS.setSelected(false);
		mTabSNS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				handleTabsChnaged(2);
			}
		});
		/********************************************************************/

		mTabMy = (ImageView) mTabsView.findViewById(R.id.my);
		mTabMy.setSelected(false);
		mTabMy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				handleTabsChnaged(3);
			}
		});
		/********************************************************************/
	}

	private void initFmContainer(View view, double lo, double la) {
		mFmList = new ArrayList<Fragment>();
		initSortFmContainer(view, lo, la);
		mViewPager = (ViewPager) view.findViewById(R.id.fm_viewpager);
		mViewPager.setAdapter(new MyFMStatePagerAdapter(getSupportFragmentManager(), mFmList));
		mViewPager.setCurrentItem(0);
		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				// handleTabsChnaged(arg0);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				handleTabsChnaged(arg0);
			}
		});
	}

	private void initSortFmContainer(View view, double lo, double la) {
		// for (int i = 0; i < 4; ++i) {
		// mFmList.add(new FragmentSNS(this, lo, la));
		// }
		mFmList.add(new FragmentDiscover(this));
		mFmList.add(new FragmentMarket(this));
		mFmList.add(new FragmentSNS(this, lo, la));
		mFmList.add(new FragmentMy());
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final LayoutInflater inflater = LayoutInflater.from(this);
		final View view = inflater.inflate(R.layout.activity_main, null);
		initBottomTabs(view);
		initFmContainer(view, gps_lo, gps_la);
		setContentView(view);
		initPopWindow();
//		updateTitle(R.drawable.logo_red);
		mViewPager.setOffscreenPageLimit(2);

		mPushMessageReceiver = new MyPushMessageReceiver();
		IntentFilter filter0 = new IntentFilter();
		filter0.addAction(MainActivity.MESSAGE_RECEIVED_ACTION);
		registerReceiver(mPushMessageReceiver, filter0);
		 //UserManager.checkLogin(this);
        mNetworkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkChangeReceiver, filter1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPushMessageReceiver != null) {
            unregisterReceiver(mPushMessageReceiver);
        }
        if (mNetworkChangeReceiver != null) {
            unregisterReceiver(mNetworkChangeReceiver);
        }
        // TODO: static have to be clean here s
    }

	@Override
	protected void onResume() {
		updateTitle(R.drawable.logo_red);
		super.onResume();
		LogUtils.e("main onResume");
		for (int x = 0; x < pushMessages.size();) {
		}

	}

	private void updateTitle(int resId) {
		mTitleBar = getTitleBar();
		if (mTitleBar != null) {
			// mTitleBar.setTitle(resId);
			mTitleBar.setImageTitle(resId);
			mLeftAction = mTitleBar.showLeftAction(R.drawable.message_notifications);
			mLeftAction.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showSpinWindow();
				}
			});
		}
	}

	private void showSpinWindow() {
		Log.e("", "showSpinWindow");
		// mSpinerPopWindow.setWidth(mTView.getWidth());
		// mSpinerPopWindow.showAsDropDown(mTView); //显示在下面
		mSpinerPopWindow.setWidth(DeviceInfo.scrrenWidth(this) / 3);
		mSpinerPopWindow.showAsDropDown(mLeftAction);
	}

	private void initPopWindow() {

		ItemsEntry = getResources().getStringArray(R.array.entrance_popwindow);
		mAdapter = new CustemSpinerAdapter(this);
		mAdapter.refreshData(ItemsEntry, 0);
		mSpinerPopWindow = new SpinerPopWindow(this);
		mSpinerPopWindow.setAdatper(mAdapter);
		mSpinerPopWindow.setItemListener(this);
	}

	@Override
	public void onItemClick(int pos) {
		// TODO Auto-generated method stub
		switch (pos) {
		case 0: {
			final Intent intent = new Intent();
			ActivityLauncher.startReplyMeActivity(this, intent);
			break;
		}
		case 1: {
			break;
		}

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.e("MainActivity onActivityResult ：" + currentPage);
		mFmList.get(currentPage).onActivityResult(requestCode, resultCode, data);
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// private void saveDraftDialog() {
	// final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
	// final View view =
	// LayoutInflater.from(this).inflate(R.layout.view_dialog_save_draft, null);
	// final TextView yes = (TextView) view.findViewById(R.id.draft_yes);
	// final TextView no = (TextView) view.findViewById(R.id.draft_no);
	// dialog.setCancelable(false);
	// no.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// dialog.dismiss();
	//// TopicUtil.removewTopic(MainActivity.this, typeId);
	// MainActivity.this.finish();
	// }
	// });
	// yes.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	//// TopicUtil.saveTopic(PostActivity.this, genJsonObject(), typeId);
	// dialog.dismiss();
	// MainActivity.this.finish();
	// }
	// });
	// // set up the custom view width.
	// view.setMinimumWidth((int) (0.6 * DeviceInfo.scrrenWidth(this)));
	// dialog.setContentView(view);
	// dialog.show();
	// }

	/***
	 * 推送消息广播接受者
	 */
	private class MyPushMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				pushMessages.put("", pushMessages.get("") + 1);

			}
		}
	}
}
