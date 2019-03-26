package com.hand.hcf.app.prepayment.web.dto;

import lombok.Data;

import java.util.UUID;

/**
 * Created by 刘亮 on 2017/9/8.
 */
@Data
public class PartnerBankInfo {
    private UUID userOID;

    private UUID contactBankAccountOID;

    private String bankAccountNo;

    private String bankAccountName;

    private String bankName;

    private String branchName;

    private String accountLocation;

    private String originalBankAccountNo;

    private boolean isPrimary = true;

    private boolean enable = true;

    private String bankCode;
    private Long tenantId;
    private Long id;

}
