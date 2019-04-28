package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowTask;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.service.WorkflowPassService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.util.CheckUtil;

/**
 * 通过实例动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowPassInstanceAction implements WorkflowAction {
    private WorkflowActionService service;
    private WorkflowBaseService workflowBaseService;
    /** 操作的实例 */
    private WorkflowInstance instance;
    /** 操作的用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "pass instance";

    /** 结果-通过成功 */
    public static final String RESULT_PASS_SUCCESS = "pass success";

    public WorkflowPassInstanceAction(WorkflowActionService service, WorkflowInstance instance, WorkflowUser user, String remark) {
        this.service = service;
        this.workflowBaseService = service.getWorkflowBaseService();
        this.instance = instance;
        this.user = user;
        this.remark = remark;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        // 通过实例
        return passInstance(this, instance, user, remark);
    }

    /**
     * 通过实例
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param action 动作
     * @param instance 操作的实例
     * @param user 操作的用户（可选）
     * @param remark 备注（可选）
     * @return 操作的结果
     */
    protected WorkflowResult passInstance(WorkflowPassInstanceAction action, WorkflowInstance instance, WorkflowUser user, String remark) {
        CheckUtil.notNull(action, "action null");
        CheckUtil.notNull(instance, "instance null");

        // 通过实例的逻辑：
        // 有节点审批通过
        //   1.清除跟实例关联的所有未完成任务
        //   2.更新实例的状态成通过
        //   下一动作：null
        // 没有节点审批通过
        //   1.抛出异常

        // 若有节点审批通过则单据通过（当前判断有完成的任务则单据通过）
        int approvedTotal = workflowBaseService.countTasks(instance, WorkflowTask.APPROVAL_STATUS_APPROVED);
        if (approvedTotal == 0) {
            throw new BizException(MessageConstants.CHAIN_NOT_EXISTS_TASK);
        }

        // 清除跟该实例关联的所有未完成任务
        workflowBaseService.clearUnfinishedTasks(instance);

        // 更新实例的状态成通过
        instance.setApprovalStatus(WorkflowInstance.APPROVAL_STATUS_PASS);
        workflowBaseService.updateInstance(instance);

        WorkflowResult result = new WorkflowResult();
        result.setEntity(instance);
        result.setStatus(WorkflowPassInstanceAction.RESULT_PASS_SUCCESS);
        // 没有下一个动作
        result.setNext(null);
        return result;
    }

}
