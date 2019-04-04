package com.hand.hcf.app.expense.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
