package com.xiakee.xkxsns.ui.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.BaseBean;
import com.xiakee.xkxsns.ui.view.wheel.OnWheelScrollListener;
import com.xiakee.xkxsns.ui.view.wheel.WheelView;
import com.xiakee.xkxsns.ui.view.wheel.adapters.ArrayWheelAdapter;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * @author zhy
 */
public class CitiesSetActivity extends BaseActivity implements OnWheelScrollListener, View.OnClickListener {
    /**
     * 把全国的省市区的信息以json的格式保存，解析完成后赋值为null
     */
    private JSONObject mJsonObj;
    /**
     * 省的WheelView控件
     */
    private WheelView mProvince;
    /**
     * 市的WheelView控件
     */
    private WheelView mCity;

    /**
     * 所有省
     */
    private String[] mProvinceDatas;
    /**
     * key - 省 value - 市s
     */
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();

    /**
     * 当前省的名称
     */
    private String currentProvince;
    /**
     * 当前市的名称
     */
    private String currentCity;

    private TextView tvAddress;

    private Intent mResultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_address);
        TitleBar titleBar = getTitleBar();
        titleBar.setTitle("所在地");
        titleBar.showLeftAction(R.drawable.title_back_arrow);
        titleBar.showCustomAction_Text("保存");
        titleBar.setRightActionOnClickListener(this);

        mProvince = (WheelView) findViewById(R.id.id_province);
        mCity = (WheelView) findViewById(R.id.id_city);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvAddress.setTextColor(Color.BLACK);

        initJsonData();
        initDatas();

        mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceDatas));
        // 添加change事件
        mProvince.addScrollingListener(this);
        // 添加change事件
        mCity.addScrollingListener(this);

        mProvince.setVisibleItems(5);
        mCity.setVisibleItems(5);

        updateProvinces();

        mResultIntent = new Intent();
        setResult(RESULT_OK, mResultIntent);
    }

    /**
     * 更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mCity.getCurrentItem();
        currentCity = mCitisDatasMap.get(currentProvince)[pCurrent];

        StringBuilder sb = new StringBuilder();
        sb.append(currentProvince);
        sb.append(" ");
        sb.append(currentCity);
        tvAddress.setText(sb.toString());
    }

    /**
     * 更新身的信息，同时更新市WheelView的信息
     */
    private void updateProvinces() {
        int pCurrent = mProvince.getCurrentItem();
        currentProvince = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(currentProvince);
        if (cities == null) {
            cities = new String[]{""};
        }
        mCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        mCity.setCurrentItem(0);
        updateCities();
    }

    /**
     * 解析整个Json对象，完成后释放Json对象的内存
     */
    private void initDatas() {
        try {
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            mProvinceDatas = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
                String province = jsonP.getString("p");// 省名字

                mProvinceDatas[i] = province;

                JSONArray jsonCs = null;
                try {
                    /**
                     * Throws JSONException if the mapping doesn't exist or is
                     * not a JSONArray.
                     */
                    jsonCs = jsonP.getJSONArray("c");
                } catch (Exception e1) {
                    continue;
                }
                String[] mCitiesDatas = new String[jsonCs.length()];
                for (int j = 0; j < jsonCs.length(); j++) {
                    JSONObject jsonCity = jsonCs.getJSONObject(j);
                    String city = jsonCity.getString("n");// 市名字
                    mCitiesDatas[j] = city;
                }

                mCitisDatasMap.put(province, mCitiesDatas);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonObj = null;
    }

    /**
     * 从assert文件夹中读取省市区的json文件，然后转化为json对象
     */
    private void initJsonData() {
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = getAssets().open("city.json");
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len, "utf-8"));
            }
            is.close();
            mJsonObj = new JSONObject(sb.toString());
            LogUtils.e("mJsonObj：" + mJsonObj);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onScrollingStarted(WheelView wheel) {
    }

    @Override
    public void onScrollingFinished(WheelView wheel) {
        if (wheel == mProvince) {
            updateProvinces();
        } else if (wheel == mCity) {
            updateCities();
        }
    }

    @Override
    public void onClick(View v) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("请稍后");
        dialog.show();

        Map<String, String> map = new HashMap<String, String>();
        map.put("province", currentProvince);
        map.put("city", currentCity);

        UserManager.changeUserInfo(this, map, new UserManager.ChangeUserInfoCallback() {
            @Override
            public void onSucceed(BaseBean baseBean) {
                LogUtils.e(baseBean + "");
                mResultIntent.putExtra("province", currentProvince).putExtra("city", String.valueOf(currentCity));
                dialog.dismiss();
                CitiesSetActivity.this.finish();
            }

            @Override
            public void onError(String error) {
                LogUtils.e(error);
                dialog.dismiss();
            }
        });

    }

}
