package com.xiakee.xkxsns.ui.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.BaseBean;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/11/27.
 */
public class DateSetActivity extends BaseActivity implements DatePicker.OnDateChangedListener, View.OnClickListener {

    @Bind(R.id.date_picker)
    DatePicker mDatePicker;

    @Bind(R.id.tv_age)
    TextView tvAge;

    private String mDate;
    private String mAge;
    private Intent mResultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if (i != null) {
            mDate = i.getStringExtra("date");
            mAge = i.getStringExtra("age");
        }
        if (TextUtils.isEmpty(mAge)) {
            mAge = getString(R.string.not_filled_out);
        }

        setContentView(R.layout.activity_set_date);
        ButterKnife.bind(this);

        TitleBar titleBar = getTitleBar();
        titleBar.setTitle("出生日期");
        titleBar.showLeftAction(R.drawable.title_back_arrow);
        titleBar.showCustomAction_Text("保存");
        titleBar.setRightActionOnClickListener(this);

        tvAge.setText(mAge);
        initPicker();

        mResultIntent = new Intent();
        setResult(RESULT_OK, mResultIntent);
    }


    private void initPicker() {
        //设置日期简略显示 否则详细显示 包括:星期周
        mDatePicker.setCalendarViewShown(false);
        //初始化当前日期
        Date date = getDate(mDate);
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTimeInMillis(date.getTime());
        } else {
            calendar.setTimeInMillis(System.currentTimeMillis());
        }

        mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);
    }


    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%d-%02d-%02d",
                year,
                monthOfYear + 1,
                dayOfMonth));
        String dateString = sb.toString();
        Calendar newC = getCalander(dateString);
        long difference = System.currentTimeMillis() - newC.getTimeInMillis();
        newC.setTimeInMillis(difference);
        int newAge = newC.get(Calendar.YEAR) - 1970;
        if (newAge >= 0) {
            mDate = dateString;
            mAge = Integer.toString(newAge);
            tvAge.setText(mAge);
        } else {
            Date date = getDate(mDate);
            Calendar calendar = Calendar.getInstance();
            if (date != null) {
                calendar.setTimeInMillis(date.getTime());
            } else {
                calendar.setTimeInMillis(System.currentTimeMillis());
            }
            showErrorToast("年龄不能小于0岁");
            mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), this);
        }
    }

    private Toast toast;

    private void showErrorToast(String context) {
        if (toast == null) {
            toast = Toast.makeText(this, context, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.setText(context);
        toast.show();
    }

    private Date getDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
        Date d = null;
        if (!TextUtils.isEmpty(date)) {
            try {
                d = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return d;
    }

    private Calendar getCalander(String date) {
        Calendar calendar = Calendar.getInstance();
        Date d = getDate(date);
        calendar.setTime(d);
        return calendar;
    }

    @Override
    public void onClick(View v) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("请稍后");
        dialog.show();
        Map<String, String> map = new HashMap<String, String>();
        map.put("birthdays", mDate);

        UserManager.changeUserInfo(this, map,
                new UserManager.ChangeUserInfoCallback() {
                    @Override
                    public void onSucceed(BaseBean baseBean) {
                        LogUtils.e(baseBean + "");
                        tvAge.setText(String.valueOf(mAge));
                        mResultIntent.putExtra("date", mDate).putExtra("age", String.valueOf(mAge));
                        dialog.dismiss();
                        DateSetActivity.this.finish();
                    }

                    @Override
                    public void onError(String error) {
                        LogUtils.e(error);
                        dialog.dismiss();
                    }
                });
    }
}
