package com.hand.hcf.app.base.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainEnable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 */
@Data
@TableName("sys_role_menu_button")
public class RoleMenuButton  extends DomainEnable {

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("button_id")
    private Long buttonId;// 按钮ID

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("role_id")
    private Long roleId;// 角色Id

}
