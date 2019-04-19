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
@ApiModel("�ص�����")
public class LocationDetail implements Serializable {

    @TableId
    @ApiModelProperty("ID")
    private Long id;

    @TableField("code")
    @ApiModelProperty("����")
    private String code;

    @TableField("language")
    @ApiModelProperty("����")
    private String language;

    @TableField("country")
    @ApiModelProperty("����")
    private String country;

    @TableField("state")
    @ApiModelProperty("ʡ")
    private String state;

    @TableField("city")
    @ApiModelProperty("����")
    private String city;

    @TableField("district")
    @ApiModelProperty("����")
    private String district;

    @TableField("description")
    @ApiModelProperty("����")
    private String description;

    @TableField("abbreviation")
    @ApiModelProperty("��д")
    private String abbreviation;

    @TableField(exist = false)
    @ApiModelProperty("�ص��������")
    private LocationDetailCode locationDetailCode;
    @TableField(exist = false)
    @ApiModelProperty("��Ӧ�̱�������")
    private List<VendorAlias> vendorAliasList;
    @TableField(exist = false)
    @ApiModelProperty("�ص���")
    private Location location;
    public LocationDetail() {

    }
}
