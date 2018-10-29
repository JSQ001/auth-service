/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.cloudhelios.atlantis.security.domain.Authority;
import com.helioscloud.atlantis.domain.Role;
import com.helioscloud.atlantis.domain.enumeration.UserLockedEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A DTO representing a user, with his authorities.
 */
@Data
public class UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 5;
    public static final int PASSWORD_MAX_LENGTH = 100;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Pattern(regexp = "^[a-z0-9]*$")
    @NotNull
    @Size(min = 1, max = 50)
    private String login;

    private UUID userOID;

    private UUID companyOID;

    @NotNull
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    @Size(max = 100)
    private String fullName;

    private String firstName;

    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    private String mobile;

    private String employeeID;

    private String title;

    private boolean activated = false;

    private Set<Authority> authorities;

    private UUID departmentOID;

    private String departmentName;

    private boolean isSenior;

    private String filePath;

    @JsonIgnore
    private UUID avatarOID;
    private String avatar;

    private boolean isDeleted;
    //状态
    private Integer status;

    private String companyName;

    //法人实体
    private UUID corporationOID;

    private String language;

    //财务角色OID
    private UUID financeRoleOID;//财务角色编号
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    private UUID directManager;

    private String directManagerId;

    private String directManagerName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;
    private String setOfBooksName;

    private Integer passwordAttempt = 0;

    private Integer lockStatus = UserLockedEnum.UNLOCKED.getID();

    //账号锁定截止日期，过了这个时间用户解锁，才可以登录
    @JsonIgnore
    private DateTime lockDateDeadline;

    @CreatedDate
    @JsonIgnore
    private DateTime createdDate = DateTime.now();

    private String deviceVerificationStatus;

    public UserDTO() {

    }
    private String tenantName;
    private List<Role> roleList;
}
