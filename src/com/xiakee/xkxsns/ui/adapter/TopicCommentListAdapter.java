package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.android.util.SNSAPI;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.Comment.CommentData;
import com.xiakee.xkxsns.bean.Comment.CommentData2;
import com.xiakee.xkxsns.bean.CommentPraise;
import com.xiakee.xkxsns.bean.TopicDetailsBean.TopicDetails;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.activity.CommentReplyDetailsActivity;
import com.xiakee.xkxsns.ui.activity.LoginActivity;
import com.xiakee.xkxsns.ui.activity.TopicDetailsActivity;
import com.xiakee.xkxsns.ui.activity.UserSpaceActivity;
import com.xiakee.xkxsns.ui.view.CircleImageView;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.PicassoUtils;
import com.xiakee.xkxsns.util.TextParser;
import com.xiakee.xkxsns.util.UserManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/11/3.
 */
public class TopicCommentListAdapter extends BaseAdapter {
    private Activity mContext;
    private List<CommentData> mList;
    private TopicDetails mTopicDetail;

    private String seeMore = "查看更多%d条评论";

    public TopicCommentListAdapter(Activity context, List<CommentData> list, TopicDetails topicDetail) {
        this.mContext = context;
        this.mList = list;
        this.mTopicDetail = topicDetail;
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public void addComments(List<CommentData> list) {
        mList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void addComment(CommentData data) {
        mList.add(data);
        this.notifyDataSetChanged();
    }

    public String getLastCommentId() {
        int index = mList.size() - 1;
        if (index >= 0) {
            return mList.get(index).commentId;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CommentData getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_comment, null);
        }

        final ViewHolder holder = ViewHolder.getHolder(convertView);
        final CommentData data = getItem(position);

        if (data.userId.equals(mTopicDetail.userId)) {
            holder.tvFlag.setVisibility(View.VISIBLE);
        } else {
            holder.tvFlag.setVisibility(View.INVISIBLE);
        }

        holder.tvNickname.setText(data.userName);
        holder.tvLevel.setText(String.format(mContext.getString(R.string.level), data.lv));
        holder.tvLevel.setSelected("1".equals(data.sex));
        holder.tvFloor.setText(data.floor + "F");

        PicassoUtils.loadIcon(mContext, data.photo, holder.ivIcon);
        holder.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, UserSpaceActivity.class)
                        .putExtra(SNSAPI.USERID, Integer.parseInt(data.userId))
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        holder.tvTime.setText(data.commentTime);

        holder.tvContent.setText(data.title);
        holder.tvPraise.setSelected(data.isPraise());
        holder.tvPraise.setText(String.valueOf(data.goodCount));
        holder.tvPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserManager.isLogin()) {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    return;
                }

                holder.tvPraise.setSelected(!holder.tvPraise.isSelected());
                if (holder.tvPraise.isSelected()) {
                    data.goodCount++;
                    data.commentsGoodStatus = "1";
                } else {
                    data.goodCount--;
                    data.commentsGoodStatus = "0";
                }

                holder.tvPraise.setText(String.valueOf(data.goodCount));

                //http://127.0.0.1/comm/topic/commentsGood?commentsId=53&loginUserId=1898
                Ion.with(mContext).
                        load(HttpUrl.GOOD_AT_COMMENT).
                        setBodyParameter("commentsId", data.commentId).
                        setBodyParameter("token", UserManager.getToken()).
                        setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                        as(CommentPraise.class).
                        setCallback(new FutureCallback<CommentPraise>() {
                            @Override
                            public void onCompleted(Exception e, CommentPraise commentPraise) {
                                LogUtils.e(commentPraise + "");
                                if (commentPraise != null) {
                                    //点赞状态0取消点赞成功1成功点赞
                                    //data.commentsGoodStatus = commentPraise.goodStatus;
                                }
                            }
                        });

            }
        });

        if (TextUtils.isEmpty(data.img)) {
            holder.ivImg.setVisibility(View.GONE);
        } else {
            holder.ivImg.setVisibility(View.VISIBLE);
            //ImageLoadUtils.loadImage(data.img, holder.ivImg);
            PicassoUtils.load(mContext, data.img, holder.ivImg);
        }

        List<CommentData2> commentsList = data.commentsList;
        if (null != commentsList && commentsList.size() > 0) {
            holder.llComReply1.setVisibility(View.VISIBLE);

            CommentData2 data1 = commentsList.get(0);
            parseReply(position, data1, holder.tvContent1);

            if (commentsList.size() == 1 || commentsList.size() == 2) {
                holder.tvMore.setVisibility(View.GONE);
                holder.tvMore.setText("");
            } else {
                holder.tvMore.setVisibility(View.VISIBLE);
                holder.tvMore.setText(String.format(seeMore, data.commentsList.size() - 2));
            }

            if (commentsList.size() < 2) {
                holder.llComReply2.setVisibility(View.GONE);
            } else {
                holder.llComReply2.setVisibility(View.VISIBLE);
                CommentData2 data2 = commentsList.get(1);
                parseReply(position, data2, holder.tvContent2);
            }


        } else {
            //holder.tvMore.setOnClickListener(null);
            holder.llComReply1.setVisibility(View.GONE);
            holder.llComReply2.setVisibility(View.GONE);
            holder.tvMore.setVisibility(View.GONE);
            holder.tvMore.setText("");
        }

        return convertView;
    }

    /***
     * 解析评论内容
     *
     * @param data
     * @param tv
     */
    private void parseReply(final int position, final CommentData2 data, TextView tv) {
        final String nickname = data.userName;//昵称
        if (null == nickname) {
            return;
        }

        int xSize = mContext.getResources().getDimensionPixelSize(R.dimen.dp12);
        int size = mContext.getResources().getDimensionPixelSize(R.dimen.dp10);

        final CommentData commentData = getItem(position);
        String XUserId = mTopicDetail.userId; //贴主的ID
        String LUserId = commentData.userId;//楼主的ID
        final String MUserId = data.userId;//评论自身的ID
        int type = data.commentType;//type评论的类型0评论的是帖子，1是回复楼层, 2回复的楼层下的用户

        TextParser parser = new TextParser();
        parser.appendBold(nickname, xSize, Color.BLACK, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, UserSpaceActivity.class)
                        .putExtra(SNSAPI.USERID, Integer.parseInt(MUserId)).
                                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


        if (MUserId.equals(XUserId)) {
            parser.appendBlank();
            parser.appendWithBackground("\u0020" + mContext.getString(R.string.user_flag) + "\u0020", size, Color.BLACK, Color.YELLOW);
        }


        final String parentUserId = data.parentUserId;//当前的评论的父评论ID

        if (type == 2) {
            parser.append(String.format(mContext.getString(R.string.reply_to), data.parentUserName), xSize, Color.BLACK,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, UserSpaceActivity.class)
                                    .putExtra(SNSAPI.USERID, Integer.parseInt(parentUserId)).
                                            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    });

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
                    mContext.startActivityForResult(new Intent(mContext, CommentReplyDetailsActivity.class).
                            putExtra("CommentData", commentData).
                            putExtra("topicId", mTopicDetail.topicId).
                            putExtra("myUserId", mTopicDetail.userId).
                            putExtra("position", position).
                            putExtra("parentUserId", data.userId).
                            putExtra("parentUserName", data.userName), TopicDetailsActivity.VIEW_DETAILS);
                }
            });
        }
        //tv.setOnClickListener();
        parser.parse(tv);
    }

    static class ViewHolder {
        @Bind(R.id.iv_icon)
        CircleImageView ivIcon;
        @Bind(R.id.tv_nickname)
        TextView tvNickname;
        @Bind(R.id.tv_level)
        TextView tvLevel;
        @Bind(R.id.tv_floor)
        TextView tvFloor;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.tv_praise)
        TextView tvPraise;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.iv_img)
        ImageView ivImg;
        @Bind(R.id.tv_more)
        TextView tvMore;
        @Bind(R.id.tv_flag)
        TextView tvFlag;

        //------------------------------------------------------------
        @Bind(R.id.ll_comment_reply1)
        LinearLayout llComReply1;
        @Bind(R.id.tv_content1)
        TextView tvContent1;
        //------------------------------------------------------------
        //------------------------------------------------------------
        @Bind(R.id.ll_comment_reply2)
        LinearLayout llComReply2;
        @Bind(R.id.tv_content2)
        TextView tvContent2;
        //------------------------------------------------------------

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
