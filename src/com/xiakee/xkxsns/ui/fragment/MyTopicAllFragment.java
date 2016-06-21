package com.xiakee.xkxsns.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import com.android.xlistview.XListView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.PostSubject;
import com.xiakee.xkxsns.bean.PostSubject.PostSubjectData;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.adapter.MyTopicAllListAdapter;
import com.xiakee.xkxsns.ui.view.SwipeLayout;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by William on 2015/11/4.
 */
public class MyTopicAllFragment extends BaseFragment implements XListView.IXListViewListener {
    public final String TAG = this.getClass().getSimpleName();
    String subject = "主题(%d)";
    @Bind(R.id.lv_subject_list)
    XListView lvSubjectList;

    private int currentComPage = 1;//当前评论页数，默认第一页
    private MyTopicAllListAdapter adapter;
    List<PostSubject.PostSubjectData> mTopicList;

    //用于存放已经打开的item
    private Map<Integer, SwipeLayout> mOpenedItems;

    @Override
    public View onCreateRootView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_topic_subject, null);
    }

    @Override
    public void onCreateFinished() {
        lvSubjectList.setPullLoadEnable(true);
        lvSubjectList.setPullRefreshEnable(false);
        lvSubjectList.setHeaderDividersEnabled(false);
        lvSubjectList.setFooterDividersEnabled(false);
        lvSubjectList.setXListViewListener(this);

        mTopicList = new ArrayList<PostSubjectData>();
        adapter = new MyTopicAllListAdapter(mActivity, mTopicList);
        lvSubjectList.setAdapter(adapter);
//        lvSubjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                int index = position - lvSubjectList.getHeaderViewsCount();
//                if (index >= 0) {
//                    startActivity(new Intent(mActivity, TopicDetailsActivity.class).
//                            putExtra(TopicDetailsActivity.TOPIC_ID, mTopicList.get(index).topicId));
//                }
//            }
//        });


        mOpenedItems = adapter.getOpenedItems();
        lvSubjectList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                LogUtils.e("滑动");
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    //关闭所有的item
                    for (SwipeLayout layout : mOpenedItems.values()) {
                        layout.smoothClose();
                    }
                    mOpenedItems.clear();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        lvSubjectList.startLoadMore();
    }


    private void addTopics(String page) {
        //http://127.0.0.1/comm/person/myTopic?loginUserId=1898&page=1
        Ion.with(mActivity).
                load(HttpUrl.MY_TOPIC_ALL).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                setBodyParameter("page", page).
                as(PostSubject.class).
                setCallback(new FutureCallback<PostSubject>() {
                    @Override
                    public void onCompleted(Exception e, PostSubject postSubject) {
                        LogUtils.e(postSubject + "");
                        if (null != postSubject) {
                            List<PostSubject.PostSubjectData> commentsList = postSubject.topicList;
                            if (commentsList != null && commentsList.size() > 0) {
                                mTopicList.addAll(commentsList);
                                adapter.notifyDataSetChanged();
                                currentComPage++;
                                lvSubjectList.stopLoadMore(false);
                            } else {
                                lvSubjectList.stopLoadMore(true);
                            }

                        } else {
                            if (lvSubjectList != null) {
                                lvSubjectList.stopLoadMore();
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
