package com.xiakee.xkxsns.ui.adapter;

import java.io.File;
import java.util.List;

import com.android.util.SNSAPI;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.PostReply.PostReplyData;
import com.xiakee.xkxsns.ui.activity.UserSpaceActivity;
import com.xiakee.xkxsns.ui.view.CircleImageView;
import com.xiakee.xkxsns.util.PicassoUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/11/3.
 */
public class MyTopicReplyListAdapter extends BaseAdapter {
    private Context mContext;
    private List<PostReplyData> mList;

    public MyTopicReplyListAdapter(Context context, List<PostReplyData> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public PostReplyData getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private String username;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_topic_reply, null);
        }
        ViewHolder holder = ViewHolder.getHolder(convertView);
        final PostReplyData data = getItem(position);
        String iconUrl = UserManager.getLocalUserIcon();
        if (!TextUtils.isEmpty(iconUrl)) {
            File file = new File(iconUrl);
            if (file.exists()) {
                PicassoUtils.loadIcon(mContext, file, holder.ivIcon);
            }
        }

        holder.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, UserSpaceActivity.class)
                        .putExtra(SNSAPI.USERID, Integer.parseInt(UserManager.getLoginUserId())).
                                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        if (TextUtils.isEmpty(username)) {
            username = UserManager.getUserName();
        }
        holder.tvNickname.setText(username);
        holder.tvTime.setText(data.topicTime);
        holder.tvContent.setText(data.comment);
        holder.tvPraise.setText(data.goodCount);
        holder.tvReplyContent.setText("：" + data.title);

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
        @Bind(R.id.tv_praise)
        TextView tvPraise;
        @Bind(R.id.tv_reply_content)
        TextView tvReplyContent;

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
