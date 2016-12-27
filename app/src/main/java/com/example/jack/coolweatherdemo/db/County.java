package com.example.jack.coolweatherdemo.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Jack on 2016/12/27.
 */

public class County extends DataSupport {

    private int id;
    private int countyId;
    private int countyName;
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    public int getCountyName() {
        return countyName;
    }

    public void setCountyName(int countyName) {
        this.countyName = countyName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
