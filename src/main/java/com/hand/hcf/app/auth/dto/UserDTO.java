package com.hand.hcf.app.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.app.auth.enums.UserLockedEnum;
import com.hand.hcf.app.core.security.domain.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {

    private Long id;
    private UUID userOid;
    private String login;
    private String password;
    private Boolean activated;
    private Set<Authority> authorities = new HashSet();
    private Integer status;
    private String language;
    private ZonedDateTime createdDate;
    private Long tenantId;
    private String userName;
    private String email;
    private String mobile;
    private String remark;

    private Integer lockStatus = UserLockedEnum.UNLOCKED.getId();

    //账号锁定截止日期，过了这个时间用户解锁，才可以登录
    @JsonIgnore
    private ZonedDateTime lockDateDeadline;

    private Integer passwordAttampt;

    private ZonedDateTime lastUpdatedDate;
}
