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
 * 菜单按钮
 */
@Data
@TableName("sys_menu_button")
public class MenuButton extends VersionDomainObject {
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("id")
    private Long id;// 主键

    @TableField("menu_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long menuId;//菜单ID

    @TableField("button_code")
    private String buttonCode;

    @TableField("is_hide")
    private Boolean hide; // 是否隐藏

    @TableField(
            value = "is_deleted",
            strategy = FieldStrategy.NOT_NULL,
            fill = FieldFill.INSERT_UPDATE
    )
    protected Boolean isDeleted;
}
