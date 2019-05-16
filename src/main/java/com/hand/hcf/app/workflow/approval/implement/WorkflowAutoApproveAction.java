package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.*;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.service.ApprovalChainService;
import com.hand.hcf.app.workflow.util.CheckUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动审批动作
 * @author mh.z
 * @date 2019/04/26
 */
public class WorkflowAutoApproveAction implements WorkflowAction {
    private WorkflowActionService service;
    private ApprovalChainService approvalChainService;
    /** 审批操作 */
    private WorkflowApproval approval;

    /** 动作名称 */
    public static final String ACTION_NAME = "auto approve";

    /** 结果-任务审批 */
    public static final String RESULT_TASK_APPROVE = "task approve";
    /** 结果-任务无效 */
    public static final String RESULT_TASK_INVALID = "task invalid";

    public WorkflowAutoApproveAction(WorkflowActionService service, WorkflowApproval approval) {
        this.service = service;
        this.approvalChainService = service.getApprovalChainService();
        this.approval = approval;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        // 自动审批
        return autoApprove(this, approval);
    }


    /**
     * 自动审批（对任务执行审批操作）
     * @version 1.0
     * @author mh.z
     * @date 2019/04/26
     *
     * @param action 动作
     * @param approval 审批操作
     * @return 操作的结果
     */
    protected WorkflowResult autoApprove(WorkflowAutoApproveAction action, WorkflowApproval approval) {
        CheckUtil.notNull(action, "action null");
        WorkflowTask task = CheckUtil.notNull(approval.getTask(), "approval.task null");
        Integer operation = CheckUtil.notNull(approval.getOperation(), "approval.operation null");
        String remark = approval.getRemark();
        Long taskId = CheckUtil.notNull(task.getId(), "approval.task.id null");
        WorkflowInstance instance = CheckUtil.notNull(task.getInstance(), "approval.task.instance null");
        WorkflowUser user = CheckUtil.notNull(task.getUser(), "approval.task.user null");

        // 获取任务最新的审批状态
        ApprovalChain approvalChainPO = approvalChainService.selectById(taskId);
        task.setApprovalChain(approvalChainPO);
        Integer approvalStatus = task.getApprovalStatus();

        // 不是审批中的任务不需要执行审批操作（可能审批节点已经通过或驳回）
        if (!WorkflowTask.APPROVAL_STATUS_APPROVAL.equals(approvalStatus)) {
            WorkflowResult result = new WorkflowResult();
            result.setEntity(task);
            result.setStatus(WorkflowAutoApproveAction.RESULT_TASK_INVALID);
            // 没有下一个动作
            result.setNext(null);
            return result;
        }

        WorkflowAction nextAction = null;
        String returnStatus = null;

        if (WorkflowApproval.OPERATION_PASS.equals(operation)) {
            returnStatus = WorkflowAutoApproveAction.RESULT_TASK_APPROVE;

            // 下一个动作是通过任务
            WorkflowPassTaskAction workflowPassTaskAction = new WorkflowPassTaskAction(service, instance, user, remark);
            workflowPassTaskAction.setTask(task);
            nextAction = workflowPassTaskAction;
        } else if (WorkflowApproval.OPERATION_REJECT.equals(operation)) {
            returnStatus = WorkflowAutoApproveAction.RESULT_TASK_APPROVE;

            // 下一个动作是驳回任务
            WorkflowRejectTaskAction workflowRejectTaskAction = new WorkflowRejectTaskAction(service, instance, user, remark);
            workflowRejectTaskAction.setTask(task);
            nextAction = workflowRejectTaskAction;
        } else {
            String format = "approval.operation(%s) invalid";
            throw new IllegalArgumentException(String.format(format, operation));
        }

        WorkflowResult result = new WorkflowResult();
        result.setEntity(task);
        result.setStatus(returnStatus);
        // 设置下一动作
        result.setNext(nextAction);
        return result;
    }

    /**
     * 创建自动审批动作并返回
     * @verson 1.0
     * @author mh.z
     * @date 2019/04/27
     *
     * @param workflowActionService
     * @param approvalList 审批操作
     * @return 自动审批动作
     */
    public static List<WorkflowAutoApproveAction> createActions(WorkflowActionService workflowActionService, List<WorkflowApproval> approvalList) {
        CheckUtil.notNull(workflowActionService, "workflowActionService null");
        CheckUtil.notNull(approvalList, "approvalList null");

        List<WorkflowAutoApproveAction> actionList = new ArrayList<WorkflowAutoApproveAction>();
        WorkflowAutoApproveAction action = null;

        for (WorkflowApproval approval : approvalList) {
            action = new WorkflowAutoApproveAction(workflowActionService, approval);
            actionList.add(action);
        }

        return actionList;
    }

}
