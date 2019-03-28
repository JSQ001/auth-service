package com.hand.hcf.app.base.userRole.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainLogicEnable;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 菜单按钮
 */
@Data
@TableName("sys_menu_button")
public class MenuButton extends DomainLogicEnable {

    @TableField("menu_id")

    private Long menuId;//菜单ID

    @TableField("button_code")
    private String buttonCode;

    @TableField("button_name")
    private String buttonName ;// 按钮名称

    //不保存到数据库
    @TableField(exist = false)
    private String flag; // 创建:1001，删除:1002
    /*@TableField("is_hide")
    private Boolean hide; // 是否隐藏*/



}
