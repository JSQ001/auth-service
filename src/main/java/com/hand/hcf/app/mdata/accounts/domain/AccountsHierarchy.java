package com.hand.hcf.app.mdata.accounts.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.core.domain.DomainLogicEnable;
import lombok.Data;

@TableName("sys_accounts_hierarchy")
@Data
public class AccountsHierarchy extends DomainLogicEnable {

    private Long parentAccountId;

    private Long subAccountId;
    @JsonIgnore

    private Long tenantId;
}
