package com.xiakee.xkxsns.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.xlistview.XListView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.FocusLabel;
import com.xiakee.xkxsns.bean.FocusLabel.FocusLabelData;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.adapter.MyFocusLabelsAdapter;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/12/7.
 */
public class MyFocusLabelsActivity extends BaseActivity implements XListView.IXListViewListener {
    private int currentPage = 1;

    @Bind(R.id.lv_labels_list)
    XListView lvLabelList;

    private MyFocusLabelsAdapter mAdapter;

    private List<FocusLabel.FocusLabelData> mLabelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_focus_labels);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getTitleBar().setTitle(getString(R.string.my_focus_label));
        getTitleBar().showLeftAction(R.drawable.title_back_arrow);

        lvLabelList.setPullLoadEnable(true);
        lvLabelList.setPullRefreshEnable(false);
        lvLabelList.setHeaderDividersEnabled(false);
        lvLabelList.setFooterDividersEnabled(false);
        lvLabelList.setXListViewListener(this);

        mLabelList = new ArrayList<FocusLabelData>();
        mAdapter = new MyFocusLabelsAdapter(this, mLabelList);
        lvLabelList.setAdapter(mAdapter);
        lvLabelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = position - lvLabelList.getHeaderViewsCount();
                if (index >= 0) {
                    MyFocusLabelsActivity.this.startActivity(new Intent(MyFocusLabelsActivity.this, TopicByLabelActivity.class).
                            putExtra(TopicByLabelActivity.LABEL_ID, mLabelList.get(index).labelId));
                }
            }
        });

        lvLabelList.startLoadMore();
    }


    private void addFocusLabels(String page) {
        //http://127.0.0.1/comm/label/myFocus?loginUserId=141&page=1
        Ion.with(this).
                load(HttpUrl.MY_FOCUS_LABELS).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                setBodyParameter("page", page).
                as(FocusLabel.class).
                setCallback(new FutureCallback<FocusLabel>() {
                    @Override
                    public void onCompleted(Exception e, FocusLabel focusLabel) {
                        LogUtils.e(focusLabel + "");

                        if (null != focusLabel) {
                            List<FocusLabel.FocusLabelData> list = focusLabel.labelList;
                            if (list != null && list.size() > 0) {
                                mLabelList.addAll(list);
                                mAdapter.notifyDataSetChanged();

                                currentPage++;
                                lvLabelList.stopLoadMore(false);
                            } else {
                                lvLabelList.stopLoadMore(true);
                            }
                        } else {
                            if (lvLabelList != null) {
                                lvLabelList.stopLoadMore();
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
        addFocusLabels(String.valueOf(currentPage));
    }
}
