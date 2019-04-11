package com.hand.hcf.app.workflow.dto;

import lombok.Data;

@Data
public class WebApprovalHistoryDTO {
    /**
     * 审批节点名称、审批时间（示例2017-05-10 16:49 ）、审批人工号和审批人姓名、审批意见（目前只有驳回，后续会增加审批通过也有意见）
     中间价主动获取数据
     */
    /**
     * 审批动作\
     */
    private Integer operation;
    /**
     * 审批类型
     */
    private Integer operationType;
    /**
     * 审批时间
     */
    private String lastUpdatedDate;

    /**
     * 员工工号
     */
    private String employeeId;
    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 审批意见
     */
   private String operationDetail;
    /**
     * 加签规则
     */
    private Integer countersignType;
    /**
     * 审批动作描述
     */
    private String operationRemark;

    /**
     * 审批节点名称
     */
    private String approvalNodeName;


}
