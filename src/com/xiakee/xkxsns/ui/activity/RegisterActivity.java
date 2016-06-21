package com.xiakee.xkxsns.ui.activity;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.SmsCode;
import com.xiakee.xkxsns.bean.UserInfo;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.receiver.SMSContentObserver;
import com.xiakee.xkxsns.util.CommonUtils;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.ToastUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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

/***
 * 注册页面
 *
 * @author Administrator
 */
public class RegisterActivity extends BaseActivity {
    private final static String TAG = RegisterActivity.class.getName();
    public static final int REGISTER_SUCCEED = 3213213;

    private static final int GET_SMS_CODE = 1;
    private static final int INIT_TIME = 60;

    private SMSContentObserver mSmsReceiver;
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
    @Bind(R.id.et_input_pwd)
    EditText mEtPassword;
    @Bind(R.id.et_sms_code)
    EditText mEtCode;

    private Button btnGetCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        String phoneNumber = CommonUtils.getPhoneNumber();
        if (!TextUtils.isEmpty(phoneNumber)) {
            if (phoneNumber.startsWith("+86")) {
                phoneNumber = phoneNumber.substring("+86".length() - 1);
            }
            mEtPhoneNum.setText(phoneNumber);
            mEtPhoneNum.setSelection(phoneNumber.length());
        }

        getTitleBar().setTitle(getString(R.string.register));
        getTitleBar().showLeftAction(R.drawable.title_back_arrow);

    }

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

    @OnClick(R.id.btn_register)
    void register() {
        hideSoftKeyboard();
        final Dialog dialog = showRegisterDialog();

        String phoneNumber = mEtPhoneNum.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        String smsCode = mEtCode.getText().toString().trim();

        UserManager.register(this, phoneNumber, password, smsCode, new UserManager.RegisterCallback() {
            @Override
            public void onSucceed(UserInfo.UserInfoData userInfoData) {
                dialog.dismiss();
                setResult(REGISTER_SUCCEED);
                RegisterActivity.this.finish();
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                ToastUtils.showToast(error);
            }
        });
    }

    /***
     * 获取短信验证码
     *
     * @return
     */
    private void getSmsCode(Activity activity, String phoneNumber) {
        LogUtils.e("点击");
        //实例化短信监听器
        if (mSmsReceiver == null) {
            mSmsReceiver = new SMSContentObserver(activity, new Handler(), mEtCode);
            // 注册短信变化监听
            activity.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mSmsReceiver);
        }

        //  http://127.0.0.1/comm/ecstore/sendCode?phone=13126506185

        handler.sendEmptyMessage(GET_SMS_CODE);
        btnGetCode.setEnabled(false);// 设置button不可用
        btnGetCode.setTextColor(Color.GRAY);

        Ion.with(activity).
                load(HttpUrl.GET_SMS_CODE).
                setBodyParameter("phone", phoneNumber).
                as(SmsCode.class).
                setCallback(new FutureCallback<SmsCode>() {
                    @Override
                    public void onCompleted(Exception e, SmsCode smsCode) {
                        LogUtils.e(smsCode + "");
                        if (smsCode != null) {
                            errorDialog(smsCode.resmsg);

                            handler.removeCallbacksAndMessages(null);
                            btnGetCode.setEnabled(true);
                            btnGetCode.setTextColor(Color.BLACK);
                            btnGetCode.setText("获取验证码");
                        }
                    }
                });
    }


    private Dialog errorDialog;

    private void errorDialog(String text) {
        if (errorDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setPositiveButton(R.string.sure, null);
            builder.setNegativeButton(R.string.cancel, null);
            errorDialog = builder.create();
        }
        errorDialog.setTitle(text);
        errorDialog.show();
    }

    private ProgressDialog registerDialog;

    /***
     * 弹出正在注册对话框
     *
     * @return
     */
    private Dialog showRegisterDialog() {
        if (registerDialog == null) {
            registerDialog = new ProgressDialog(this);
            registerDialog.setMessage(getString(R.string.being_registered));
        }

        registerDialog.show();
        return registerDialog;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 如果验证短信接收失败，在页面结束的时候也应该解除注册
        if (mSmsReceiver != null) {
            //解除注册
            this.getContentResolver().unregisterContentObserver(mSmsReceiver);
            mSmsReceiver = null;
        }
    }
}
