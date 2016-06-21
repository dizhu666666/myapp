package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.FocusLabel;
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
 * Created by William on 2015/12/7.
 */
public class MyFocusLabelsAdapter extends BaseAdapter {
    private Context mContext;
    private List<FocusLabel.FocusLabelData> mList;

    public MyFocusLabelsAdapter(Context context, List<FocusLabel.FocusLabelData> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public FocusLabel.FocusLabelData getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_focus_label, null);
        }

        ViewHolder holder = ViewHolder.getHolder(convertView);

        FocusLabel.FocusLabelData data = getItem(position);
        PicassoUtils.loadIcon(mContext, data.logo, holder.ivIcon);
        holder.tvName.setText(data.title);


        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.iv_icon)
        ImageView ivIcon;
        @Bind(R.id.tv_name)
        TextView tvName;

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
