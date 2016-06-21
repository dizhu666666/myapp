package com.xiakee.xkxsns.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.Fans;
import com.xiakee.xkxsns.ui.adapter.MyOrderListAdapter;
import com.xiakee.xkxsns.util.ToastUtils;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by William on 2015/11/4.
 */
public class MyOrderActivity extends BaseActivity {
    @Bind(R.id.lv_order_list)
    ListView lvOrderList;

    private MyOrderListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getTitleBar().setTitle("我的订单");
        getTitleBar().showLeftAction(R.drawable.title_back_arrow);

        List<Fans> list = new ArrayList<Fans>();
        for (int x = 0; x < 3; x++) {
            Fans f = new Fans();
            list.add(f);
        }

        adapter = new MyOrderListAdapter(this, list);
        lvOrderList.setAdapter(adapter);
        lvOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.showToast("position：" + position);
            }
        });
    }
}
