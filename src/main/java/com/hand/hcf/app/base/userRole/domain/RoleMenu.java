package com.hand.hcf.app.base.userRole.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 角色菜单关联表
 */
@Data
@TableName("sys_role_menu")
public class RoleMenu extends DomainEnable {

    @NotNull

    @TableField("role_id")
    private Long roleId; //角色ID

    @NotNull

    @TableField("menu_id")
    private Long menuId; //菜单ID
}
