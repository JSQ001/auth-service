package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.cloudhelios.atlantis.domain.VersionDomainObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 角色
 */
@Data
@TableName("sys_role")
public class Role extends VersionDomainObject {

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("id")
    private Long id;// 主键

    @TableField("role_code")
    private String roleCode; //角色代码

    @TableField("role_name")
    private String roleName; // 角色名称

    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;  // 租户ID

    @TableField(
            value = "is_deleted",
            strategy = FieldStrategy.NOT_NULL,
            fill = FieldFill.INSERT_UPDATE
    )
    protected Boolean isDeleted;
}
