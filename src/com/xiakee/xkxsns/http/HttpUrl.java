package com.xiakee.xkxsns.http;

/**
 * Created by William on 2015/11/2.
 */
public class HttpUrl {
    //主机
    public static final String HOST = "http://119.254.211.89";

    //获取用户信息
    public static final String GET_USER_INFO = HOST + "/comm/person/myInfo";

    //获取验证码
    public static final String GET_SMS_CODE = HOST + "/comm/ecstore/sendCode";

    //注册
    public static final String REGISTER = HOST + "/comm/ecstore/register";

    //登录
    public static final String LOGIN = HOST + "/comm/ecstore/login";

    //反馈
    public static final String ADVICE = HOST + "/comm/person/advice";

    //我的收藏
    public static final String MY_COLLECTION = HOST + "/comm/person/collections";

    //他人帖子
    public static final String MY_TOPIC = HOST + "/comm/person/myTopic";

    //我的帖子 - 所有
    public static final String MY_TOPIC_ALL = HOST + "/comm/person/myAllTopic";

    //我回复的帖子
    public static final String MY_TOPIC_REPLY = HOST + "/comm/person/myComments";

    //获取帖子详情
    public static final String GET_POST_DETAILS = HOST + "/comm/topic/detail";

    //获取帖子评论
    public static final String GET_TOPIC_COMMENT = HOST + "/comm/topic/comments";

    //我的粉丝
    public static final String MY_FANS = HOST + "/comm/person/focusMy";

    //我的关注
    public static final String MY_FOCUS = HOST + "/comm/person/myFocus";

    //签到
    public static final String SIGN = HOST + "/comm/person/signIn";

    //更改用户资料
    public static final String CHANGE_USER_INFO = HOST + "/comm/person/updateInfo";

    //添加评论
    public static final String SEND_COMMENT = HOST + "/comm/topic/postComment";

    // 关注某人/取消关注
    public static final String FOCUS_OR_CANCEL = HOST + "/comm/person/focus";

    //获取置顶帖子列表
    public static final String GET_TOPIC_TOP = HOST + "/comm/type/topicTopList";

    //对帖子点赞，取消点赞
    public static final String PRAISE = HOST + "/comm/topic/good";

    //查看评论下面的所有评论
    public static final String SUB_COMMENTS = HOST + "/comm/topic/subComments";

    //记密码 — 发送手机验证码  http://127.0.0.1/comm/ecstore/lostSendCode?phone=13126506188
    public static final String FIND_PWD_SEND_CODE = HOST + "/comm/ecstore/lostSendCode";

    //忘记密码 — 验证手机验证码 http://127.0.0.1/comm/ecstore/lostVerifyCode?phone=13126506188&vcode=195596
    public static final String FIND_PWD_CHECK_CODE = HOST + "/comm/ecstore/lostVerifyCode";

    //忘记密码 — 重置密码 http://127.0.0.1/comm/ecstore/resetPassword?lostToken=13126506188&memberId=15&phone=13125655555&password=156548
    public static final String FIND_PWD_RESET_PWD = HOST + "/comm/ecstore/resetPassword";

    // 对评论点赞
    public static final String GOOD_AT_COMMENT = HOST + "/comm/topic/commentsGood";

    //获取用户的签到状态
    public static final String GET_SIGN_STATUS = HOST + "/comm/person/signStatus";

    //获取帖子列表，根据标签id
    public static final String GET_TOPIC_BY_LABELID = HOST + "/comm/label/topicList";

    //获取标签信息，根据标签id
    public static final String GET_LABEL_INFO = HOST + "/comm/label/labelInfo";

    //关注标签/取消关注
    public static final String FOCUS_LABEL = HOST + "/comm/label/focus";

    //获取个人空间用户信息
    public static final String GET_USER_SPACE_INFO = HOST + "/comm/person/info";

    //第三方登录
    public static final String OAUTH_LOGIN = HOST + "/comm/ecstore/oauthLogin";

    //回复我的
    public static final String REPLY_ME = HOST + "/comm/person/commentsMe";

    //验证token是否有效
    public static final String CHECK_LOGIN = HOST + "/comm/person/vToken";

    //我关注的标签列表
    public static final String MY_FOCUS_LABELS = HOST + "/comm/label/myFocus";

    //删除帖子 http://127.0.0.1/comm/topic/remove?loginUserId=141&token=32323&topicId=12
    public static final String DELETE_TOPIC = HOST + "/comm/topic/remove";
}
