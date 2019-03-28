package com.hand.hcf.app.mdata.location.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by vance on 2016/12/14.
 * The response dto object
 * <p>
 * {
 * "code": "CHN031009000"
 * }
 */

public @Data  class CreateVendorAliasResponseDTO implements Serializable {

    @Setter
    @Getter
    private String code;

}
