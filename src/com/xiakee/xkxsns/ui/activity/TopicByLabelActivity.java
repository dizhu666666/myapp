package com.xiakee.xkxsns.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.util.DeviceInfo;
import com.android.xlistview.XListView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.FocusLabelStatus;
import com.xiakee.xkxsns.bean.LabelInfo;
import com.xiakee.xkxsns.bean.TopicById;
import com.xiakee.xkxsns.bean.TopicById.TopicByIdData;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.adapter.TopicByLabelAdapter;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.PicassoUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/11/26.
 */
public class TopicByLabelActivity extends BaseActivity implements XListView.IXListViewListener, View.OnClickListener {
    public static final String LABEL_ID = "labelId";
    public static final String MARKED_TOPIC_ID = "marked_topicId";

    @Bind(R.id.lv_topic_list)
    XListView lvTopicList;

    private String mLabelId;

    private List<TopicById.TopicByIdData> mTopicList;
    private TopicByLabelAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if (i != null) {
            mLabelId = i.getStringExtra(LABEL_ID);
            if (TextUtils.isEmpty(mLabelId)) {
                return;
            }
        } else {
            return;
        }
        setContentView(R.layout.activity_topic_bylabel);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getTitleBar().showLeftAction(R.drawable.title_back_arrow);
        getTitleBar().setTitle("标签详情");

        lvTopicList.setPullLoadEnable(true);
        lvTopicList.setPullRefreshEnable(true);
        lvTopicList.setHeaderDividersEnabled(false);
        lvTopicList.setFooterDividersEnabled(false);
        lvTopicList.setXListViewListener(this);

        mTopicList = new ArrayList<TopicByIdData>();
        lvTopicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = position - lvTopicList.getHeaderViewsCount();
                if (index >= 0) {
                    startActivity(new Intent(TopicByLabelActivity.this, TopicDetailsActivity.class).
                            putExtra(TopicDetailsActivity.TOPIC_ID, mTopicList.get(index).topicId));
                }

            }
        });
        initHeaderView();

    }

    private ImageView ivIcon;
    private TextView tvName;
    private TextView tvTopicCount;
    private ImageView ivAttention;
    private TextView tvDesc;

    private void initHeaderView() {
        View headerView = View.inflate(this, R.layout.topic_label_header, null);
        ivIcon = (ImageView) headerView.findViewById(R.id.iv_icon);
        tvName = (TextView) headerView.findViewById(R.id.tv_name);
        tvTopicCount = (TextView) headerView.findViewById(R.id.tv_topic_count);
        ivAttention = (ImageView) headerView.findViewById(R.id.iv_attention);
        tvDesc = (TextView) headerView.findViewById(R.id.tv_desc);
        ivAttention.setOnClickListener(this);

        //http://127.0.0.1/comm/label/labelInfo?labelId=1&loginUserId=1898
        Ion.with(this).
                load(HttpUrl.GET_LABEL_INFO).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                setBodyParameter("token", UserManager.getToken()).
                setBodyParameter("labelId", mLabelId).as(LabelInfo.class).
                setCallback(new FutureCallback<LabelInfo>() {
                    @Override
                    public void onCompleted(Exception e, LabelInfo labelInfo) {
                        if (labelInfo != null) {
                            LabelInfo.LabelInfoData data = labelInfo.labelInfo;
                            if (data != null) {
                                LogUtils.e(data + "");
                                tvName.setText(data.title);
                                tvDesc.setText(data.labelDesc);
                                tvTopicCount.setText(data.topicCount + "篇");
                                ivAttention.setSelected("1".equals(data.focusStatus));
                                PicassoUtils.loadIcon(TopicByLabelActivity.this, data.logo, ivIcon);

                                mAdapter = new TopicByLabelAdapter(TopicByLabelActivity.this, mTopicList);
                                lvTopicList.setAdapter(mAdapter);
                                lvTopicList.startLoadMore();
                                lvTopicList.setRefreshTime(DeviceInfo.getCurrentTime());
                            }

                        }
                    }
                });


        lvTopicList.addHeaderView(headerView, null, false);

    }

    @Override
    public void onRefresh() {
        addTopics("1", mAdapter.getFirstTopicId());
    }

    @Override
    public void onLoadMore() {
        addTopics("0", mAdapter.getLastTopicId());
    }

    private void addTopics(final String operation, String topicId) {

        //http://127.0.0.1/comm/label/topicList?labelId=3&loginUserId=1898&marked_topicId=1&operation=0
        Ion.with(this).
                load(HttpUrl.GET_TOPIC_BY_LABELID).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                setBodyParameter(LABEL_ID, mLabelId).
                setBodyParameter(MARKED_TOPIC_ID, topicId).
                setBodyParameter("operation", operation).
                as(TopicById.class).
                setCallback(new FutureCallback<TopicById>() {
                    private boolean isRefresh;

                    @Override
                    public void onCompleted(Exception e, TopicById topicById) {
                        isRefresh = "1".equals(operation);
                        LogUtils.e(topicById + "");
                        if (null != topicById) {
                            List<TopicById.TopicByIdData> list = topicById.topicList;
                            if (list != null && list.size() > 0) {
                                if (isRefresh) {
                                    mTopicList.addAll(0, list);
                                    lvTopicList.setRefreshTime(DeviceInfo.getCurrentTime());
                                    lvTopicList.stopRefresh();
                                } else {
                                    mTopicList.addAll(list);
                                    lvTopicList.stopLoadMore(false);
                                }
                                mAdapter.notifyDataSetChanged();

                            } else {
                                if (isRefresh) {
                                    lvTopicList.setRefreshTime(DeviceInfo.getCurrentTime());
                                    lvTopicList.stopRefresh();
                                } else {
                                    lvTopicList.stopLoadMore(true);
                                }
                            }

                        } else {
                            if (lvTopicList != null) {
                                if (isRefresh) {
                                    lvTopicList.setRefreshTime(DeviceInfo.getCurrentTime());
                                    lvTopicList.stopRefresh();
                                } else {
                                    lvTopicList.stopLoadMore();
                                }

                            }

                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_attention:
                // http://127.0.0.1/comm/label/focus?labelId=4&loginUserId=1898
                ivAttention.setSelected(!ivAttention.isSelected());
                Ion.with(this).
                        load(HttpUrl.FOCUS_LABEL).
                        setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                        setBodyParameter("token", UserManager.getToken()).
                        setBodyParameter(LABEL_ID, mLabelId).
                        as(FocusLabelStatus.class).
                        setCallback(new FutureCallback<FocusLabelStatus>() {
                            @Override
                            public void onCompleted(Exception e, FocusLabelStatus focusLabelStatus) {

                            }
                        });

                break;
        }
    }
}
