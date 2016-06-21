package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyFMStatePagerAdapter extends FragmentStatePagerAdapter {

	private List<Fragment> mFmList;

	public MyFMStatePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public MyFMStatePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.mFmList = fragments;
	}

	@Override
	public int getCount() {
		return mFmList != null ? mFmList.size() : 0;
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFmList.get(arg0);
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

}
