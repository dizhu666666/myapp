package com.xiakee.xkxsns.ui.activity;

import java.lang.reflect.Method;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.util.CommonUtils;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.SettingUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 系统设置页面
 * Created by William on 2015/11/3.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    public static final int GET_CACHE_SIZE = 1231;
    public static final int LOGOUT_SUCCEED = 1211;
    public static final int SETTING_LOGIN = 10024;

    @Bind(R.id.iv_download_choose)
    ImageView ivDownloadChoose;//下载图片
    @Bind(R.id.iv_push_choose)
    ImageView ivPushChoose;//消息通知

    @Bind(R.id.btn_logout)
    Button btnLogout;

    @Bind(R.id.tv_cache_size)
    TextView tvCacheSize;//缓存大小


    @OnClick(R.id.rl_message_push)
    void pushSet() {
        ivPushChoose.setSelected(!ivPushChoose.isSelected());
        SettingUtils.isPushMe(ivPushChoose.isSelected());
    }


    @OnClick(R.id.rl_download_choose)
    void downloadSet() {
        ivDownloadChoose.setSelected(!ivDownloadChoose.isSelected());
        SettingUtils.isDownloadImage(ivDownloadChoose.isSelected());
    }

    /***
     * 意见反馈
     */
    @OnClick(R.id.rl_feedback)
    void feedback() {
        startActivity(new Intent(this, FeedbackActivity.class));
    }

    /***
     * 清除缓存
     */
    @OnClick(R.id.rl_clean_cache)
    void cleanCache() {
        CommonUtils.cleanFile(this.getCacheDir());
        getCacheSize(this);
    }

    /***
     * 关于我们
     */
    @OnClick(R.id.rl_about)
    void about() {

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case GET_CACHE_SIZE:
                    LogUtils.e(msg.obj.toString());
                    tvCacheSize.setText("缓存大小:" + msg.obj.toString());
                    break;

            }

        }
    };

    private boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        getTitleBar().setTitle(getString(R.string.system_setting));
        getTitleBar().showLeftAction(R.drawable.title_back_arrow);
        getCacheSize(this);
        ivDownloadChoose.setSelected(SettingUtils.isDownloadImage());
        ivPushChoose.setSelected(SettingUtils.isPushMe());

        btnLogout.setOnClickListener(this);

        isLogin = UserManager.isLogin();
        btnLogout.setSelected(isLogin);
        if (isLogin) {
            btnLogout.setText(getString(R.string.logout_current_user));
        } else {
            btnLogout.setText(getString(R.string.please_login));
        }

    }

    /***
     * 通过反射获取应用缓存大小，请参见 MyCacheSizeObserver类
     *
     * @param context
     * @return
     */
    private void getCacheSize(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            Method method = pm.getClass().getMethod(
                    "getPackageSizeInfo", String.class,
                    IPackageStatsObserver.class);
            method.invoke(pm, context.getPackageName(), new MyCacheSizeObserver());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                if (isLogin) {
                    if (UserManager.logout(this)) {
                        setResult(LOGOUT_SUCCEED);
                        //ToastUtils.showToast(getString(R.string.logout_succeed));
                        this.finish();
                    } else {
                        //ToastUtils.showToast(getString(R.string.logout_error));
                    }

                } else {
                    startActivityForResult(new Intent(SettingActivity.this, LoginActivity.class), SETTING_LOGIN);
                }


                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_LOGIN) {

            if (resultCode == LoginActivity.LOGIN_SUCCEED || resultCode == LoginActivity.UM_LOGIN_SUCCEED) {
                isLogin = UserManager.isLogin();
                btnLogout.setSelected(isLogin);
                btnLogout.setText(getString(R.string.logout_current_user));
                setResult(resultCode, data);
            }

        }
    }

    /***
     * 通过远程服务获取到应用缓存大小
     */
    class MyCacheSizeObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            String cacheSize = Formatter.formatFileSize(getContext(), pStats.cacheSize);
            handler.sendMessage(handler.obtainMessage(GET_CACHE_SIZE, cacheSize));
        }
    }
}
