package com.xiakee.xkxsns.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.xiakee.xkxsns.ui.fragment.MyTopicAllFragment;
import com.xiakee.xkxsns.ui.fragment.MyTopicReplyFragment;

/**
 * Created by William on 2015/12/4.
 */
public class MyTopicPagerAdapter extends FragmentPagerAdapter {
    private SparseArray<Fragment> fragments;

    private String[] titles = new String[2];

    public MyTopicPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new SparseArray<Fragment>();
        titles[0] = "我的帖子";
        titles[1] = "我的回帖";
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
                    fragment = new MyTopicAllFragment();
                    break;

                case 1:
                    fragment = new MyTopicReplyFragment();
                    break;
            }
            fragments.put(position, fragment);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
