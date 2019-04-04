package com.hand.hcf.app.mdata.location.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/3/27
 */
@Data
public class LocationInfoDTO implements Serializable {
    private Long id;
    private String description;
    private String code;
}
