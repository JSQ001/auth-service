package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 公司银行账户CO
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2018/12/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyBankCO {

    private Long companyId;

    private Long tenantId;

    private String bankName;

    private String bankBranchName;

    private String bankCode;

    private String bankAccountName;

    private String bankAccountNumber;

    private String currencyCode;

    private String accountCode;

    private String swiftCode;

    private String remark;

    private String country;

    private String city;

    private String province;

    private String provinceCode;

    private String countryCode;

    private String cityCode;

    private String bankAddress;

    //修改迁移部分字段：
    private String companyBankCode;

    private String bankKey;

    private Long setOfBooksId;

    private String setOfBooksCode;

    private String companyCode;

    private String accountOpeningAddress;
}
