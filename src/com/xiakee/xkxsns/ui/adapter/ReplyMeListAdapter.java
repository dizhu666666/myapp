package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.android.util.SNSAPI;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.ReplyMe;
import com.xiakee.xkxsns.ui.activity.UserSpaceActivity;
import com.xiakee.xkxsns.ui.view.CircleImageView;
import com.xiakee.xkxsns.util.PicassoUtils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/11/3.
 */
public class ReplyMeListAdapter extends BaseAdapter {
    private Context mContext;
    private List<ReplyMe.ReplyMeData> mList;

    public ReplyMeListAdapter(Context context, List<ReplyMe.ReplyMeData> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public ReplyMe.ReplyMeData getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_reply_me, null);
        }
        ViewHolder holder = ViewHolder.getHolder(convertView);
        final ReplyMe.ReplyMeData data = getItem(position);


        PicassoUtils.loadIcon(mContext, data.photo, holder.ivIcon);
        holder.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, UserSpaceActivity.class)
                        .putExtra(SNSAPI.USERID, data.userId).
                                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        String type = data.commentType;
        if ("0".equals(type)) {//帖子
            holder.tvType.setText("回复我的帖子");
        } else if ("1".equals(type)) {//楼层
            holder.tvType.setText("回复我的回帖");
        } else if ("2".equals(type)) {//用户
            holder.tvType.setText("回复我的评论");
        }
        holder.tvNickname.setText(data.userName);
        holder.tvTime.setText(data.commentTime);
        holder.tvContent.setText(data.title);
        holder.tvReplyContent.setText("：" + data.parentTitle);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_icon)
        CircleImageView ivIcon;
        @Bind(R.id.tv_nickname)
        TextView tvNickname;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.tv_reply_content)
        TextView tvReplyContent;
        @Bind(R.id.tv_type)
        TextView tvType;

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
