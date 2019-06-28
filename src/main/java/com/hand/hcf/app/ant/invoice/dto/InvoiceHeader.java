package com.hand.hcf.app.ant.invoice.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import com.hand.hcf.app.expense.common.utils.RespCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@ApiModel(description = "蚂蚁发票头")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ant_invoice_header")
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceHeader extends Domain {
    @TableId
    @JsonSerialize(
            using = ToStringSerializer.class
    )
    private Long id;


    /**
     * 租户ID
     */
    @NotNull
    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "租户ID",dataType = "Long",required = true)
    private Long tenantId;

    /**
     * 账套ID
     */
    @NotNull
    @TableField("set_of_book_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "账套ID",dataType = "Long",required = true)
    private Long setOfBooksId;

    /**
     * 发票类型
     */
    @ApiModelProperty(value = "发票类型")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "invoice_type")
    private String invoiceType;

    /**
     * 发票代码
     */
    @ApiModelProperty(value = "发票代码")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "invoice_code")
    private String invoiceCode;

    /**
     * 发票号码
     */
    @ApiModelProperty(value = "发票号码")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "invoice_number")
    private String invoiceNumber;

    /**
     * 购方机构OU
     */
    @ApiModelProperty(value = "购方机构OU //购方公司ID")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "buyer_OU")
    private String buyerOU;

    /**
     * 价税合计金额
     */
    @ApiModelProperty(value = "价税合计金额")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "price_tax_total_amount")
    private BigDecimal priceTaxTotalAmount;

    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "currency")
    private String currency;

    /**
     * 开票日期
     */
    @ApiModelProperty(value = "开票日期")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "make_invoice_date")
    private ZonedDateTime makeInvoiceDate;

    /**
     * 收票日期
     */
    @ApiModelProperty(value = "收票日期")
    @TableField(value = "take_invoice_date")
    private ZonedDateTime takeInvoiceDate;

    /**
     * 销方公司ID
     */
    @ApiModelProperty(value = "销方公司ID")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "seller_company_id")
    private String sellerCompanyId;

    /**
     * 销方公司名称
     */
    @ApiModelProperty(value = "销方公司名称")
    @TableField(exist = false)
    private String sellerCompanyName;

    /**
     * 销售方税号
     */
    @ApiModelProperty(value = "销售方税号")
    @TableField(value = "seller_tax_number")
    private String sellerTaxNumber;

    /**
     * 销售方公司地址
     */
    @ApiModelProperty(value = "销售方公司地址")
    @TableField(value = "seller_company_address")
    private String sellerCompanyAddress;

    /**
     * 销售方公司电话
     */
    @ApiModelProperty(value = "销售方公司电话")
    @TableField(value = "seller_company_address")
    private String sellerCompanyPhone;

    /**
     * 销售方开户行名称
     */
    @ApiModelProperty(value = "销售方开户行名称")
    @TableField(value = "seller_bank_name")
    private String sellerBankName;

    /**
     * 销方银行账号
     */
    @ApiModelProperty(value = "销方银行账号")
    @TableField(value = "seller_bank_account")
    private String sellerBankAccount;

    /**
     * 购方抬头信息
     */
    @ApiModelProperty(value = "购方抬头信息")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "buyer_header_info")
    private String buyerHeaderInfo;

    /**
     * 购方税号
     */
    @ApiModelProperty(value = "购方税号")
    @TableField(value = "buyer_tax_number")
    private String buyerTaxNumber;

    /**
     * 购方公司地址
     */
    @ApiModelProperty(value = "购方公司地址")
    @TableField(value = "buyer_company_address")
    private String buyerCompanyAddress;

    /**
     * 购方公司电话
     */
    @ApiModelProperty(value = "购方公司电话")
    @TableField(value = "buyer_company_phone")
    private String buyerCompanyPhone;

    /**
     * 购方开户行名称
     */
    @ApiModelProperty(value = "购方开户行名称")
    @TableField(value = "buyer_bank_name")
    private String buyerBankName;

    /**
     * 购方银行账号
     */
    @ApiModelProperty(value = "购方银行账号")
    @TableField(value = "buyer_bank_account")
    private String buyerBankAccount;

    /**
     * 摘要
     */
    @ApiModelProperty(value = "摘要")
    @TableField(value = "comment")
    private String comment;

    /**
     * 发票行信息
     */
    @ApiModelProperty(value = "发票行信息")
    @TableField(exist = false)
    private List<InvoiceLine> invoiceLines;

}
