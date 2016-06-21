package com.xiakee.xkxsns.ui.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.android.iosdialog.IosDialog;
import com.android.iosdialog.SheetItem;
import com.soundcloud.android.crop.Crop;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.BaseBean;
import com.xiakee.xkxsns.bean.UserInfo;
import com.xiakee.xkxsns.bean.UserInfo.UserInfoData;
import com.xiakee.xkxsns.ui.activity.LoginActivity;
import com.xiakee.xkxsns.ui.activity.MyFansActivity;
import com.xiakee.xkxsns.ui.activity.MyFocusActivity;
import com.xiakee.xkxsns.ui.activity.MyFocusLabelsActivity;
import com.xiakee.xkxsns.ui.activity.MyTopicActivity;
import com.xiakee.xkxsns.ui.activity.SettingActivity;
import com.xiakee.xkxsns.ui.activity.UserDetailsActivity;
import com.xiakee.xkxsns.ui.view.CircleImageView;
import com.xiakee.xkxsns.util.CommonUtils;
import com.xiakee.xkxsns.util.ImageUtils;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.PicassoUtils;
import com.xiakee.xkxsns.util.ToastUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;

public class FragmentMy extends BaseFragment implements View.OnClickListener, IosDialog.OnMyItemClickListner {
    public static final int LOGIN = 3213;
    public static final int SETTING = 1213;
    public static final int SET_USER_INFO = 2111;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;

    @Bind(R.id.tv_nickname)
    TextView tvNickname;//昵称
    @Bind(R.id.tv_level)
    TextView tvLevel;//用户等级
    @Bind(R.id.iv_icon)
    CircleImageView ivIcon;//用户头像
    @Bind(R.id.iv_sign)
    ImageView ivSign;//签到

    @Bind(R.id.rl_user_details)
    RelativeLayout rlUserDetails;
    @Bind(R.id.ll_please_login)
    LinearLayout llPleaseLogin;


    private UserInfoData mUserData;
    private File iconFile;

    private MyLoginStatusChangeReceiver mReceiver;

    /***
     * 点击进入用户详情
     */
    @OnClick(R.id.rl_user_details)
    void clickUserDetails() {
        startActivityForResult(new Intent(mActivity, UserDetailsActivity.class).
                putExtra("mUserData", mUserData).putExtra("localIconUrl",
                iconFile == null ? null : iconFile.getAbsolutePath()), SET_USER_INFO);
    }

    /***
     * 我的帖子
     */
    @OnClick(R.id.rl_my_post)
    void clickMyPost() {
        if (UserManager.isLogin()) {
            startActivity(new Intent(mActivity, MyTopicActivity.class));
        } else {
            startLoginActivity();
        }

    }

    /***
     * 我的粉丝
     */
    @OnClick(R.id.rl_my_fans)
    void clickMyFans() {
        if (UserManager.isLogin()) {
            startActivity(new Intent(mActivity, MyFansActivity.class));
        } else {
            startLoginActivity();
        }
    }

    /***
     * 我关注的人
     */
    @OnClick(R.id.rl_my_focus)
    void clickMyFocus() {
        if (UserManager.isLogin()) {
            startActivity(new Intent(mActivity, MyFocusActivity.class));
        } else {
            startLoginActivity();
        }
    }

    /***
     * 我关注的标签
     */
    @OnClick(R.id.rl_my_labels)
    void clickMyLabels() {
        if (UserManager.isLogin()) {
            startActivity(new Intent(mActivity, MyFocusLabelsActivity.class));
        } else {
            startLoginActivity();
        }
    }


    /***
     * 系统设置
     */
    @OnClick(R.id.rl_my_setting)
    void clickMySetting() {
        startActivityForResult(new Intent(mActivity, SettingActivity.class), SETTING);
    }

    @Override
    public View onCreateRootView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragement_my, null);
    }

    @Override
    public void onCreateFinished() {
        llPleaseLogin.setOnClickListener(this);
        ivIcon.setOnClickListener(this);
        ivSign.setOnClickListener(this);

        if (mReceiver == null) {
            mReceiver = new MyLoginStatusChangeReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(UserManager.LOGIN_ACTION);
            mActivity.registerReceiver(mReceiver, filter);
        }

        refreshView(null);
    }

    /***
     * 在登录和未登陆状态view显示刷新
     */
    private void refreshView(UserInfoData data) {
        boolean isLogin = UserManager.isLogin();

        rlUserDetails.setEnabled(isLogin);
        llPleaseLogin.setEnabled(!isLogin);
        if (isLogin) {
            rlUserDetails.setVisibility(View.VISIBLE);
            llPleaseLogin.setVisibility(View.GONE);
            if (data != null) {
                mUserData = data;
                tvNickname.setText(mUserData.userName);
                tvLevel.setText(String.format(getString(R.string.level), mUserData.lv));
                tvLevel.setSelected("1".equals(mUserData.sex));
                UserManager.loadIcon(mActivity, mUserData.photo, ivIcon);

                ivSign.setEnabled("0".equals(mUserData.signStatus));
            } else {
                setUserData();
            }

        } else {
            rlUserDetails.setVisibility(View.INVISIBLE);
            llPleaseLogin.setVisibility(View.VISIBLE);
            mUserData = null;//退出登录后赋值为null
        }

    }

    /***
     * 获取用户信息
     */
    private void setUserData() {
        UserManager.loadUserInfo(mActivity, new UserManager.LoadUserInfoCallback() {
            @Override
            public void onSucceed(UserInfo.UserInfoData userInfoData) {
                LogUtils.e(userInfoData + "");
                mUserData = userInfoData;

                tvNickname.setText(mUserData.userName);
                tvLevel.setText(String.format(getString(R.string.level), mUserData.lv));
                tvLevel.setSelected("1".equals(mUserData.sex));
                UserManager.loadIcon(mActivity, mUserData.photo, ivIcon);

                ivSign.setEnabled("0".equals(mUserData.signStatus));
            }

            @Override
            public void onError(String error) {
                LogUtils.e(error);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN) {
            if (resultCode == LoginActivity.LOGIN_SUCCEED) {
                // refreshView();
            } else if (resultCode == LoginActivity.UM_LOGIN_SUCCEED) {
                String nickname = data.getStringExtra("screen_name");
                String iconUrl = data.getStringExtra("profile_image_url");
                //refreshView(null);
            }

        } else if (requestCode == SETTING) {
            if (resultCode == SettingActivity.LOGOUT_SUCCEED) {
                startLoginActivity();
                //refreshView();
            } else if (resultCode == LoginActivity.LOGIN_SUCCEED) {
                //refreshView();
            } else if (resultCode == LoginActivity.UM_LOGIN_SUCCEED) {
                String nickname = data.getStringExtra("screen_name");
                String iconUrl = data.getStringExtra("profile_image_url");
                //refreshView(null);
            }

        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        beginCrop(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists()) {
                    LogUtils.e("发送照片!!!!!!!!!!!!!");
                    beginCrop(Uri.fromFile(ImageUtils.getSmallBitmap(cameraFile.getAbsolutePath())));
                }

            } else if (requestCode == Crop.REQUEST_CROP) {//裁剪图片，上传头像
                LogUtils.e("裁剪图片，上传头像");
                Uri uri = Crop.getOutput(data);
                final String imgPath = CommonUtils.getImgPathByUri(mActivity, uri);

                Map<String, String> map = new HashMap<String, String>();
                map.put("img", imgPath);
                UserManager.changeUserInfo(mActivity, map,
                        new UserManager.ChangeUserInfoCallback() {
                            @Override
                            public void onSucceed(BaseBean baseBean) {
                                File file = new File(imgPath);
                                if (file.exists()) {
                                    iconFile = file;
                                    PicassoUtils.loadIcon(mActivity, iconFile, ivIcon);
                                }

                            }

                            @Override
                            public void onError(String error) {
                                ToastUtils.showToast(mActivity.getResources().getString(R.string.upload_img_error));
                            }
                        });
            }
        } else if (requestCode == SET_USER_INFO) {
            if (resultCode == UserDetailsActivity.SET_INFOS_SUCCEED) {
                String localIconPath = data.getStringExtra("localIconPath");
                String nickname = data.getStringExtra("nickname");
                String sign = data.getStringExtra("sign");
                String date = data.getStringExtra("date");
                String age = data.getStringExtra("age");
                String province = data.getStringExtra("province");
                String city = data.getStringExtra("city");
                String sex = data.getStringExtra("sex");

                if (!TextUtils.isEmpty(localIconPath)) {
                    File file = new File(localIconPath);
                    if (file.exists()) {
                        iconFile = file;
                        PicassoUtils.loadIcon(mActivity, iconFile, ivIcon);
                    }
                }

                if (!TextUtils.isEmpty(nickname)) {
                    mUserData.userName = nickname;
                    tvNickname.setText(nickname);
                }
                if (!TextUtils.isEmpty(sign)) {
                    mUserData.sign = sign;
                }

                if (!TextUtils.isEmpty(date)) {
                    mUserData.birthdays = date;
                }
                if (!TextUtils.isEmpty(age) && Integer.parseInt(age) > 0) {
                    mUserData.age = age;
                }

                if (!TextUtils.isEmpty(province)) {
                    mUserData.province = province;
                }

                if (!TextUtils.isEmpty(city)) {
                    mUserData.city = city;
                }

                if (!TextUtils.isEmpty(sex)) {
                    mUserData.sex = sex;
                }

            }

        }
    }


    private IosDialog iosDialog;

    /***
     * 弹出修改头像菜单
     */
    private void showMenuPop() {
        if (iosDialog == null) {
            iosDialog = new IosDialog(mActivity, this);
            List<SheetItem> listSheetItems = new ArrayList<SheetItem>();
            listSheetItems.add(new SheetItem(this.getResources().getString(R.string.take_pic), 1));
            listSheetItems.add(new SheetItem(this.getResources().getString(R.string.select_from_album), 2));
            iosDialog.setSheetItems(listSheetItems);

            iosDialog.getContentView().setFocusable(true);
            iosDialog.getContentView().setFocusableInTouchMode(true);
            iosDialog.getContentView().setOnKeyListener(new android.view.View.OnKeyListener() {
                @Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
                    LogUtils.e(" iosDialog");
                    if (keyCode == KeyEvent.KEYCODE_MENU && iosDialog.isShowing()) {
                        iosDialog.dismiss();
                        LogUtils.e(" iosDialog.dismiss();");
                        return true;
                    }
                    return false;
                }
            });
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
            Toast.makeText(mActivity, st, Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()
                + "/xiakee");
        cameraFile = new File(file, System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();

        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).
                        putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
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

    private void beginCrop(Uri source) {
        File cropFile = new File(mActivity.getCacheDir(), UUID.randomUUID().toString() + ".jpg");
        Uri destination = Uri.fromFile(cropFile);
        Crop.of(source, destination).asSquare().start(mActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_please_login:
                startLoginActivity();
                break;

            case R.id.iv_icon:
                showMenuPop();
                break;

            case R.id.iv_sign:
                UserManager.sign(mActivity, new UserManager.SignCallback() {
                    @Override
                    public void onSucceed(BaseBean baseBean) {
                        ivSign.setEnabled(false);
                    }

                    @Override
                    public void onError(String error) {
                        LogUtils.e(error);
                    }
                });

                break;

        }
    }

    private void startLoginActivity() {
        startActivityForResult(new Intent(mActivity, LoginActivity.class), LOGIN);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //当fragment调用onDestroyView()方法时，绑定的view都是null，此时应该注销广播，防止空指针
        if (mReceiver != null) {
            mActivity.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private class MyLoginStatusChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            UserInfoData data = intent.getParcelableExtra("userInfo");
            refreshView(data);
        }
    }
}
