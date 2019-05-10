package com.hand.hcf.app.workflow.approval.implement;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.workflow.approval.dto.*;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.service.WorkflowPassService;
import com.hand.hcf.app.workflow.approval.service.WorkflowRepeatApproveService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.service.ApprovalChainService;
import com.hand.hcf.app.workflow.util.CheckUtil;
import com.hand.hcf.app.workflow.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 通过节点动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowPassNodeAction implements WorkflowAction {
    private WorkflowActionService service;
    private WorkflowBaseService workflowBaseService;
    private ApprovalChainService approvalChainService;
    private WorkflowRepeatApproveService workflowRepeatApproveService;
    /** 操作的节点 */
    private WorkflowNode node;
    /** 操作的用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;
    /** 最后操作的任务 */
    private WorkflowTask lastTask;

    /** 动作名称 */
    public static final String ACTION_NAME = "pass node";

    /** 结果-通过成功 */
    public static final String RESULT_PASS_SUCCESS = "pass success";
    /** 结果-通过待定（还需要其他人审批） */
    public static final String RESULT_PASS_PEND = "pass pend";

    public WorkflowPassNodeAction(WorkflowActionService service, WorkflowNode node, WorkflowUser user, String remark) {
        this.service = service;
        this.workflowBaseService = service.getWorkflowBaseService();
        this.approvalChainService = service.getApprovalChainService();
        this.workflowRepeatApproveService = service.getWorkflowRepeatApproveService();
        this.node = node;
        this.user = user;
        this.remark = remark;
    }

    public WorkflowTask getLastTask() {
        return lastTask;
    }

    public void setLastTask(WorkflowTask lastTask) {
        this.lastTask = lastTask;
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
        String returnMessage = null;

        // 通过节点的逻辑：
        // 根据会签规则可以通过节点
        //   1.清除跟节点关联的所有未完成任务
        //   下一动作：没有未完成的任务则下一个动作是下一个节点
        //             节点设置无需重复审批且有重复的审批则下一个动作是自动审批，否则下一个动作是null
        // 根据会签规则还需要其他人审批
        //   下一动作：null

        boolean canPassNode = true;
        // 这个会签规则要求同个节点上所有人都通过才能通过节点
        if (WorkflowRule.COUNTERSIGN_ALL_PASS_OR_ANY_REJECT.equals(countersignFlag)) {
            int unfinishedTotal = workflowBaseService.countTasks(node, WorkflowTask.APPROVAL_STATUS_APPROVAL);
            canPassNode = unfinishedTotal == 0;
        }

        Object nextAction = null;
        String returnStatus = null;

        if (canPassNode) {
            // 清除跟节点关联的所有当前任务
            workflowBaseService.clearCurrentTasks(node);

            List<WorkflowTask> unfinishedTaskList = null;
            // 获取最后操作的任务
            WorkflowTask lastTask = action.getLastTask();

            if (lastTask != null) {
                // 激活任务
                unfinishedTaskList = getUnfinishedTask(node);
            }

            if (CollectionUtils.isNotEmpty(unfinishedTaskList)) {
                returnStatus = WorkflowPassNodeAction.RESULT_PASS_PEND;
                nextAction = processUnfinishedTasks(node, unfinishedTaskList);
            } else {
                returnStatus = WorkflowPassNodeAction.RESULT_PASS_SUCCESS;
                // 下一个动作是移到下个节点
                nextAction = new WorkflowNextNodeAction(service, instance, node);
            }

            returnMessage = StringUtil.concat("unfinished task total is ", unfinishedTaskList.size());
        } else {
            returnStatus = WorkflowPassNodeAction.RESULT_PASS_PEND;
            // 没有下一个动作
            nextAction = null;
        }

        WorkflowResult result = new WorkflowResult();
        result.setEntity(node);
        result.setStatus(returnStatus);
        result.setNext(nextAction);
        result.setMessage(returnMessage);
        return result;
    }

    /**
     * 处理未完成的任务
     * @version 1.0
     * @author mh.z
     * @date 2019/05/04
     *
     * @param node 当前的节点
     * @param taskList 未完成的任务
     * @return 下一个动作
     */
    protected Object processUnfinishedTasks(WorkflowNode node, List<WorkflowTask> taskList) {
        CheckUtil.notNull(node, "node null");
        CheckUtil.notNull(taskList, "taskList null");
        WorkflowRule rule = CheckUtil.notNull(node.getRule(), "node.rule null");

        // 修改审批状态成审批中
        for (WorkflowTask task : taskList) {
            task.setApprovalStatus(WorkflowTask.APPROVAL_STATUS_APPROVAL);
            workflowBaseService.saveTask(task);
        }

        List<WorkflowApproval> approvalList = null;
        // 获取重复的审批
        if (WorkflowRule.REPEAT_SKIP.equals(rule.getRepeatRule())) {
            approvalList = workflowRepeatApproveService.getRepeatApprovals(taskList);
        }

        if (CollectionUtils.isEmpty(approvalList)) {
            return null;
        }

        List<WorkflowAutoApproveAction> actionList = WorkflowAutoApproveAction
                .createActions(service, approvalList);
        return actionList;
    }

    /**
     * 返回节点上未完成的任务
     * @author mh.z
     * @date 2019/04/29
     *
     * @param node 当前的节点
     * @return 未完成的任务
     */
    protected List<WorkflowTask> getUnfinishedTask(WorkflowNode node) {
        CheckUtil.notNull(node, "node null");
        WorkflowInstance instance = CheckUtil.notNull(node.getInstance(), "node.instance null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        List<ApprovalChain> approvalChainList = approvalChainService
                .listNextApprovalChain(entityType, entityOid);
        List<WorkflowTask> taskList = new ArrayList<WorkflowTask>();

        if (approvalChainList.isEmpty()) {
            return taskList;
        }

        for (ApprovalChain approvalChain : approvalChainList) {
            WorkflowUser user = new WorkflowUser(approvalChain.getApproverOid());
            WorkflowTask task = new WorkflowTask(approvalChain, instance, node, user);
            taskList.add(task);
        }

        return taskList;
    }

}
