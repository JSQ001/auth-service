package com.hand.hcf.app.mdata.location.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vance on 2017/3/4.
 */
@Data
public class CustomLocationDetailCodeDTO implements Serializable {

    private String alphabet;
    private List<CustomLocationDetailDTO> customLocationDetailDTOS;

}
