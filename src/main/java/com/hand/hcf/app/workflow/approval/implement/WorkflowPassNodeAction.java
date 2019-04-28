package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.*;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.service.WorkflowPassService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.util.CheckUtil;

/**
 * 通过节点动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowPassNodeAction implements WorkflowAction {
    private WorkflowActionService service;
    private WorkflowBaseService workflowBaseService;
    /** 操作的节点 */
    private WorkflowNode node;
    /** 操作的用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "pass node";

    /** 结果-通过成功 */
    public static final String RESULT_PASS_SUCCESS = "pass success";
    /** 结果-通过待定（还需要其他人审批） */
    public static final String RESULT_PASS_PEND = "pass pend";

    public WorkflowPassNodeAction(WorkflowActionService service, WorkflowNode node, WorkflowUser user, String remark) {
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
        // 通过节点
        return passNode(this, node, user, remark);
    }

    /**
     * 通过节点
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
    protected WorkflowResult passNode(WorkflowPassNodeAction action, WorkflowNode node, WorkflowUser user, String remark) {
        CheckUtil.notNull(action, "action null");
        CheckUtil.notNull(node, "node null");
        WorkflowInstance instance = CheckUtil.notNull(node.getInstance(), "node.instance null");
        WorkflowRule rule = CheckUtil.notNull(node.getRule(), "node.rule null");
        Integer countersignFlag = CheckUtil.notNull(rule.getCountersignRule(), "node.rule.countersignRule null");

        // 通过节点的逻辑：
        // 根据会签规则可以通过节点
        //   1.清除跟节点关联的所有未完成任务
        //   下一动作：下个节点
        // 根据会签规则还需要其他人审批
        //   下一动作：null

        boolean canPassNode = true;
        // 这个会签规则要求同个节点上所有人都通过才能通过节点
        if (WorkflowRule.COUNTERSIGN_ALL_PASS_OR_ANY_REJECT.equals(countersignFlag)) {
            int unfinishedTotal = workflowBaseService.countTasks(node, WorkflowTask.APPROVAL_STATUS_APPROVAL);
            canPassNode = unfinishedTotal == 0;
        }

        WorkflowAction nextAction = null;
        String returnStatus = null;

        if (canPassNode) {
            // 清除跟节点关联的所有未完成任务
            workflowBaseService.clearUnfinishedTasks(node);

            returnStatus = WorkflowPassNodeAction.RESULT_PASS_SUCCESS;
            // 下个动作是移到下个节点
            nextAction = new WorkflowNextNodeAction(service, instance, node);
        } else {
            returnStatus = WorkflowPassNodeAction.RESULT_PASS_PEND;
            // 没有下一个动作
            nextAction = null;
        }

        WorkflowResult result = new WorkflowResult();
        result.setEntity(node);
        result.setStatus(returnStatus);
        result.setNext(nextAction);
        return result;
    }

}
