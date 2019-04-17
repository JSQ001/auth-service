package com.hand.hcf.app.mdata.contact.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

@TableName("sys_user_group_condition")
@Data
public class UserGroupCondition extends DomainLogicEnable {

    private Long userGroupId;
    private Integer conditionSeq;
    private String conditionLogic;
    private String conditionProperty;
}
