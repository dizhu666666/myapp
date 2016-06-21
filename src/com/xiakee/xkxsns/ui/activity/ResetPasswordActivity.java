package com.xiakee.xkxsns.ui.activity;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.BaseBean;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.ToastUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by William on 2015/11/19.
 */
public class ResetPasswordActivity extends BaseActivity {
    private String lostToken;
    private String memberId;
    private String phone;

    @Bind(R.id.et_new_password)
    EditText etNewPassword;
    @Bind(R.id.et_sure_new_password)
    EditText etSureNewPassword;


    @OnClick(R.id.btn_complete)
    void complete() {
        String newPassword = etNewPassword.getText().toString().trim();
        if (TextUtils.isEmpty(newPassword)) {
            ToastUtils.showToast("请输入新的密码");
            return;
        }

        String sureNewPassword = etSureNewPassword.getText().toString().trim();
        if (TextUtils.isEmpty(sureNewPassword)) {
            ToastUtils.showToast("请确认新的密码");
            return;
        }

        if (!newPassword.equals(sureNewPassword)) {
            ToastUtils.showToast("两次输入的密码不一致");
            return;
        }

        //http://127.0.0.1/comm/ecstore/resetPassword?lostToken=13126506188&memberId=15&phone=13125655555&password=156548
        Ion.with(this).
                load(HttpUrl.FIND_PWD_RESET_PWD).
                setBodyParameter("lostToken", lostToken).
                setBodyParameter("memberId", memberId).
                setBodyParameter("phone", phone).
                setBodyParameter("password", newPassword).
                as(BaseBean.class).
                setCallback(new FutureCallback<BaseBean>() {
                    @Override
                    public void onCompleted(Exception e, BaseBean baseBean) {
                        LogUtils.e(baseBean + "");
                        if (baseBean != null && baseBean.checkData()) {
                            alertDialog("重置密码成功");
                        } else {
                            //重置密码错误
                            alertDialog("重置密码错误");
                        }
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if (i != null) {
            lostToken = i.getStringExtra("lostToken");
            memberId = i.getStringExtra("memberId");
            phone = i.getStringExtra("phone");
            if (TextUtils.isEmpty(lostToken) || TextUtils.isEmpty(memberId) || TextUtils.isEmpty(phone)) {
                return;
            }
        } else {
            return;
        }
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        TitleBar titleBar = getTitleBar();
        titleBar.setTitle(getString(R.string.reset_password));
        titleBar.showLeftAction(R.drawable.title_back_arrow);

    }

    private void alertDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ResetPasswordActivity.this.finish();
            }
        });

        builder.show();
    }
}
