package com.hand.hcf.app.mdata.location.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vance on 2016/12/14.
 */
@Data
@TableName("sys_location_detail")
@ApiModel("地点详情")
public class LocationDetail implements Serializable {

    @TableId
    @ApiModelProperty("ID")
    private Long id;

    @TableField("code")
    @ApiModelProperty("代码")
    private String code;

    @TableField("language")
    @ApiModelProperty("语言")
    private String language;

    @TableField("country")
    @ApiModelProperty("国家")
    private String country;

    @TableField("state")
    @ApiModelProperty("省")
    private String state;

    @TableField("city")
    @ApiModelProperty("城市")
    private String city;

    @TableField("district")
    @ApiModelProperty("地区")
    private String district;

    @TableField("description")
    @ApiModelProperty("描述")
    private String description;

    @TableField("abbreviation")
    @ApiModelProperty("缩写")
    private String abbreviation;

    @TableField(exist = false)
    @ApiModelProperty("地点详情代码")
    private LocationDetailCode locationDetailCode;
    @TableField(exist = false)
    @ApiModelProperty("供应商别名集合")
    private List<VendorAlias> vendorAliasList;
    @TableField(exist = false)
    @ApiModelProperty("地点类")
    private Location location;
    public LocationDetail() {

    }
}
