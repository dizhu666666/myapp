package com.xiakee.xkxsns.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import android.widget.ScrollView;

import com.android.xlistview.XListView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.TopicDetail;
import com.xiakee.xkxsns.bean.TopicList;
import com.xiakee.xkxsns.bean.UserSpaceInfo;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.activity.TopicDetailsActivity;
import com.xiakee.xkxsns.ui.adapter.UserSpaceAdapter;
import com.xiakee.xkxsns.ui.view.userspace.NFListView;
import com.xiakee.xkxsns.util.LogUtils;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import butterknife.Bind;

/**
 * Created by William on 2015/11/25.
 */
public class UserSpaceTopicFragment extends BaseFragment implements XListView.IXListViewListener {

    @Bind(R.id.lv_topic_list)
    NFListView mListView;

    private List<TopicDetail> mTopicList;
    private UserSpaceAdapter mAdapter;

    private UserSpaceInfo mData;
    public ScrollView outer;

    private int currentComPage = 1;//当前评论页数，默认第一页

    public UserSpaceTopicFragment() {

    }

    @Override
    public View onCreateRootView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_userspace_topic, null);
    }

    @Override
    public void onCreateFinished() {
        mData = (UserSpaceInfo) getArguments().get("mData");
        if (mData == null) {
            return;
        }

        mListView.parentScrollView = outer;
        mListView.setPullEnable(false);
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(false);
        mListView.setHeaderDividersEnabled(false);
        mListView.setFooterDividersEnabled(false);
        mListView.setXListViewListener(this);
        mTopicList = new ArrayList<TopicDetail>();
        mAdapter = new UserSpaceAdapter(mActivity, mListView, mTopicList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = position - mListView.getHeaderViewsCount();
                if (index >= 0) {
                    LogUtils.e(mTopicList.get(index).topicId + "");
                    startActivity(new Intent(mActivity, TopicDetailsActivity.class).
                            putExtra(TopicDetailsActivity.TOPIC_ID,
                                    String.valueOf(mTopicList.get(index).topicId)));
                }
            }
        });
        mListView.startLoadMore();
    }

    private void addTopics(String page) {
        //http://127.0.0.1/comm/person/myTopic?loginUserId=1898&page=1
        Ion.with(mActivity).
                load(HttpUrl.MY_TOPIC).
                setBodyParameter("loginUserId", String.valueOf(mData.userId)).
                setBodyParameter("page", page).
                as(TopicList.class).
                setCallback(new FutureCallback<TopicList>() {
                    @Override
                    public void onCompleted(Exception e, TopicList topicList) {
                        if (null != topicList) {
                            List<TopicDetail> list = topicList.getTopicList();
                            if (list != null && list.size() > 0) {
                                mTopicList.addAll(list);
                                currentComPage++;
                                mListView.stopLoadMore(false);
                            } else {
                                mListView.stopLoadMore(true);
                            }

                        } else {
                            if (mListView != null) {
                                mListView.stopLoadMore();
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
        addTopics(String.valueOf(currentComPage));
    }
}
