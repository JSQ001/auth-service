package com.hand.hcf.app.mdata.contact.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * A ContactBankAccount.
 */
@Data
@TableName("sys_contact_bank_account")
public class ContactBankAccount extends DomainLogicEnable {


    @NotNull
    @TableField(value = "contact_bank_account_oid")
    private UUID contactBankAccountOid;

    @TableField(value = "user_oid")
    private UUID userOid;

    private String bankAccountNo;

    private String bankAccountName;

    private String bankName;

    private String branchName;

    private String accountLocation;

    @NotNull
    private Boolean primary;

    private String bankCode;

    /**
     * 收款方编码
     * (对公支付时取供应商id,非对公支付取员工工号)
     */
    @Transient
    @TableField(exist = false)
    private String payeeCode;
    /**
     * 收款方id
     * (对公支付取供应商id，非对公支付取员工id）
     */
    @Transient
    @TableField(exist = false)
    private Long payeeId;

    /**
     * 是否是对公支付
     * (默认非对公支付)
     */
    @Transient
    @TableField(exist = false)
    private Boolean isPublicPayment = false;


}
