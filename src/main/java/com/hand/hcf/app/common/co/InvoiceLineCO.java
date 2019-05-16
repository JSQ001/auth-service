package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @description: 发票验真行返回信息
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceLineCO{
    /**
     * 租户ID
     */
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    /**
     * 账套ID
     */
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    /**
     * 发票行序号
     */
    @NotNull
    private Integer invoiceLineNum;

    /**
     * 货物或应税劳务、服务名称
     */
    private String goodsName;

    /**
     * 规格型号
     */
    private String specificationModel;

    /**
     * 单位
     */
    private String unit;

    /**
     * 数量
     */
    private Long num;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 金额
     */
    @NotNull
    private BigDecimal detailAmount;

    /**
     * 税率
     */
    @NotNull
    private String taxRate;

    /**
     * 税额
     */
    @NotNull
    private BigDecimal taxAmount;

    /**
     * 币种
     */
    @NotNull
    private String currencyCode;

    /**
     * 汇率
     */
    @NotNull
    private BigDecimal exchangeRate;

}
