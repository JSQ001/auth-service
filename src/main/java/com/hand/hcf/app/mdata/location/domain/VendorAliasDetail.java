package com.hand.hcf.app.mdata.location.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("sys_vendor_alias_detail")
public class VendorAliasDetail implements Serializable {

    @TableId

    private Long id;

    @TableField("code")
    private String code;

    @TableField("vendor_type")
    private String vendorType;

    @TableField("city_alias_code")
    private String cityAliasCode;

    @TableField("country_alias_code")
    private String countryAliasCode;

    @TableField(exist = false)
    private List<VendorAlias> vendorAliasList;
}
