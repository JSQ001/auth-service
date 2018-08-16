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
 * 菜单
 */
@Data
@TableName("sys_menu")
public class Menu extends VersionDomainObject {
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("id")
    private Long id;// 主键

    @TableField("menu_code")
    private String menuCode; // 菜单代码

    @TableField("menu_name")
    private String menuName;// 菜单名称

    @TableField("seq_number")
    private Integer seqNumber;// 菜单序号

    @TableField("menu_type")
    private Integer menuTypeEnum;// 菜单类型 1000：功能，1001：目录，1002：组件，引用MenuTypeEnum枚举类

    @TableField("parent_menu_id")
    private Long parentMenuId;//父菜单ID

    @TableField("menu_icon")
    private String menuIcon;// 菜单图标

    @TableField("menu_url")
    private String menuUrl;//菜单URL

    @TableField(
            value = "is_deleted",
            strategy = FieldStrategy.NOT_NULL,
            fill = FieldFill.INSERT_UPDATE
    )
    protected Boolean isDeleted;
}
