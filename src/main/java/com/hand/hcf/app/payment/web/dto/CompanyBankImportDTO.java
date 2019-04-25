package com.hand.hcf.app.payment.web.dto;

import lombok.Data;

/**
 * 公司银行账户导入信息字段
 * @author zhongyan.zhao
 */
@Data
public class CompanyBankImportDTO {
    /**
     * 序号
     */
    private String rowNumber;
    /**
     * 账套code
     */
    private String setOfBooksCode;
    /**
     * 公司code
     */
    private String companyCode;
    /**
     * 银行code
     */
    private String bankCode;
    /**
     * 银行支行名称
     */
    private String bankBranchName;
    /**
     * 银行账户名称
     */
    private String bankAccountName;
    /**
     * 银行账户账号
     */
    private String bankAccountNumber;
    /**
     * 银行账户编码
     */
    private String accountCode;
    /**
     * swiftCode
     */
    private String swiftCode;
    /**
     * 币种
     */
    private String currencyCode;
    /**
     * 备注
     */
    private String remark;
    /**
     * 启用标志
     */
    private String enabled;
    /**
     * 删除标志
     */
    private String deleted;
}
