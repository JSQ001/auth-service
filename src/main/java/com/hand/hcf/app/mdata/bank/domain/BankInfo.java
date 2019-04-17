package com.hand.hcf.app.mdata.bank.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

@Data
@TableName("sys_bank_info")
public class BankInfo extends DomainLogicEnable {

    private String bankCode;

    private String bankBranchName;

    private String bankName;

    private String countryName;

    private String countryCode;

    private String bankType;

    private String province;

    private String provinceCode;

    private String city;

    private String cityCode;

    private String bankHead;


    private Long tenantId;          // 租户id

    private String swiftCode;       // swift 编码

    private String openAccount;     // 开户地

    private String detailAddress;   // 详细地址

    //员工或供应商银行账号
    @TableField(exist = false)
    private String number;

}
