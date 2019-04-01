package com.hand.hcf.app.workflow.workflow.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import java.util.UUID;

/**
 * A ApprovalHistory.
 */
@Data
@TableName("sys_approval_history")
public class ApprovalHistory extends Domain {

    private static final long serialVersionUID = 1L;

    private Integer entityType;

    @TableField(value = "entity_oid")
    private UUID entityOid;

    private Integer operationType;

    private Integer operation;

    private Integer countersignType;

    private boolean apportionmentFlag = false;

    @TableField(value = "operator_oid")
    private UUID operatorOid;

    @TableField(value = "current_applicant_oid")
    private UUID currentApplicantOid;

    //审批节点Oid
    @TableField(value = "approval_node_oid")
    private UUID  approvalNodeOid;

    //审批节点名称
    private String approvalNodeName;

    /**
     * 操作详情，报销单审批  单笔驳回费用时，此字段存入驳回的费用类型Oid、金额和驳回理由，在查询时费用类型名称需要处理多语言
     */
    private String operationDetail;

    @TableField(value = "step_id")
    private Long stepID;

    private String remark;


    @TableField(value = "rule_approval_node_oid")
    private UUID ruleApprovalNodeOid;

    private Long refApprovalChainId;

}
