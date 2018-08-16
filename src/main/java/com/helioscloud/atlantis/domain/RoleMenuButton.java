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
 */
@Data
@TableName("sys_role_menu_button")
public class RoleMenuButton  extends VersionDomainObject{
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("id")
    private Long id;// 主键


    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("button_id")
    private Long buttonId;// 按钮ID

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("role_id")
    private Long roleId;// 角色Id

    @TableField(
            value = "is_deleted",
            strategy = FieldStrategy.NOT_NULL,
            fill = FieldFill.INSERT_UPDATE
    )
    protected Boolean isDeleted;
}
