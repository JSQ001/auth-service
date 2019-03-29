package com.hand.hcf.app.mdata.accounts.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

@TableName("sys_accounts")
@Data
public class Accounts extends DomainI18nEnable {

    private Long accountSetId;
    private String accountCode;
    @I18nField
    private String accountName;
    @I18nField
    private String accountDesc;
    private String accountType;
    private String balanceDirection;
    private String reportType;
    private Boolean summaryFlag;
    @JsonIgnore

    private Long tenantId;
}
