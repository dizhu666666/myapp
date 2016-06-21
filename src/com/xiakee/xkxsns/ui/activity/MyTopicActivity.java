package com.xiakee.xkxsns.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.ui.adapter.MyTopicPagerAdapter;
import com.xiakee.xkxsns.ui.view.PagerSlidingTabStrip;
import com.xiakee.xkxsns.util.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的帖子页面
 * Created by William on 2015/11/4.
 */
public class MyTopicActivity extends BaseActivity {

    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @Bind(R.id.vp_pager)
    ViewPager vpPager;

    private MyTopicPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_topic);
        LogUtils.e("MyTopicActivity  onCreate");
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getTitleBar().setTitle(getString(R.string.my_topic));
        getTitleBar().showLeftAction(R.drawable.title_back_arrow);
        initViewPager();
    }

    private void initViewPager() {
        mPagerAdapter = new MyTopicPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(mPagerAdapter);
        tabs.setViewPager(vpPager);
    }

}
