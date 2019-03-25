package com.hand.hcf.app.base.userRole.dto;

import com.hand.hcf.app.base.userRole.domain.Role;
import lombok.Data;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/15.
 */
@Data
public class UserRoleDTO {

    private Long id;

    private Long userId;

    private Long roleId;

    private Role role;

    private List<UserAssignRoleDTO> assignRoleList;//用于用户批量分配角色
}
