package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowTask;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.service.WorkflowRejectService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.util.CheckUtil;

/**
 * 驳回任务动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowRejectTaskAction implements WorkflowAction {
    private WorkflowActionService service;
    private WorkflowBaseService workflowBaseService;
    /** 操作的实例 */
    private WorkflowInstance instance;
    /** 操作的用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;
    /** 操作的任务（如果该值是null，则驳回任务的逻辑里根据实例和用户获取任务） */
    private WorkflowTask task;

    /** 动作名称 */
    public static final String ACTION_NAME = "reject task";

    /** 结果-驳回成功 */
    public static final String RESULT_REJECT_SUCCESS = "reject success";

    public WorkflowRejectTaskAction(WorkflowActionService service, WorkflowInstance instance, WorkflowUser user, String remark) {
        this.service = service;
        this.workflowBaseService = service.getWorkflowBaseService();
        this.instance = instance;
        this.user = user;
        this.remark = remark;
    }

    public WorkflowTask getTask() {
        return task;
    }

    public void setTask(WorkflowTask task) {
        this.task = task;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        // 驳回任务
        return rejectTask(this, instance, user, remark);
    }


    /**
     * 驳回任务
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param action 动作
     * @param instance 操作的实例
     * @param user 操作的用户
     * @param remark 备注
     * @return 操作的结果
     */
    public WorkflowResult rejectTask(WorkflowRejectTaskAction action, WorkflowInstance instance, WorkflowUser user, String remark) {
        CheckUtil.notNull(action, "action null");
        CheckUtil.notNull(instance, "instance null");
        CheckUtil.notNull(user, "user null");

        // 驳回任务的逻辑：
        // 1.查找要驳回的任务
        // 2.更新任务的审批状态成已审批和状态成无效
        // 3.保存驳回操作的历史
        // 4.更新实例最后审批人
        // 下一个动作：驳回节点

        // 获取要驳回的任务（没有指定任务则通过实例和用户查找任务）
        WorkflowTask task = action.getTask();
        if (task == null) {
            task = workflowBaseService.findTask(instance, user);
        }

        if (task == null) {
            throw new BizException(MessageConstants.NOT_FIND_THE_TASK);
        }

        Integer approvalStatus = CheckUtil.notNull(task.getApprovalStatus(), "WorkflowTask.approvalStatus null");
        // 只能驳回审批中的任务
        if (!WorkflowTask.APPROVAL_STATUS_APPROVAL.equals(approvalStatus)) {
            throw new BizException(MessageConstants.TASK_STATUS_CANNOT_OPERATE);
        }

        // 更新任务的审批状态成已审批
        task.setApprovalStatus(WorkflowTask.APPROVAL_STATUS_APPROVED);
        // 更新任务的状态成无效
        task.setStatus(WorkflowTask.STATUS_INVALID);
        workflowBaseService.updateTask(task);

        // 保存驳回操作的历史
        workflowBaseService.saveHistory(task, WorkflowRejectTaskAction.ACTION_NAME, remark);

        // 更新实例最后审批人
        instance.setLastApproverOid(user.getUserOid());
        workflowBaseService.updateInstance(instance);

        WorkflowRejectNodeAction nextAction = new WorkflowRejectNodeAction(service, task.getNode(), user, remark);
        WorkflowResult result = new WorkflowResult();
        result.setEntity(task);
        result.setStatus(WorkflowRejectTaskAction.RESULT_REJECT_SUCCESS);
        // 下一个动作是驳回节点
        result.setNext(nextAction);
        return result;
    }

}
