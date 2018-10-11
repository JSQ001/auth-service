package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.cloudhelios.atlantis.domain.Domain;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/9/20.
 */
@Data
@TableName("sys_component_button")
public class ComponentButton extends Domain {
    @TableField("component_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long componentId;

    @TableField("button_code")
    private String buttonCode;

    @TableField("button_name")
    private String buttonName ;// 按钮名称

    //不保存到数据库
    @TableField(exist = false)
    private String flag; // 创建:1001，删除:1002

}
