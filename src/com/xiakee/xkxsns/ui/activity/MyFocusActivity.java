package com.xiakee.xkxsns.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.util.SNSAPI;
import com.android.xlistview.XListView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.Focus;
import com.xiakee.xkxsns.bean.Focus.FocusData;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.adapter.MyFocusListAdapter;
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
public class MyFocusActivity extends BaseActivity implements XListView.IXListViewListener {
    private int focusPage = 1;

    @Bind(R.id.lv_focus_list)
    XListView lvFocusList;//关注列表

    private MyFocusListAdapter adapter;
    private List<Focus.FocusData> mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_focus);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getTitleBar().setTitle(getString(R.string.my_focus));
        getTitleBar().showLeftAction(R.drawable.title_back_arrow);

        lvFocusList.setPullLoadEnable(true);
        lvFocusList.setPullRefreshEnable(false);
        lvFocusList.setHeaderDividersEnabled(false);
        lvFocusList.setFooterDividersEnabled(false);
        lvFocusList.setXListViewListener(this);

        mUserList = new ArrayList<FocusData>();
        adapter = new MyFocusListAdapter(getApplicationContext(), mUserList);
        lvFocusList.setAdapter(adapter);
        lvFocusList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = position - lvFocusList.getHeaderViewsCount();
                if (index >= 0) {
                    MyFocusActivity.this.startActivity(new Intent(MyFocusActivity.this, UserSpaceActivity.class)
                            .putExtra(SNSAPI.USERID, Integer.parseInt(mUserList.get(index).userId)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });

        lvFocusList.startLoadMore();
    }

    private void addFocus(String page) {
        //http://127.0.0.1/comm/person/myFocus?loginUserId=1898&page=1
        Ion.with(this).
                load(HttpUrl.MY_FOCUS).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                setBodyParameter("page", page).
                as(Focus.class).
                setCallback(new FutureCallback<Focus>() {
                    @Override
                    public void onCompleted(Exception e, Focus focus) {
                        LogUtils.e(focus + "");
                        if (null != focus) {
                            List<Focus.FocusData> userList = focus.userList;
                            if (userList != null && userList.size() > 0) {
                                adapter.addFocusList(focus.userList);
                                focusPage++;
                                lvFocusList.stopLoadMore(false);
                            } else {
                                lvFocusList.stopLoadMore(true);
                            }
                        } else {
                            if (lvFocusList != null) {
                                lvFocusList.stopLoadMore();
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
        addFocus(String.valueOf(focusPage));
    }
}
