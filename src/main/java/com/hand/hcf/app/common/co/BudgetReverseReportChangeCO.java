package com.hand.hcf.app.common.co;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by kai.zhang on 2017-12-25.
 * 预算占用变更-报销单指定节点可修改金额(只能小于原金额)
 */
@Data
public class BudgetReverseReportChangeCO {
    @NotNull
    private String businessType;         //EXP_REQUISITION:费用申请单,EXP_REPORT:费用报销单
    @NotNull
    private Long documentId;         //费用申请单/报销单头ID
    @NotNull
    private Long documentLineId;         //费用申请单/报销单分配行ID
    @NotNull
    private BigDecimal amount;       //变更后的原币金额
    @NotNull
    private BigDecimal functionalAmount;       //变更后的本币金额

}
