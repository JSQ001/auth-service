package com.hand.hcf.app.common.co;

import lombok.Data;

import java.util.UUID;

/**
 * @Description:
 * @Date: Created in 10:52 2018/7/10
 * @Modified by
 */
@Data
public class UserBankAccountCO {

    private UUID userOid;
    private Long userId;

    private UUID contactBankAccountOid;

    private String bankAccountNo;

    private String bankAccountName;

    private String bankCode;

    private String bankName;

    private String branchName;

    private String accountLocation;

    private String originalBankAccountNo;

}
