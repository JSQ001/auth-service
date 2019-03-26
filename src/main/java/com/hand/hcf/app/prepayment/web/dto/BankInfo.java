package com.hand.hcf.app.prepayment.web.dto;

import lombok.Data;

/**
 * Created by 刘亮 on 2018/1/31.
 */
@Data
public class BankInfo {
    //员工或供应商银行账号
    private String number;

    //员工或供应商银行账户名称
    private String BankNumberName;

    //开户银行code
    private String bankCode;

    //开户银行name
    private String bankName;

    //是否默认
    private Boolean primary;

}
