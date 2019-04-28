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
    /*
    只提供获取/设置通用的字段
     */

    private RuleApprovalNode ruleApprovalNode;
    /** 节点关联的实例 */
    private WorkflowInstance instance;
    /** 规则 */
    private WorkflowRule rule;

    /** 节点类型-审批节点 */
    public static final Integer TYPE_USER = RuleApprovalEnum.NODE_TYPE_APPROVAL.getId();
    /** 节点类型-机器人节点 */
    public static final Integer TYPE_ROBOT = RuleApprovalEnum.NODE_TYPE_ROBOT.getId();
    /** 节点类型-结束节点 */
    public static final Integer TYPE_END = RuleApprovalEnum.NODE_TYPE_EED.getId();
    /** 节点类型-通知节点 */
    public static final Integer TYPE_NOTICE = RuleApprovalEnum.NODE_TYPE_NOTICE.getId();

    public WorkflowNode(RuleApprovalNode ruleApprovalNode, WorkflowInstance instance) {
        this.ruleApprovalNode = ruleApprovalNode;
        this.instance = instance;
        this.rule = new WorkflowRule(ruleApprovalNode);
    }

    public RuleApprovalNode getRuleApprovalNode() {
        return ruleApprovalNode;
    }

    /**
     * 返回节点关联的实例
     *
     * @return
     */
    public WorkflowInstance getInstance() {
        return instance;
    }

    /**
     * 返回节点的规则
     *
     * @return
     */
    public WorkflowRule getRule() {
        return rule;
    }

    /**
     * 返回节点id
     *
     * @return
     */
    public Long getId() {
        return ruleApprovalNode.getId();
    }

    /**
     * 返回节点oid
     *
     * @return
     */
    public UUID getNodeOid() {
        return ruleApprovalNode.getRuleApprovalNodeOid();
    }

    /**
     * 返回节点名称
     *
     * @return
     */
    public String getName() {
        return ruleApprovalNode.getRemark();
    }

    /**
     * 返回节点类型
     *
     * @return
     */
    public Integer getType() {
        return ruleApprovalNode.getTypeNumber();
    }

}
