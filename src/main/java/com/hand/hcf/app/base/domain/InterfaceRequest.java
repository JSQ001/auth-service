package com.hand.hcf.app.base.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.core.domain.DomainLogicEnable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 系统接口管理
 */
@Data
@TableName("sys_interface_request")
public class InterfaceRequest extends DomainLogicEnable {

    @TableField("name")
    private String name; //请求名称

    @TableField("req_type")
    private String reqType; // 参数类型

    @TableField("position")
    private String position; // 位置

    @TableField("key_code")
    private String keyCode; // 请求代码

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("parent_id")
    private Long parentId; // 上级ID

    @TableField("remark")
    private String remark; // 备注说明

    @TableField("interface_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long interfaceId;  // 接口ID
    @TableField("default_value")
    private String defaultValue;//默认值

    @TableField("required_flag")
    private Boolean requiredFlag;//是否必填
    @TableField("union_flag")
    private Boolean unionFlag;//是否唯一

    /**
     * 请求时默认的组织架构信息
     */
    @TableField(value = "default_flag", strategy = FieldStrategy.IGNORED)
    private String defaultFlag;

}
