package com.hand.hcf.app.prepayment.web.dto;

import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * 预付款单关联申请单的DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashPaymentRequisitionLineAssoReqDTO extends CashPaymentRequisitionLine {

    /**
     * 申请人名称
     */
    private String applyName;
    /**
     * 申请单单据类型名称
     */
    private String typeName;

    /**
     * 申请日期
     */
    private ZonedDateTime requisitionDate;

    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 申请单头上金额
     */
    private BigDecimal reqAmount;

}
