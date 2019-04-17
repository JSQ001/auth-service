package com.hand.hcf.app.mdata.accounts.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.Data;

@TableName("sys_account_set")
@Data
public class AccountSet extends DomainI18nEnable {
    private String accountSetCode;
    @I18nField
    private String accountSetDesc;
    @JsonIgnore

    private Long tenantId;
}
