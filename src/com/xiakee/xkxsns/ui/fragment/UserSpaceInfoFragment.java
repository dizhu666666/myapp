package com.xiakee.xkxsns.ui.fragment;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.UserSpaceInfo;
import com.xiakee.xkxsns.ui.view.userspace.NFScrollView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.Bind;

/**
 * Created by William on 2015/11/25.
 */
public class UserSpaceInfoFragment extends BaseFragment {


    @Bind(R.id.root)
    NFScrollView mScrollView;
    @Bind(R.id.tv_sex)
    TextView tvSex;
    @Bind(R.id.tv_age)
    TextView tvAge;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_sign)
    TextView tvSign;

    public ScrollView outer;

    private UserSpaceInfo mData;

    @Override
    public View onCreateRootView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_userspace_info, null);
    }

    @Override
    public void onCreateFinished() {
        mData = (UserSpaceInfo) getArguments().get("mData");
        if (mData == null) {
            return;
        }
        mScrollView.parentScrollView = outer;

        tvSex.setText("1".equals(mData.sex) ? getString(R.string.man) : getString(R.string.woman));
        tvAge.setText(mData.age);

        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(mData.province)) {
            sb.append(mData.province);
            if (!TextUtils.isEmpty(mData.city)) {
                sb.append(" ");
                sb.append(mData.city);
            }
        } else {
            sb.append(getString(R.string.not_filled_out));
        }


        String sign = mData.sign;
        if (TextUtils.isEmpty(sign)) {
            tvSign.setText(getString(R.string.not_filled_out));
        } else {
            tvSign.setText(sign);
        }

        tvAddress.setText(sb.toString());
    }
}
