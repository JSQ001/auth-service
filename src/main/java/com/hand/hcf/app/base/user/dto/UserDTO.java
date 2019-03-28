

package com.hand.hcf.app.base.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.app.base.user.enums.UserLockedEnum;
import com.hand.hcf.app.base.user.enums.UserStatusEnum;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * A DTO representing a user, with his authorities.
 */
@Data
public class UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 5;
    public static final int PASSWORD_MAX_LENGTH = 100;
    private Long id;

    @Pattern(regexp = "^[a-z0-9]*$")
    @NotNull
    @Size(min = 1, max = 50)
    private String login;

    private UUID userOid;

    private String userName;

    private String remark;

    @NotNull
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    private String mobile;

    private Boolean activated = false;

    @JsonIgnore
    private UUID avatarOid;
    private String avatar;

    private Boolean deleted;
    //状态
    private UserStatusEnum status;

    private String language;

    private Long tenantId;

    private Integer passwordAttempt = 0;

    private Integer lockStatus = UserLockedEnum.UNLOCKED.getId();

    //账号锁定截止日期，过了这个时间用户解锁，才可以登录
    @JsonIgnore
    private ZonedDateTime lockDateDeadline;

    @CreatedDate
    @JsonIgnore
    private ZonedDateTime createdDate = ZonedDateTime.now();

    private String deviceVerificationStatus;
    private Boolean deviceValidate;

    private Boolean resetPassword;

    private String tenantName;
    private Long lastUpdatedBy;
    private ZonedDateTime lastUpdatedDate;
}
