package com.hand.hcf.app.mdata.location.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 地点
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/3/28 22:31
 */
@Data
public class LocationDTO implements Serializable {
    /**
     * 地点id
     */
    private Long locationId;
    /**
     * 地点code
     */
    private String code;
    /**
     * 国家
     */
    private String country;
    /**
     * 国家code
     */
    private String countryCode;
    /**
     *  城市
     */
    private String city;
    /**
     * 城市code
     */
    private String cityCode;
    /**
     * 区县
     */
    private String district;
    /**
     * 区县code
     */
    private String districtCode;
    /**
     *  地点类型
     */
    private String type;
    /**
     *  地点类型描述
     */
    private String typeDesc;

    /**
     * 省市
     */
    private String state;
    /**
     * 省市代码
     */
    private String stateCode;
    /**
     * 描述
     */
    private String description;

}
