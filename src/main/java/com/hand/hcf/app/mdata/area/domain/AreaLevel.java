package com.hand.hcf.app.mdata.area.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.util.UUID;

/**
 * Created by Ray Ma on 2017/12/21.
 */
@TableName(value = "sys_area_level")
@Data
public class AreaLevel extends Domain {

    @TableField(value = "level_oid")

    private UUID levelOid;

    private String areaCode;

    private String areaName;

    private String type;

    private String country;

    private String state;

    private String city;

    private String district;

    @TableField(exist = false)
    private Level level;
}
