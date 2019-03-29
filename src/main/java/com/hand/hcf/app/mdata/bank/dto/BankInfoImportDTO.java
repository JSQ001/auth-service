package com.hand.hcf.app.mdata.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 银行信息导入视图对象类
 * Created by Strive on 18/3/22.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankInfoImportDTO {
    // 国家编码
    private String countryCode;
    // 国家名称
    private String countryName;
    // 银行编码
    private String bankCode;
    // swift 编码
    private String swiftCode;
    // 银行名称
    private String bankName;
    // 支行银行名称
    private String bankBranchName;
    // 开户地
    private String openAccount;
    // 详细地址
    private String detailAddress;
    // 是否启用
    private String enabled;
    // 错误描述
    private String errorDetail;
    // 行号
    private Integer rowNum;

    public BankInfoImportDTO(String countryCode, String countryName, String bankCode, String swiftCode, String bankName, String bankBranchName, String openAccount, String detailAddress, String enabled) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.bankCode = bankCode;
        this.swiftCode = swiftCode;
        this.bankName = bankName;
        this.bankBranchName = bankBranchName;
        this.openAccount = openAccount;
        this.detailAddress = detailAddress;
        this.enabled = enabled;
    }
}
