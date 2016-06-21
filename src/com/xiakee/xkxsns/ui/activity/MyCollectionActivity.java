package com.xiakee.xkxsns.ui.activity;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.Collection;
import com.xiakee.xkxsns.http.HttpUrl;
import com.xiakee.xkxsns.ui.adapter.MyCollectionListAdapter;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.os.Bundle;
import android.widget.GridView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/11/4.
 */
public class MyCollectionActivity extends BaseActivity {

    @Bind(R.id.gv_collection_list)
    GridView gvCollectionList;

    private MyCollectionListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getTitleBar().setTitle("我的收藏");
        getTitleBar().showLeftAction(R.drawable.arrow_right);

        Ion.with(this).load(HttpUrl.MY_COLLECTION).setBodyParameter("loginUserId", UserManager.getLoginUserId()).
                as(Collection.class).setCallback(new FutureCallback<Collection>() {
            @Override
            public void onCompleted(Exception e, Collection collection) {
                LogUtils.e(collection + "");
                if (null != collection) {
                    if (adapter == null) {
                        adapter = new MyCollectionListAdapter(getApplicationContext(), collection.topicList);
                        gvCollectionList.setAdapter(adapter);
                    }
                }

            }
        });

    }
}
