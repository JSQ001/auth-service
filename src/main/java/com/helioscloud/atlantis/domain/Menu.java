package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.cloudhelios.atlantis.domain.VersionDomainObject;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 菜单
 */
@Data
@TableName("sys_menu")
public class Menu extends VersionDomainObject {

    @TableField("menu_code")
    private String menuCode; // 菜单代码

    @TableField("menu_name")
    private String menuName;// 菜单名称

    @TableField("seq_number")
    private Integer seqNumber;// 菜单序号

    @TableField("menu_type")
    private Integer menuTypeEnum;// 菜单类型 1000：功能，1001：目录，1002：组件，引用MenuTypeEnum枚举类

    @TableField("parent_menu_id")
    private Long parentMenuId;//父菜单ID 如果没有上线，则默认为0

    @TableField("menu_icon")
    private String menuIcon;// 菜单图标

    @TableField("menu_url")
    private String menuUrl;//菜单URL
}
