package com.hand.hcf.app.base.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainLogicEnable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 语言
 */
@Data
@TableName("sys_front_key")
public class FrontKey extends DomainLogicEnable {

    @TableField("key_code")
    private String keyCode; //界面keyCode

    @TableField("lang")
    private String lang; // 语言

    @TableField("descriptions")
    private String descriptions; // 语言描述

    @TableField("module_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;  // 模块ID

}
