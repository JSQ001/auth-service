package com.hand.hcf.app.common.co;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by kai.zhang on 2017-11-09.
 * 预算占用反冲 提供信息
 */
@Data
public class BudgetReverseRollbackCO {
    @NotNull
    private String businessType;         //EXP_REQUISITION:费用申请单,EXP_REPORT:费用报销单
    
    private Long documentId;         //费用申请单/报销单头ID
    @NotNull
    private Long documentLineId;         //费用申请单/报销单分配行ID
//    private Double amount;         //金额
//    private Double functionalAmount;         //本位币金额
//    private Integer quantity;         //数量
    @Valid
    private BudgetReportRequisitionReleaseCO releaseMsg;       //预算占用/释放关联关系
}
