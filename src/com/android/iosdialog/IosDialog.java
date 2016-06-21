package com.android.iosdialog;

import java.util.List;

import com.xiakee.xkxsns.R;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class IosDialog {
    public interface OnMyItemClickListner {

        void onClickItem(int which);
    }

    public interface OnSheetItemClickListener {
        void onClick(int which);
    }

    public enum SheetItemColor {
        Blue("#037BFF"), Red("#FD4A2E");

        private String name;

        private SheetItemColor(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private Context context;
    private Dialog dialog;
    private TextView txt_title;
    private TextView txt_cancel;
    private LinearLayout lLayout_content;
    private ScrollView sLayout_content;

    private boolean showTitle = false;

    private List<SheetItem> listSheetItems;

    private Display display;

    private OnMyItemClickListner mItemLisenner;

    public IosDialog(Context context, OnMyItemClickListner itemLisennr) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        mItemLisenner = itemLisennr;
        builder();
    }

    public IosDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_ios_theme_dialog, null);

        // 设置Dialog最小宽度为屏幕宽度
        // view.setMinimumWidth(display.getWidth());
        Point size = new Point();
        display.getSize(size);
        view.setMinimumWidth(size.x);

        // 获取自定义Dialog布局中的控件
        sLayout_content = (ScrollView) view.findViewById(R.id.sLayout_content);
        lLayout_content = (LinearLayout) view
                .findViewById(R.id.lLayout_content);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_cancel = (TextView) view.findViewById(R.id.txt_cancel);
        txt_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        return this;
    }

    public IosDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public IosDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    /**
     * 设置条目布局
     */
    public void setSheetItems(List<SheetItem> sheetItemList) {

        this.listSheetItems = sheetItemList;
        if (sheetItemList == null || sheetItemList.size() <= 0) {
            return;
        }

        int size = sheetItemList.size();

        // TODO 高度控制，非最佳解决办法
        // 添加条目过多的时候控制高度
        if (size >= 7) {
            LinearLayout.LayoutParams params = (LayoutParams) sLayout_content
                    .getLayoutParams();
            params.height = display.getHeight() / 2;
            sLayout_content.setLayoutParams(params);
        }

        // 循环添加条目
        for (int i = 1; i <= size; i++) {
            final int index = i;
            final SheetItem sheetItem = sheetItemList.get(i - 1);
            String strItem = sheetItem.getStrItemName();

            TextView textView = new TextView(context);
            textView.setText(strItem);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);

            // 背景图片
            if (size == 1) {
                if (showTitle) {
                    textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
                } else {
                    textView.setBackgroundResource(R.drawable.actionsheet_single_selector);
                }
            } else {
                if (showTitle) {
                    if (i >= 1 && i < size) {
                        textView.setBackgroundResource(R.drawable.actionsheet_middle_selector);
                    } else {
                        textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
                    }
                } else {
                    if (i == 1) {
                        textView.setBackgroundResource(R.drawable.actionsheet_top_selector);
                    } else if (i < size) {
                        textView.setBackgroundResource(R.drawable.actionsheet_middle_selector);
                    } else {
                        textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
                    }
                }
            }

            textView.setTextColor(Color.parseColor(SheetItemColor.Blue
                    .getName()));

            // 高度
            float scale = context.getResources().getDisplayMetrics().density;
            int height = (int) (45 * scale + 0.5f);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, height));

            // 点击事件
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemLisenner.onClickItem(sheetItem.getITEM_CODE());
                    dialog.dismiss();
                }
            });

            lLayout_content.addView(textView);
        }
    }

    public IosDialog setTitle(String title) {
        showTitle = true;
        txt_title.setVisibility(View.VISIBLE);
        txt_title.setText(title);
        return this;
    }

    public void show() {
        dialog.show();
    }

    public boolean isShowing(){
        return dialog.isShowing();
    }

    public void dismiss(){
        dialog.dismiss();
    }

    public View getContentView() {
        return lLayout_content;
    }
}
