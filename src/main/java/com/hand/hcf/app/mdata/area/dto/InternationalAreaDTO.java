package com.hand.hcf.app.mdata.area.dto;

import com.hand.hcf.app.mdata.area.domain.Level;
import lombok.Data;

import java.io.Serializable;

@Data
public class InternationalAreaDTO implements Serializable {

    private String code;

    private String type;

    private String country;

    private String state;

    private String city;

    private String district;

    private String vendorType;

    private Level level;
}
