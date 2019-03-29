package com.hand.hcf.app.mdata.location.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

/**
 * Created by vance on 2016/12/13.
 */
@Data
@TableName("sys_location")
public class Location implements Serializable {

    @TableId

    private Long id;

    @TableField(value = "code")
    private String code;

    @TableField(value = "type")
    private String type;

    @TableField(value = "country_code")
    private String country_code;

    //州、省编号
    @TableField(value = "state_code")
    private String state_code;

    //城市编号
    @TableField(value = "city_code")
    private String city_code;

    //
    @TableField(value = "district_code")
    private String district_code;

    @TableField(value = "enabled")
    private String enabled;

    @TableField(value = "created_date")
    private Date created_date;

    @TableField(value = "created_by")
    private String created_by;

    @TableField(value = "last_updated_date")
    private Date last_updated_date;

    @TableField(value = "last_updated_by")
    private String last_updated_by;

    @TableField(exist = false)
    private List<LocationDetail> locationDetailList;

    @TableField(exist = false)
    private List<VendorAlias> vendorAliasList;

    public Location() {
        super();
    }

    public Location(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, code='%s', type='%s']",
                id, code, type);
    }
}
