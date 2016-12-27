package com.example.jack.coolweatherdemo;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jack.coolweatherdemo.db.City;
import com.example.jack.coolweatherdemo.db.County;
import com.example.jack.coolweatherdemo.db.Province;
import com.example.jack.coolweatherdemo.util.HttpUtil;
import com.example.jack.coolweatherdemo.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Jack on 2016/12/27.
 */

public class ChooseAreaFragment extends Fragment {

    //标题
    private TextView titleText;
    //回退按钮
    private Button backButton;
    //省级列表
    private List<Province> provinces;
    //市级列表
    private List<City> cities;
    //县级列表
    private List<County> counties;
    //显示列表
    private List<String> dataList = new ArrayList<>();
    //级别
    private final int LEVEL_PROVINCE = 0;
    private final int LEVEL_CITY = 1;
    private final int LEVEL_COUNTY = 2;
    //当前级别
    private int currentLevel;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    //进度框
    private ProgressDialog progressDialog;
    //选中的省
    private Province selectProvince;
    //选中的市
    private City selectCity;
    //选中的县
    private County selectCounty;

    //初始化操作
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvinces();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provinces.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectCity = cities.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    selectCounty = counties.get(position);
                    Toast.makeText(getActivity(), selectCounty.getCountyName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
    }

    //将省级数据加载到初始化的界面上
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinces = DataSupport.findAll(Province.class);
        //Toast.makeText(getActivity(), provinces.size() + "", Toast.LENGTH_SHORT).show();
        if (provinces.size() > 0) {
            dataList.clear();
            for (Province province : provinces) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryService(address, "province");
        }
    }

    //将市级城市列表加载到界面上
    private void queryCities() {
        titleText.setText(selectProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cities = DataSupport.where("provinceid=?", String.valueOf(selectProvince.getId())).find(City.class);
        if (cities.size() > 0) {
            dataList.clear();
            for (City city : cities) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            String address = "http://guolin.tech/api/china" + "/" + selectProvince.getProvinceCode();
            queryService(address, "city");
        }
    }

    //将县级城市列表加载到界面上
    private void queryCounties() {
        titleText.setText(selectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        counties = DataSupport.where("cityid=?", String.valueOf(selectCity.getId())).find(County.class);
        if (counties.size() > 0) {
            dataList.clear();
            for (County county : counties) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            String address = "http://guolin.tech/api/china" + "/" + selectProvince.getProvinceCode() + "/" + selectCity.getCityCode();
            queryService(address, "county");
        }
    }

    //在线查找数据
    private void queryService(String address, final String TYPE) {
        showProgressProgress();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressProgress();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responsestr = response.body().string();
                boolean result = false;
                if ("province".equals(TYPE)) {
                    Utility.handleProvinceResponse(responsestr);
                    result = true;
                } else if ("city".equals(TYPE)) {
                    Utility.handleCityResponse(responsestr, selectProvince.getId());
                    result = true;
                } else if ("county".equals(TYPE)) {
                    Utility.handleCountyResponse(responsestr, selectCity.getId());
                    result = true;
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressProgress();
                            if ("province".equals(TYPE)) {
                                queryProvinces();
                            } else if ("city".equals(TYPE)) {
                                queryCities();
                            } else if ("county".equals(TYPE)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    //进度框显示
    private void showProgressProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("加载中，请稍后...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    //取消进度框
    private void closeProgressProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
