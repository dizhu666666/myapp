package com.xiakee.xkxsns.ui.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.android.xlistview.XListView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.PostReply;
import com.xiakee.xkxsns.bean.PostReply.PostReplyData;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.activity.TopicDetailsActivity;
import com.xiakee.xkxsns.ui.adapter.MyTopicReplyListAdapter;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.UserManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by William on 2015/11/4.
 */
public class MyTopicReplyFragment extends BaseFragment implements XListView.IXListViewListener {
    public final String TAG = this.getClass().getSimpleName();

    String reply = "回复(%d)";

    private int currentComPage = 1;//当前评论页数，默认第一页
    private MyTopicReplyListAdapter adapter;
    List<PostReply.PostReplyData> mTopicList;
    @Bind(R.id.lv_reply_list)
    XListView lvReplyList;

    @Override
    public View onCreateRootView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_topic_reply, null);
    }

    @Override
    public void onCreateFinished() {
        lvReplyList.setPullLoadEnable(true);
        lvReplyList.setPullRefreshEnable(false);
        lvReplyList.setHeaderDividersEnabled(false);
        lvReplyList.setFooterDividersEnabled(false);
        lvReplyList.setXListViewListener(this);

        mTopicList = new ArrayList<PostReplyData>();
        adapter = new MyTopicReplyListAdapter(mActivity, mTopicList);
        lvReplyList.setAdapter(adapter);
        lvReplyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = position - lvReplyList.getHeaderViewsCount();
                if (index >= 0) {
                    startActivity(new Intent(mActivity, TopicDetailsActivity.class).
                            putExtra(TopicDetailsActivity.TOPIC_ID, mTopicList.get(index).topicId));
                }

            }
        });
        lvReplyList.startLoadMore();
    }

    private void addTopics(String page) {

        //http://127.0.0.1/comm/person/myComments?loginUserId=1898&page=1
        Ion.with(mActivity).
                load(HttpUrl.MY_TOPIC_REPLY).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                setBodyParameter("page", page).
                as(PostReply.class).
                setCallback(new FutureCallback<PostReply>() {
                    @Override
                    public void onCompleted(Exception e, PostReply postReply) {
                        LogUtils.e(postReply + "");
                        if (null != postReply) {
                            List<PostReply.PostReplyData> commentsList = postReply.commentsList;
                            if (commentsList != null && commentsList.size() > 0) {
                                mTopicList.addAll(commentsList);
                                adapter.notifyDataSetChanged();
                                currentComPage++;
                                lvReplyList.stopLoadMore(false);
                            } else {
                                lvReplyList.stopLoadMore(true);
                            }

                        } else {
                            if (lvReplyList != null)
                                lvReplyList.stopLoadMore();
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
