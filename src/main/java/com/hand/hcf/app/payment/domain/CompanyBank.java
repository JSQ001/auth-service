package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by 刘亮 on 2017/9/8.
 */
@Data
@TableName("csh_company_bank")
public class CompanyBank extends DomainI18nEnable implements Serializable {

    @TableField("company_id")
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    @TableField(value = "tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonIgnore
    private Long tenantId;

    @TableField("bank_name")
    @Length(max = 50)
    private String bankName;

    @TableField("bank_branch_name")
    private String bankBranchName;

    @TableField("bank_code")
    @Length(max = 50)
    private String bankCode;

    @I18nField
    @TableField("bank_account_name")
    @NotNull
    private String bankAccountName;

    @NotNull
    @Length(max = 36)
    @TableField("bank_account_number")
    private String bankAccountNumber;

    @Length(max = 36)
    @TableField("currency_code")
    @NotNull
    private String currencyCode;

    @TableField("account_code")
    private String accountCode;

    @Length(max = 50)
    @TableField("swift_code")
    private String swiftCode;

    @TableField("remark")
    private String remark;

    @TableField("country")
    private String country;

    @TableField("city")
    private String city;

    @Length(max = 50)
    @TableField("province")
    private String province;

    @Length(max = 50)
    @TableField("province_code")
    private String provinceCode;

    @TableField("country_code")
    @Length(max = 50)
    private String countryCode;

    @TableField("city_code")
    @Length(max = 50)
    private String cityCode;

    @TableField("bank_address")
    private String bankAddress;

    //修改迁移部分字段：
    @TableField("company_bank_code")
    @Length(max = 50)
    private String companyBankCode;

    @TableField("bank_key")
    @Length(max = 200)
    private String bankKey;

    @TableField("set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    @TableField("set_of_books_code")
    private String setOfBooksCode;

    @TableField("company_code")
    private String companyCode;

    @TableField("account_opening_address")
    private String accountOpeningAddress;
}
