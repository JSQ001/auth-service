package com.hand.hcf.app.mdata.location.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by vance on 2017/3/4.
 */
@Data
public class LocationDetailDTO implements Serializable {

    private Long id;

    private String code;

    private String language;

    private String country;

    private String state;

    private String city;

    private String district;

    private String description;

    private String abbreviation;

    private String shortPinyin;


}
