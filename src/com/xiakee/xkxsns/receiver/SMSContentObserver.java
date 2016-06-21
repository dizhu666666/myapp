package com.xiakee.xkxsns.receiver;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.EditText;

import com.xiakee.xkxsns.util.LogUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/10/16.
 * 短信监听器，用于自动填充验证码
 */
public class SMSContentObserver extends ContentObserver {
    public final String SMS_URI_INBOX = "content://sms/inbox";//收信箱
    private Activity activity = null;
    private String smsContent = "";//验证码
    private EditText verifyText = null;//验证码编辑框
    private String SMS_ADDRESS_PRNUMBER = "10690365722702330311";//短息发送提供商
    private String smsID = "";//短信观察者 收到一条短信时 onchange方法会执行两次，所以比较短信id，如果一致则不处理

    public SMSContentObserver(Activity activity, Handler handler, EditText verifyText) {
        super(handler);
        this.activity = activity;
        this.verifyText = verifyText;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        Cursor cursor = null;// 光标
        // 读取收件箱中指定号码的短信
        cursor = activity.getContentResolver().query(Uri.parse(SMS_URI_INBOX),
                new String[]{"_id", "address", "body", "read"}, //要读取的属性
                "address=? and read=?", //查询条件是什么
                new String[]{SMS_ADDRESS_PRNUMBER, "0"},//查询条件赋值
                "date desc");//排序
        LogUtils.e("收到了短信");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                //比较和上次接收到短信的ID是否相等
                if (!smsID.equals(cursor.getString(cursor.getColumnIndex("_id")))) {

                    String smsbody = cursor.getString(cursor.getColumnIndex("body"));

                    //用正则表达式匹配验证码
                    Pattern pattern = Pattern.compile("\\d{6}");
                    Matcher matcher = pattern.matcher(smsbody);

                    if (matcher.find(0)) {//匹配到4位的验证码，从第0个位置开始
                        smsContent = matcher.group();
                        LogUtils.e("获取到了验证码");
                        if (verifyText != null && null != smsContent && !"".equals(smsContent)) {
                            verifyText.requestFocus();//获取焦点
                            verifyText.setText(smsContent);//设置文本
                            verifyText.setSelection(smsContent.length());//设置光标位置
                        }
                    }

                    smsID = cursor.getString(cursor.getColumnIndex("_id"));
                }

            }
        }
    }
}
