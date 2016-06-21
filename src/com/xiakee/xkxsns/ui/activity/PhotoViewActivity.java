package com.xiakee.xkxsns.ui.activity;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iosdialog.IosDialog;
import com.android.iosdialog.SheetItem;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.adapter.PhotoPagerAdapter;
import com.xiakee.xkxsns.util.CommonUtils;
import com.xiakee.xkxsns.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/12/9.
 */
public class PhotoViewActivity extends BaseActivity implements IosDialog.OnMyItemClickListner {
    public static final String INDEX = "index";
    public static final String PHOTO_LIST = "photoList";

    @Bind(R.id.vp_photo)
    ViewPager vpPhoto;
    @Bind(R.id.tv_count)
    TextView tvCount;

    private int currentPage;//当前页

    private ArrayList<String> photos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        if (i != null) {
            photos = i.getStringArrayListExtra(PHOTO_LIST);
            if (photos == null || photos.size() <= 0) {
                return;
            }
            currentPage = i.getIntExtra(INDEX, 0);
        } else {
            return;
        }

        setContentView(R.layout.activity_photoview);
        ButterKnife.bind(this);

        TitleBar titleBar = getTitleBar();
        titleBar.setTitle("图片浏览");
        titleBar.showLeftAction(R.drawable.title_back_arrow);
        titleBar.showRightAction("", R.drawable.iv_more);
        titleBar.setRightActionOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuPop();
            }
        });

        initData();
    }


    private String appendCount(int currentIndex) {
        StringBuilder sb = new StringBuilder();
        sb.append(currentIndex + 1);
        sb.append("/");
        sb.append(photos.size());
        return sb.toString();
    }


    protected void initData() {

        PhotoPagerAdapter pagerAdapter = new PhotoPagerAdapter(this, photos);
        vpPhoto.setPageMargin(27);//设置页面间距
        vpPhoto.setAdapter(pagerAdapter);
        tvCount.setText(appendCount(currentPage));
        vpPhoto.setCurrentItem(currentPage);
        vpPhoto.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                tvCount.setText(appendCount(currentPage));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        showMenuPop();
        return false;// 返回为true 则显示系统menu
    }

    private IosDialog iosDialog;

    /***
     * 弹出菜单
     */
    private void showMenuPop() {
        if (iosDialog == null) {
            iosDialog = new IosDialog(this, this);
            List<SheetItem> listSheetItems = new ArrayList<SheetItem>();
            listSheetItems.add(new SheetItem("保存图片到本地", 0));
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
            case 0:
                savePhoto(HttpUrl.HOST + photos.get(currentPage));
                break;

        }
    }

    private Toast customToast;
    private TextView tvToast;

    private void alertToast(String msg) {
        if (customToast == null) {
            customToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            View view = View.inflate(this, R.layout.toast_white_text, null);
            tvToast = (TextView) view.findViewById(R.id.tv_toast);
            customToast.setView(view);
            customToast.setGravity(Gravity.TOP, 0, getTitleBar().getHeight());
        }
        tvToast.setText(msg);
        customToast.show();
    }

    /**
     * 保存图片
     *
     * @param url
     */
    private void savePhoto(String url) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/xiakee/xiakee_photo");
            if (!file.exists()) {
                file.mkdirs();
            }

            File imageFile = new File(file, CommonUtils.MD5Encoding(url) + ".jpg");

            if (!imageFile.exists()) {
                Ion.with(this).load(url).write(imageFile).setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        if (file != null && file.exists()) {
                            String path = file.getAbsolutePath();
                            MediaScannerConnection.scanFile(getContext(), new String[]{path}, null, null);//下载完图片后可以立即在图库查看
                            alertToast("图片已保存在：" + path);
                        } else {
                            alertToast("保存失败");
                        }

                    }
                });
            } else {
                alertToast("图片已保存在本地：" + imageFile.getAbsolutePath() + "，请勿重复保存");
            }


        } else {
            alertToast("保存失败：SD卡不存在");
        }

    }
}
