package com.xiakee.xkxsns.ui.view;


import java.util.ArrayList;
import java.util.List;

import com.android.util.SNSAPI;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.TopicPraise;
import com.xiakee.xkxsns.bean.TopicDetailsBean.TopicDetails;
import com.xiakee.xkxsns.bean.TopicDetailsBean.TopicDetailsGoodUser;
import com.xiakee.xkxsns.bean.TopicDetailsBean.TopicDetailsImage;
import com.xiakee.xkxsns.bean.TopicDetailsBean.TopicDetailsImgLabel;
import com.xiakee.xkxsns.bean.TopicDetailsBean.TopicLabel;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.activity.LoginActivity;
import com.xiakee.xkxsns.ui.activity.PhotoViewActivity;
import com.xiakee.xkxsns.ui.activity.TopicByLabelActivity;
import com.xiakee.xkxsns.ui.activity.UserSpaceActivity;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.PicassoUtils;
import com.xiakee.xkxsns.util.TextParser;
import com.xiakee.xkxsns.util.ToastUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by William on 2015/11/9.
 */
public class TopicDetailsHeader extends LinearLayout implements View.OnClickListener {
    //ListView的headerView ---------------------------------

    private LinearLayout llTopicContent;//内容
    private LinearLayout llTopicLabels;//标签
    private ImageView ivLabel;//标签图标
    private LinearLayout llLabel;//标签根布局
    private LinearLayout llTopicGoodPhoto;//赞的人头像
    private TextView tvGoodAndCom;//赞的人和评论的人
    private TextView tvPraise;//点赞

    // -------------------------------------------------------
    private TopicDetails mTopicDetail;
    private Context mContext;

    private Display mDisplay;

    public TopicDetailsHeader(Context context) {
        this(context, null);
    }

    public TopicDetailsHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopicDetailsHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        if (mDisplay == null) {
            WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            mDisplay = manager.getDefaultDisplay();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        llTopicContent = (LinearLayout) this.findViewById(R.id.ll_topic_content);
        llTopicLabels = (LinearLayout) this.findViewById(R.id.ll_topic_label);
        llTopicGoodPhoto = (LinearLayout) this.findViewById(R.id.ll_good_photo);
        tvGoodAndCom = (TextView) this.findViewById(R.id.tv_good_comment);
        tvPraise = (TextView) this.findViewById(R.id.tv_praise);
        llLabel = (LinearLayout) this.findViewById(R.id.ll_label);
        ivLabel = (ImageView) this.findViewById(R.id.iv_label);
    }


    public void setData(final TopicDetails topicDetail) {
        mTopicDetail = topicDetail;
        if (mTopicDetail == null) {
            return;
        }

        String goodCount = String.valueOf(mTopicDetail.goodCount);
        String commentCount = String.valueOf(mTopicDetail.commentCount);
        parseGoodAndComment(goodCount, commentCount);

        boolean isPraise = mTopicDetail.isPraise();
        tvPraise.setSelected(isPraise);
        tvPraise.setText(isPraise ? "取消点赞" : "点赞");
        tvPraise.setOnClickListener(this);

        /** 添加title */
        String title = mTopicDetail.title;
        TextView tvTitle = new TextView(getContext());
        LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        int size = getResources().getDimensionPixelSize(R.dimen.dp15);
        vlp.topMargin = size;
        vlp.bottomMargin = size;

        tvTitle.setLayoutParams(vlp);
        tvTitle.setText(title);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tvTitle.setTextColor(Color.BLACK);
        llTopicContent.addView(tvTitle);

        //添加文章内容
        List<TopicDetailsImage> topicImages = mTopicDetail.topicImgs;
        addContent(topicImages);

        List<TopicDetailsGoodUser> goodUsers = mTopicDetail.goodUsers;
        //添加赞的用户头像
        addGoodUsers(goodUsers);

        //----------------------------------------------
        //添加帖子标签
        List<TopicLabel> labels = mTopicDetail.topicLabels;
        addTopicLabels(labels);

    }

    /***
     * 解析赞和评论的数量
     *
     * @param goodCount
     * @param commentCount
     */
    private void parseGoodAndComment(String goodCount, String commentCount) {
        int size = (int) (getResources().getDisplayMetrics().scaledDensity * 13 + 0.5);
        TextParser textParser = new TextParser();
        textParser.append("总共有", size, Color.BLACK);
        textParser.append(goodCount, size, Color.RED);
        textParser.append("个赞，", size, Color.BLACK);
        textParser.append(commentCount, size, Color.RED);
        textParser.append("条评论", size, Color.BLACK);
        textParser.parse(tvGoodAndCom);
    }

    /***
     * 添加赞的用户头像
     *
     * @param goodUsers
     */
    private void addGoodUsers(List<TopicDetailsGoodUser> goodUsers) {
        if (goodUsers == null) {
            return;
        }

        for (final TopicDetailsGoodUser goodUser : goodUsers) {
            if (llTopicGoodPhoto.getChildCount() == 8) {
                return;
            }

            if (!TextUtils.isEmpty(goodUser.photo)) {
                CircleImageView iv = new CircleImageView(getContext());
                LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                int size = mContext.getResources().getDimensionPixelSize(R.dimen.dp30);
                vlp.width = size;
                vlp.height = size;
                vlp.rightMargin = mContext.getResources().getDimensionPixelSize(R.dimen.dp10);
                iv.setLayoutParams(vlp);
                PicassoUtils.scaleLoadIcon(mContext, size, size, goodUser.photo, iv);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, UserSpaceActivity.class)
                                .putExtra(SNSAPI.USERID, Integer.parseInt(goodUser.userId)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
                llTopicGoodPhoto.addView(iv);

            }

        }
    }

    private ArrayList<String> photos;

    /***
     * 添加文章内容
     *
     * @param topicImages
     */
    private void addContent(List<TopicDetailsImage> topicImages) {
        if (null != topicImages) {
            photos = new ArrayList<String>();

            for (int x = 0; x < topicImages.size(); x++) {

                TopicDetailsImage tdi = topicImages.get(x);
                String text = tdi.text;
                String url = tdi.url;

                //添加文字描述
                if (!TextUtils.isEmpty(text)) {
                    TextView tv = new TextView(getContext());
                    LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    tv.setLayoutParams(vlp);
                    tv.setTextColor(Color.BLACK);
                    tv.setText(text);
                    llTopicContent.addView(tv);
                }


                //添加图片
                if (!TextUtils.isEmpty(url)) {
                    photos.add(url);

                    View imgView = View.inflate(mContext, R.layout.topic_details_image, null);
                    ImageView iv = (ImageView) imgView.findViewById(R.id.iv);
                    LinearLayout llLabels = (LinearLayout) imgView.findViewById(R.id.ll_labels);

                    final int finalX = photos.size() - 1;
                    iv.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, PhotoViewActivity.class).
                                    putExtra(PhotoViewActivity.PHOTO_LIST, photos).
                                    putExtra(PhotoViewActivity.INDEX, finalX).
                                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    });
                    Picasso.with(mContext).
                            load(HttpUrl.HOST + url).
                            error(R.drawable.xiakee_small).
                            into(iv);

                    llTopicContent.addView(imgView);

                    //----------------------------------------------
                    //添加图片标签
                    List<TopicDetailsImgLabel> labels = tdi.imgLabels;
                    addImgLabels(llLabels, labels);
                }

            }

        }
    }


    /***
     * 添加图片标签
     *
     * @param labels
     */
    private void addImgLabels(LinearLayout llLabels, List<TopicDetailsImgLabel> labels) {
        if (labels == null) {
            return;
        }

        if (labels.size() > 0) {
            for (final TopicDetailsImgLabel label : labels) {
                LogUtils.e("添加了标签：" + label.title);
                if (!TextUtils.isEmpty(label.title)) {
                    TextView tvLabel = new TextView(getContext());
                    LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    vlp.topMargin = mContext.getResources().getDimensionPixelSize(R.dimen.dp5);
                    vlp.gravity = Gravity.CENTER_VERTICAL;
                    tvLabel.setLayoutParams(vlp);
                    tvLabel.setTextColor(Color.WHITE);
                    tvLabel.setShadowLayer(10, 5f, 5f, Color.BLACK);
                    tvLabel.setText(label.title);
                    tvLabel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, TopicByLabelActivity.class).
                                    putExtra(TopicByLabelActivity.LABEL_ID, label.labelId).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    });

                    llLabels.addView(tvLabel);
                }

            }
        }
    }

    /***
     * 添加帖子标签
     *
     * @param labels
     */
    private void addTopicLabels(List<TopicLabel> labels) {
        if (labels == null) {
            return;
        }

        if (labels.size() > 0) {
            llLabel.setVisibility(VISIBLE);
            for (final TopicLabel label : labels) {
                LogUtils.e("添加了标签：" + label.title);
                if (!TextUtils.isEmpty(label.title)) {
                    TextView tvLabel = new TextView(getContext());
                    LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    vlp.leftMargin = mContext.getResources().getDimensionPixelSize(R.dimen.dp8);
                    vlp.gravity = Gravity.CENTER_VERTICAL;
                    tvLabel.setLayoutParams(vlp);
                    tvLabel.setTextColor(Color.RED);
                    tvLabel.setText(label.title);
                    tvLabel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, TopicByLabelActivity.class).
                                    putExtra(TopicByLabelActivity.LABEL_ID, label.labelId).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    });
                    llTopicLabels.addView(tvLabel);
                }

            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_praise:
                if (!UserManager.isLogin()) {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    return;
                }

                tvPraise.setSelected(!tvPraise.isSelected());
                if (tvPraise.isSelected()) {
                    mTopicDetail.goodCount++;
                    mTopicDetail.goodStatus = "1";
                } else {
                    mTopicDetail.goodCount--;
                    mTopicDetail.goodStatus = "0";
                }
                if (mTopicDetail.isPraise()) {
                    tvPraise.setText("取消点赞");
                } else {
                    tvPraise.setText("点赞");
                }

                parseGoodAndComment(String.valueOf(mTopicDetail.goodCount), String.valueOf(mTopicDetail.commentCount));

                //http://127.0.0.1/comm/topic/good?topicId=1&loginUserId=1
                Ion.with(mContext).
                        load(HttpUrl.PRAISE).
                        setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                        setBodyParameter("token", UserManager.getToken()).
                        setBodyParameter("topicId", mTopicDetail.topicId).
                        as(TopicPraise.class).
                        setCallback(new FutureCallback<TopicPraise>() {
                            @Override
                            public void onCompleted(Exception e, TopicPraise praise) {
                                LogUtils.e(praise + "");
                                if (praise != null) {
                                    //mTopicDetail.goodStatus = praise.goodStatus;
                                } else {
                                    ToastUtils.showToast("点击错误");
                                }

                            }
                        });

                break;

        }
    }

    public void onSendComment() {
        mTopicDetail.commentCount++;
        parseGoodAndComment(String.valueOf(mTopicDetail.goodCount), String.valueOf(mTopicDetail.commentCount));
    }
}
