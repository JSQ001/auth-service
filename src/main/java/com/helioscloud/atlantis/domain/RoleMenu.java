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
 * 角色菜单关联表
 */
@Data
@TableName("sys_role_menu")
public class RoleMenu extends VersionDomainObject {
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("id")
    private Long id;// 主键

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("role_id")
    private Long roleId; //角色ID

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("menu_id")
    private Long menuId; //菜单ID
    @TableField(
            value = "is_deleted",
            strategy = FieldStrategy.NOT_NULL,
            fill = FieldFill.INSERT_UPDATE
    )
    protected Boolean isDeleted;
}
