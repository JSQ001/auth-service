package com.hand.hcf.app.base.user.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hcf.app.auth.enums.UserLockedEnum;
import com.hand.hcf.app.base.user.enums.UserStatusEnum;
import com.hand.hcf.core.domain.DomainLogic;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * A user.
 */
@Data
@TableName("sys_user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends DomainLogic implements Serializable {

    private static final long serialVersionUID = -2444306351387075117L;

    @NotNull
    @TableField(value = "user_oid")
    protected UUID userOid;

    @NotNull
    @Size(min = 1, max = 50)
    protected String login;

    private String userName;

    private String remark;

    @JsonIgnore
    @TableField(value = "password_hash")
    protected String passwordHash;

    @TableField(exist = false)
    private String password;

    protected Boolean activated = false;

    protected String language;

    private String email;

    private String mobile;

    private UUID avatarOid;

    private UserStatusEnum status;

    @JsonIgnore
    @TableField("passwd_hash_last_updated_date")
    private ZonedDateTime passwordHashLastUpdatedDate;


    private Integer passwordAttempt = 0;

    private Integer lockStatus = UserLockedEnum.UNLOCKED.getId();

    //账号锁定截止日期，过了这个时间用户解锁，才可以登录
    @JsonIgnore
    private ZonedDateTime lockDateDeadline;

    @JsonIgnore
    private ZonedDateTime activatedDate;

    private Long tenantId;

    private String dataSource;

    @TableField("device_verification_status")
    private String deviceVerificationStatus;

    private Boolean resetPassword;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        return login.equals(user.login);

    }

}
