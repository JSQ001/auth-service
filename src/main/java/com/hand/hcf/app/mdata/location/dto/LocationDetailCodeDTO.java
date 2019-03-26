package com.hand.hcf.app.mdata.location.dto;

import com.hand.hcf.app.mdata.location.domain.LocationDetail;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by vance on 2017/3/4.
 */
@Data
public class LocationDetailCodeDTO implements Serializable {

    private Long id;
    private String code;
    private String country_pinyin;
    private String country_code;
    private String state_pinyin;
    private String state_code;
    private String city_pinyin;
    private String city_code;

//    private Location location;

    private LocationDetail locationDetail;



}
