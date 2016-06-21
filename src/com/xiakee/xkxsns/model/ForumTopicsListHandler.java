package com.xiakee.xkxsns.model;

import java.util.ArrayList;
import java.util.List;

import com.android.util.DeviceInfo;
import com.android.util.SNSAPI;
import com.android.xlistview.XListView;
import com.android.xlistview.XListView.IXListViewListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.TopicDetail;
import com.xiakee.xkxsns.bean.TopicList;
import com.xiakee.xkxsns.bean.TopicTop;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.activity.TopicDetailsActivity;
import com.xiakee.xkxsns.ui.adapter.TopicListAdapter;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ForumTopicsListHandler extends IonRequest implements IXListViewListener, OnItemClickListener {

    public ForumTopicsListHandler(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    private List<TopicDetail> mTopicList;
    private BaseAdapter mAdapter;
    // private Context mContext;
    private XListView mListView;
    private Handler mHandler;
    // private int start = 0;
    private int refreshIndex = -1;
    private boolean bUp = true;
    private String mLastRefreshTime;
    private int mForumTypeId = 0;

    //置顶帖子列表
    private View mHeaderView;
    private LinearLayout llTopicTopContent;
    private List<TopicDetail> mTopicTopList;

    public ForumTopicsListHandler(Context c, View view, int forumTypeId) {
        super(c);
        mForumTypeId = forumTypeId;
        initListView(view, forumTypeId);
    }

    public void initListView(View view, int typeId) {
        // topicList = new TopicList();
        mTopicList = new ArrayList<TopicDetail>();
        mListView = (XListView) view.findViewById(R.id.forum_topic_list);
        mListView.setHeaderDividersEnabled(false);

        mTopicTopList = new ArrayList<TopicDetail>();
        getTopicTopList(typeId);

        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
        mHandler = new Handler();
        mAdapter = new TopicListAdapter(mContext, mListView, mTopicList);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        getMoreTopicList(typeId);
    }

    private void getTopicTopList(int typeId) {
        //http://127.0.0.1/comm/type/topicTopList?typeId=1&loginUserId=1898
        Ion.with(mContext).
                load(HttpUrl.GET_TOPIC_TOP).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                setBodyParameter("typeId", String.valueOf(typeId)).
                as(TopicTop.class).setCallback(new FutureCallback<TopicTop>() {
            @Override
            public void onCompleted(Exception e, TopicTop topicTop) {
                if (topicTop != null) {
                    List<TopicDetail> list = topicTop.topicTopList;
                    LogUtils.e(list.size() + "topicList");
                    if (list != null && list.size() > 0) {
                        mTopicTopList.addAll(list);
                        mHeaderView = View.inflate(mContext, R.layout.common_forum_header, null);
                        llTopicTopContent = (LinearLayout) mHeaderView.findViewById(R.id.ll_topic_top_content);
                        for (final TopicDetail td : mTopicTopList) {
                            View topicTopView = View.inflate(mContext, R.layout.list_topic_top, null);
                            TextView tvTitle = (TextView) topicTopView.findViewById(R.id.tv_title);
                            tvTitle.setText(td.getTitle());
                            llTopicTopContent.addView(topicTopView);
                            topicTopView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mContext.startActivity(new Intent(mContext, TopicDetailsActivity.class).
                                            putExtra(TopicDetailsActivity.TOPIC_ID, String.valueOf(td.getTopicId())));
                                }
                            });
                        }
                        mListView.addHeaderView(mHeaderView);
                    }
                }
            }
        });


    }

    @Override
    protected void handleError(String errmsg) {
        handleError(errmsg, false);
        onLoad_error();
    }

    @Override
    protected void handleResult(JsonObject result) {
        Gson gson = new Gson();
        // final TopicList topicList = new TopicList();
        final String bbsListString = result.toString();
        TopicList topicList = gson.fromJson(bbsListString, TopicList.class);
        final int currentSize = topicList.getTopicList().size();
        refreshIndex += currentSize;
        if (bUp) {
            if (currentSize == 0) {
                onLoad(true);
            } else {
                refresh(addMoreTopicList(topicList.getTopicList()));
                onLoad(false);
            }
        } else {
            refresh(addLatestTopicList(topicList.getTopicList()));
            onLoad(false);
        }
    }

    public List<TopicDetail> addMoreTopicList(List<TopicDetail> topicList) {
        mTopicList.addAll(topicList);
        return mTopicList;
    }

    public List<TopicDetail> addLatestTopicList(List<TopicDetail> topicList) {
        mTopicList.addAll(0, topicList);
        return mTopicList;
    }

    private void refresh(List<TopicDetail> topicList) {
        // TODO Auto-generated method stub
        ((TopicListAdapter) mAdapter).refresh(topicList);
    }

    private void getMoreTopicList(int typeId) {
        httpGet(SNSAPI.getForumTopicListUrl(typeId, refreshIndex > 0 ? mTopicList.get(refreshIndex).getTopicId() : 0,
                SNSAPI.PULL_UP, SNSAPI.TEST_USERID));
    }

    private void getLatestTopicList(int typeId) {
        httpGet(SNSAPI.getForumTopicListUrl(typeId, refreshIndex > 0 ? mTopicList.get(0).getTopicId() : 0, SNSAPI.PULL_DOWN, SNSAPI.TEST_USERID));
    }

    /*
     * private void geneItems() { for (int i = 0; i != 5; ++i) { items.add(
     * "refresh cnt " + (++start)); } }
     */
    private void onLoad(boolean isEnd) {
        mListView.stopRefresh();
        mListView.stopLoadMore(isEnd);
        mListView.setRefreshTime(DeviceInfo.getCurrentTime());
    }

    private void onLoad_error() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(mLastRefreshTime);
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bUp = false;
                mLastRefreshTime = DeviceInfo.getCurrentTime();
                getLatestTopicList(mForumTypeId);
            }
        }, 500);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // geneItems();
                bUp = true;
                getMoreTopicList(mForumTypeId);
            }
        }, 500);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        int index = position - mListView.getHeaderViewsCount();
        mContext.startActivity(new Intent(mContext, TopicDetailsActivity.class).
                putExtra(TopicDetailsActivity.TOPIC_ID, String.valueOf(mTopicList.get(index).getTopicId())));
    }

    private class InsideHttp extends IonRequest {

        public InsideHttp(Context c) {
            super(c);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void handleError(String errmsg) {
            handleError(errmsg, false);
            onLoad_error();
        }

        @Override
        protected void handleResult(JsonObject result) {
            Gson gson = new Gson();
            // final TopicList topicList = new TopicList();
            final String bbsListString = result.toString();
            TopicList topicList = gson.fromJson(bbsListString, TopicList.class);
            final int currentSize = topicList.getTopicList().size();
            refreshIndex += currentSize;
            if (bUp) {
                if (currentSize == 0) {
                    onLoad(true);
                } else {
                    refresh(addMoreTopicList(topicList.getTopicList()));
                    onLoad(false);
                }
            } else {
                refresh(addLatestTopicList(topicList.getTopicList()));
                onLoad(false);
            }
        }
    }

}
