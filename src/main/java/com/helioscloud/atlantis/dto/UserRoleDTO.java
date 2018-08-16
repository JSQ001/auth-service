package com.helioscloud.atlantis.dto;

import com.helioscloud.atlantis.domain.Role;
import lombok.Data;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/15.
 */
@Data
public class UserRoleDTO {
    private Long id;

    private Long userId;

    private Long roleId;

    private Role role;

}
