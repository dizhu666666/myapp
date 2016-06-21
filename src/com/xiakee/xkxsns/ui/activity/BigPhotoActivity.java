package com.xiakee.xkxsns.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.http.HttpUrl;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 查看大头像
 * Created by William on 2015/12/11.
 */
public class BigPhotoActivity extends Activity {
    @Bind(R.id.photo)
    ImageView ivPhoto;
    @Bind(R.id.pb_progress)
    ProgressBar pbProgress;

    private String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if (i != null) {
            photoUrl = i.getStringExtra("photoUrl");
            if (TextUtils.isEmpty(photoUrl)) {
                return;
            }
        } else {
            return;
        }

        setContentView(R.layout.activity_big_photo);
        ButterKnife.bind(this);

        pbProgress.setVisibility(View.VISIBLE);
        Picasso.with(this).load(HttpUrl.HOST + photoUrl).into(ivPhoto, new Callback() {
            @Override
            public void onSuccess() {
                pbProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {

            }
        });

    }

}
