package com.hand.hcf.app.workflow.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : mingming.zhang
 * @description : 审批驳回报销单中的费用，记录驳回的费用类型、金额、驳回理由
 * @since : 2018/4/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovalRejectInvoiceDetailDTO {

    /**
     * 费用类型ID
     */
    private Long expenseTypeID;

    /**
     * 费用类型名称，在查询报销单详情时需要查出多语言
     */
    private String expenseTypeName;

    /**
     * 费用金额
     */
    private Double amount;

    /**
     * 驳回理由
     */
    private String rejectReason;
}
