package com.hand.hcf.app.common.co;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @description: 发票验真头返回信息
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceHeadCO{

    /**
     * 发票ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 发票类型ID
     */
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long invoiceTypeId;

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
     * 开票日期
     */
    @NotNull
    private ZonedDateTime invoiceDate;

    /**
     * 发票号码
     */
    @NotNull
    private String invoiceNo;

    /**
     * 发票代码
     */
    @NotNull
    private String invoiceCode;

    /**
     * 设备编号
     */
    private String machineNo;

    /**
     * 校验码(后6位)
     */
    private String checkCode;

    /**
     * 价税合计
     */
    private BigDecimal totalAmount;

    /**
     * 金额合计
     */
    private BigDecimal invoiceAmount;

    /**
     * 税额合计
     */
    private BigDecimal taxTotalAmount;

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

    /**
     * 备注
     */
    private String remark;

    /**
     * 购方名称
     */
    private String buyerName;

    /**
     * 购方纳税人识别号
     */
    private String buyerTaxNo;

    /**
     * 购方地址/电话
     */
    private String buyerAddPh;

    /**
     * 购方开户行/账号
     */
    private String buyerAccount;

    /**
     * 销方名称
     */
    private String salerName;

    /**
     * 销方纳税人识别号
     */
    private String salerTaxNo;

    /**
     * 销方地址/电话
     */
    private String salerAddPh;

    /**
     * 销方开户行/账号
     */
    private String salerAccount;

    /**
     * 作废标志
     */
    private Boolean cancelFlag;

    /**
     * 红票标志
     */
    private Boolean redInvoiceFlag;

    /**
     * 发票行信息
     */
    @Valid
    private List<InvoiceLineCO> invoiceLineCOList;

}
