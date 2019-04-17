package com.hand.hcf.app.mdata.contact.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

@TableName("sys_user_group_cond_detail")
@Data
public class UserGroupConditionDetail extends DomainLogicEnable {
    private Long conditionId;
    private String conditionValue;
}
