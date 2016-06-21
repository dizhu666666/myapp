package com.xiakee.xkxsns.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.photo.zoom.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.http.HttpUrl;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/18.
 */
public class PhotoPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mPhotos;

    public PhotoPagerAdapter(Context context, ArrayList<String> photos) {
        this.mContext = context;
        this.mPhotos = photos;
    }

    @Override
    public int getCount() {
        return mPhotos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View contentView = View.inflate(mContext, R.layout.photo_page, null);
        PhotoView image = (PhotoView) contentView.findViewById(R.id.image);
        final ProgressBar pb = (ProgressBar) contentView.findViewById(R.id.pb_load);

        Picasso.with(mContext).load(HttpUrl.HOST + mPhotos.get(position)).error(R.drawable.xiakee_small).into(image, new Callback() {
            @Override
            public void onSuccess() {
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                pb.setVisibility(View.GONE);
            }
        });


        container.addView(contentView);
        return contentView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        object = null;
    }

}
