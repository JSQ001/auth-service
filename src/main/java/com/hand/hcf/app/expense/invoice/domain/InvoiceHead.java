package com.hand.hcf.app.expense.invoice.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @description: 发票头表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("invoice_head")
@ApiModel(description = "发票头信息")
public class InvoiceHead extends Domain {
    /**
     * 发票类型ID
     */
    @NotNull
    @TableField("invoice_type_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "发票类型ID",dataType = "Long")
    private Long invoiceTypeId;

    /**
     * 抵扣标志
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "抵扣标志",dataType = "String",readOnly = true)
    private String deductionFlag;

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
    @TableField("set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "账套ID",dataType = "Long",required = true)
    private Long setOfBooksId;

    /**
     * 开票日期
     */
    @TableField("invoice_date")
    @ApiModelProperty(value = "开票日期",dataType = "ZonedDateTime")
    private ZonedDateTime invoiceDate;

    /**
     * 发票号码
     */
    @NotNull
    @TableField("invoice_no")
    @ApiModelProperty(value = "发票号码",dataType = "String",required = true)
    private String invoiceNo;

    /**
     * 发票代码
     */
    @NotNull
    @TableField("invoice_code")
    @ApiModelProperty(value = "发票代码",dataType = "String",required = true)
    private String invoiceCode;

    /**
     * 设备编号
     */
    @TableField("machine_no")
    @ApiModelProperty(value = "设备编号",dataType = "String")
    private String machineNo;

    /**
     * 校验码(后6位)
     */
    @TableField("check_code")
    @ApiModelProperty(value = "校验码(后6位)",dataType = "String")
    private String checkCode;

    /**
     * 价税合计
     */
    @NotNull
    @TableField("total_amount")
    @ApiModelProperty(value = "价税合计",dataType = "BigDecimal",required = true)
    private BigDecimal totalAmount;

    /**
     * 金额合计
     */
    @TableField("invoice_amount")
    @ApiModelProperty(value = "金额合计",dataType = "BigDecimal")
    private BigDecimal invoiceAmount;

    /**
     * 税额合计
     */
    @TableField("tax_total_amount")
    @ApiModelProperty(value = "税额合计",dataType = "BigDecimal")
    private BigDecimal taxTotalAmount;

    /**
     * 币种
     */
    @NotNull
    @TableField("currency_code")
    @ApiModelProperty(value = "币种",dataType = "String",required = true)
    private String currencyCode;

    /**
     * 汇率
     */
    @NotNull
    @TableField("exchange_rate")
    @ApiModelProperty(value = "汇率",dataType = "BigDecimal",required = true)
    private BigDecimal exchangeRate;

    /**
     * 备注
     */
    @TableField("remark")
    @ApiModelProperty(value = "备注",dataType = "String")
    private String remark;

    /**
     * 购方名称
     */
    @TableField("buyer_name")
    @ApiModelProperty(value = "购方名称",dataType = "String")
    private String buyerName;

    /**
     * 购方纳税人识别号
     */
    @TableField("buyer_tax_no")
    @ApiModelProperty(value = "购方纳税人识别号",dataType = "String")
    private String buyerTaxNo;

    /**
     * 购方地址/电话
     */
    @TableField("buyer_add_ph")
    @ApiModelProperty(value = "购方地址/电话",dataType = "String")
    private String buyerAddPh;

    /**
     * 购方开户行/账号
     */
    @TableField("buyer_account")
    @ApiModelProperty(value = "购方开户行/账号",dataType = "String")
    private String buyerAccount;

    /**
     * 销方名称
     */
    @TableField("saler_name")
    @ApiModelProperty(value = "销方名称",dataType = "String")
    private String salerName;

    /**
     * 销方纳税人识别号
     */
    @TableField("saler_tax_no")
    @ApiModelProperty(value = "销方纳税人识别号",dataType = "String")
    private String salerTaxNo;

    /**
     * 销方地址/电话
     */
    @TableField("saler_add_ph")
    @ApiModelProperty(value = "销方地址/电话",dataType = "String")
    private String salerAddPh;

    /**
     * 销方开户行/账号
     */
    @TableField("saler_account")
    @ApiModelProperty(value = "销方开户行/账号",dataType = "String")
    private String salerAccount;

    /**
     * 作废标志
     */
    @NotNull
    @TableField("cancel_flag")
    @ApiModelProperty(value = "作废标志",dataType = "Boolean", required = true)
    private Boolean cancelFlag;

    /**
     * 红票标志
     */
    @NotNull
    @TableField("red_invoice_flag")
    @ApiModelProperty(value = "红票标志",dataType = "Boolean", required = true)
    private Boolean redInvoiceFlag;

    /**
     * 创建方式
     */
    @NotNull
    @TableField("created_method")
    @ApiModelProperty(value = "创建方式",dataType = "String", required = true)
    private String createdMethod;

    /**
     * 验真状态
     */
    @NotNull
    @TableField("check_result")
    @ApiModelProperty(value = "验真状态",dataType = "Boolean", required = true)
    private Boolean checkResult;

    /**
     * 是否来源账本
     */
    @TableField("from_book")
    @ApiModelProperty(value = "是否来源账本",dataType = "Boolean")
    private Boolean fromBook;

    /**
     * 入账标志
     */
    @TableField("accounting_flag")
    @ApiModelProperty(value = "入账标志",dataType = "String")
    private String accountingFlag;

    /**
     * 红冲标志
     */
    @TableField("red_flag")
    @ApiModelProperty(value = "红冲标志",dataType = "String")
    private String redFlag;

    /**
     * 认证状态
     */
    @TableField("certification_status")
    @ApiModelProperty(value = "认证状态",dataType = "String")
    private String certificationStatus;

    /**
     * 认证日期
     */
    @TableField("certification_date")
    @ApiModelProperty(value = "认证日期",dataType = "ZonedDateTime")
    private ZonedDateTime certificationDate;

    /**
     * 认证失败原因
     */
    @TableField("certification_reason")
    @ApiModelProperty(value = "认证失败原因",dataType = "String")
    private String certificationReason;

    /**
     * 发票状态
     */
    @TableField("invoice_status")
    @ApiModelProperty(value = "发票状态",dataType = "String")
    private String invoiceStatus;

    /**
     * String格式的开票日期(给导出用)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "String格式的开票日期(给导出用)",dataType = "String",readOnly = true)
    private String stringInvoiceDate;

    /**
     * String格式的验真状态(给导出用)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "String格式的验真状态(给导出用)",dataType = "String",readOnly = true)
    private String stringCheckResult;

    /**
     * 发票类型名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发票类型名称",dataType = "String",readOnly = true)
    private String invoiceTypeName;

    /**
     * 创建方式名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "创建方式名称",dataType = "String",readOnly = true)
    private String createdMethodName;

    /**
     * 报账进度
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "报账进度",dataType = "String",readOnly = true)
    private String reportProgress;
    /**
     * 报账进度名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "报账进度名称",dataType = "String",readOnly = true)
    private String reportProgressName;

    /**
     * 发票行信息
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发票行信息",dataType = "List<InvoiceLine>",readOnly = true)
    private List<InvoiceLine> invoiceLineList;

    /**
     * 发票查验失败原因
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发票查验失败原因",dataType = "String",readOnly = true)
    private String checkResultReason;

    /**
     * 发票类型编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发票类型编码",dataType = "String",readOnly = true)
    private String invoiceType;

}
