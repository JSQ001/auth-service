package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by 刘亮 on 2017/9/8.
 */
@ApiModel(description = "公司银行账户表")
@Data
@TableName("csh_company_bank")
public class CompanyBank extends DomainI18nEnable implements Serializable {

    @ApiModelProperty(value = "公司id")
    @TableField("company_id")
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    @ApiModelProperty(value = "租户id")
    @TableField(value = "tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonIgnore
    private Long tenantId;

    @ApiModelProperty(value = "开户银行")
    @TableField("bank_name")
    @Length(max = 50)
    private String bankName;

    @ApiModelProperty(value = "银行支行名称")
    @TableField("bank_branch_name")
    private String bankBranchName;

    @ApiModelProperty(value = "银行代码")
    @TableField("bank_code")
    @Length(max = 50)
    private String bankCode;

    @ApiModelProperty(value = "银行账户名称")
    @I18nField
    @TableField("bank_account_name")
    @NotNull
    private String bankAccountName;

    @ApiModelProperty(value = "银行账户账号")
    @NotNull
    @Length(max = 36)
    @TableField("bank_account_number")
    private String bankAccountNumber;

    @ApiModelProperty(value = "币种")
    @Length(max = 36)
    @TableField("currency_code")
    @NotNull
    private String currencyCode;

    @ApiModelProperty(value = "账户代码")
    @TableField("account_code")
    private String accountCode;

    @ApiModelProperty(value = "银行的国际代码")
    @Length(max = 50)
    @TableField("swift_code")
    private String swiftCode;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "国家")
    @TableField("country")
    private String country;

    @ApiModelProperty(value = "城市")
    @TableField("city")
    private String city;

    @ApiModelProperty(value = "省份")
    @Length(max = 50)
    @TableField("province")
    private String province;

    @ApiModelProperty(value = "省份代码")
    @Length(max = 50)
    @TableField("province_code")
    private String provinceCode;

    @ApiModelProperty(value = "国家代码")
    @TableField("country_code")
    @Length(max = 50)
    private String countryCode;

    @ApiModelProperty(value = "城市代码")
    @TableField("city_code")
    @Length(max = 50)
    private String cityCode;

    @ApiModelProperty(value = "银行地址")
    @TableField("bank_address")
    private String bankAddress;

    //修改迁移部分字段：
    @ApiModelProperty(value = "公司银行代码")
    @TableField("company_bank_code")
    @Length(max = 50)
    private String companyBankCode;

    @ApiModelProperty(value = "银行联行号")
    @TableField("bank_key")
    @Length(max = 200)
    private String bankKey;

    @ApiModelProperty(value = "帐套id")
    @TableField("set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    @ApiModelProperty(value = "帐套代码")
    @TableField("set_of_books_code")
    private String setOfBooksCode;

    @ApiModelProperty(value = "公司代码")
    @TableField("company_code")
    private String companyCode;

    @ApiModelProperty(value = "开户地")
    @TableField("account_opening_address")
    private String accountOpeningAddress;
}
