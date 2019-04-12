package com.hand.hcf.app.workflow.approval.dto;

import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;

/**
 * 工作流规则
 * @author mh.z
 * @date 2019/04/11
 */
public class WorkflowRule {
    private RuleApprovalNode ruleApprovalNode;

    /** 会签规则-所有审批人（所有审批人审批通过则单据审批通过，任一审批人审批驳回则单据被驳回） */
    public static final Integer COUNTERSIGN_ALL_PASS_OR_ANY_REJECT = RuleApprovalEnum.RULE_CONUTERSIGN_ALL.getId();
    /** 会签规则-任一人（任一审批人审批通过则单据被审批通过，所有审批人都审批驳回则单据才被驳回） */
    public static final Integer COUNTERSIGN_ANY_PASS_OR_ALL_REJECT = RuleApprovalEnum.RULE_CONUTERSIGN_ALL_REJECT.getId();
    /** 会签规则-一票通过/一票否决（一位审批人审批通过则单据被审批通过，一位审批人审批驳回后则单据被驳回） */
    public static final Integer COUNTERSIGN_ANY_PASS_OR_ANY_REJECT = RuleApprovalEnum.RULE_CONUTERSIGN_ANY.getId();

    /** 节点为空跳过 */
    public static final Integer EMPTY_NODE_SKIP = RuleApprovalEnum.RULE_NULLABLE_SKIP.getId();
    /** 节点为空不跳过 */
    public static final Integer EMPTY_NODE_NOT_SKIP = RuleApprovalEnum.RULE_NULLABLE_THROW.getId();

    public WorkflowRule(RuleApprovalNode ruleApprovalNode) {
        this.ruleApprovalNode = ruleApprovalNode;
    }

    public RuleApprovalNode getRuleApprovalNode() {
        return ruleApprovalNode;
    }

    public Integer getEmptyNodeRule() {
        return ruleApprovalNode.getNullableRule();
    }

    public Integer getCountersignRule() {
        return ruleApprovalNode.getCountersignRule();
    }

}
