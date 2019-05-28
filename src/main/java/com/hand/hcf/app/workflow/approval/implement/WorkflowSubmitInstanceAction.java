package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.service.RuleApprovalNodeService;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.util.CheckUtil;

/**
 * 提交实例动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowSubmitInstanceAction implements WorkflowAction {
    private WorkflowActionService service;
    private WorkflowBaseService workflowBaseService;
    private RuleApprovalNodeService ruleApprovalNodeService;
    /** 操作的实例 */
    private WorkflowInstance instance;
    /** 操作的用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "submit instance";

    /** 结果-提交成功 */
    public static final String RESULT_SUBMIT_SUCCESS = "submit success";

    public WorkflowSubmitInstanceAction(WorkflowActionService service, WorkflowInstance instance, WorkflowUser user, String remark) {
        this.service = service;
        this.workflowBaseService = service.getWorkflowBaseService();
        this.ruleApprovalNodeService = service.getRuleApprovalNodeService();
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
        // 提交实例
        return submitInstance(this, instance, user, remark);
    }

    /**
     * 提交实例
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
    protected WorkflowResult submitInstance(WorkflowSubmitInstanceAction action, WorkflowInstance instance, WorkflowUser user, String remark) {
        CheckUtil.notNull(instance, "instance null");
        Integer approvalStatus = CheckUtil.notNull(instance.getApprovalStatus(), "instance.approvalStatus null");

        // 提交实例的逻辑：
        // 1.更新实例的状态成审批中
        // 2.保存提交操作的历史
        // 下一个动作：
        //   a.驳回后再次提交处理的规则是跳回本节点，则下一个动作是移到指定节点
        //   b.驳回后再次提交处理的规则是重新审批，则下一个动作是移到下一节点（第一个节点）

        // 不能提交审批中和已经通过的实例
        if (WorkflowInstance.APPROVAL_STATUS_APPROVAL.equals(approvalStatus)
                || WorkflowInstance.APPROVAL_STATUS_PASS.equals(approvalStatus)) {
            throw new BizException(MessageConstants.INSTANCE_STATUS_CANNOT_SUBMIT);
        }

        // 更新实例的状态成审批中
        instance.setApprovalStatus(WorkflowInstance.APPROVAL_STATUS_APPROVAL);
        workflowBaseService.updateInstance(instance);

        // 保存提交操作的历史
        workflowBaseService.saveHistory(instance, user, WorkflowSubmitInstanceAction.ACTION_NAME, remark);

        // 获取提交后要跳到的节点
        WorkFlowDocumentRef workFlowDocumentRef = instance.getWorkFlowDocumentRef();
        Long jumpNodeId = workFlowDocumentRef.getJumpNodeId();
        WorkflowAction nextAction = null;

        // 如果驳回后再次提交处理的规则是直接跳回本节点则jumpNodeId记录的是要跳回的节点id，
        // 所以这里判断jumpNodeId有值则要跳到指定的节点
        if (jumpNodeId != null) {
            // 这里必须更新实例的jumpNodeId字段成null，防止下次提交的时候跳到错误的节点
            workFlowDocumentRef = instance.getWorkFlowDocumentRef();
            workFlowDocumentRef.setJumpNodeId(null);
            workflowBaseService.updateInstance(instance);

            RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.selectById(jumpNodeId);
            WorkflowNode targetNode = new WorkflowNode(ruleApprovalNode, instance);
            // 下一个动作是移到指定节点
            nextAction = new WorkflowMoveNodeAction(service, instance, targetNode);
        } else {
            // 下一个动作是移到下一个节点（第一个节点）
            nextAction = new WorkflowNextNodeAction(service, instance, null);
        }

        WorkflowResult result = new WorkflowResult();
        result.setEntity(instance);
        result.setStatus(WorkflowSubmitInstanceAction.RESULT_SUBMIT_SUCCESS);
        // 设置下一个动作
        result.setNext(nextAction);
        return result;
    }

}
