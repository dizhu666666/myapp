package com.xiakee.xkxsns.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.android.util.SNSAPI;
import com.android.xlistview.XListView;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.BaseBean;
import com.xiakee.xkxsns.bean.Comment;
import com.xiakee.xkxsns.bean.Comment.CommentData2;
import com.xiakee.xkxsns.bean.CommentPraise;
import com.xiakee.xkxsns.bean.CommentReply;
import com.xiakee.xkxsns.bean.SendComment;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.adapter.CommentReplyListAdapter;
import com.xiakee.xkxsns.ui.view.CircleImageView;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.PicassoUtils;
import com.xiakee.xkxsns.util.ToastUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by William on 2015/11/12.
 */
public class CommentReplyDetailsActivity extends BaseActivity implements XListView.IXListViewListener, View.OnClickListener {

    CircleImageView ivIcon;
    TextView tvNickname;
    TextView tvLevel;
    TextView tvTime;
    TextView tvContent;
    ImageView ivImg;
    TextView tvPraise;
    TextView tvFlag;

    private Comment.CommentData mData;
    private String mTopicId;
    //private String mUserId;
    private ArrayList<Comment.CommentData2> newAddCommentsList;//新添加评论的集合


    @Bind(R.id.lv_comment_reply)
    XListView lvCommentReply;

    @Bind(R.id.et_add_comment)
    EditText etComment;

    @Bind(R.id.iv_selected_photo)
    ImageView ivSelectedPhoto;

    private CommentReplyListAdapter mAdapter;

    private String currentCommentId;
    private String currentUserId;
    private String currentUserName;
    private int currentType;

    private String mUserId;
    private int mPosition;

    private Intent mReturnIntent;

    @OnClick(R.id.tv_send)
    void addComment() {
        if (UserManager.isLogin()) {
            String content = etComment.getText().toString().trim();
            sendCmment(content);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        if (getIntent() != null) {
            mData = i.getParcelableExtra("CommentData");
            LogUtils.e(mData + "");
            mTopicId = i.getStringExtra("topicId");
            mUserId = i.getStringExtra("myUserId");//贴主ID
            mPosition = i.getIntExtra("position", -1);
            if (mData == null || TextUtils.isEmpty(mTopicId) || TextUtils.isEmpty(mUserId)) {
                return;
            }
        } else {
            return;
        }

        setContentView(R.layout.activity_com_reply_details);
        ButterKnife.bind(this);

        currentCommentId = mData.commentId;

        currentUserId = i.getStringExtra("parentUserId");

        if (!TextUtils.isEmpty(currentUserId)) {
            currentUserName = i.getStringExtra("parentUserName");
            currentType = 2;
            //对于刚跳到一个新的界面就要弹出软键盘的情况上述代码可能由于界面为加载完全而无法弹出软键盘。
            // 此时应该适当的延迟弹出软键盘如500毫秒
            etComment.requestFocus();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
				public void run() {
                    showSoftKeyboard();
                }
            }, 500);
        } else {
            currentUserId = mData.userId;
            currentUserName = mData.userName;
            currentType = 1;
        }
        etComment.setHint(String.format(getString(R.string.reply_to), currentUserName));
        //LogUtils.e("commendId:" + currentCommentId + ",currentUserId" + currentUserId + ",currentUserName" + currentUserName);

        mReturnIntent = new Intent();
        mReturnIntent.putExtra("position", mPosition);

        setResult(RESULT_OK, mReturnIntent);
        initView();
    }

    private void initView() {
        getTitleBar().setTitle(getString(R.string.comment_details));
        getTitleBar().showLeftAction(R.drawable.title_back_arrow);

        ivSelectedPhoto.setVisibility(View.GONE);

        lvCommentReply.setPullLoadEnable(true);
        lvCommentReply.setPullRefreshEnable(false);
        lvCommentReply.setXListViewListener(this);
        lvCommentReply.setDivider(new ColorDrawable());

        initHeaderView();

        mAdapter = new CommentReplyListAdapter(this, mData, mUserId);
        lvCommentReply.setAdapter(mAdapter);
        mAdapter.setOnContentClickListener(new CommentReplyListAdapter.OnContentClickListener() {
            @Override
            public void onContentClick(Comment.CommentData2 data) {
                if (data != null) {
                    currentUserId = data.userId;
                    currentUserName = data.userName;
                    currentType = 2;
                    LogUtils.e("currentUserId:" + currentUserId + ",currentUserName:" + currentUserName);
                    etComment.setHint(String.format(getString(R.string.reply_to), currentUserName));
                    etComment.requestFocus();
                    showSoftKeyboard();
                }
            }
        });
    }


    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
        if (mAdapter != null) {
            addMoreComments(mAdapter.getLastCommentId());
        }
    }


    private void addMoreComments(String commentId) {
        LogUtils.e("commentId：" + commentId + "，currentCommentId：" + currentCommentId);
        // http://127.0.0.1/comm/topic/subComments?parentId=74&commentsId=258
        Ion.with(this).
                load(HttpUrl.SUB_COMMENTS).
                setBodyParameter("parentId", currentCommentId).
                setBodyParameter("commentsId", commentId).
                as(CommentReply.class).
                setCallback(new FutureCallback<CommentReply>() {
                    @Override
                    public void onCompleted(Exception e, CommentReply commentReply) {
                        LogUtils.e(commentReply + "");
                        if (commentReply != null) {
                            List<Comment.CommentData2> commentsList = commentReply.commentsList;
                            if (commentsList != null && commentsList.size() > 0) {
                                if (newAddCommentsList == null) {
                                    newAddCommentsList = new ArrayList<CommentData2>();
                                    mReturnIntent.putParcelableArrayListExtra("newAddCommentsList", newAddCommentsList);
                                }
                                newAddCommentsList.addAll(commentsList);
                                mAdapter.addComments(commentsList);
                            }
                        }
                        lvCommentReply.stopLoadMore(true);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /***
     * 添加评论
     *
     * @param content
     */
    private void sendCmment(String content) {
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showToast(getString(R.string.say_something));
            return;
        }
        etComment.getText().clear();
        hideSoftKeyboard();

        if (!TextUtils.isEmpty(currentUserId)) {
            SendComment.SendCommentData sendData = new SendComment.SendCommentData();
            sendData.type = currentType;
            sendData.title = content;
            sendData.userId = UserManager.getLoginUserId();
            sendData.topicId = mTopicId;
            sendData.parentId = mData.commentId;
            sendData.parentUserId = currentUserId;
            sendData.parentCommentsId = currentCommentId;
            String json = new Gson().toJson(sendData);
            LogUtils.e(json + "");

            //http://127.0.0.1/comm/topic/postComment
            Ion.with(this).
                    load(HttpUrl.SEND_COMMENT).
                    setBodyParameter("comment", json).
                    setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                    setBodyParameter("token", UserManager.getToken()).
                    as(BaseBean.class).setCallback(new FutureCallback<BaseBean>() {
                @Override
                public void onCompleted(Exception e, BaseBean baseBean) {
                    LogUtils.e(baseBean + "");
                    if (baseBean != null && baseBean.checkData()) {
                        //添加评论成功
                        lvCommentReply.startLoadMore();
                    }
                }
            });
        }

    }

    private InputMethodManager inputMethodManager;

    /***
     * 隐藏软键盘
     */
    void hideSoftKeyboard() {
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (this.getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /***
     * 弹出软键盘
     */
    void showSoftKeyboard() {
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        inputMethodManager.showSoftInput(etComment, 0);
    }

    private ArrayList<String> photos;

    private void initHeaderView() {
        View headerView = View.inflate(this, R.layout.topic_com_details_header, null);
        ivIcon = (CircleImageView) headerView.findViewById(R.id.iv_icon);
        tvNickname = (TextView) headerView.findViewById(R.id.tv_nickname);
        tvLevel = (TextView) headerView.findViewById(R.id.tv_level);
        tvTime = (TextView) headerView.findViewById(R.id.tv_time);
        tvContent = (TextView) headerView.findViewById(R.id.tv_content);
        ivImg = (ImageView) headerView.findViewById(R.id.iv_img);
        tvPraise = (TextView) headerView.findViewById(R.id.tv_praise);
        tvFlag = (TextView) headerView.findViewById(R.id.tv_flag);

        if (mData.userId.equals(mUserId)) {
            tvFlag.setVisibility(View.VISIBLE);
        } else {
            tvFlag.setVisibility(View.INVISIBLE);
        }

        if (TextUtils.isEmpty(mData.img)) {
            ivImg.setVisibility(View.GONE);
        } else {
            photos = new ArrayList<String>();
            photos.add(mData.img);
            ivImg.setVisibility(View.VISIBLE);
            ivImg.setOnClickListener(this);
            PicassoUtils.load(this, mData.img, ivImg);
        }

        tvPraise.setSelected(mData.isPraise());
        tvPraise.setText(String.valueOf(mData.goodCount));
        tvPraise.setOnClickListener(this);

        PicassoUtils.loadIcon(this, mData.photo, ivIcon);
        ivIcon.setOnClickListener(this);

        tvNickname.setText(mData.userName);
        tvTime.setText(mData.commentTime);
        tvLevel.setText(String.format(getString(R.string.level), mData.lv));
        tvLevel.setSelected("1".equals(mData.sex));
        tvContent.setText(mData.title);
        tvContent.setOnClickListener(this);
        lvCommentReply.addHeaderView(headerView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点赞
            case R.id.tv_praise:
                if (!UserManager.isLogin()) {
                    this.startActivity(new Intent(this, LoginActivity.class));
                    return;
                }

                tvPraise.setSelected(!tvPraise.isSelected());
                if (tvPraise.isSelected()) {
                    mData.goodCount++;
                } else {
                    mData.goodCount--;
                }
                mReturnIntent.putExtra("commentsGoodCount", mData.goodCount);
                tvPraise.setText(String.valueOf(mData.goodCount));

                //http://127.0.0.1/comm/topic/commentsGood?commentsId=53&loginUserId=1898
                Ion.with(CommentReplyDetailsActivity.this).
                        load(HttpUrl.GOOD_AT_COMMENT).
                        setBodyParameter("commentsId", mData.commentId).
                        setBodyParameter("token", UserManager.getToken()).
                        setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                        as(CommentPraise.class).
                        setCallback(new FutureCallback<CommentPraise>() {
                            @Override
                            public void onCompleted(Exception e, CommentPraise commentPraise) {
                                LogUtils.e(commentPraise + "");
                                if (commentPraise != null) {
                                    //点赞状态0取消点赞成功1成功点赞
                                    //mData.commentsGoodStatus = commentPraise.goodStatus;
                                    //mReturnIntent.putExtra("commentsGoodStatus", mData.commentsGoodStatus);
                                }
                            }
                        });


                break;
            //点击回复内容
            case R.id.tv_content:
                currentUserName = mData.userName;
                currentUserId = mData.userId;
                currentType = 1;
                etComment.setHint(String.format(getString(R.string.reply_to), currentUserName));
                etComment.requestFocus();
                showSoftKeyboard();
                break;

            //点击楼主头像
            case R.id.iv_icon:
                this.startActivity(new Intent(this, UserSpaceActivity.class)
                        .putExtra(SNSAPI.USERID, Integer.parseInt(mData.userId)).
                                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;

            case R.id.iv_img:
                startActivity(new Intent(this, PhotoViewActivity.class).putExtra(PhotoViewActivity.PHOTO_LIST, photos));
                break;

        }

    }
}
