package com.hand.hcf.app.mdata.supplier.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/4 16:04
 */
@TableName("ven_vendor_bank_account")
@Data
public class VendorBankAccount extends Domain {

    private Long vendorInfoId;

    private String companyOid;

    private String vendorId;

    private String bankAccount;

    private String bankName;

    private String venBankNumberName;

    private Integer status;

    private String openingBank;

    private String openingBankCity;

    private String openingBankLineNum;

    private String bankCode;

    private String bankAddress;

    private String country;

    private String swiftCode;

    private Boolean primaryFlag;

    private String importCode;

    private String remark;
}
