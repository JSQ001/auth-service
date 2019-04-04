package com.hand.hcf.app.expense.invoice.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @description: 发票分配行表DTO(发票分摊行表DTO)
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceLineDistDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    //租户ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    //账套ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    //关联发票行ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long invoiceLineId;

    //发票号码
    private String invoiceNo;

    //发票代码
    private String invoiceCode;

    //货物或应税劳务、服务名称
    private String goodsName;

    //规格型号
    private String specificationModel;

    //单位
    private String unit;

    //数量
    private Long num;

    //单价
    private BigDecimal unitPrice;

    //金额
    private BigDecimal detailAmount;

    //税率
    private String taxRate;

    //税额
    private BigDecimal taxAmount;

    //币种
    private String currencyCode;

    //汇率
    private BigDecimal exchangeRate;

    private ZonedDateTime createdDate;
    private Long createdBy;
    private ZonedDateTime lastUpdatedDate;
    private Long lastUpdatedBy;
    private Integer versionNumber;



    //发票类型id
    private Long invoiceTypeId;
    //发票类型名称
    private String invoiceTypeName;

    //发票头id
    private Long invoiceHeadId;

    //开票日期
    private ZonedDateTime invoiceDate;
    //开票日期
    private String stringInvoiceDate;

    //金额合计
    private BigDecimal invoiceAmount;

    //发票行号
    private Integer invoiceLineNum;

    //发票行金额
    private BigDecimal lineDetailAmount;

    //报账单号
    private String expenseNum;

    //申请日期
    private ZonedDateTime applicationDate;
    //申请日期
    private String stringApplicationDate;

    //申请人
    private Long applicant;

    private String applicantName;

    //单据状态
    private String documentState;

    //费用行号
    private Integer costLineNumber;

    //费用类型
    private String costType;

    //费用金额
    private BigDecimal costAmount;

    //分期抵扣
    private Boolean installmentDeduction;
}
