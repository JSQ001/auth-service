package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by kai.zhang on 2017-11-03.
 * 预算占用、预算释放关联关系
 */
@Data
public class BudgetReportRequisitionReleaseCO {
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long releaseID;        //关联id
    //关联单据信息
    @NotNull
    private String releaseBusinessType;         //EXP_REQUISITION:费用申请单,EXP_REPORT:费用报销单
    @NotNull
    private Long releaseDocumentId;         //单据头ID
    @NotNull
    private Long releaseDocumentLineId;        //单据行ID
}
