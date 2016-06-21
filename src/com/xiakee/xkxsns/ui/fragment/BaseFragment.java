package com.xiakee.xkxsns.ui.fragment;

import com.xiakee.xkxsns.util.CommonUtils;
import com.xiakee.xkxsns.util.LogUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

/***
 * fragment基类
 */
public abstract class BaseFragment extends Fragment {

    protected FragmentActivity mActivity;
    protected View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = onCreateRootView(inflater);
        } else {
            CommonUtils.removeSelfFromParent(mRootView);
        }
        LogUtils.e(this.getClass().getSimpleName() + ":onCreateView");
        ButterKnife.bind(this, mRootView);
        return mRootView;
    }


    /***
     * 让子类实现此方法，初始化view
     *
     * @return
     */
    public abstract View onCreateRootView(LayoutInflater inflater);

    /***
     * 当activity创建完毕时调用此方法
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();//获取所依赖的activity，如果不在此方法中获取，得到的activity可能为null
        onCreateFinished();
    }

    /***
     * 当生命周期方法 onActivityCreated()调用时，则代表fragment所依赖的activity被创建，这时应该调用此方法让子类去处理自己的逻辑
     */
    public abstract void onCreateFinished();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.e(this.getClass().getSimpleName() + ":onDestroyView");
        ButterKnife.unbind(this);//解除view绑定
    }
}
