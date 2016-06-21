package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.android.util.SNSAPI;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.Comment;
import com.xiakee.xkxsns.bean.Comment.CommentData2;
import com.xiakee.xkxsns.ui.activity.UserSpaceActivity;
import com.xiakee.xkxsns.util.PicassoUtils;
import com.xiakee.xkxsns.util.TextParser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/11/11.
 */
public class CommentReplyListAdapter extends BaseAdapter {
    private Context mContext;
    private List<CommentData2> mList;
    private Comment.CommentData mData;
    private String mUserId;//贴主

    public CommentReplyListAdapter(Context context, Comment.CommentData data, String userId) {
        this.mContext = context;
        this.mData = data;
        this.mList = mData.commentsList;
        this.mUserId = userId;
    }

    public void addComments(List<CommentData2> list) {
        mList.addAll(list);
        this.notifyDataSetChanged();
    }

    public String getLastCommentId() {
        int index = mList.size() - 1;
        if (index >= 0) {
            return mList.get(index).commentId;
        }
        return null;
    }

    public CommentData2 getLastComment() {
        int index = mList.size() - 1;
        if (index >= 0) {
            return mList.get(index);
        }
        return null;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CommentData2 getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_comment_reply, null);
        }

        ViewHolder holder = ViewHolder.getHolder(convertView);
        final CommentData2 data = getItem(position);
        //PicassoUtils.load(mContext, data.photo, holder.ivIcon);
        parseReply(data, holder.tvContent);


        if (TextUtils.isEmpty(data.img)) {
            holder.ivImg.setVisibility(View.GONE);
        } else {
            holder.ivImg.setVisibility(View.VISIBLE);
            PicassoUtils.load(mContext, data.img, holder.ivImg);
        }

        return convertView;
    }

    /***
     * 解析评论内容
     *
     * @param data
     * @param tv
     */
    private void parseReply(final CommentData2 data, TextView tv) {
        final String nickname = data.userName;//昵称
        if (null == nickname) {
            return;
        }

        int xSize = mContext.getResources().getDimensionPixelSize(R.dimen.dp12);
        int size = mContext.getResources().getDimensionPixelSize(R.dimen.dp10);

        String XUserId = mUserId; //贴主的ID
        String LUserId = mData.userId;//楼主的ID
        final String MUserId = data.userId;//评论自身的ID
        int type = data.commentType;

       // LogUtils.e("帖主ID：" + XUserId + "，楼主ID：" + LUserId + "，我的ID：" + MUserId);

        TextParser parser = new TextParser();
        parser.appendBold(nickname, xSize, Color.BLACK, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, UserSpaceActivity.class)
                        .putExtra(SNSAPI.USERID, Integer.parseInt(MUserId)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        if (MUserId.equals(XUserId)) {
            parser.appendBlank();
            parser.appendWithBackground("\u0020" + mContext.getString(R.string.user_flag) + "\u0020", size, Color.BLACK, Color.YELLOW);
        }


        final String parentUserId = data.parentUserId;//当前的评论的父评论ID

        //如果父评论ID不等于楼主的ID
        if (type == 2) {
            parser.append(String.format(mContext.getString(R.string.reply_to), data.parentUserName), xSize, Color.BLACK, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, UserSpaceActivity.class)
                            .putExtra(SNSAPI.USERID, Integer.parseInt(parentUserId)).
                                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });

            //如果父评论ID等于帖主ID
            if (parentUserId.equals(XUserId)) {
                parser.appendBlank();
                parser.appendWithBackground("\u0020" + mContext.getString(R.string.user_flag) + "\u0020", size, Color.BLACK, Color.YELLOW);
            }
        }


        parser.appendBold("：", xSize, Color.BLACK, null);

        String content = data.title;//内容
        if (!TextUtils.isEmpty(content)) {
            parser.append(content, xSize, Color.GRAY, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContentClickListener != null) {
                        mContentClickListener.onContentClick(data);
                    }
                }
            });
        }
        parser.parse(tv);
    }

    static class ViewHolder {
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.iv_img)
        ImageView ivImg;

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

    private OnContentClickListener mContentClickListener;

    public interface OnContentClickListener {
        void onContentClick(CommentData2 data);
    }

    public void setOnContentClickListener(OnContentClickListener listener) {
        this.mContentClickListener = listener;
    }
}
