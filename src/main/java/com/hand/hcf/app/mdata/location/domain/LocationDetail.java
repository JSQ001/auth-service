package com.hand.hcf.app.mdata.location.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vance on 2016/12/14.
 */
@Data
@TableName("sys_location_detail")
public class LocationDetail implements Serializable {

    @TableId

    private Long id;

    @TableField("code")
    private String code;

    @TableField("language")
    private String language;

    @TableField("country")
    private String country;

    @TableField("state")
    private String state;

    @TableField("city")
    private String city;

    @TableField("district")
    private String district;

    @TableField("description")
    private String description;

    @TableField("abbreviation")
    private String abbreviation;

    @TableField(exist = false)
    private LocationDetailCode locationDetailCode;
    @TableField(exist = false)
    private List<VendorAlias> vendorAliasList;
    @TableField(exist = false)
    private Location location;
    public LocationDetail() {

    }
}
