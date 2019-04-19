package com.hand.hcf.app.mdata.contact.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@TableName("sys_contact_bank_import")
public class ContactBankAccountTempDomain extends Domain {

    @TableField("row_number")
    private String rowNumber;

    @TableField("contact_bank_account_oid")
    private UUID contactBankAccountOid;

    @TableField("employee_id")
    private String employeeId;

    @TableField("user_oid")
    private UUID userOid;

    @TableField("bank_account_name")
    private String bankAccountName;

    @TableField("bank_account_no")
    private String bankAccountNo;

    @TableField("account_location")
    private String accountLocation;

    @TableField("branch_name")
    private String branchName;

    @TableField("bank_code")
    private String bankCode;

    @TableField("bank_name")
    private String bankName;

    @TableField("primary_str")
    private String primaryStr;

    @TableField("has_primary")
    private boolean hasPrimary;

    @TableField("primary_flag")
    @JsonProperty(value = "primary")
    private boolean primaryFlag;

    @TableField("enabled_str")
    private String enabledStr;

    @TableField("enabled")
    private boolean enabled;

    @TableField("batch_number")
    private String batchNumber ;

    @TableField("error_detail")
    private String errorDetail;

    @TableField("error_flag")
    private Boolean errorFlag;
}
