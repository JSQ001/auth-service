package com.hand.hcf.app.base.userRole.dto;

import com.hand.hcf.app.base.userRole.domain.UserRole;
import lombok.Data;

@Data
public class UserAssignRoleDataAuthority extends UserRole {

    /**
     * 角色代码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 数据权限代码
     */
    private String dataAuthorityCode;

    /**
     * 数据权限名称
     */
    private String dataAuthorityName;

}
