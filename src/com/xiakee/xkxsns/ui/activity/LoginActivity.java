package com.xiakee.xkxsns.ui.activity;

import java.util.Map;

import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.UserInfo;
import com.xiakee.xkxsns.global.Constants;
import com.xiakee.xkxsns.global.PrefsConfig;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.PrefsUtils;
import com.xiakee.xkxsns.util.ToastUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by William on 2015/11/6.
 */
public class LoginActivity extends BaseActivity {
    public static final int REGISTER = 32132;
    public static final int LOGIN_SUCCEED = 1111;
    public static final int UM_LOGIN_SUCCEED = 1112;

    // 整个平台的Controller, 负责管理整个SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(Constants.UMENG_LOGIN);

    @Bind(R.id.et_username)
    EditText etUsername;
    @Bind(R.id.et_password)
    EditText etPassword;

    @OnClick(R.id.tv_lose_pwd)
    void losePwd() {
        startActivity(new Intent(this, FindPasswordActivity.class));
    }

    @OnClick(R.id.tv_login_qq)
    void qqLogin() {
        login(SHARE_MEDIA.QZONE);
    }

    @OnClick(R.id.tv_login_wx)
    void wxLogin() {
        login(SHARE_MEDIA.WEIXIN);
    }

    @OnClick(R.id.tv_login_xlwb)
    void xlwbLogin() {
        login(SHARE_MEDIA.SINA);
    }

    @OnClick(R.id.btn_login)
    void login() {
        final Dialog dialog = showLoginDialog();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        UserManager.login(this, username, password, new UserManager.LoginCallback() {
            @Override
            public void onSucceed(UserInfo.UserInfoData userInfoData) {
                dialog.dismiss();
                hideSoftKeyboard();
                setResult(LOGIN_SUCCEED);
                LoginActivity.this.finish();
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                ToastUtils.showToast(error);
            }
        });
    }

    @OnClick(R.id.tv_register)
    void register() {
        startActivityForResult(new Intent(this, RegisterActivity.class), REGISTER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 配置需要分享的相关平台
        configPlatforms();
    }

    private void initView() {
        getTitleBar().setTitle(getString(R.string.login));
        getTitleBar().showLeftAction(R.drawable.title_back_arrow);
    }

    private ProgressDialog loginDialog;

    /***
     * 弹出正在注册对话框
     *
     * @return
     */
    private Dialog showLoginDialog() {
        if (loginDialog == null) {
            loginDialog = new ProgressDialog(this);
            loginDialog.setMessage(getString(R.string.is_logged_in));
        }

        loginDialog.show();
        return loginDialog;
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

    /**
     * 配置分享平台参数</br>
     */
    private void configPlatforms() {
        //设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
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
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,
                appId, appKey);
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }


    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */
    private void login(final SHARE_MEDIA platform) {
//        if (OauthHelper.isAuthenticatedAndTokenNotExpired(getApplicationContext(), platform)) {
//            //ToastUtils.showToast("已经授权");
//            showLoginDialog();
//            getUserInfo(platform);
//        } else {
        mController.doOauthVerify(this, platform, new SocializeListeners.UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                showLoginDialog();
                //ToastUtils.showToast("start");
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                ToastUtils.showToast(getString(R.string.login_error));
                if (loginDialog.isShowing()) {
                    loginDialog.dismiss();
                }

            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if (loginDialog.isShowing()) {
                    loginDialog.dismiss();
                }
                // ToastUtils.showToast("onComplete");
                String uid = value.getString("uid");
                if (!TextUtils.isEmpty(uid)) {
                    getUserInfo(platform);
                } else {
                    ToastUtils.showToast(getString(R.string.login_error));
                }

            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                if (loginDialog.isShowing()) {
                    loginDialog.dismiss();
                }
                //ToastUtils.showToast("取消登录");
            }
        });
    }

    /**
     * 获取授权平台的用户信息</br>
     */
    private void getUserInfo(final SHARE_MEDIA platform) {

        mController.getPlatformInfo(this, platform, new SocializeListeners.UMDataListener() {

            @Override
            public void onStart() {
                showLoginDialog();
            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if (loginDialog.isShowing()) {
                    loginDialog.dismiss();
                }
                if (info != null) {
                    //ToastUtils.showToast("登录成功");
                    // String nickname = info.get("screen_name").toString();
                    //String iconUrl = info.get("profile_image_url").toString();

                    PrefsUtils.putInt(PrefsConfig.LOGIN_STATE, Constants.UM_LOGIN);
                    initUser(info);
                    //setResult(UM_LOGIN_SUCCEED, new Intent().putExtra("screen_name", nickname).putExtra("profile_image_url", iconUrl));
                    // LoginActivity.this.finish();

                } else {
                    ToastUtils.showToast("获取用户信息失败");
                }
            }
        });
    }


    private void initUser(Map<String, Object> info) {
        showLoginDialog();
        //{sex=0, nickname=lol, unionid=oLGz-v26oD5KVdJf6W6DlBk93pDQ, province=, openid=ocCqPtwkyN8cYsvujA7bLd-9SZvg, language=zh_CN, headimgurl=, country=中国, city=}
        //{uid=AF323DA1DD42A3CD01ED0CBAF7796035, gender=男, screen_name=不才��, openid=AF323DA1DD42A3CD01ED0CBAF7796035,
        // profile_image_url=http://qzapp.qlogo.cn/qzapp/100424468/AF323DA1DD42A3CD01ED0CBAF7796035/100, access_token=E85CF319B814068A8D0B85A11108C1E1, verified=0}

        Object urlTemp = info.get("headimgurl");
        if (urlTemp == null) {
            urlTemp = info.get("profile_image_url");
        }
        String url = urlTemp == null ? null : urlTemp.toString();

        Object nameTemp = info.get("nickname");
        if (nameTemp == null) {
            nameTemp = info.get("screen_name");
        }
        String name = nameTemp == null ? null : nameTemp.toString();

        Object openidTemp = info.get("openid");
        if (openidTemp == null) {
            openidTemp = info.get("uid");
        }
        String openid = openidTemp == null ? null : openidTemp.toString();

        Object cityTemp = info.get("city");
        String city = cityTemp == null ? null : cityTemp.toString();

        Object provinceTemp = info.get("province");
        String province = provinceTemp == null ? null : provinceTemp.toString();

        Object sexTemp = info.get("sex");
        if (sexTemp == null) {
            sexTemp = info.get("gender");
        }
        String sex = sexTemp == null ? null : sexTemp.toString();

        if ("0".equals(sex) || "男".equals(sex)) {
            sex = "1";
        } else if ("1".equals(sex) || "女".equals(sex)) {
            sex = "2";
        }

        // http://127.0.0.1/comm/ecstore/oauthLogin
        Ion.with(this).
                load(HttpUrl.OAUTH_LOGIN).
                setBodyParameter("openid", openid).
                setBodyParameter("nickname", name).
                setBodyParameter("url", url).
                setBodyParameter("gender", sex).
                setBodyParameter("province", province).
                setBodyParameter("city", city).
                as(UserInfo.class).setCallback(new FutureCallback<UserInfo>() {
            @Override
            public void onCompleted(Exception e, UserInfo userInfo) {
                LogUtils.e(userInfo + "");
                if (loginDialog.isShowing()) {
                    loginDialog.dismiss();
                }
                if (null != userInfo) {
                    UserInfo.UserInfoData data = userInfo.userInfo;
                    if (data != null) {
                        LoginActivity.this.sendBroadcast(new Intent(UserManager.LOGIN_ACTION).putExtra("userInfo", data));
                        UserManager.saveUserData(LoginActivity.this, data, Constants.UM_LOGIN);
                        LogUtils.e(PrefsUtils.putString(PrefsConfig.USER_DATA, new Gson().toJson(data)) + "");
                        setResult(UM_LOGIN_SUCCEED, new Intent());
                        LoginActivity.this.finish();
                    }
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER && resultCode == RegisterActivity.REGISTER_SUCCEED) {
            //LogUtils.e("注册成功");
            setResult(LOGIN_SUCCEED);
            LoginActivity.this.finish();
        }

        UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

}
