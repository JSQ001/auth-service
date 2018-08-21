package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.cloudhelios.atlantis.domain.VersionDomainObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 语言
 */
@Data
@TableName("sys_front_key")
public class FrontKey extends VersionDomainObject {

    @TableField("key")
    private String key; //界面Key

    @TableField("lang")
    private String lang; // 语言

    @TableField("descriptions")
    private String descriptions; // 语言描述

    @TableField("module_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;  // 模块ID

}
