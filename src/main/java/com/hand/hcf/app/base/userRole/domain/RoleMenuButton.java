package com.hand.hcf.app.base.userRole.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 */
@Data
@TableName("sys_role_menu_button")
public class RoleMenuButton  extends DomainEnable {

    @NotNull

    @TableField("button_id")
    private Long buttonId;// 按钮ID

    @NotNull

    @TableField("role_id")
    private Long roleId;// 角色Id

}
