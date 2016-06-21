package com.xiakee.xkxsns.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.xiakee.xkxsns.R;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by William on 2015/12/10.
 */
public class TopicTextEditActivity extends BaseActivity {

    @Bind(R.id.et_content)
    EditText etContent;

    @OnClick(R.id.tv_cancel)
    void calcel() {
        this.finish();
        this.overridePendingTransition(0, R.anim.ap_in);
    }

    @OnClick(R.id.tv_sure)
    void sure() {
        String contnet = etContent.getText().toString().trim();
        setResult(RESULT_OK, new Intent().putExtra("content", contnet).putExtra("position", position));
        this.finish();
        this.overridePendingTransition(0, R.anim.ap_in);
    }

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_text_edit);
        ButterKnife.bind(this);
        initTitleBar();

        Intent i = getIntent();
        if (i != null) {
            position = i.getIntExtra("position", -1);
            if (position < 0) {
                return;
            }

            String content = i.getStringExtra("content");
            if (!TextUtils.isEmpty(content)) {
                etContent.setText(content);
            }
        } else {
            return;
        }

        etContent.setFocusable(true);
        etContent.setFocusableInTouchMode(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        etContent.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
			public void run() {
                showSoftKeyboard();
            }
        }, 200);
    }

    private void initTitleBar() {
        TitleBar titleBar = getTitleBar();
        TextView ostAction = titleBar.showRightAction(R.string.post, -1);
        ostAction.setTextColor(getResources().getColor(R.color.gray));
    }

    private InputMethodManager inputMethodManager;

    /***
     * 弹出软键盘
     */
    void showSoftKeyboard() {
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        inputMethodManager.showSoftInput(etContent, 0);
    }

    @Override
	public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, R.anim.ap_in);
    }
}
