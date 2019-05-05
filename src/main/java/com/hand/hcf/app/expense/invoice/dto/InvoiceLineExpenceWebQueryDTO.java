package com.hand.hcf.app.expense.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @description: 发票行报销记录DTO
 * @version: 1.0
 * @author: shouting.cheng@hand-china.com
 * @date: 2019/3/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceLineExpenceWebQueryDTO {

    private Long id;

    /**
     * 发票分配行ID
     */
    private Long invoiceLineDistId;
    /**
     * 发票行ID
     */
    private Long invoiceLineId;
    /**
     * 发票行序号
     */
    private Integer invoiceLineSequence;

    /**
     * 报账单头ID
     */
    private Long reportHeadId;

    /**
     * 报账单单号
     */
    private String requisitionNumber;

    /**
     * 报账单类型id
     */
    private Long reportTypeId;

    /**
     * 报账单类型名称
     */
    private String reportTypeName;

    /**
     * 报账单申请人id
     */
    private Long applicantId;

    /**
     * 报账单申请人名称
     */
    private String applicantName;

    /**
     * 报账单申请日期
     */
    private ZonedDateTime requisitionDate;

    /**
     * 报账单行ID
     */
    private Long reportLineId;
    /**
     * 费用类型id
     */
    private Long expenseTypeId;
    /**
     * 费用类型名称
     */
    private String expenseTypeName;
    /**
     * 报账单行备注
     */
    private String remarks;

    /**
     * 发票袋号码
     */
    private String invoiceBagNo;

    /**
     * 发票袋号码确认标志
     */
    private String invoiceBagConfirmFlag;
    /**
     * 发票袋号码确认描述
     */
    private String invoiceBagConfirmFlagDesc;
    /**
     * 发票号码
     */
    private String invoiceNo;
    /**
     * 发票代码
     */
    private String invoiceCode;
    /**
     *  开票日期
     */
    private ZonedDateTime invoiceDate;
    /**
     * 发票金额
     */
    private BigDecimal detailAmount;
    /**
     * 税率
     */
    private String taxRate;
    /**
     * 税额
     */
    private BigDecimal taxAmount;
    /**
     * 发票是否匹配标志
     */
    private String invoiceMateFlag;
    /**
     * 发票是否匹配描述
     */
    private String invoiceMateFlagDesc;

}
