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

/**
 * @description: 发票分配行表(发票分摊行表)
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("invoice_line_dist")
public class InvoiceLineDist extends Domain {
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

    //关联发票行ID
    @NotNull
    @TableField("invoice_line_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long invoiceLineId;

    //发票号码
    @NotNull
    @TableField("invoice_no")
    private String invoiceNo;

    //发票代码
    @NotNull
    @TableField("invoice_code")
    private String invoiceCode;

    //货物或应税劳务、服务名称
    @TableField("goods_name")
    private String goodsName;

    //规格型号
    @TableField("specification_model")
    private String specificationModel;

    //单位
    @TableField("unit")
    private String unit;

    //数量
    @TableField("num")
    private Long num;

    //单价
    @TableField("unit_price")
    private BigDecimal unitPrice;

    //金额
    @NotNull
    @TableField("detail_amount")
    private BigDecimal detailAmount;

    //税率
    @NotNull
    @TableField("tax_rate")
    private String taxRate;

    //税额
    @NotNull
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    //币种
    @NotNull
    @TableField("currency_code")
    private String currencyCode;

    //汇率
    @NotNull
    @TableField("exchange_rate")
    private BigDecimal exchangeRate;

    //入账标识
    @TableField("accounting_flag")
    private String accountingFlag;
}
