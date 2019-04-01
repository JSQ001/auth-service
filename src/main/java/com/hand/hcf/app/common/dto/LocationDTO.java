package com.hand.hcf.app.common.dto;

/**
 * Created by houyin.zhang@hand-china.com on 2018/7/11.
 * 地区
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by vance on 2016/12/14.
 * The response qo object
 * <p>
 * {
 * "code": "CHN031009000",
 * "language": "cn",
 * "type": "COUNTRY|STATE|CITY|DC",
 * "countryCode": "CHN",
 * "country": "中国
 * "stateCode": "031",
 * "state": "上海",
 * "cityCode": "009"
 * "city": "虹口区",
 * "districtCode": "000",
 * "district": ""
 * }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public  class LocationDTO implements Serializable {

    private Long id;

    private String code;

    private String language;

    private String type;

    private String country;

    private String state;

    private String countryCode;

    private String stateCode;

    private String cityCode;

    private String city;

    private String districtCode;

    private String district;

    private String description;

    private String vendorAlias;

    private String vendorCode;

    private String vendorCountryCode;

}

