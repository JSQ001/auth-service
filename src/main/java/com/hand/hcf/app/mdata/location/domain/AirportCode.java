package com.hand.hcf.app.mdata.location.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by fangmin on 2017/7/10.
 */
@Data
@TableName("sys_airport_code")
public class AirportCode implements Serializable{

    @TableId
    private Long id;

    @TableField("airport_code")
    private String airportCode;

    @TableField("code")
    private String code;
}
