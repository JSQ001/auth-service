package com.hand.hcf.app.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class LocalizationStateDTO {
    private String code;
    private String type;
    private String country;
    private String state;
    private String city;
    private String district;
    private String vendorType;
    private List<LocalizationDTO> children;

}
