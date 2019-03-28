package com.hand.hcf.app.mdata.location.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by vance on 2016/12/14.
 * The response dto object
 * <p>
 * {
 * "alias": "上海"
 * }
 */
@Data
public class GetAliasByVendorCodeResponseDTO implements Serializable {

    private String alias;

    private String cityAlias;

    private String countryAlias;

}
