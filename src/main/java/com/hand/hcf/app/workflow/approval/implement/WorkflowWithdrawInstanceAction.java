package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowTask;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.service.WorkflowWithdrawService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import com.hand.hcf.app.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.service.ApprovalFormService;
import com.hand.hcf.app.workflow.util.CheckUtil;

import java.util.UUID;

/**
 * 撤回实例动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowWithdrawInstanceAction implements WorkflowAction {
    private WorkflowActionService service;
    private WorkflowBaseService workflowBaseService;
    private ApprovalFormService approvalFormService;
    /** 操作的实例 */
    private WorkflowInstance instance;
    /** 操作的用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "withdraw instance";
    /** 结果-撤回成功 */
    public static final String RESULT_WITHDRAW_SUCCESS = "withdraw success";

    public WorkflowWithdrawInstanceAction(WorkflowActionService service, WorkflowInstance instance, WorkflowUser user, String remark) {
        this.service = service;
        this.workflowBaseService = service.getWorkflowBaseService();
        this.approvalFormService = service.getApprovalFormService();
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
        // 撤回实例
        return withdrawInstance(this, instance, user, remark);
    }


    /**
     * 撤回实例
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param action 动作
     * @param instance 操作的实例
     * @param user 操作的用户
     * @param remark 备注（可选）
     * @return 操作的结果
     */
    public WorkflowResult withdrawInstance(WorkflowWithdrawInstanceAction action, WorkflowInstance instance, WorkflowUser user, String remark) {
        CheckUtil.notNull(action, "action null");
        CheckUtil.notNull(instance, "instance null");
        CheckUtil.notNull(user, "user null");

        // 撤回实例的逻辑：
        // 1.清除跟实例关联的所有任务
        // 2.更新实例的状态成撤回
        // 3.保存撤回操作的历史
        // 下一动作：null

        // 检查是否可以撤回
        checkWithdraw(instance);

        // 清除跟实例关联的所有任务
        workflowBaseService.clearAllTasks(instance);

        // 更新实例的状态成撤回
        instance.setApprovalStatus(WorkflowInstance.APPROVAL_STATUS_WITHDRAW);
        // 记录撤回的原因
        instance.setRejectReason(remark);
        workflowBaseService.updateInstance(instance);

        // 保存撤回操作的历史
        workflowBaseService.saveHistory(instance, user, WorkflowWithdrawInstanceAction.ACTION_NAME, remark);

        WorkflowResult result = new WorkflowResult();
        result.setEntity(instance);
        result.setStatus(WorkflowWithdrawInstanceAction.RESULT_WITHDRAW_SUCCESS);
        // 没有下一个动作
        result.setNext(null);
        return result;
    }

    /**
     * 检查是否可以撤回
     * @version 1.0
     * @author mh.z
     * @date 2019/04/27
     *
     * @param instance 撤回的实例
     */
    protected void checkWithdraw(WorkflowInstance instance) {
        CheckUtil.notNull(instance, "instance null");
        Integer approvalStatus = CheckUtil.notNull(instance.getApprovalStatus(), "instance.approvalStatus null");

        if (!WorkflowInstance.APPROVAL_STATUS_APPROVAL.equals(approvalStatus)) {
            // 只能撤回审批中的实例
            throw new BizException(MessageConstants.INSTANCE_STATUS_CANNOT_WITHDRAW);
        }

        WorkFlowDocumentRef workFlowDocumentRef = instance.getWorkFlowDocumentRef();
        UUID formOid = workFlowDocumentRef.getFormOid();
        ApprovalForm approvalForm = approvalFormService.getByOid(formOid);
        // 获取撤回规则
        Boolean withdrawFlag = approvalForm.getWithdrawFlag();
        Integer withdrawRule = approvalForm.getWithdrawRule();
        withdrawFlag = withdrawFlag == null ? true : withdrawFlag;
        withdrawRule = withdrawRule == null ? RuleConstants.RULE_WITHDRAW_NONE_APPROVAL_HISTORY : withdrawRule;

        if (!withdrawFlag) {
            // 审批流未启用撤回
            throw new BizException(MessageConstants.FORM_RULE_CANNOT_WITHDRAW);
        }

        if (!RuleConstants.RULE_WITHDRAW_BEFORE_APPROVAL_END.equals(withdrawRule)) {
            // 获取审批过的任务数
            int approvedTotal = workflowBaseService.countTasks(instance, WorkflowTask.APPROVAL_STATUS_APPROVED);
            if (approvedTotal > 0) {
                // 审批流设置了只能撤回无审批记录的审批流
                throw new BizException(MessageConstants.ONLY_WITHDRAW_NONE_APPROVAL_HISTORY);
            }
        }
    }

}
