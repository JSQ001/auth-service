package com.hand.hcf.app.mdata.contact.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

@Data
@TableName("sys_user_group_permission")
public class UserGroupPermission extends DomainLogicEnable {
    private Long tenantId;
    private Long userGroupId;
    private Long objectId;
    private String objectType;
}
