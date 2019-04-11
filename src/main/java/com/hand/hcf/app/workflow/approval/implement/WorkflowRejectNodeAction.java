package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.service.WorkflowRejectService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;

/**
 * 驳回节点动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowRejectNodeAction implements WorkflowAction {
    private WorkflowRejectService service;
    /** 节点 */
    private WorkflowNode node;
    /** 用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "rejectNode";
    /** 驳回成功 */
    public static final String RESULT_REJECT_SUCCESS = "rejectSuccess";
    /** 驳回待定 */
    public static final String RESULT_REJECT_PEND = "rejectPend";

    public WorkflowRejectNodeAction(WorkflowRejectService service, WorkflowNode node, WorkflowUser user, String remark) {
        this.service = service;
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
        return service.rejectNode(node, user, remark);
    }

}
