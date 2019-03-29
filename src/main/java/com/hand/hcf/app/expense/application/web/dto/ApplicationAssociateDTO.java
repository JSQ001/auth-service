package com.hand.hcf.app.expense.application.web.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 *  被关联申请单的信息
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/3/22
 */
@Data
public class ApplicationAssociateDTO {
    /**
     * 报账单id
     */
    private Long reportId;
    /**
     * 申请单id
     */
    private Long applicationHeaderId;
    /**
     * 申请单行id
     */
    private Long applicationLineId;
    /**
     * 关联金额
     */
    private BigDecimal amount;
    /**
     * 报账单编号
     */
    private String reportNumber;
    /**
     * 报账单审核状态
     */
    private String auditFlag;
}
