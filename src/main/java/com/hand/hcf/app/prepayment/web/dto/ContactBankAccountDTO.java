package com.hand.hcf.app.prepayment.web.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by 刘亮 on 2017/12/16.
 */
@Data
public class ContactBankAccountDTO {
    @NotNull
    private UUID userOID;

    private UUID contactBankAccountOID;

    @NotNull
    private String bankAccountNo;

    @NotNull
    private String bankAccountName;

    @NotNull
    private String bankName;

    @NotNull
    private String branchName;

    @NotNull
    private String accountLocation;

    private String originalBankAccountNo;

    private boolean isPrimary;

    private boolean enable = true;

    private String bankCode;
}
