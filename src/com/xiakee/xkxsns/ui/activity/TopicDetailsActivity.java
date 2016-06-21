package com.xiakee.xkxsns.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import com.android.iosdialog.IosDialog;
import com.android.iosdialog.SheetItem;
import com.android.util.SNSAPI;
import com.android.xlistview.XListView;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.BaseBean;
import com.xiakee.xkxsns.bean.Comment;
import com.xiakee.xkxsns.bean.Comment.CommentData;
import com.xiakee.xkxsns.bean.SendComment;
import com.xiakee.xkxsns.bean.TopicDetailsBean.Topic;
import com.xiakee.xkxsns.bean.TopicDetailsBean.TopicDetails;
import com.xiakee.xkxsns.global.Constants;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.adapter.TopicCommentListAdapter;
import com.xiakee.xkxsns.ui.view.CircleImageView;
import com.xiakee.xkxsns.ui.view.TopicDetailsHeader;
import com.xiakee.xkxsns.util.CommonUtils;
import com.xiakee.xkxsns.util.ImageUtils;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.PicassoUtils;
import com.xiakee.xkxsns.util.ToastUtils;
import com.xiakee.xkxsns.util.UserManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 帖子详情
 * Created by William on 2015/11/7.
 */
public class TopicDetailsActivity extends BaseActivity implements XListView.IXListViewListener, View.OnClickListener, IosDialog.OnMyItemClickListner {
    private final UMSocialService mController = UMServiceFactory
            .getUMSocialService(Constants.UMENG_SHAR);

    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;

    public static final int VIEW_DETAILS = 3213;

    public static final String TOPIC_ID = "topicId";

    //ListView的headerView ---------------------------------
    private CircleImageView ivIcon;//楼主头像
    private TextView tvNickname;//楼住昵称
    private TextView tvLevel;//用户等级
    private TextView tvTime;//发帖时间
    private TextView tvAddress;//地址
    //private ImageView ivAddAttention;//加关注

    private String mTopicId;

    @Bind(R.id.lv_post_details)
    XListView lvPostDetails;

    @Bind(R.id.et_add_comment)
    EditText etComment;


    @Bind(R.id.rl_photo)
    RelativeLayout rlPhoto;
    @Bind(R.id.iv_photo)
    ImageView ivPhoto;

    @Bind(R.id.pb_progress)
    ProgressBar pb;

    @OnClick(R.id.iv_cancel)
    void cancelImg() {
        ivPhoto.setImageBitmap(null);
        rlPhoto.setVisibility(View.GONE);
        mImgUrl = null;
    }

    @OnClick(R.id.iv_selected_photo)
    void selectedPhoto() {
        showMenuPop();
        hideSoftKeyboard();
    }

    // -------------------------------------------------------
    private TopicDetails mTopicDetail;

    private TopicCommentListAdapter mAdapter;
    private List<Comment.CommentData> mList;

    private TopicDetailsHeader mHeaderView;


    @OnClick(R.id.tv_send)
    void addComment() {
        if (UserManager.isLogin()) {
            String content = etComment.getText().toString().trim();
            sendComment(content);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //如果传递过来的帖子ID为空，直接return
        Intent i = getIntent();
        if (i != null) {
            mTopicId = i.getStringExtra(TOPIC_ID);
            LogUtils.e("topicId：" + mTopicId);
            if (TextUtils.isEmpty(mTopicId)) {
                return;
            }
        }
        setContentView(R.layout.activity_topic_details);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        TitleBar titleBar = getTitleBar();
        titleBar.setTitle(getString(R.string.topic_details));
        titleBar.showLeftAction(R.drawable.title_back_arrow);
        titleBar.showRightAction("", R.drawable.iv_more);
        titleBar.setRightActionOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMore();
            }
        });
        mList = new ArrayList<CommentData>();
        titleBar.getRCustomAction().setEnabled(false);

        lvPostDetails.setPullLoadEnable(true);
        lvPostDetails.setPullRefreshEnable(false);
        lvPostDetails.setHeaderDividersEnabled(false);
        lvPostDetails.setFooterDividersEnabled(false);
        lvPostDetails.setXListViewListener(this);

        initHeaderView();

        lvPostDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = position - lvPostDetails.getHeaderViewsCount();
                if (index >= 0) {
                    Comment.CommentData data = mList.get(index);
                    TopicDetailsActivity.this.startActivityForResult(new Intent(TopicDetailsActivity.this, CommentReplyDetailsActivity.class).
                            putExtra("CommentData", data).
                            putExtra("topicId", mTopicId).
                            putExtra("myUserId", mTopicDetail.userId).
                            putExtra("position", index)
                            , VIEW_DETAILS);
                }
            }
        });

        //lvPostDetails.setOnScrollListener(new OnScrollListenerImple());//设置滑动监听，如果滑动到底部自动加载更多
    }

    /***
     * 如果有使用任一平台的SSO授权或者集成了facebook平台, 则必须在对应的activity中实现onActivityResult方法, 并添加如下代码
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        LogUtils.e("requestCode：" + requestCode + "，resultCode：" + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == VIEW_DETAILS) {
                ArrayList<Comment.CommentData2> newAddCommentsList = data.getParcelableArrayListExtra("newAddCommentsList");
                int position = data.getIntExtra("position", -1);
                int commentsGoodCount = data.getIntExtra("commentsGoodCount", -1);

                LogUtils.e("position ：" + position + "，commentsGoodCount ：" + commentsGoodCount);
                if (position >= 0) {
                    boolean flag = false;

                    Comment.CommentData commentData = mList.get(position);

                    if (newAddCommentsList != null && newAddCommentsList.size() > 0) {
                        commentData.commentsList.addAll(newAddCommentsList);
                        flag = true;
                    }

                    if (commentsGoodCount >= 0) {
                        if (commentData.goodCount > commentsGoodCount) {
                            commentData.commentsGoodStatus = "0";
                            commentData.goodCount = commentsGoodCount;
                            flag = true;
                        } else if (commentData.goodCount < commentsGoodCount) {
                            commentData.commentsGoodStatus = "1";
                            commentData.goodCount = commentsGoodCount;
                            flag = true;
                        }
                    }

                    if (flag) {
                        mAdapter.notifyDataSetChanged();
                    }

                }

            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        String imagePath = CommonUtils.getImgPathByUri(TopicDetailsActivity.this, selectedImage);
                        if (imagePath != null) {
                            File f = ImageUtils.getSmallBitmap(imagePath);
                            if (f != null && f.exists()) {
                                Drawable drawable = Drawable.createFromPath(f.getAbsolutePath());
                                int width = drawable.getIntrinsicWidth();
                                int height = drawable.getIntrinsicHeight();
                                mImgUrl = f;
                                Picasso.with(this).load(f).resize(width, height).centerCrop().into(ivPhoto);
                                rlPhoto.setVisibility(View.VISIBLE);
                            }

                        }

                    }
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists()) {
                    File f = ImageUtils.getSmallBitmap(cameraFile.getAbsolutePath());

                    if (f != null && f.exists()) {
                        Drawable drawable = Drawable.createFromPath(f.getAbsolutePath());
                        int width = drawable.getIntrinsicWidth();
                        int height = drawable.getIntrinsicHeight();
                        mImgUrl = f;
                        Picasso.with(this).load(f).resize(width, height).centerCrop().into(ivPhoto);
                        rlPhoto.setVisibility(View.VISIBLE);
                    }
                }
            }

        }
    }

    /***
     * 弹出更多菜单
     */
    private void showMore() {
        mController.openShare(this, false);
        //showMorePop();
    }

    /***
     * 加载评论列表
     *
     * @param commentId
     */
    private void addComments(String commentId) {
        //http://127.0.0.1/comm/topic/comments?topicId=299&commentId=21&loginUserId=1898
        Ion.with(this).
                load(HttpUrl.GET_TOPIC_COMMENT).
                setBodyParameter("topicId", mTopicId).
                setBodyParameter("commentId", commentId).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                as(Comment.class).
                setCallback(new FutureCallback<Comment>() {
                    @Override
                    public void onCompleted(Exception e, Comment comment) {
                        LogUtils.e(comment + "");
                        if (null != comment) {
                            if (null != comment.topicComments && comment.topicComments.size() > 0) {
                                mAdapter.addComments(comment.topicComments);
                                lvPostDetails.stopLoadMore(false);
                            } else {
                                // ToastUtils.showToast("已经没有更多的评论");
                                lvPostDetails.stopLoadMore(true);
                            }
                        } else {
                            //ToastUtils.showToast("加载错误");
                            lvPostDetails.stopLoadMore();
                        }
                    }
                });
    }

    private File mImgUrl;

    /***
     * 添加评论
     *
     * @param content
     */
    private void sendComment(String content) {

        if (TextUtils.isEmpty(content)) {
            ToastUtils.showToast(getString(R.string.say_something));
            return;
        }
        etComment.getText().clear();
        ivPhoto.setImageBitmap(null);
        rlPhoto.setVisibility(View.GONE);
        hideSoftKeyboard();
        if (mTopicDetail != null) {
            SendComment.SendCommentData data = new SendComment.SendCommentData();
            data.type = 0;//评论帖子，所以是0
            data.title = content;
            data.userId = UserManager.getLoginUserId();
            data.topicId = mTopicId;
            data.parentId = "0";
            data.parentUserId = mTopicDetail.userId;
            data.parentCommentsId = "0";
            String json = new Gson().toJson(data);
            LogUtils.e(json + "");

            //http://127.0.0.1/comm/topic/postComment
            Builders.Any.M m = Ion.with(this).
                    load(HttpUrl.SEND_COMMENT).
                    setMultipartParameter("comment", json).
                    setMultipartParameter("loginUserId", UserManager.getLoginUserId()).
                    setMultipartParameter("token", UserManager.getToken());


            if (mImgUrl != null && mImgUrl.exists()) {
                LogUtils.e(Formatter.formatFileSize(this, mImgUrl.length()));
                m.setMultipartFile("img", "Application/jpeg", new File(mImgUrl.getAbsolutePath()));
                mImgUrl = null;
            }
            pb.setVisibility(View.VISIBLE);
            m.as(BaseBean.class).setCallback(new FutureCallback<BaseBean>() {
                @Override
                public void onCompleted(Exception e, BaseBean baseBean) {
                    pb.setVisibility(View.INVISIBLE);
                    LogUtils.e(baseBean + "");
                    if (baseBean != null && baseBean.checkData()) {
                        //添加评论成功
                        lvPostDetails.startLoadMore();
                        ToastUtils.showToast(getString(R.string.add_comment_succeed));
                        mHeaderView.onSendComment();
                    }
                }
            });

        }
    }

    /***
     * 初始化headerView
     */
    private void initHeaderView() {
        /**   勿改，改了会有bug   */
        View userView = View.inflate(this, R.layout.topic_details_user, null);
        ivIcon = (CircleImageView) userView.findViewById(R.id.iv_icon);
        tvNickname = (TextView) userView.findViewById(R.id.tv_nickname);
        tvTime = (TextView) userView.findViewById(R.id.tv_time);
        tvAddress = (TextView) userView.findViewById(R.id.tv_address);
        tvLevel = (TextView) userView.findViewById(R.id.tv_level);
        //ivAddAttention = (ImageView) userView.findViewById(R.id.iv_add_attention);
        lvPostDetails.addHeaderView(userView, null, false);//添加用户头部
        /**      */

        mHeaderView = (TopicDetailsHeader) View.inflate(getApplicationContext(), R.layout.topic_details_header, null);
        lvPostDetails.addHeaderView(mHeaderView, null, false);//传递false让headerView不可点击

        //http://127.0.0.1/comm/topic/detail?topicId=1&loginUserId=1
        Ion.with(this).
                load(HttpUrl.GET_POST_DETAILS).
                setBodyParameter("topicId", mTopicId).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                as(Topic.class).setCallback(new FutureCallback<Topic>() {

            @Override
            public void onCompleted(Exception e, Topic topic) {
                if (null != topic) {
                    mTopicDetail = topic.topicDetail;
                    if (null != mTopicDetail) {
                        LogUtils.e(mTopicDetail + "");
                        tvNickname.setText(mTopicDetail.userName);
                        tvLevel.setText(String.format(getContext().getString(R.string.level), mTopicDetail.lv));
                        tvLevel.setSelected("1".equals(mTopicDetail.sex));
                        tvTime.setText(mTopicDetail.topicTime);
                        tvAddress.setText(mTopicDetail.postCity);

                        PicassoUtils.loadIcon(TopicDetailsActivity.this, mTopicDetail.photo, ivIcon);
                        ivIcon.setOnClickListener(TopicDetailsActivity.this);
                        // ivAddAttention.setOnClickListener(TopicDetailsActivity.this);
                        mHeaderView.setData(mTopicDetail);
                        mAdapter = new TopicCommentListAdapter(TopicDetailsActivity.this, mList, mTopicDetail);
                        lvPostDetails.setAdapter(mAdapter);
                        lvPostDetails.startLoadMore();

                        // 配置需要分享的相关平台
                        configPlatforms();
                        // 设置分享的内容
                        setShareContent();
                        getTitleBar().getRCustomAction().setEnabled(true);
                    }
                }
            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_icon:
                this.startActivity(new Intent(this, UserSpaceActivity.class)
                        .putExtra(SNSAPI.USERID, Integer.parseInt(mTopicDetail.userId)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }

    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
        if (mAdapter != null) {
            addComments(mAdapter.getLastCommentId());
        }
    }


    private IosDialog iosDialog;

    /***
     * 弹出上传图片菜单
     */
    private void showMenuPop() {
        if (iosDialog == null) {
            iosDialog = new IosDialog(this, this);
            List<SheetItem> listSheetItems = new ArrayList<SheetItem>();
            listSheetItems.add(new SheetItem(this.getResources().getString(R.string.take_pic), 1));
            listSheetItems.add(new SheetItem(this.getResources().getString(R.string.select_from_album), 2));
            iosDialog.setSheetItems(listSheetItems);
        }

        iosDialog.show();
    }

    @Override
    public void onClickItem(int which) {
        switch (which) {
            case 1:
                selectPicFromCamera();
                break;
            case 2:
                selectPicFromLocal();
                break;
        }
    }


    /**
     * 照相获取图片
     */
    private File cameraFile;

    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            String st = getResources().getString(R.string.sd_card_does_not_exist);
            Toast.makeText(this, st, Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/xiakee");
        cameraFile = new File(file, System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();

        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }


    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }


    //------------------------------------------------------

    private PopupWindow mPopMore;

    /***
     * 更多菜单
     */
    private void showMorePop() {
        if (this.getCurrentFocus() == null) {
            return;
        }


        if (mPopMore == null) {
            View mContentView = View.inflate(this, R.layout.pop_topic_more, null);
            mPopMore = new PopupWindow(mContentView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopMore.setBackgroundDrawable(new ColorDrawable());
            mPopMore.setAnimationStyle(R.style.popupAnimation);

            ImageView ivReport = (ImageView) mContentView.findViewById(R.id.iv_report);

            TextView tvCancel = (TextView) mContentView.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPopMore.isShowing()) {
                        mPopMore.dismiss();
                    }
                }
            });

            mPopMore.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1f);
                }
            });

        }
        backgroundAlpha(0.8f);
        mPopMore.showAtLocation(this.getCurrentFocus(), Gravity.BOTTOM, 0, 0);

    }


    /***
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 配置分享平台参数</br>
     */
    private void configPlatforms() {
        mController.getConfig().setPlatforms(
                SHARE_MEDIA.QQ,
                SHARE_MEDIA.QZONE,
                SHARE_MEDIA.SINA,
                SHARE_MEDIA.TENCENT,
                SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE
        );


//        mController.registerListener(new SocializeListeners.SnsPostListener() {
//
//            @Override
//            public void onStart() {
//               // Toast.makeText(getApplicationContext(), "share start...", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
//               // Toast.makeText(getApplicationContext(), "code : " + eCode, Toast.LENGTH_SHORT).show();
//            }
//        });

        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加腾讯微博SSO授权
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

        // 添加QQ、QZone平台
        addQQQZonePlatform();

    }

    /**
     * @return
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     * image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     * 要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     * : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     */
    private void addQQQZonePlatform() {
        String appId = "100424468";
        String appKey = "c7394704798a158208a74ab60104f0ba";

        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, appId, appKey);
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }


    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private void setShareContent() {

        mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能。http://www.umeng.com/social");

        UMImage urlImage = null;
        if (mTopicDetail.topicImgs.size() > 0) {
            urlImage = new UMImage(this, HttpUrl.HOST + mTopicDetail.topicImgs.get(0).url);
        } else {
            urlImage = new UMImage(this, R.drawable.icon);
        }
        String targetUrl = HttpUrl.HOST + "/comm/topic/share?topicId=" + mTopicId;
        String title = mTopicDetail.title;

        //微信
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(title);
        weixinContent.setTitle("遐客行");
        weixinContent.setTargetUrl(targetUrl);
        weixinContent.setShareMedia(urlImage);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(title);
        circleMedia.setTitle("遐客行");
        circleMedia.setTargetUrl(targetUrl);
        circleMedia.setShareMedia(urlImage);
        mController.setShareMedia(circleMedia);

        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(title);
        qzone.setTitle("遐客行");
        qzone.setTargetUrl(targetUrl);
        qzone.setShareMedia(urlImage);
        mController.setShareMedia(qzone);


        //QQ分享
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(title);
        qqShareContent.setTitle("遐客行");
        qqShareContent.setTargetUrl(targetUrl);
        qqShareContent.setShareMedia(urlImage);
        mController.setShareMedia(qqShareContent);


        //腾讯微博
        TencentWbShareContent tencent = new TencentWbShareContent();
        tencent.setShareContent(title);
        tencent.setTitle("遐客行");
        tencent.setTargetUrl(targetUrl);
        tencent.setShareMedia(urlImage);
        mController.setShareMedia(tencent);

        //新浪微博
        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setShareContent(title);
        sinaContent.setTitle("遐客行");
        sinaContent.setTargetUrl(targetUrl);
        sinaContent.setShareMedia(urlImage);
        mController.setShareMedia(sinaContent);

    }


    private InputMethodManager inputMethodManager;

    /***
     * 弹出软键盘
     */
    void showSoftKeyboard() {
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
            if (this.getCurrentFocus() != null)
                inputMethodManager.showSoftInput(this.getCurrentFocus(),
                        InputMethodManager.SHOW_FORCED);
        }
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //删除上传图片的时候保存在本地的压缩图
        CommonUtils.cleanFile(ImageUtils.getAlbumDir());
    }

}

