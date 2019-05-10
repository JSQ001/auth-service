package com.hand.hcf.app.workflow.approval.dto;

import com.hand.hcf.app.workflow.brms.constant.RuleConstants;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;

/**
 * 工作流规则
 * @author mh.z
 * @date 2019/04/11
 */
public class WorkflowRule {
    /*
    只提供获取/设置通用的字段
     */

    private RuleApprovalNode ruleApprovalNode;

    /** 驳回规则（不为null的时候会覆盖节点设置的驳回规则） */
    private Integer rejectRule;

    /** 会签规则-所有审批人（所有审批人审批通过则单据审批通过，任一审批人审批驳回则单据被驳回） */
    public static final Integer COUNTERSIGN_ALL_PASS_OR_ANY_REJECT = RuleApprovalEnum.RULE_CONUTERSIGN_ALL.getId();
    /** 会签规则-任一人（任一审批人审批通过则单据被审批通过，所有审批人都审批驳回则单据才被驳回） */
    public static final Integer COUNTERSIGN_ANY_PASS_OR_ALL_REJECT = RuleApprovalEnum.RULE_CONUTERSIGN_ALL_REJECT.getId();
    /** 会签规则-一票通过/一票否决（一位审批人审批通过则单据被审批通过，一位审批人审批驳回后则单据被驳回） */
    public static final Integer COUNTERSIGN_ANY_PASS_OR_ANY_REJECT = RuleApprovalEnum.RULE_CONUTERSIGN_ANY.getId();

    /** 节点为空-跳过 */
    public static final Integer EMPTY_NODE_SKIP = RuleApprovalEnum.RULE_NULLABLE_SKIP.getId();
    /** 节点为空-不跳过 */
    public static final Integer EMPTY_NODE_NOT_SKIP = RuleApprovalEnum.RULE_NULLABLE_THROW.getId();

    /** 驳回后再次提交处理-重新全部审批 */
    public static final Integer REJECT_SUBMIT_RESTART = RuleConstants.RULE_REJECT_SUBMIT_RESTART;
    /** 驳回后再次提交处理-直接跳回本节点 */
    public static final Integer REJECT_SUBMIT_SKIP = RuleConstants.RULE_REJECT_SUBMIT_SKIP;
    /** 驳回后再次提交处理-驳回人自主判断 */
    public static final Integer REJECT_USER_DECIDE = RuleConstants.RULE_REJECT_USER_DECIDE;

    /** 重复规则-跳过审批 */
    public static final Integer REPEAT_SKIP = RuleApprovalEnum.RULE_REPEAR_SKIP.getId();
    /** 重复规则-审批 */
    public static final Integer REPEAT_APPROVAL = RuleApprovalEnum.RULE_REPEAR_APPROVAL.getId();

    public WorkflowRule(RuleApprovalNode ruleApprovalNode) {
        this.ruleApprovalNode = ruleApprovalNode;
    }

    public RuleApprovalNode getRuleApprovalNode() {
        return ruleApprovalNode;
    }

    /**
     * 返回节点为空规则
     *
     * @return
     */
    public Integer getEmptyNodeRule() {
        return ruleApprovalNode.getNullableRule();
    }

    /**
     * 返回会签规则
     *
     * @return
     */
    public Integer getCountersignRule() {
        return ruleApprovalNode.getCountersignRule();
    }


    /**
     * 返回驳回规则
     *
     * @return
     */
    public Integer getRejectRule() {
        if (rejectRule != null) {
            return rejectRule;
        }

        return ruleApprovalNode.getRejectRule();
    }

    /**
     * 驳回规则
     *
     * @param rejectRule
     */
    public void setRejectRule(Integer rejectRule) {
        this.rejectRule = rejectRule;
    }

    /**
     * 返回重复审批规则
     *
     * @return
     */
    public Integer getRepeatRule() {
        return ruleApprovalNode.getRepeatRule();
    }

    /**
     * 判断是否允许转交
     *
     * @return
     */
    public Boolean getTransferFlag() {
        return ruleApprovalNode.getTransferFlag();
    }

    /**
     * 判断是否允许加签
     *
     * @return
     */
    public Boolean getAddSignFlag() {
        return ruleApprovalNode.getAddsignFlag();
    }

}
