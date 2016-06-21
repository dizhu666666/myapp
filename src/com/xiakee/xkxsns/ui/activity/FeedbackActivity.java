package com.xiakee.xkxsns.ui.activity;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.BaseBean;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.ToastUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 意见反馈页面
 * Created by William on 2015/11/3.
 */
public class FeedbackActivity extends BaseActivity {
    @Bind(R.id.et_content)
    EditText etContent;//反馈内容


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getTitleBar().setTitle(getString(R.string.feedback));
        getTitleBar().showLeftAction(R.drawable.title_back_arrow);
        getTitleBar().showCustomAction_Text(getString(R.string.send));
        getTitleBar().setRightActionOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserManager.isLogin()) {
                    startActivity(new Intent(FeedbackActivity.this, LoginActivity.class));
                    return;
                }

                String content = etContent.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showToast(getString(R.string.content_not_empty));
                } else {
                    //http://127.0.0.1/comm/person/advic?loginUserId=1898&content=3333
                    Ion.with(getApplicationContext()).
                            load(HttpUrl.ADVICE).
                            setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                            setBodyParameter("token", UserManager.getToken()).
                            setBodyParameter("content", content).
                            as(BaseBean.class).
                            setCallback(new FutureCallback<BaseBean>() {
                                @Override
                                public void onCompleted(Exception e, BaseBean baseBean) {
                                    hideSoftKeyboard();
                                    LogUtils.e(baseBean + "");
                                    if (null != baseBean && baseBean.checkData()) {
                                        ToastUtils.showToast(getString(R.string.feedback_succeed));
                                        etContent.getText().clear();
                                    } else {
                                        ToastUtils.showToast(getString(R.string.feedback_error));
                                    }
                                }
                            });
                }
            }
        });
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
