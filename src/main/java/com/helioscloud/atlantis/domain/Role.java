package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.cloudhelios.atlantis.domain.DomainLogicEnable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 角色
 */
@Data
@TableName("sys_role")
public class Role extends DomainLogicEnable {

    @TableField("role_code")
    private String roleCode; //角色代码

    @TableField("role_name")
    private String roleName; // 角色名称

    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;  // 租户ID

}
