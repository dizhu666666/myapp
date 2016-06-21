package com.xiakee.xkxsns.ui.activity;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.BaseBean;
import com.xiakee.xkxsns.bean.FindPassword;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.util.CommonUtils;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.ToastUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 找回密码页面
 * Created by William on 2015/11/19.
 */
public class FindPasswordActivity extends BaseActivity {

    private static final int GET_SMS_CODE = 1;
    private static final int INIT_TIME = 60;

    private int countDown = INIT_TIME;// 再次发送短信验证倒计时时长

    String againSend = "重新获取(%d)";

    private Handler handler = new Handler() {
        @Override
		public void handleMessage(Message msg) {

            switch (msg.what) {
                case GET_SMS_CODE:
                    if (countDown > 0) {
                        // 设置时间
                        btnGetCode.setText(String.format(againSend, countDown));
                        countDown--;
                        handler.sendEmptyMessageDelayed(GET_SMS_CODE, 1000);// 每隔一秒再次发送消息
                    } else {
                        // 设置获取验证码button可用
                        btnGetCode.setEnabled(true);
                        countDown = INIT_TIME;// 初始化倒计时长
                        btnGetCode.setText("重新获取");
                        btnGetCode.setTextColor(Color.BLACK);
                    }
                    break;

                default:
                    break;
            }

        }
    };


    @Bind(R.id.et_phone_number)
    EditText mEtPhoneNum;
    @Bind(R.id.et_sms_code)
    EditText mEtCode;

    private Button btnGetCode;

    @OnClick(R.id.btn_get_code)
    void getSMSCode(Button btn) {
        if (btnGetCode == null) {
            btnGetCode = btn;
        }
        String phoneNum = mEtPhoneNum.getText().toString().trim();

        if (!TextUtils.isEmpty(phoneNum)) {
            if (CommonUtils.mobileNumberCheck(phoneNum)) {
                getSmsCode(this, phoneNum);
            } else {
                ToastUtils.showToast(getString(R.string.please_input_right_phone));
            }

        } else {
            ToastUtils.showToast(getString(R.string.phone_not_empty));
        }
    }

    @OnClick(R.id.btn_complete)
    void complete() {
        hideSoftKeyboard();

        String phoneNumber = null;
        String smsCode = null;
        boolean checkResult = false;
        phoneNumber = mEtPhoneNum.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNumber)) {
            smsCode = mEtCode.getText().toString().trim();
            if (!TextUtils.isEmpty(smsCode)) {
                checkResult = true;
            } else {
                ToastUtils.showToast(getString(R.string.please_input_code));
            }

        } else {
            ToastUtils.showToast(getString(R.string.please_input_phone));
        }
        if (checkResult) {
            final Dialog dialog = showDialog();
            //http://127.0.0.1/comm/ecstore/lostVerifyCode?phone=13126506188&vcode=195596
            final String finalPhoneNumber = phoneNumber;
            Ion.with(this).
                    load(HttpUrl.FIND_PWD_CHECK_CODE).
                    setBodyParameter("phone", finalPhoneNumber).
                    setBodyParameter("vcode", smsCode).
                    as(FindPassword.class).
                    setCallback(new FutureCallback<FindPassword>() {
                        @Override
                        public void onCompleted(Exception e, FindPassword findPassword) {
                            LogUtils.e(findPassword + "");
                            if (null != findPassword) {
                                String lostToken = findPassword.lostToken;
                                String memberId = findPassword.memberId;
                                startActivity(new Intent(FindPasswordActivity.this, ResetPasswordActivity.class).
                                        putExtra("lostToken", lostToken).putExtra("memberId", memberId)
                                        .putExtra("phone", finalPhoneNumber));
                                FindPasswordActivity.this.finish();
                            } else {
                                ToastUtils.showToast(getString(R.string.error));
                            }
                            dialog.dismiss();
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        TitleBar titleBar = getTitleBar();
        titleBar.setTitle(getString(R.string.find_password));
        titleBar.showLeftAction(R.drawable.title_back_arrow);

    }

    /***
     * 获取短信验证码
     *
     * @return
     */
    private void getSmsCode(Activity activity, String phoneNumber) {
        LogUtils.e("点击");

        //    http://127.0.0.1/comm/ecstore/lostSendCode?phone=13126506188

        handler.sendEmptyMessage(GET_SMS_CODE);
        btnGetCode.setEnabled(false);// 设置button不可用
        btnGetCode.setTextColor(Color.GRAY);

        Ion.with(activity).
                load(HttpUrl.FIND_PWD_SEND_CODE).
                setBodyParameter("phone", phoneNumber).
                as(BaseBean.class).
                setCallback(new FutureCallback<BaseBean>() {
                    @Override
                    public void onCompleted(Exception e, BaseBean baseBean) {
                        LogUtils.e(baseBean + "");
                        if (baseBean != null && baseBean.checkData()) {

                        } else {
                            //发送验证码出错
                        }
                    }
                });
    }

    private ProgressDialog dialog;

    /***
     * 弹出对话框
     *
     * @return
     */
    private Dialog showDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.please_waite));
        }
        dialog.show();
        return dialog;
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

}
