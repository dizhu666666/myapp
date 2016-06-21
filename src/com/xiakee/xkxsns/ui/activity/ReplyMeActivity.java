package com.xiakee.xkxsns.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.xlistview.XListView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.ReplyMe;
import com.xiakee.xkxsns.bean.ReplyMe.ReplyMeData;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.adapter.ReplyMeListAdapter;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/12/2.
 */
public class ReplyMeActivity extends BaseActivity implements XListView.IXListViewListener {

    private int currentComPage = 1;//当前评论页数，默认第一页
    private ReplyMeListAdapter adapter;
    List<ReplyMe.ReplyMeData> mTopicList;
    @Bind(R.id.lv_reply_list)
    XListView lvReplyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_me);
        ButterKnife.bind(this);

        TitleBar titleBar = getTitleBar();
        titleBar.setTitle("回复我的");
        titleBar.showLeftAction(R.drawable.title_back_arrow);

        lvReplyList.setPullLoadEnable(true);
        lvReplyList.setPullRefreshEnable(false);
        lvReplyList.setHeaderDividersEnabled(false);
        lvReplyList.setFooterDividersEnabled(false);
        lvReplyList.setXListViewListener(this);

        mTopicList = new ArrayList<ReplyMeData>();
        adapter = new ReplyMeListAdapter(this, mTopicList);
        lvReplyList.setAdapter(adapter);
        lvReplyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = position - lvReplyList.getHeaderViewsCount();
                if (index >= 0) {
                    startActivity(new Intent(ReplyMeActivity.this, TopicDetailsActivity.class).
                            putExtra(TopicDetailsActivity.TOPIC_ID, mTopicList.get(index).topicId));
                }

            }
        });
        lvReplyList.startLoadMore();
    }

    private void addTopics(String page) {

        //http://127.0.0.1/comm/person/commentsMe?loginUserId=1898&page=1
        Ion.with(this).
                load(HttpUrl.REPLY_ME).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                setBodyParameter("page", page).
                as(ReplyMe.class).
                setCallback(new FutureCallback<ReplyMe>() {
                    @Override
                    public void onCompleted(Exception e, ReplyMe replyMe) {
                        LogUtils.e(replyMe + "");
                        if (null != replyMe) {
                            List<ReplyMe.ReplyMeData> commentsList = replyMe.commentsList;
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
