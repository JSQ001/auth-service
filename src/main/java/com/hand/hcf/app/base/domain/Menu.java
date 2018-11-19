package com.hand.hcf.app.base.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainLogicEnable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 菜单
 */
@Data
@TableName("sys_menu")
public class Menu extends DomainLogicEnable implements Comparable<Menu>{

    @TableField("menu_code")
    private String menuCode; // 菜单代码

    @TableField("menu_name")
    private String menuName;// 菜单名称

    @TableField("seq_number")
    private Integer seqNumber = 0;// 菜单序号

    @TableField("menu_type")
    private Integer menuTypeEnum;// 菜单类型 1000：功能，1001：目录，1002：组件，引用MenuTypeEnum枚举类

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("parent_menu_id")
    private Long parentMenuId=0L;//父菜单ID 如果没有上线，则默认为0

    @TableField("menu_icon")
    private String menuIcon;// 菜单图标

    @TableField("menu_url")
    private String menuUrl;//菜单URL

    @TableField("has_child_catalog")
    private Boolean hasChildCatalog;//是否有子级目录

    @TableField("fromSource")
    private String fromSource;// 来源 DB 来自数据库 或 FILE 来自文件

    @TableField(exist = false)
    private List<MenuButton> buttonList;//按钮集合

    @TableField("component_version_id")
    private Long componentVersionId; //组件版本的ID

    @TableField("component_id")
    private Long componentId;// 组件的ID

    @Override
    public int compareTo(Menu o) {
        //如果上级相同，则按序号排序，如果不同，则按上级排序
        if(this.getParentMenuId().compareTo(o.getParentMenuId()) == 0){
            return ( o.getSeqNumber() - this.getSeqNumber() );
        }else{
            return (this.getParentMenuId() - o.getParentMenuId() > 0 ? 1:-1);
        }
    }

    @Override
    public String toString() {
        return "Menu{" +
                "menuCode='" + menuCode + '\'' +
                ", menuName='" + menuName + '\'' +
                ", seqNumber=" + seqNumber +
                ", menuTypeEnum=" + menuTypeEnum +
                ", parentMenuId=" + parentMenuId +
                ", menuIcon='" + menuIcon + '\'' +
                ", menuUrl='" + menuUrl + '\'' +
                ", hasChildCatalog=" + hasChildCatalog +
                ", fromSource='" + fromSource + '\'' +
                ", buttonList=" + buttonList +
                ", componentVersionId=" + componentVersionId +
                ", componentId=" + componentId +
                '}';
    }
}
