package com.hand.hcf.app.base.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainEnable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 系统组件
 */
@Data
@TableName("sys_component")
public class Component extends DomainEnable {

    @TableField("component_type")
    private String componentType; //类型 1为组件，2为界面

    @TableField("component_name")
    private String componentName; // 组件名称

    /*@TableField("module_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long moduleId;  // 模块ID 20180829 与前端商量去掉*/

    @TableField("menu_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long menuId;  // 菜单ID 不必填

    @TableField(exist = false)
    private List<ComponentButton> buttonList;// 菜单对应的按钮集合 不存到数据库当中

}
