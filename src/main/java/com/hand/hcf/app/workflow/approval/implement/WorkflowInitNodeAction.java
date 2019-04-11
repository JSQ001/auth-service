package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.service.WorkflowMoveNodeService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;

/**
 * 初始节点动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowInitNodeAction implements WorkflowAction {
    private WorkflowMoveNodeService service;
    /** 节点 */
    private WorkflowNode node;

    /** 动作名称 */
    public static final String ACTION_NAME = "initNode";
    /** 人工审批 */
    public static final String RESULT_MANUAL_APPROVE = "manualApprove";
    /** 自动审批 */
    public static final String RESULT_AUTO_APPROVE = "autoApprove";
    /** 空白节点 */
    public static final String RESULT_EMPTY_NODE = "emptyNode";
    /** 结束节点 */
    public static final String RESULT_END_NODE = "endNode";
    /** 通知节点 */
    public static final String RESULT_NOTICE_NODE = "noticeNode";

    public WorkflowInitNodeAction(WorkflowMoveNodeService service, WorkflowNode node) {
        this.service = service;
        this.node = node;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        // 初始节点
        return service.initNode(node);
    }

}
