package com.hand.hcf.app.mdata.bank.dto;

import lombok.Data;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/1/23 22:12
 */
@Data
public class BankAccountDTO {
    //员工银行账号
    private String Account;

    //员工账户名称
    private String BankAccountName;

    //开户银行code
    private String bankCode;

    //开户银行name
    private String bankName;

    //是否默认
    private Boolean primary;
}
