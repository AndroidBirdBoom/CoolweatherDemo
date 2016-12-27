package com.example.jack.coolweatherdemo.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Jack on 2016/12/27.
 */

public class Province extends DataSupport {
    private int id;
    private int provinceId;
    private String provinceName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
