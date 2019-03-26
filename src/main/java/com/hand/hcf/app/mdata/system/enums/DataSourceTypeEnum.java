package com.hand.hcf.app.mdata.system.enums;

import java.io.Serializable;

/**
 * Created by Ray Ma on 2018/3/13.
 */
public enum DataSourceTypeEnum implements Serializable {
    WEB("web"),
    EXCEL("excel"),
    IMPLEMENT("implement");

    private String dataSourceType;

    DataSourceTypeEnum(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getDataSourceType() {
        return this.dataSourceType;
    }
}
