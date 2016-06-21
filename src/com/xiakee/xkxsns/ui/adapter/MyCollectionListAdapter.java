package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.joooonho.SelectableRoundedImageView;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.Collection.CollectionData;
import com.xiakee.xkxsns.util.PicassoUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/11/3.
 */
public class MyCollectionListAdapter extends BaseAdapter {
    private Context mContext;
    private List<CollectionData> mList;

    public MyCollectionListAdapter(Context context, List<CollectionData> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CollectionData getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.grid_item_collection, null);
        }

        CollectionData data = getItem(position);
        ViewHolder holder = ViewHolder.getHolder(convertView);
        holder.tvNickname.setText(data.userName);
        holder.tvDesc.setText(data.topicDesc);
        holder.tvPraise.setText(data.goodCount);
        holder.tvComment.setText(data.commentCount);

        // ImageLoadUtils.loadImage(data.photo, holder.ivIcon);
        //ImageLoadUtils.loadImage(data.imgUrl1, holder.ivPhoto);
        PicassoUtils.load(mContext, data.photo, holder.ivIcon);
        PicassoUtils.load(mContext, data.imgUrl1, holder.ivPhoto);
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_icon)
        SelectableRoundedImageView ivIcon;
        @Bind(R.id.tv_nickname)
        TextView tvNickname;
        @Bind(R.id.iv_photo)
        ImageView ivPhoto;
        @Bind(R.id.tv_desc)
        TextView tvDesc;
        @Bind(R.id.tv_praise)
        TextView tvPraise;
        @Bind(R.id.tv_comment)
        TextView tvComment;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }

        public static ViewHolder getHolder(View convertView) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder(convertView);
            }
            return holder;
        }
    }

}
