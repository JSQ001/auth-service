package com.hand.hcf.app.mdata.location.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by vance on 2017/3/4.
 */
@Data
@TableName("sys_location_detail_code_type")
public class LocationDetailCode implements Serializable {
    @TableId

    private Long id;

    @TableField("code")
    private String code;

    @TableField("country_pinyin")
    private String country_pinyin;

    @TableField("country_code")
    private String country_code;

    @TableField("state_pinyin")
    private String state_pinyin;

    @TableField("state_code")
    private String state_code;

    @TableField("city_pinyin")
    private String city_pinyin;

    @TableField("city_code")
    private String city_code;

    @TableField(exist = false)
    private LocationDetail locationDetail;
}
