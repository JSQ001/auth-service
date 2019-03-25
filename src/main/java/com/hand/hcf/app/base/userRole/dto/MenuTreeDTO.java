package com.hand.hcf.app.base.userRole.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/21.
 * 菜单树结构
 */
@Data
public class MenuTreeDTO implements Comparable<MenuTreeDTO>,Serializable {

    private List<MenuTreeDTO> children = new ArrayList<>();
    private String menuCode; // 菜单代码
    private String menuName;// 菜单名称
    private Integer seqNumber=0;// 菜单序号
    private Integer menuTypeEnum;// 菜单类型 1000：功能，1001：目录，1002：组件，引用MenuTypeEnum枚举类

    private Long parentMenuId=0L;//父菜单ID 如果没有上线，则默认为0
    private String menuIcon;// 菜单图标
    private String menuUrl;//菜单URL

    private Long id;
    @JsonIgnore
    private MenuTreeDTO parent;

    @Override
    public int compareTo(MenuTreeDTO m) {
        //如果上级相同，则按序号排序，如果不同，则按上级排序
        if(this.getParentMenuId().compareTo(m.getParentMenuId()) == 0){
            return (m.seqNumber - this.seqNumber);
        }else{
            return (this.getParentMenuId() - m.getParentMenuId() > 0 ? 1:-1);
        }
    }
    @Override
    public String toString() {
        return "MenuTreeDTO{" +
                ", menuCode='" + menuCode + '\'' +
                ", menuName='" + menuName + '\'' +
                ", seqNumber=" + seqNumber +
                ", menuTypeEnum=" + menuTypeEnum +
                ", parentMenuId=" + parentMenuId +
                ", menuIcon='" + menuIcon + '\'' +
                ", menuUrl='" + menuUrl + '\'' +
                ", id=" + id +
                ", parent=" + parent +
                '}';
    }
}
