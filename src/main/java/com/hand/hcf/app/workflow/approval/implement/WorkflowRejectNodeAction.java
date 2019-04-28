package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.*;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.service.WorkflowRejectService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.util.CheckUtil;

/**
 * 驳回节点动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowRejectNodeAction implements WorkflowAction {
    private WorkflowActionService service;
    private WorkflowBaseService workflowBaseService;
    /** 操作的节点 */
    private WorkflowNode node;
    /** 操作的用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "reject node";

    /** 结果-驳回成功 */
    public static final String RESULT_REJECT_SUCCESS = "reject success";
    /** 结果-驳回待定 */
    public static final String RESULT_REJECT_PEND = "reject pend";

    public WorkflowRejectNodeAction(WorkflowActionService service, WorkflowNode node, WorkflowUser user, String remark) {
        this.service = service;
        this.workflowBaseService = service.getWorkflowBaseService();
        this.node = node;
        this.user = user;
        this.remark = remark;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        // 驳回节点
        return rejectNode(this, node, user, remark);
    }

    /**
     * 驳回节点
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param action 动作
     * @param node 操作的节点
     * @param user 操作的用户（可选）
     * @param remark 备注（可选）
     * @return 操作的结果
     */
    public WorkflowResult rejectNode(WorkflowRejectNodeAction action, WorkflowNode node, WorkflowUser user, String remark) {
        CheckUtil.notNull(action, "action null");
        CheckUtil.notNull(node, "node null");
        WorkflowInstance instance = CheckUtil.notNull(node.getInstance(), "node.instance null");
        WorkflowRule rule = CheckUtil.notNull(node.getRule(), "node.rule null");
        Integer countersignFlag = CheckUtil.notNull(rule.getCountersignRule(), "node.rule.countersignFlag null");

        // 驳回节点的逻辑：
        // 根据会签规则可以驳回节点
        //   1.清除跟节点关联的所有任务
        //   下一动作：驳回实例
        // 根据会签规则还需要其他人审批
        //   下一动作：null

        boolean canRejectNode = true;
        // 这个会签规则要求同个节点上所有人都驳回才能驳回节点
        if (WorkflowRule.COUNTERSIGN_ANY_PASS_OR_ALL_REJECT.equals(countersignFlag)) {
            int unfinishedTotal = workflowBaseService.countTasks(node, WorkflowTask.APPROVAL_STATUS_APPROVAL);
            canRejectNode = unfinishedTotal == 0;
        }

        WorkflowAction nextAction = null;
        String returnStatus = null;

        if (canRejectNode) {
            // 清除跟节点关联的所有任务
            workflowBaseService.clearAllTasks(node);

            returnStatus = WorkflowRejectNodeAction.RESULT_REJECT_SUCCESS;

            // 下个动作是驳回实例
            WorkflowRejectInstanceAction workflowRejectInstanceAction = new WorkflowRejectInstanceAction(
                    service, instance, user, remark);
            // 记录驳回的节点
            workflowRejectInstanceAction.setRejectNode(node);
            nextAction = workflowRejectInstanceAction;
        } else {
            returnStatus = WorkflowRejectNodeAction.RESULT_REJECT_PEND;

            // 没有下个动作
            nextAction = null;
        }

        WorkflowResult result = new WorkflowResult();
        result.setEntity(node);
        result.setStatus(returnStatus);
        // 设置下一个动作
        result.setNext(nextAction);
        return result;
    }

}
