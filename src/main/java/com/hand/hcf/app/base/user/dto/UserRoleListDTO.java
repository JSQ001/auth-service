

package com.hand.hcf.app.base.user.dto;

import com.hand.hcf.app.base.userRole.domain.Role;
import lombok.Data;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.UUID;

/**
 * A DTO representing a user, with his authorities.
 */
@Data
public class UserRoleListDTO {

    private Long id;

    private String login;

    private UUID userOid;

    private String userName;

    private String remark;

    @Email
    private String email;

    private String mobile;

    private List<Role> roleList;

}
