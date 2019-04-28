package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.service.WorkflowInitNodeService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.dto.RuleApprovalChainDTO;
import com.hand.hcf.app.workflow.brms.service.RuleApprovalNodeService;
import com.hand.hcf.app.workflow.brms.service.RuleService;
import com.hand.hcf.app.workflow.util.CheckUtil;

import java.util.UUID;

/**
 * 移到下一节点动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowNextNodeAction implements WorkflowAction {
    private WorkflowActionService service;
    private WorkflowBaseService workflowBaseService;
    private WorkflowInitNodeService workflowInitNodeService;
    private RuleService ruleService;
    private RuleApprovalNodeService ruleApprovalNodeService;
    /** 当前的实例 */
    private WorkflowInstance instance;
    /** 当前的节点 */
    private WorkflowNode node;

    /** 动作名称 */
    public static final String ACTION_NAME = "next node";

    public WorkflowNextNodeAction(WorkflowActionService service, WorkflowInstance instance, WorkflowNode node) {
        this.service = service;
        this.workflowBaseService = service.getWorkflowBaseService();
        this.workflowInitNodeService = service.getWorkflowInitNodeService();
        this.ruleService = service.getRuleService();
        this.ruleApprovalNodeService = service.getRuleApprovalNodeService();
        this.instance = instance;
        this.node = node;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        // 下一节点
        return nextNode(this, instance, node);
    }


    /**
     * 移到下一节点
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param action 动作
     * @param instance 当前的实例
     * @param node 当前的节点
     * @return 操作的结果
     */
    public WorkflowResult nextNode(WorkflowNextNodeAction action, WorkflowInstance instance, WorkflowNode node) {
        CheckUtil.notNull(action, "action null");
        CheckUtil.notNull(instance, "instance null");

        // 移到下一节点的逻辑：
        // 1.查找下一节点
        // 2.更新实例的当前节点
        // 3.初始节点（生成任务等）

        // 查找下一节点
        WorkflowNode nextNode = findNext(instance, node);

        if (nextNode != null) {
            // 更新实例的当前节点
            instance.setLastNodeOid(nextNode.getNodeOid());
            instance.setLastNodeName(nextNode.getName());
            workflowBaseService.updateInstance(instance);
        }

        // 初始节点
        WorkflowResult result = workflowInitNodeService.initNode(nextNode);
        return result;
    }


    /**
     * 返回下一个节点
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance 实例
     * @param node 节点
     * @return 没有下一个节点则返回null
     */
    public WorkflowNode findNext(WorkflowInstance instance, WorkflowNode node) {
        CheckUtil.notNull(instance, "instance null");
        UUID applicantOid = CheckUtil.notNull(instance.getApplicantOid(), "instance.applicantOid null");
        UUID formOid = instance.getWorkFlowDocumentRef().getFormOid();

        UUID ruleApprovalChainOid = null;
        Integer ruleApprovalNodeSequence = null;

        // 获取审批链
        if (node == null) {
            RuleApprovalChainDTO ruleApprovalChainDTO = ruleService.getApprovalChainByFormOid(formOid, applicantOid,
                    false, false, false);
            ruleApprovalChainOid = ruleApprovalChainDTO.getRuleApprovalChainOid();
            ruleApprovalNodeSequence = 0;
        } else {
            ruleApprovalChainOid = node.getRuleApprovalNode().getRuleApprovalChainOid();
            ruleApprovalNodeSequence = node.getRuleApprovalNode().getSequenceNumber();
        }

        // 获取下一个节点
        RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService
                .getNextByRuleApprovalChainOid(ruleApprovalChainOid, ruleApprovalNodeSequence);
        if (ruleApprovalNode == null) {
            return null;
        }

        WorkflowNode nextNode = new WorkflowNode(ruleApprovalNode, instance);
        return nextNode;
    }

}
