package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.Fans;

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
public class MyOrderListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Fans> mList;

    public MyOrderListAdapter(Context context, List<Fans> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Fans getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_order, null);
        }

        // ViewHolder holder = ViewHolder.getHolder(convertView);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_icon)
        ImageView ivIcon;//头像
        @Bind(R.id.tv_nickname)
        TextView tvNickname;//昵称
        @Bind(R.id.tv_signature)
        TextView tvSignature;//个性签名
        @Bind(R.id.iv_add_attention)
        ImageView ivAddAttention;//添加关注

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
