package com.xiakee.xkxsns.util;

import java.io.File;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.BaseBean;
import com.xiakee.xkxsns.bean.UserInfo;
import com.xiakee.xkxsns.global.Constants;
import com.xiakee.xkxsns.global.PrefsConfig;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.activity.LoginActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by William on 2015/11/9.
 */
public class UserManager {
    public static String LOGIN_ACTION = "com.xiakee.login.change";


    /***
     * 检查登录验证
     *
     * @param context
     */
    public static void checkLogin(final Context context) {
        if (CommonUtils.isNetworkAvailable(context)) {
            if (isLogin()) {
                // http://127.0.0.1/comm/person/vToken?loginUserId=1898&token=12312kkljdklf
                Ion.with(context).
                        load(HttpUrl.CHECK_LOGIN).
                        setBodyParameter("loginUserId", getLoginUserId()).
                        setBodyParameter("token", getToken()).as(BaseBean.class).
                        setCallback(new FutureCallback<BaseBean>() {
                            @Override
                            public void onCompleted(Exception e, BaseBean baseBean) {
                                if (baseBean == null || !baseBean.checkData()) {
                                    if (logout(context)) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("登录验证失败，请重新登录");
                                        builder.setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                            }
                                        });
                                        builder.setNegativeButton(context.getString(R.string.cancel), null);
                                        builder.show();
                                    }

                                }
                            }
                        });
            }
        }
    }

    /***
     * 访问网络加载数据
     */
    public static void loadUserInfo(final Context context, final LoadUserInfoCallback callBack) {
        if (CommonUtils.isNetworkAvailable(context)) {
            LogUtils.e("有网络连接");
            Ion.with(context).
                    load(HttpUrl.GET_USER_INFO).
                    setBodyParameter("loginUserId", getLoginUserId()).
                    as(UserInfo.class).
                    setCallback(new FutureCallback<UserInfo>() {
                        @Override
                        public void onCompleted(Exception e, UserInfo userInfo) {
                            if (null != userInfo) {
                                UserInfo.UserInfoData userData = userInfo.userInfo;
                                if (userData != null) {
                                    PrefsUtils.putString(PrefsConfig.USER_DATA, new Gson().toJson(userData));
                                    if (callBack != null) {
                                        callBack.onSucceed(userData);
                                    }
                                } else {
                                    if (callBack != null) {
                                        callBack.onError(userInfo.resmsg);
                                    }
                                }
                            } else {
                                UserInfo.UserInfoData userData = getUserData();
                                if (callBack != null) {
                                    if (userData != null) {
                                        callBack.onSucceed(userData);
                                    } else {
                                        callBack.onError("load error");
                                    }

                                }
                            }
                        }
                    });
        } else {
            LogUtils.e("无网络连接");
            UserInfo.UserInfoData userData = getUserData();
            if (callBack != null) {
                if (userData != null) {
                    callBack.onSucceed(userData);
                } else {
                    callBack.onError("load error");
                }

            }
        }

    }


    public static UserInfo.UserInfoData getUserData() {
        String jsonData = PrefsUtils.getString(PrefsConfig.USER_DATA, null);
        LogUtils.e(jsonData + "");
        UserInfo.UserInfoData userInfo = null;

        if (!TextUtils.isEmpty(jsonData)) {
            Gson gson = new Gson();
            userInfo = gson.fromJson(jsonData, UserInfo.UserInfoData.class);

        }
        return userInfo;
    }

    public static String getLoginUserId() {

        return PrefsUtils.getString(PrefsConfig.LOGIN_USER_ID, "");
    }

    public static String getUserName() {

        return PrefsUtils.getString(PrefsConfig.USER_NAME, null);
    }

    public static String getToken() {

        return PrefsUtils.getString(PrefsConfig.TOKEN, null);
    }

    public static String getLocalUserIcon() {
        return PrefsUtils.getString(PrefsConfig.LOCAL_ICON_URL, null);
    }

    public static boolean logout(Context context) {
        boolean flag = PrefsUtils.clear();
        if (flag) {
            context.sendBroadcast(new Intent(LOGIN_ACTION));
            JPushInterface.setAlias(context, null, null);
        }
        return flag;
    }

    public static boolean isLogin() {
        int loginState = PrefsUtils.getInt(PrefsConfig.LOGIN_STATE, Constants.NO_LOGIN);
        boolean isLogin = false;
        if (loginState == Constants.MY_LOGIN || loginState == Constants.UM_LOGIN) {
            String loginUserId = getLoginUserId();
            String token = getToken();
            isLogin = !TextUtils.isEmpty(loginUserId) && !TextUtils.isEmpty(token);
            LogUtils.e("loginUserId:" + loginUserId);
        }

        return isLogin;
    }

    public static void register(final Context context, String username, String password, String code, final RegisterCallback callBack) {
        boolean checkResult = false;

        if (!TextUtils.isEmpty(username)) {

            if (!CommonUtils.mobileNumberCheck(username)) {
                if (callBack != null) {
                    callBack.onError(context.getString(R.string.please_input_right_phone));
                }
                return;
            }

            if (!TextUtils.isEmpty(password)) {

                if (!TextUtils.isEmpty(code)) {
                    checkResult = true;
                } else {
                    if (callBack != null) {
                        callBack.onError(context.getString(R.string.please_input_code));
                    }
                }

            } else {
                if (callBack != null) {
                    callBack.onError(context.getString(R.string.please_input_password));
                }
            }
        } else {
            if (callBack != null) {
                callBack.onError(context.getString(R.string.please_input_phone));
            }
        }
        if (checkResult) {
            //http://127.0.0.1/comm/person/register?phone=13126590618&passWord=99923&code=906668
            Ion.with(context).
                    load(HttpUrl.REGISTER).
                    setBodyParameter("phone", username).
                    setBodyParameter("passWord", password).
                    setBodyParameter("code", code).
                    as(UserInfo.class).
                    setCallback(new FutureCallback<UserInfo>() {
                        @Override
                        public void onCompleted(Exception e, UserInfo userInfo) {

                            LogUtils.e(userInfo + "");
                            if (null != userInfo) {
                                UserInfo.UserInfoData data = userInfo.userInfo;
                                if (data != null) {
                                    if (!TextUtils.isEmpty(data.userId)) {
                                        saveUserData(context, data, Constants.MY_LOGIN);
                                        context.sendBroadcast(new Intent(LOGIN_ACTION).putExtra("userInfo", data));
                                        if (callBack != null) {
                                            callBack.onSucceed(data);
                                        }
                                    } else {
                                        if (callBack != null) {
                                            callBack.onError(userInfo.resmsg);
                                        }
                                    }
                                } else {
                                    if (callBack != null) {
                                        callBack.onError(userInfo.resmsg);
                                    }
                                }

                            } else {
                                if (callBack != null) {
                                    callBack.onError(context.getString(R.string.network_error));
                                }
                            }
                        }
                    });
        }
    }

    public static void login(final Context context, String username, String password, final LoginCallback callBack) {

        if (TextUtils.isEmpty(username)) {
            if (callBack != null) {
                callBack.onError(context.getString(R.string.please_input_username));
            }
        } else {
            if (!CommonUtils.mobileNumberCheck(username)) {
                if (callBack != null) {
                    callBack.onError(context.getString(R.string.please_input_right_phone));
                }
                return;
            }

            if (TextUtils.isEmpty(password)) {
                if (callBack != null) {
                    callBack.onError(context.getString(R.string.please_input_password));
                }
            } else {
                //http://127.0.0.1/comm/ecstore/login?account=13126506185&passWord=99923
                Ion.with(context).
                        load(HttpUrl.LOGIN).
                        setBodyParameter("account", username).
                        setBodyParameter("passWord", password).
                        as(UserInfo.class).
                        setCallback(new FutureCallback<UserInfo>() {
                            @Override
                            public void onCompleted(Exception e, UserInfo userInfo) {

                                if (null != userInfo) {
                                    UserInfo.UserInfoData data = userInfo.userInfo;
                                    if (data != null) {
                                        if (!TextUtils.isEmpty(data.userId)) {
                                            saveUserData(context, data, Constants.MY_LOGIN);
                                            context.sendBroadcast(new Intent(LOGIN_ACTION).putExtra("userInfo", data));
                                            if (callBack != null) {
                                                callBack.onSucceed(data);
                                            }
                                        } else {
                                            if (callBack != null) {
                                                callBack.onError(userInfo.resmsg);
                                            }
                                        }
                                    } else {
                                        if (callBack != null) {
                                            callBack.onError(userInfo.resmsg);
                                        }
                                    }
                                } else {

                                    if (callBack != null) {
                                        callBack.onError(context.getString(R.string.network_error));
                                    }
                                }
                            }
                        });
            }

        }
    }

    public static void sign(final Context context, final SignCallback callback) {
        //http://127.0.0.1/comm/person/signIn?loginUserId=1898
        Ion.with(context).
                load(HttpUrl.SIGN).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                setBodyParameter("token", UserManager.getToken()).
                as(BaseBean.class).
                setCallback(new FutureCallback<BaseBean>() {
                    @Override
                    public void onCompleted(Exception e, BaseBean baseBean) {
                        if (null != baseBean) {
                            if (baseBean.checkData()) {
                                if (callback != null) {
                                    callback.onSucceed(baseBean);
                                }
                            } else {
                                if (callback != null) {
                                    callback.onError(baseBean.resmsg);
                                }
                            }
                        } else {
                            if (callback != null) {
                                callback.onError(context.getString(R.string.network_error));
                            }
                        }
                    }
                });
    }


    public static void loadIcon(final Context context, String iconUrl, final ImageView target) {
        String fileName = CommonUtils.MD5Encoding(iconUrl);

        File localIconUrl = new File(context.getCacheDir(), fileName);
        if (localIconUrl.exists()) {
            PrefsUtils.putString(PrefsConfig.LOCAL_ICON_URL, localIconUrl.getAbsolutePath());
            LogUtils.e("头像已经保存在本地" + localIconUrl.getAbsolutePath());
            if (target != null) {
                PicassoUtils.loadIcon(context, localIconUrl, target);
            }
        } else {
            Ion.with(context).load(HttpUrl.HOST + iconUrl).write(localIconUrl).
                    setCallback(new FutureCallback<File>() {
                        @Override
                        public void onCompleted(Exception e, File file) {
                            if (file != null && file.exists()) {
                                PrefsUtils.putString(PrefsConfig.LOCAL_ICON_URL, file.getAbsolutePath());
                                if (target != null) {
                                    PicassoUtils.loadIcon(context, file, target);
                                }
                            }
                        }
                    });
        }

    }

    public static void saveUserData(Context context, UserInfo.UserInfoData data, int loginStatus) {

        PrefsUtils.putString(PrefsConfig.LOGIN_USER_ID, data.userId);
        PrefsUtils.putString(PrefsConfig.TOKEN, data.token);
        PrefsUtils.putString(PrefsConfig.USER_NAME, data.userName);
        PrefsUtils.putInt(PrefsConfig.LOGIN_STATE, loginStatus);

        JPushInterface.setAlias(context, getLoginUserId(), null);
    }


    public static void changeUserInfo(final Context context, Map<String, String> map,
                                      final ChangeUserInfoCallback callback) {
        Builders.Any.M m = Ion.with(context).
                load(HttpUrl.CHANGE_USER_INFO).
                setMultipartParameter("loginUserId", UserManager.getLoginUserId()).
                setMultipartParameter("token", UserManager.getToken());

        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        int size = entrySet.size();
        if (size > 0) {
            for (Map.Entry<String, String> entry : entrySet) {
                String key = entry.getKey();
                String value = entry.getValue();

                if ("img".equals(key)) {
                    File iconFile = new File(value);
                    if (iconFile.exists()) {
                        m.setMultipartFile(key, "Application/jpeg", iconFile);
                    }
                } else {
                    m.setMultipartParameter(key, value);
                }

            }

            m.as(BaseBean.class).setCallback(new FutureCallback<BaseBean>() {
                @Override
                public void onCompleted(Exception e, BaseBean baseBean) {
                    LogUtils.e(baseBean + "");
                    if (null != baseBean) {
                        if (baseBean.checkData()) {
                            if (callback != null) {
                                callback.onSucceed(baseBean);
                            }
                        } else {
                            if (callback != null) {
                                callback.onError(context.getResources().getString(R.string.modify_error));
                            }
                        }

                    } else {
                        if (callback != null) {
                            callback.onError(context.getString(R.string.network_error));
                        }
                    }
                }
            });
        } else {
            if (callback != null) {
                callback.onError("all args is empty");
            }
        }

    }


    public interface LoginCallback {
        void onSucceed(UserInfo.UserInfoData userInfoData);

        void onError(String error);
    }

    public interface RegisterCallback {
        void onSucceed(UserInfo.UserInfoData userInfoData);

        void onError(String error);
    }

    public interface LoadUserInfoCallback {
        void onSucceed(UserInfo.UserInfoData userInfoData);

        void onError(String error);
    }

    public interface SignCallback {
        void onSucceed(BaseBean baseBean);

        void onError(String error);
    }

    public interface ChangeUserInfoCallback {
        void onSucceed(BaseBean baseBean);

        void onError(String error);
    }
}
