package com.xiakee.xkxsns.ui.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.BaseBean;
import com.xiakee.xkxsns.bean.PostSubject.PostSubjectData;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.activity.TopicDetailsActivity;
import com.xiakee.xkxsns.ui.view.SwipeLayout;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.ToastUtils;
import com.xiakee.xkxsns.util.UserManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/11/3.
 */
public class MyTopicAllListAdapter extends BaseAdapter {
    private Context mContext;
    private List<PostSubjectData> mList;

    //用于存放已经打开的item
    private Map<Integer, SwipeLayout> mOpenedItems = new HashMap<Integer, SwipeLayout>();

    public MyTopicAllListAdapter(Context context, List<PostSubjectData> list) {
        this.mContext = context;
        this.mList = list;
    }

    public Map<Integer, SwipeLayout> getOpenedItems() {
        return mOpenedItems;
    }

    @Override
    public boolean isEnabled(int position) {

        switch (getItem(position).status) {
            //未审核
            case 0:
                return false;

            //审核通过
            case 200:
                return true;

            //未审核通过
            case 500:
                return false;

            default:
                return false;
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public PostSubjectData getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_topic_subject, null);
        }

        final PostSubjectData data = getItem(position);

        final ViewHolder holder = ViewHolder.getHolder(convertView);

        holder.tvTitle.setText(data.title);
        holder.tvTime.setText(data.topicTime);

        int status = data.status;
        switch (status) {
            //未审核
            case 0:
                holder.tvStatus.setText("未审核");
                holder.tvStatus.setTextColor(Color.rgb(138, 43, 226));
                break;
            //审核通过
            case 200:
                holder.tvStatus.setText("审核通过");
                //8a2be2
                holder.tvStatus.setTextColor(Color.rgb(0, 99, 0));
                break;
            //未审核通过
            case 500:
                holder.tvStatus.setText("审核未通过");
                holder.tvStatus.setTextColor(Color.RED);
                break;

        }

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDelDialog(position);
            }
        });

//        if (mOpenedItems.get(position) != null) {
//            holder.swipeLayout.fastOpen();
////				holder.swipeLayout.smoothOpen();
//        } else {
//            holder.swipeLayout.fastClose();
////				holder.swipeLayout.smoothClose();
//        }

        holder.swipeLayout.setOnSwipeClickListener(new SwipeLayout.OnSwipeClickListener() {
            @Override
            public void onSwipeClick(SwipeLayout swipeLayout) {
                mContext.startActivity(new Intent(mContext, TopicDetailsActivity.class).
                        putExtra(TopicDetailsActivity.TOPIC_ID, mList.get(position).topicId).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        holder.swipeLayout.setOnSwipeStateChangeListener(new SwipeLayout.OnSwipeStateChangeListener() {
            @Override
            public void onStartOpen(SwipeLayout swipeLayout) {
                for (SwipeLayout layout : mOpenedItems.values()) {
                    layout.smoothClose();
                }
            }

            @Override
            public void onStartClose(SwipeLayout swipeLayout) {
            }

            @Override
            public void onOpen(SwipeLayout swipeLayout) {
                LogUtils.e("onOpen：" + position);
                mOpenedItems.put(position, swipeLayout);
            }

            @Override
            public void onClose(SwipeLayout swipeLayout) {
                LogUtils.e("onClose：" + position);
                mOpenedItems.remove(position);
            }

            @Override
            public void onDel(SwipeLayout swipeLayout) {
                LogUtils.e("onDel：" + position);
//                mOpenedItems.remove(position);
//                mList.remove(position);
//                notifyDataSetInvalidated();
            }
        });

        return convertView;
    }

    private ProgressDialog delProgressDialog;

    /***
     * 删除帖子进度对话框
     *
     * @return
     */
    private Dialog showDelDialog() {
        if (delProgressDialog == null) {
            delProgressDialog = new ProgressDialog(mContext);
            delProgressDialog.setMessage(mContext.getString(R.string.please_waite));
        }
        delProgressDialog.show();
        return delProgressDialog;
    }

    /***
     * 删除帖子dialog
     */
    private Dialog delDialog;
    private int currentPosition;

    private void alertDelDialog(int position) {
        currentPosition = position;
        if (delDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            View contentView = View.inflate(mContext, R.layout.dialog_delete_topic, null);
            builder.setView(contentView);

            TextView tvSure = (TextView) contentView.findViewById(R.id.tv_sure);
            tvSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delDialog.dismiss();
                    showDelDialog();
                    deleteTopic(currentPosition);
                }
            });

            TextView tvCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delDialog.dismiss();
                    LogUtils.e("currentPosition：" + currentPosition);
                    mOpenedItems.get(currentPosition).smoothClose();
                }
            });

            // builder.setTitle("您确定要删除这条帖子吗？");
            delDialog = builder.create();
        }

        delDialog.show();
    }

    private void deleteTopic(final int position) {
        Ion.with(mContext).
                load(HttpUrl.DELETE_TOPIC).
                setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                setBodyParameter("token", UserManager.getToken()).setBodyParameter("topicId", getItem(position).topicId).
                as(BaseBean.class).
                setCallback(new FutureCallback<BaseBean>() {
                    @Override
                    public void onCompleted(Exception e, BaseBean baseBean) {
                        delProgressDialog.dismiss();
                        if (baseBean != null && baseBean.checkData()) {
                            //mOpenedItems.get(position).smoothDel(2); //bug待解决 mOpenedItems.remove(position);
                            mOpenedItems.get(currentPosition).smoothClose();
                            mList.remove(currentPosition);
                            notifyDataSetChanged();
                        } else {
                            ToastUtils.showToast("删除失败");
                        }
                    }
                });
    }

    static class ViewHolder {
        @Bind(R.id.swipeLayout)
        SwipeLayout swipeLayout;
        @Bind(R.id.tv_title)
        TextView tvTitle;//标题
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.tv_status)
        TextView tvStatus;//审核状态
        @Bind(R.id.tv_delete)
        TextView tvDelete;//删除

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
