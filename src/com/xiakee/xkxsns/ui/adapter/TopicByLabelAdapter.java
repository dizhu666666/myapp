package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.TopicById;
import com.xiakee.xkxsns.util.PicassoUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/11/26.
 */
public class TopicByLabelAdapter extends BaseAdapter {
    private Context mContext;
    private List<TopicById.TopicByIdData> mTopicList;

    private static int mWidth;
    private static int mHeight;
    private static float density;

    public TopicByLabelAdapter(Context context, List<TopicById.TopicByIdData> topicList) {
        this.mContext = context;
        this.mTopicList = topicList;
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWidth = windowManager.getDefaultDisplay().getWidth();
        mHeight = windowManager.getDefaultDisplay().getHeight();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        density = metrics.density;
    }

    public String getLastTopicId() {
        if (mTopicList == null) {
            return null;
        }
        if (mTopicList.size() > 0) {
            return mTopicList.get(mTopicList.size() - 1).topicId;
        }
        return null;
    }

    public String getFirstTopicId() {
        if (mTopicList == null) {
            return null;
        }
        if (mTopicList.size() > 0) {
            return mTopicList.get(0).topicId;
        }
        return null;
    }


    @Override
    public int getItemViewType(int position) {
        if (Integer.parseInt(mTopicList.get(position).imgCount) > 1) {
            return 0;
        }

        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mTopicList.size();
    }

    @Override
    public TopicById.TopicByIdData getItem(int position) {
        return mTopicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TopicById.TopicByIdData data = getItem(position);
        switch (getItemViewType(position)) {
            case 0:
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.item_topic_3pics, null);
                }
                ViewHolder holder = ViewHolder.getHolder(convertView);
                holder.topicTitle.setText(data.title);
                holder.topicDes.setText(data.topicDesc);
                holder.userName.setText(data.userName);
                holder.userLv.setText(String.format(mContext.getString(R.string.level), data.lv));
                holder.userLv.setSelected("1".equals(data.sex));
                holder.numComments.setText(data.commentCount);

                String img1 = data.imgUrl1;
                String img2 = data.imgUrl2;
                String img3 = data.imgUrl3;
                if (!TextUtils.isEmpty(img1)) {
                    holder.img1.setVisibility(View.VISIBLE);
                    PicassoUtils.load(mContext, data.imgUrl1, holder.img1);
                } else {
                    holder.img1.setVisibility(View.INVISIBLE);
                    holder.img1.setImageBitmap(null);
                }

                if (!TextUtils.isEmpty(img2)) {
                    holder.img2.setVisibility(View.VISIBLE);
                    PicassoUtils.load(mContext, data.imgUrl2, holder.img2);
                } else {
                    holder.img2.setVisibility(View.INVISIBLE);
                    holder.img2.setImageBitmap(null);
                }

                if (!TextUtils.isEmpty(img3)) {
                    holder.img3.setVisibility(View.VISIBLE);
                    PicassoUtils.load(mContext, data.imgUrl3, holder.img3);
                } else {
                    holder.img3.setVisibility(View.INVISIBLE);
                    holder.img3.setImageBitmap(null);
                }

                break;

            case 1:
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.item_topic_1pic, null);
                }
                ViewHolder2 holder2 = ViewHolder2.getHolder2(convertView);
                holder2.topicTitle.setText(data.title);
                holder2.topicDes.setText(data.topicDesc);
                holder2.userName.setText(data.userName);
                holder2.userLv.setText(String.format(mContext.getString(R.string.level), data.lv));
                holder2.userLv.setSelected("1".equals(data.sex));
                holder2.numComments.setText(data.commentCount);

                String img = data.imgUrl1;
                if (!TextUtils.isEmpty(img)) {
                    holder2.imgTopic.setVisibility(View.VISIBLE);
                    PicassoUtils.load(mContext, img, holder2.imgTopic);
                } else {
                    holder2.imgTopic.setVisibility(View.GONE);
                    holder2.imgTopic.setImageBitmap(null);
                }

                break;
        }

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.topic_title)
        TextView topicTitle;
        @Bind(R.id.topic_des)
        TextView topicDes;
        @Bind(R.id.img_1)
        ImageView img1;
        @Bind(R.id.img_2)
        ImageView img2;
        @Bind(R.id.img_3)
        ImageView img3;
        @Bind(R.id.user_name)
        TextView userName;
        @Bind(R.id.user_lv)
        TextView userLv;
        @Bind(R.id.user_location)
        TextView userLocation;
        @Bind(R.id.post_time)
        TextView postTime;
        @Bind(R.id.num_comments)
        TextView numComments;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            int width = (int) ((mWidth - 20 * density) / 3);
            img1.getLayoutParams().width = width;
            img1.getLayoutParams().height = width;

            img2.getLayoutParams().width = width;
            img2.getLayoutParams().height = width;

            img3.getLayoutParams().width = width;
            img3.getLayoutParams().height = width;

        }

        public static ViewHolder getHolder(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
            return holder;
        }
    }

    static class ViewHolder2 {
        @Bind(R.id.topic_title)
        TextView topicTitle;
        @Bind(R.id.topic_des)
        TextView topicDes;
        @Bind(R.id.img_topic)
        ImageView imgTopic;
        @Bind(R.id.user_name)
        TextView userName;
        @Bind(R.id.user_lv)
        TextView userLv;
        @Bind(R.id.user_location)
        TextView userLocation;
        @Bind(R.id.post_time)
        TextView postTime;
        @Bind(R.id.num_comments)
        TextView numComments;

        ViewHolder2(View view) {
            ButterKnife.bind(this, view);
            int height = (int) (mHeight / 3 - 16 * density);
            imgTopic.getLayoutParams().height = height;
        }

        public static ViewHolder2 getHolder2(View view) {
            ViewHolder2 holder2 = (ViewHolder2) view.getTag();
            if (holder2 == null) {
                holder2 = new ViewHolder2(view);
                view.setTag(holder2);
            }
            return holder2;
        }
    }
}
