package com.hand.hcf.app.base.system.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainLogicEnable;
import lombok.Data;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 系统组件
 */
@Data
@TableName("sys_component")
public class Component extends DomainLogicEnable {

    @TableField("component_type")
    private String componentType; //类型 1为组件，2为界面

    @TableField("component_name")
    private String componentName; // 组件名称

    @TableField("menu_id")

    private Long menuId;  // 菜单ID 不必填

    @TableField(exist = false)
    private List<ComponentButton> buttonList;// 菜单对应的按钮集合 不存到数据库当中

}
