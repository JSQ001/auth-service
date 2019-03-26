package com.hand.hcf.app.common.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Created by houyin.zhang@hand-china.com on 2018/7/11.
 */
@Data
public class VendorAliasDTO implements Serializable {

    public String language;
    public String vendorType;
    public String alias;
    public String vendorCode;
    @NotEmpty
    private String code;

}
