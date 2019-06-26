package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowRule;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.util.CheckUtil;

/**
 * 驳回实例动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowRejectInstanceAction implements WorkflowAction {
    private WorkflowActionService service;
    private WorkflowBaseService workflowBaseService;
    /** 操作的实例 */
    private WorkflowInstance instance;
    /** 操作的用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;
    /** 驳回的节点 */
    private WorkflowNode rejectNode;

    /** 动作名称 */
    public static final String ACTION_NAME = "reject instance";

    /** 结果-驳回成功 */
    public static final String RESULT_REJECT_SUCCESS = "reject success";

    public WorkflowRejectInstanceAction(WorkflowActionService service, WorkflowInstance instance, WorkflowUser user, String remark) {
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

    public WorkflowNode getRejectNode() {
        return rejectNode;
    }

    public void setRejectNode(WorkflowNode rejectNode) {
        this.rejectNode = rejectNode;
    }

    @Override
    public WorkflowResult execute() {
        // 驳回实例
        return rejectInstance(this, instance, user, remark);
    }

    /**
     * 驳回实例
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
    protected WorkflowResult rejectInstance(WorkflowRejectInstanceAction action, WorkflowInstance instance, WorkflowUser user, String remark) {
        CheckUtil.notNull(action, "action null");
        CheckUtil.notNull(instance, "instance null");

        // 驳回实例的逻辑：
        // 1.清除跟实例关联的所有任务
        // 2.更新实例的状态成驳回
        // 3.记录实例的驳回原因
        // 4.如果驳回的规则是提交后跳回本节点则更改实例的jumpNodeId字段（值是驳回节点的id）
        // 下一动作：null

        // 清除跟该实例关联的所有任务
        workflowBaseService.clearAllTasks(instance);

        // 更新实例的状态成驳回
        instance.setApprovalStatus(WorkflowInstance.APPROVAL_STATUS_REJECT);
        // 记录实例的驳回原因
        instance.setRejectReason(remark);

        // 获取驳回的节点
        WorkflowNode rejectNode = action.getRejectNode();
        if (rejectNode != null) {
            // 获取驳回的规则
            WorkflowRule rule = rejectNode.getRule();
            Integer rejectRule = rule.getRejectRule();

            // 如果驳回规则是提交后跳回本节点则更改实例的jumpNodeId字段
            if (WorkflowRule.REJECT_SUBMIT_SKIP.equals(rejectRule)) {
                instance.getWorkFlowDocumentRef().setJumpNodeId(rejectNode.getId());
            }
        }

        // 更新实例
        workflowBaseService.updateInstance(instance);

        WorkflowResult result = new WorkflowResult();
        result.setEntity(instance);
        result.setStatus(WorkflowRejectInstanceAction.RESULT_REJECT_SUCCESS);
        // 没有下一个动作
        result.setNext(null);
        return result;
    }

}
