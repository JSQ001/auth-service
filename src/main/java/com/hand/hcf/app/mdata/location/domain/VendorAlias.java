package com.hand.hcf.app.mdata.location.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by vance on 2016/12/13.
 */
@TableName("sys_vendor_alias")
@Data
public class VendorAlias implements Serializable {

    @TableId

    private Long id;

    @TableField("code")
    private String code;

    @TableField("vendor_type")
    private String vendorType;

    @TableField("alias")
    private String alias;

    @TableField("language")
    private String language;

    @TableField("vendor_code")
    private String vendorCode;

    @TableField("vendor_country_code")
    private String vendorCountryCode;

    @TableField(exist = false)
    private Location location;
    public VendorAlias() {

    }

}
