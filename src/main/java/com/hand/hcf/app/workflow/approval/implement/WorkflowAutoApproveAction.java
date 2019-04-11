package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.service.WorkflowAutoTaskService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;

/**
 * 自动审批动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowAutoApproveAction implements WorkflowAction {
    private WorkflowAutoTaskService service;
    /** 节点 */
    private WorkflowNode node;
    /** 跳过节点标志 */
    private Boolean skip;

    /** 动作名称 */
    public static final String ACTION_NAME = "autoApprove";
    /** 跳过节点 */
    public static final String RESULT_SKIP_NODE = "skipNode";
    /** 通过节点 */
    public static final String RESULT_PASS_NODE = "passNode";
    /** 驳回节点 */
    public static final String RESULT_REJECT_NODE = "rejectNode";

    public WorkflowAutoApproveAction(WorkflowAutoTaskService service, WorkflowNode node, boolean skip) {
        this.service = service;
        this.node = node;
        this.skip = skip;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        // 自动审批
        return service.autoApprove(node, skip);
    }
}
