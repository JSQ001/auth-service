package com.hand.hcf.app.workflow.approval.dto;

import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.constant.RuleConstants;

import java.util.UUID;

/**
 * 工作流节点
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowNode {
    private RuleApprovalNode ruleApprovalNode;
    /** 实例 */
    private WorkflowInstance instance;
    /** 规则 */
    private WorkflowRule rule;

    /** 审批节点 */
    public static final Integer TYPE_USER = RuleApprovalEnum.NODE_TYPE_APPROVAL.getId();
    /** 机器人节点 */
    public static final Integer TYPE_ROBOT = RuleApprovalEnum.NODE_TYPE_ROBOT.getId();
    /** 结束节点 */
    public static final Integer TYPE_END = RuleApprovalEnum.NODE_TYPE_EED.getId();
    /** 通知节点 */
    public static final Integer TYPE_NOTICE = RuleApprovalEnum.NODE_TYPE_NOTICE.getId();

    /** 审批通过 */
    public static final String ACTION_APPROVAL_PASS = String.valueOf(RuleConstants.ACTION_APPROVAL_PASS);
    /** 审批驳回 */
    public static final String ACTION_APPROVAL_REJECT = String.valueOf(RuleConstants.ACTION_APPROVAL_REJECT);

    public WorkflowNode(RuleApprovalNode ruleApprovalNode, WorkflowInstance instance) {
        this.ruleApprovalNode = ruleApprovalNode;
        this.instance = instance;
        this.rule = new WorkflowRule(ruleApprovalNode);
    }

    public RuleApprovalNode getRuleApprovalNode() {
        return ruleApprovalNode;
    }

    public WorkflowInstance getInstance() {
        return instance;
    }

    public WorkflowRule getRule() {
        return rule;
    }

    public Long getId() {
        return ruleApprovalNode.getId();
    }

    public UUID getNodeOid() {
        return ruleApprovalNode.getRuleApprovalNodeOid();
    }

    public String getName() {
        return ruleApprovalNode.getName();
    }

    public Integer getType() {
        return ruleApprovalNode.getTypeNumber();
    }

    public UUID getChainOid() {
        return ruleApprovalNode.getRuleApprovalChainOid();
    }

    public Integer getSequence() {
        return ruleApprovalNode.getSequenceNumber();
    }

    public String getApprovalAction() {
        return ruleApprovalNode.getApprovalActions();
    }

    public String getApprovalText() {
        return ruleApprovalNode.getComments();
    }

}
