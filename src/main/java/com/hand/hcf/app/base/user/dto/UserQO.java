package com.hand.hcf.app.base.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserQO {
    private Long id;
    private UUID userOid;
    private List<UUID> userOids;
    private List<Long> userIds;
    private Long tenantId;
    private String login;
    private String userBind;//关联sys_user_login_bind.login
    @Builder.Default
    private Boolean deleted = false;
    private String authorityName;
    private String userName;
    private String email;
    private String mobile;
    private String keyword;//模糊查询关键字
    @Builder.Default
    private Boolean fuzzy = false;//模糊查询
    @Builder.Default
    private Boolean isInactiveSearch=false;
    @Builder.Default
    private Boolean inverseUser=false;//反选user
    private Integer rangeNum;//范围
    @Builder.Default
    private ZonedDateTime now=ZonedDateTime.now();
    @Builder.Default
    private Boolean hasAttachment=false;
    @Builder.Default
    private Boolean hasTenant=false;
    private Boolean orderByFullName;
}
