package com.xiakee.xkxsns.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iosdialog.IosDialog;
import com.android.iosdialog.SheetItem;
import com.soundcloud.android.crop.Crop;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.BaseBean;
import com.xiakee.xkxsns.bean.UserInfo;
import com.xiakee.xkxsns.ui.view.CircleImageView;
import com.xiakee.xkxsns.util.CommonUtils;
import com.xiakee.xkxsns.util.ImageUtils;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.PicassoUtils;
import com.xiakee.xkxsns.util.ToastUtils;
import com.xiakee.xkxsns.util.UserManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户详情页面，也可以修改用户资料
 * Created by William on 2015/11/3.
 */
public class UserDetailsActivity extends BaseActivity implements IosDialog.OnMyItemClickListner {

    public static final int SET_INFOS_SUCCEED = 2133;

    public static final int SET_DATE = 2111;
    public static final int SET_ADDRESS = 2112;

    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 19;

    @Bind(R.id.tv_nick_name)
    TextView tvNickname;//昵称
    @Bind(R.id.tv_my_level)
    TextView tvLevel;//用户等级
    @Bind(R.id.tv_my_integral)
    TextView tvIntegral;//积分
    @Bind(R.id.iv_icon)
    CircleImageView ivIcon;//用户头像
    @Bind(R.id.tv_my_signature)
    TextView tvSign;//个性签名
    @Bind(R.id.tv_account)
    TextView tvAccount;//登录方式
    @Bind(R.id.tv_my_age)
    TextView tvAge;//年龄
    @Bind(R.id.tv_my_address)
    TextView tvAddress;//所在地
    @Bind(R.id.tv_my_sex)
    TextView tvSex;//性别


    @OnClick(R.id.rl_icon)
    void setIcon() {
        showMenuPop();
    }

    @OnClick(R.id.rl_my_sex)
    void setSex() {
        alertSetSexDialog();
    }

    @OnClick(R.id.rl_my_age)
    void setAge() {
        startActivityForResult(new Intent(this, DateSetActivity.class).
                putExtra("date", mUserData.birthdays).putExtra("age", mUserData.age), SET_DATE);
    }

    @OnClick(R.id.rl_nick_name)
    void setNickname() {
        alertSetNicknameDialog();
    }

    @OnClick(R.id.rl_my_signature)
    void setSign() {
        alertSetSignDialog();
    }

    @OnClick(R.id.rl_my_address)
    void setAddress() {
        startActivityForResult(new Intent(this, CitiesSetActivity.class).
                putExtra("province", mUserData.province).putExtra("city", mUserData.city)
                , SET_ADDRESS);
    }

    private UserInfo.UserInfoData mUserData;
    private String localIconUrl;

    private View mRootView;

    private Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if (i != null) {
            mUserData = i.getParcelableExtra("mUserData");
            if (mUserData == null) {
                return;
            }
            localIconUrl = i.getStringExtra("localIconUrl");
        } else {
            return;
        }
        mRootView = View.inflate(this, R.layout.activity_user_details, null);
        setContentView(mRootView);
        ButterKnife.bind(this);

        resultIntent = new Intent();
        setResult(SET_INFOS_SUCCEED, resultIntent);

        initView();
    }

    private void initView() {
        getTitleBar().setTitle(getResources().getString(R.string.my_information));
        getTitleBar().showLeftAction(R.drawable.title_back_arrow);
        setUserData();
    }


    /***
     * 设置用户信息
     */
    private void setUserData() {
        tvNickname.setText(mUserData.userName);
        tvLevel.setText(String.format(getString(R.string.level), mUserData.lv));
        tvSex.setText("1".equals(mUserData.sex) ? getString(R.string.man) : getString(R.string.woman));
        tvAge.setText(mUserData.age);

        if (TextUtils.isEmpty(mUserData.sign)) {
            tvSign.setText(R.string.not_filled_out);
        } else {
            tvSign.setText(mUserData.sign);
        }

        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(mUserData.province)) {
            sb.append(mUserData.province);
            if (!TextUtils.isEmpty(mUserData.city)) {
                sb.append(" ");
                sb.append(mUserData.city);
            }
        } else {
            sb.append(getString(R.string.not_filled_out));
        }
        tvAddress.setText(sb.toString());

        if (TextUtils.isEmpty(localIconUrl)) {
            UserManager.loadIcon(this, mUserData.photo, ivIcon);
        } else {
            PicassoUtils.loadIcon(this, new File(localIconUrl), ivIcon);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        beginCrop(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists()) {
                    beginCrop(Uri.fromFile(ImageUtils.getSmallBitmap(cameraFile.getAbsolutePath())));
                }

            } else if (requestCode == Crop.REQUEST_CROP) {//裁剪图片，上传头像
                LogUtils.e("裁剪图片，上传头像");
                Uri uri = Crop.getOutput(data);
                String imgPath = CommonUtils.getImgPathByUri(this, uri);
                if (!TextUtils.isEmpty(imgPath)) {
                    uploadImage(new File(imgPath));
                }
            } else if (requestCode == SET_DATE) {
                String birthdays = data.getStringExtra("date");
                String age = data.getStringExtra("age");

                if (!TextUtils.isEmpty(birthdays)) {
                    mUserData.birthdays = birthdays;
                    resultIntent.putExtra("date", mUserData.birthdays);
                }

                if (!TextUtils.isEmpty(age)) {
                    mUserData.age = age;
                    tvAge.setText(age);
                    resultIntent.putExtra("age", mUserData.age);
                }

            } else if (requestCode == SET_ADDRESS) {
                String province = data.getStringExtra("province");
                String city = data.getStringExtra("city");

                if (!TextUtils.isEmpty(province)) {
                    mUserData.province = province;
                    resultIntent.putExtra("province", mUserData.province);
                }

                if (!TextUtils.isEmpty(city)) {
                    mUserData.city = city;
                    resultIntent.putExtra("city", mUserData.city);
                }

                StringBuilder sb = new StringBuilder();
                if (!TextUtils.isEmpty(mUserData.province)) {
                    sb.append(mUserData.province);
                    if (!TextUtils.isEmpty(mUserData.city)) {
                        sb.append(" ");
                        sb.append(mUserData.city);
                    }
                } else {
                    sb.append(getString(R.string.not_filled_out));
                }
                tvAddress.setText(sb.toString());

            }
        }
    }

    private IosDialog iosDialog;

    /***
     * 弹出修改头像菜单
     */
    private void showMenuPop() {
        if (iosDialog == null) {
            iosDialog = new IosDialog(this, this);
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


    /***
     * 设置昵称dialog
     */
    private Dialog setNameDialog;

    private void alertSetNicknameDialog() {
        if (setNameDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View contentView = View.inflate(this, R.layout.dialog_set_nickname, null);
            builder.setView(contentView);

            final EditText etContent = (EditText) contentView.findViewById(R.id.et_content);
            etContent.requestFocus();
            etContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int length = s.length();
                    if (length > 8) {
                        length = 8;
                        CharSequence text = s.subSequence(0, length);
                        etContent.setText(text);
                        etContent.setSelection(length);
                        ToastUtils.showCenterToast("昵称长度不能超过8位哦！");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            TextView tvSure = (TextView) contentView.findViewById(R.id.tv_sure);
            tvSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newNickname = etContent.getText().toString().trim();
                    if (TextUtils.isEmpty(newNickname)) {
                        ToastUtils.showCenterToast(getResources().getString(R.string.nickname_not_empty));
                    } else {
                        setNameDialog.dismiss();
                        setNickname(newNickname);
                        etContent.getText().clear();
                    }
                }
            });

            TextView tvCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setNameDialog.dismiss();
                    etContent.getText().clear();
                }
            });

            //builder.setTitle(getResources().getString(R.string.modify_nickname));
            setNameDialog = builder.create();
        }

        setNameDialog.show();
    }


    /***
     * 设置个性签名dialog
     */
    private Dialog setSignDialog;

    private void alertSetSignDialog() {
        if (setSignDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View contentView = View.inflate(this, R.layout.dialog_set_sign, null);
            builder.setView(contentView);

            final EditText etContent = (EditText) contentView.findViewById(R.id.et_content);
            etContent.requestFocus();
            etContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int length = s.length();
                    if (length > 16) {
                        length = 16;
                        CharSequence text = s.subSequence(0, length);
                        etContent.setText(text);
                        etContent.setSelection(length);
                        ToastUtils.showCenterToast("个性签名长度不能超过16位哦！");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            TextView tvSure = (TextView) contentView.findViewById(R.id.tv_sure);
            tvSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sign = etContent.getText().toString().trim();
//                    if (TextUtils.isEmpty(sign)) {
//                        ToastUtils.showCenterToast(getResources().getString(R.string.signature_not_empty));
//                    } else {
                    setSignDialog.dismiss();
                    setSign(sign);
                    etContent.getText().clear();
                }
            });

            TextView tvCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSignDialog.dismiss();
                    etContent.getText().clear();
                }
            });

            //builder.setTitle(getResources().getString(R.string.modify_signature));
            setSignDialog = builder.create();
        }

        setSignDialog.show();
    }

    private Dialog setSexDialog;
    private int sexWhich;

    private void alertSetSexDialog() {
        if (setSexDialog == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("选择性别");
            sexWhich = "1".equals(mUserData.sex) ? 0 : 1;
            builder.setSingleChoiceItems(new String[]{getString(R.string.man), getString(R.string.woman)},
                    sexWhich,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sexWhich = which;
                        }
                    });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LogUtils.e("sex：" + sexWhich);
                    setSex(Integer.toString(sexWhich + 1));
                }
            });
            setSexDialog = builder.create();
        }
        setSexDialog.show();
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
        Uri destination = Uri.fromFile(new File(this.getCacheDir(), UUID.randomUUID().toString() + ".jpg"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void uploadImage(File file) {
        if (file == null) {
            return;
        }

        LogUtils.e(file.exists() + "" + file.getAbsolutePath());
        if (file.exists()) {
            setIcon(file.getAbsolutePath());
        }

    }


    private ProgressDialog mDialog;

    private void setIcon(final String iconUrl) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setMessage("请稍后");
        }

        mDialog.show();

        Map<String, String> map = new HashMap<String, String>();
        map.put("img", iconUrl);

        UserManager.changeUserInfo(this, map,
                new UserManager.ChangeUserInfoCallback() {
                    @Override
                    public void onSucceed(BaseBean baseBean) {
                        mDialog.dismiss();
                        LogUtils.e("上传头像成功");

                        PicassoUtils.loadIcon(UserDetailsActivity.this, new File(iconUrl), ivIcon);
                        resultIntent.putExtra("localIconPath", iconUrl);
                    }

                    @Override
                    public void onError(String error) {
                        mDialog.dismiss();
                        ToastUtils.showToast(error);
                    }
                });
    }

    private void setNickname(final String nickname) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setMessage("请稍后");
        }

        mDialog.show();

        Map<String, String> map = new HashMap<String, String>();
        map.put("userName", nickname);

        UserManager.changeUserInfo(this, map,
                new UserManager.ChangeUserInfoCallback() {
                    @Override
                    public void onSucceed(BaseBean baseBean) {
                        mDialog.dismiss();
                        tvNickname.setText(nickname);
                        resultIntent.putExtra("nickname", nickname);
                    }

                    @Override
                    public void onError(String error) {
                        mDialog.dismiss();
                        ToastUtils.showToast(error);
                    }
                });
    }

    private void setSign(final String sign) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setMessage("请稍后");
        }

        mDialog.show();

        Map<String, String> map = new HashMap<String, String>();
        map.put("sign", sign);

        UserManager.changeUserInfo(this, map,
                new UserManager.ChangeUserInfoCallback() {
                    @Override
                    public void onSucceed(BaseBean baseBean) {
                        mDialog.dismiss();
                        if (TextUtils.isEmpty(sign)) {
                            tvSign.setText(R.string.not_filled_out);
                        } else {
                            tvSign.setText(sign);
                        }
                        resultIntent.putExtra("sign", sign);
                    }

                    @Override
                    public void onError(String error) {
                        mDialog.dismiss();
                        ToastUtils.showToast(error);
                    }
                });
    }

    private void setSex(final String sex) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
            mDialog.setMessage("请稍后");
        }

        mDialog.show();

        Map<String, String> map = new HashMap<String, String>();
        map.put("sex", sex);

        UserManager.changeUserInfo(this, map,
                new UserManager.ChangeUserInfoCallback() {
                    @Override
                    public void onSucceed(BaseBean baseBean) {
                        mDialog.dismiss();
                        tvSex.setText("1".equals(sex) ? getString(R.string.man) : getString(R.string.woman));
                        resultIntent.putExtra("sex", sex);
                    }

                    @Override
                    public void onError(String error) {
                        mDialog.dismiss();
                        ToastUtils.showToast(error);
                    }
                });
    }

}
