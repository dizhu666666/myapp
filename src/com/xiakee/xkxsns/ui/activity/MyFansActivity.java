package com.xiakee.xkxsns.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.util.SNSAPI;
import com.android.xlistview.XListView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.Fans;
import com.xiakee.xkxsns.bean.Fans.FansData;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.adapter.MyFansListAdapter;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/11/3.
 */
public class MyFansActivity extends BaseActivity implements XListView.IXListViewListener {
    private int fansPage = 1;

    @Bind(R.id.lv_fans_list)
    XListView lvFansList;//粉丝列表

    private MyFansListAdapter adapter;
    private List<Fans.FansData> mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_fans);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getTitleBar().setTitle(getString(R.string.my_fans));
        getTitleBar().showLeftAction(R.drawable.title_back_arrow);

        lvFansList.setPullLoadEnable(true);
        lvFansList.setPullRefreshEnable(false);
        lvFansList.setHeaderDividersEnabled(false);
        lvFansList.setFooterDividersEnabled(false);
        lvFansList.setXListViewListener(this);

        mUserList = new ArrayList<FansData>();
        adapter = new MyFansListAdapter(getApplicationContext(), mUserList);
        lvFansList.setAdapter(adapter);
        lvFansList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = position - lvFansList.getHeaderViewsCount();
                if (index >= 0) {
                    MyFansActivity.this.startActivity(new Intent(MyFansActivity.this, UserSpaceActivity.class)
                            .putExtra(SNSAPI.USERID, Integer.parseInt(mUserList.get(index).userId)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }

            }
        });
        lvFansList.startLoadMore();
    }

    private void addFans(String page) {
        //http://127.0.0.1/comm/person/focusMy?loginUserId=1898&page=1
        Ion.with(this).
                load(HttpUrl.MY_FANS).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                setBodyParameter("page", page).
                as(Fans.class).
                setCallback(new FutureCallback<Fans>() {
                    @Override
                    public void onCompleted(Exception e, Fans fans) {
                        LogUtils.e(fans + "");
                        if (null != fans) {
                            List<Fans.FansData> userList = fans.userList;
                            if (userList != null && userList.size() > 0) {
                                adapter.addFansList(fans.userList);
                                fansPage++;
                                lvFansList.stopLoadMore(false);
                            } else {
                                lvFansList.stopLoadMore(true);
                            }
                        } else {
                            if (lvFansList != null) {
                                lvFansList.stopLoadMore();
                            }
                        }
                    }

                });
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
        addFans(String.valueOf(fansPage));
    }
}
