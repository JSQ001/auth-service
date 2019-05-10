package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.*;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.service.WorkflowRepeatApproveService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.util.CheckUtil;
import com.hand.hcf.app.core.exception.BizException;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 转交任务动作
 * @author ly
 * @date 2019/04/18
 */
public class WorkflowTransferTaskAction implements WorkflowAction {
    private WorkflowActionService service;
    private WorkflowBaseService workflowBaseService;
    private WorkflowRepeatApproveService workflowRepeatApproveService;
    /** 操作的实例 */
    private WorkflowInstance instance;
    /** 操作的用户 */
    private WorkflowUser user;
    /** 受理人（代理人） */
    private WorkflowUser assignee;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "transfer task";

    /** 结果-转交成功 */
    public static final String RESULT_TRANSFER_SUCCESS = "transfer success";

    public WorkflowTransferTaskAction(WorkflowActionService service, WorkflowInstance instance, WorkflowUser user,
                                      WorkflowUser assignee, String remark) {
        this.service = service;
        this.workflowBaseService = service.getWorkflowBaseService();
        this.workflowRepeatApproveService = service.getWorkflowRepeatApproveService();
        this.instance = instance;
        this.user = user;
        this.assignee = assignee;
        this.remark = remark;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        return transferTask(this, instance, user, assignee, remark);
    }

    /**
     * 转交任务
     * @versio 1.0
     * @author ly
     * @date 2019/04/17
     *
     * @param action 动作
     * @param instance 实例
     * @param user 用户
     * @param assignee 受理人（代理人)
     * @param remark 备注（可选）
     * @return 操作的结果
     */
    protected WorkflowResult transferTask(WorkflowTransferTaskAction action, WorkflowInstance instance,
                                          WorkflowUser user, WorkflowUser assignee, String remark) {
        CheckUtil.notNull(action, "action null");
        CheckUtil.notNull(instance, "instance null");
        CheckUtil.notNull(user, "user null");
        CheckUtil.notNull(assignee, "assignee null");

        // 转交任务的逻辑：
        // 1.查找要转交的任务
        // 2.更新当前任务的状态成无效
        // 3.创建新的任务
        // 4.保存转交操作的历史
        // 5.获取自动审批动作（根据是否无需重复审批规则）
        // 下一动作：
        //   a.如果没有重复的审批则下一个动作是null
        //   b.如果有重复的审批则下一个动作是自动审批

        // 根据实例和用户查找任务
        WorkflowTask task = workflowBaseService.findTask(instance, user);
        if (task == null) {
            throw new BizException(MessageConstants.NOT_FIND_THE_TASK);
        }

        WorkflowNode node = task.getNode();
        WorkflowRule rule = node.getRule();
        //判断节点是否可以转交
        if (!Boolean.TRUE.equals(rule.getTransferFlag())) {
            throw new BizException(MessageConstants.NODE_RULE_CANNOT_TRANSFER);
        }

        // 修改当前任务的状态成无效
        task.setStatus(WorkflowTask.STATUS_INVALID);
        workflowBaseService.updateTask(task);

        // 创建新的任务（跟当前任务同个任务组）
        WorkflowTask newTask = workflowBaseService.createTask(node, assignee, task.getGroup());
        workflowBaseService.saveTask(newTask);

        // 保存转交操作的历史
        String operation = ApprovalOperationEnum.APPROVAL_TRANSFER.getId().toString();
        workflowBaseService.saveHistory(task, operation, remark);

        // 获取自动审批动作（根据是否无需重复审批规则）
        List<WorkflowTask> taskList = new ArrayList<WorkflowTask>();
        taskList.add(newTask);
        List<WorkflowAutoApproveAction> actionList = workflowRepeatApproveService.getAutoActionsByRule(rule, taskList);

        Object nextAction = null;
        String returnStatus = null;

        if (CollectionUtils.isNotEmpty(actionList)) {
            returnStatus = RESULT_TRANSFER_SUCCESS;
            // 下一个动作是自动审批
            nextAction = actionList.get(0);
        } else {
            returnStatus = RESULT_TRANSFER_SUCCESS;
            // 没有下一个动作
            nextAction = null;
        }

        WorkflowResult result = new WorkflowResult();
        result.setEntity(task);
        result.setStatus(returnStatus);
        // 设置下一个动作
        result.setNext(nextAction);
        return result;
    }

}
