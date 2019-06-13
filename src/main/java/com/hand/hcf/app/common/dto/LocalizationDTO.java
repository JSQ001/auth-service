package com.hand.hcf.app.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by houyin.zhang@hand-china.com on 2018/7/11.
 */
@Data
public class LocalizationDTO implements Serializable {

    private Long id;

    private String code;

    private String type;

    private String countryCode;

    private String country;

    private String state;

    private String cityCode;

    private String city;

    private String district;

    private String vendorType;
}
