package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.cloudhelios.atlantis.domain.VersionDomainObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 系统接口管理
 */
@Data
@TableName("sys_request")
public class InterfaceRequest extends VersionDomainObject {

    @TableField("name")
    private String name; //请求名称

    @TableField("req_type")
    private String reqType; // 参数类型

    @TableField("position")
    private String position; // 位置

    @TableField("key_code")
    private String keyCode; // 请求代码

    @TableField("parent_id")
    private Long parentId; // 上级ID

    @TableField("remark")
    private String remark; // 备注说明

    @TableField("interface_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long interfaceId;  // 接口ID

}
