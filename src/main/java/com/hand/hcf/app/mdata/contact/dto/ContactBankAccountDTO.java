package com.hand.hcf.app.mdata.contact.dto;

import lombok.Data;

import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/20
 */
@Data
public class ContactBankAccountDTO {
    private UUID userOid;

    private UUID contactBankAccountOid;

    private String employeeId;

    private String bankAccountNo;

    private String bankAccountName;

    private String bankName;

    private String branchName;

    private String accountLocation;

    private String originalBankAccountNo;

    private Boolean primary = true;
    /**
     * 是否默认（导出银行信息用）
     */
    private String primaryStr;

    private Boolean enabled = true;
    /**
     * 是否启用（导出银行信息用）
     */
    private String enabledStr;

    private String bankCode;
    private Long tenantId;

}
