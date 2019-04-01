package com.hand.hcf.app.workflow.workflow.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import javax.persistence.Column;
import java.util.UUID;

/**
 * A ApprovalChain.
 */
@Data
@TableName("sys_approval_chain")
public class ApprovalChain extends Domain {

    private static final long serialVersionUID = 1L;

    private Integer entityType;

    @TableField(value = "entity_oid")
    private UUID entityOid;

    @TableField("sequence_number")
    private Integer sequence;

    private Integer countersignType;

    private Integer countersignRule;

    @TableField(value = "approver_oid")
    private UUID approverOid;

    private Boolean currentFlag;

    private Boolean finishFlag;

    private Integer status;

    private Boolean noticed;//是否只通知

    private Boolean apportionmentFlag = false;

    @TableField(value = "rule_approval_node_oid")
    private UUID ruleApprovalNodeOid;

    private Boolean proxyFlag;

    /**
     * 是否是加签人 0不是  1是
     */
    @Column(name = "add_sign")
    private Integer addSign;

    /**
     * 能否修改核定金额
     */
    private Integer invoiceAllowUpdateType;
    /**
     * 来源的审批链id
     */
    private Long sourceApprovalChainId;
    /**
     * chain是否完成
     */
    private boolean allFinished;

}
