package com.hand.hcf.app.mdata.accounts.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class AccountsDTO {

    private Long id;

    private Long accountSetId;
    private String accountSetCode;
    private String accountCode;
    private String accountName;
    private String accountDesc;
    private String accountType;
    private String accountTypeName;
    private String balanceDirection;
    private String balanceDirectionName;
    private String reportType;
    private String reportTypeName;
    private Boolean summaryFlag;
    private Boolean enabled;

    //是否被分配
    private Boolean assigned;

    @JsonIgnore
    private Long tenantId;
}
