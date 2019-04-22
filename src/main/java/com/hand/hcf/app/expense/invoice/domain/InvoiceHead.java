package com.hand.hcf.app.expense.invoice.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
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
public class InvoiceHead extends Domain {
    //发票类型ID
    @NotNull
    @TableField("invoice_type_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long invoiceTypeId;

    /**
     * 抵扣标志
     */
    @TableField(exist = false)
    private String deductionFlag;

    //租户ID
    @NotNull
    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    //账套ID
    @NotNull
    @TableField("set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    //开票日期
    @TableField("invoice_date")
    private ZonedDateTime invoiceDate;

    //发票号码
    @NotNull
    @TableField("invoice_no")
    private String invoiceNo;

    //发票代码
    @NotNull
    @TableField("invoice_code")
    private String invoiceCode;

    //设备编号
    @TableField("machine_no")
    private String machineNo;

    //校验码(后6位)
    @TableField("check_code")
    private String checkCode;

    //价税合计
    @NotNull
    @TableField("total_amount")
    private BigDecimal totalAmount;

    //金额合计
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;

    //税额合计
    @TableField("tax_total_amount")
    private BigDecimal taxTotalAmount;

    //币种
    @NotNull
    @TableField("currency_code")
    private String currencyCode;

    //汇率
    @NotNull
    @TableField("exchange_rate")
    private BigDecimal exchangeRate;

    //备注
    @TableField("remark")
    private String remark;

    //购方名称
    @TableField("buyer_name")
    private String buyerName;

    //购方纳税人识别号
    @TableField("buyer_tax_no")
    private String buyerTaxNo;

    //购方地址/电话
    @TableField("buyer_add_ph")
    private String buyerAddPh;

    //购方开户行/账号
    @TableField("buyer_account")
    private String buyerAccount;

    //销方名称
    @TableField("saler_name")
    private String salerName;

    //销方纳税人识别号
    @TableField("saler_tax_no")
    private String salerTaxNo;

    //销方地址/电话
    @TableField("saler_add_ph")
    private String salerAddPh;

    //销方开户行/账号
    @TableField("saler_account")
    private String salerAccount;

    //作废标志
    @NotNull
    @TableField("cancel_flag")
    private Boolean cancelFlag;

    //红票标志
    @NotNull
    @TableField("red_invoice_flag")
    private Boolean redInvoiceFlag;

    //创建方式
    @NotNull
    @TableField("created_method")
    private String createdMethod;

    //验真状态
    @NotNull
    @TableField("check_result")
    private Boolean checkResult;

    //是否来源账本
    @TableField("from_book")
    private Boolean fromBook;

    //入账标志
    @TableField("accounting_flag")
    private String accountingFlag;

    //红冲标志
    @TableField("red_flag")
    private String redFlag;

    //认证状态
    @TableField("certification_status")
    private Long certificationStatus;

    //认证日期
    @TableField("certification_date")
    private ZonedDateTime certificationDate;

    //认证失败原因
    @TableField("certification_reason")
    private String certificationReason;

    //发票状态
    @TableField("invoice_status")
    private Long invoiceStatus;

    //String格式的开票日期(给导出用)
    @TableField(exist = false)
    private String stringInvoiceDate;

    //String格式的验真状态(给导出用)
    @TableField(exist = false)
    private String stringCheckResult;

    //发票类型名称
    @TableField(exist = false)
    private String invoiceTypeName;

    //创建方式名称
    @TableField(exist = false)
    private String createdMethodName;

    //报账进度
    @TableField(exist = false)
    private String reportProgress;
    //报账进度名称
    @TableField(exist = false)
    private String reportProgressName;

    //发票行信息
    @TableField(exist = false)
    private List<InvoiceLine> invoiceLineList;

}
