package com.xiakee.xkxsns.ui.adapter;

import com.xiakee.xkxsns.bean.UserSpaceInfo;
import com.xiakee.xkxsns.ui.fragment.UserSpaceInfoFragment;
import com.xiakee.xkxsns.ui.fragment.UserSpaceTopicFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.widget.ScrollView;

/**
 * Created by William on 2015/11/25.
 */
public class UserSpacePagerAdapter extends FragmentPagerAdapter {
    private SparseArray<Fragment> fragments;
    private String[] titles = new String[2];
    private UserSpaceInfo mData;

    private ScrollView scrollView;

    public UserSpacePagerAdapter(FragmentManager fm, UserSpaceInfo data, ScrollView outer) {
        super(fm);
        fragments = new SparseArray<Fragment>();
        titles[0] = "资料";
        titles[1] = "帖子";
        this.mData = data;
        this.scrollView = outer;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragments.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new UserSpaceInfoFragment();
                    UserSpaceInfoFragment u = (UserSpaceInfoFragment) fragment;
                    u.outer = scrollView;
                    break;
                case 1:
                    fragment = new UserSpaceTopicFragment();
                    UserSpaceTopicFragment t = (UserSpaceTopicFragment) fragment;
                    t.outer = scrollView;
                    break;
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable("mData", mData);
            fragment.setArguments(bundle);
            fragments.put(position, fragment);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
