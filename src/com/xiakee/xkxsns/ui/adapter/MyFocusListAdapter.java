package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.Focus.FocusData;
import com.xiakee.xkxsns.bean.FocusState;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.util.PicassoUtils;
import com.xiakee.xkxsns.util.UserManager;

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
public class MyFocusListAdapter extends BaseAdapter {
    private Context mContext;
    private List<FocusData> mList;

    public MyFocusListAdapter(Context context, List<FocusData> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void addFocusList(List<FocusData> list) {
        mList.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public FocusData getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_focus, null);
        }

        final FocusData data = getItem(position);
        final ViewHolder holder = ViewHolder.getHolder(convertView);

        //ImageLoadUtils.loadImage(data.photo, holder.ivIcon);
        PicassoUtils.loadIcon(mContext, data.photo, holder.ivIcon);
        holder.tvNickname.setText(data.userName);
        holder.tvSignature.setText(data.sign);
        holder.ivFocusState.setSelected(data.isMutualFocus());

        //http://127.0.0.1/comm/person/focus?loginUserId=1898&focusUserId=1899
        holder.ivFocusState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivFocusState.setEnabled(false);
                Ion.with(mContext).
                        load(HttpUrl.FOCUS_OR_CANCEL).
                        setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                        setBodyParameter("token", UserManager.getToken()).
                        setBodyParameter("focusUserId", data.userId).
                        as(FocusState.class).
                        setCallback(new FutureCallback<FocusState>() {
                            @Override
                            public void onCompleted(Exception e, FocusState focusState) {
                                holder.ivFocusState.setEnabled(true);
                                //String msg = "操作错误";
                                if (null != focusState) {
                                    //  1关注成功 0取消关注成功
                                    if ("0".equals(focusState.focusStatus)) {
                                        holder.ivFocusState.setImageResource(R.drawable.add_focus);//图片设置为添加关注
                                        // msg = "取消关注成功";
                                    } else if ("1".equals(focusState.focusStatus)) {
                                        //图片设置为已关注，如果是双向关注，显示双向关注的图片
                                        holder.ivFocusState.setImageResource(R.drawable.selector_focus);
                                        holder.ivFocusState.setSelected(data.isMutualFocus());
                                        //msg = "添加关注成功";
                                    }
                                    // ToastUtils.showToast(msg);
                                }

                            }
                        });
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_icon)
        ImageView ivIcon;//头像
        @Bind(R.id.tv_nickname)
        TextView tvNickname;//昵称
        @Bind(R.id.tv_signature)
        TextView tvSignature;//个性签名
        @Bind(R.id.iv_focus_state)
        ImageView ivFocusState;//关注状态

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
