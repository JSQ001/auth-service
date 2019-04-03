package com.hand.hcf.app.expense.invoice.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @description: 发票行表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("invoice_line")
public class InvoiceLine extends Domain {
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

    //发票头ID
    @NotNull
    @TableField("invoice_head_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long invoiceHeadId;

    //发票行序号
    @NotNull
    @TableField("invoice_line_num")
    private Integer invoiceLineNum;

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

}
