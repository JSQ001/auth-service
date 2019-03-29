package com.hand.hcf.app.mdata.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by fangmin on 2017/9/15.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolrLocationDTO implements Serializable{

    private String id;

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

    private String cityPinyin;

    private String vendorType;

    private String vendorAlias;

    private String vendorCode;

    private String vendorCountryCode;

}
