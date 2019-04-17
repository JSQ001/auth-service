package com.hand.hcf.app.base.userRole.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 角色
 */
@Data
@TableName("sys_role")
public class Role extends DomainLogicEnable {

    @TableField("role_code")
    private String roleCode; //角色代码

    @TableField("role_name")
    private String roleName; // 角色名称

    @TableField("tenant_id")

    private Long tenantId;  // 租户ID

}
