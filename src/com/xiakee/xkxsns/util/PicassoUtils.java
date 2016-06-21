package com.xiakee.xkxsns.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.http.HttpUrl;

import java.io.File;

/**
 * Created by William on 2015/11/16.
 */
public class PicassoUtils {

    public static void scaleLoad(Context context, int width, int height, String url, ImageView target) {
        Picasso.with(context).
                load(HttpUrl.HOST + url).
                resize(width, height).
                error(R.drawable.xiakee_small).
                placeholder(R.drawable.xiakee_small).
                centerCrop().
                into(target);
    }

    public static void scaleLoad(Context context, int width, int height, File file, ImageView target) {
        Picasso.with(context).
                load(file).
                resize(width, height).
                error(R.drawable.xiakee_small).
                placeholder(R.drawable.xiakee_small).
                centerCrop().
                into(target);
    }

    public static void load(Context context, String url, ImageView target) {
        Picasso.with(context).
                load(HttpUrl.HOST + url).
                error(R.drawable.xiakee_small).
                placeholder(R.drawable.xiakee_small).
                into(target);
    }

    public static void loadIcon(Context context, String url, ImageView target) {
        Picasso.with(context).
                load(HttpUrl.HOST + url).
                error(R.drawable.default_avatar).
                placeholder(R.drawable.default_avatar).
                into(target);
    }

    public static void scaleLoadIcon(Context context, int width, int height, String url, ImageView target) {
        Picasso.with(context).
                load(HttpUrl.HOST + url).
                resize(width, height).
                error(R.drawable.default_avatar).
                placeholder(R.drawable.default_avatar).
                into(target);
    }

    public static void loadIcon(Context context, File file, ImageView target) {
        Picasso.with(context).
                load(file).
                error(R.drawable.default_avatar).
                placeholder(R.drawable.default_avatar).
                into(target);
    }

}
