package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.service.WorkflowMoveNodeService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;

/**
 * 下一节点动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowNextNodeAction implements WorkflowAction {
    private WorkflowMoveNodeService service;
    /** 实例 */
    private WorkflowInstance instance;
    /** 节点 */
    private WorkflowNode node;

    /** 动作名称 */
    public static final String ACTION_NAME = "nextNode";
    /** 移到下一节点 */
    public static final String RESULT_MOVE_NEXT = "moveNext";
    /** 移到结束节点 */
    public static final String RESULT_MOVE_END = "moveEnd";

    public WorkflowNextNodeAction(WorkflowMoveNodeService service, WorkflowInstance instance, WorkflowNode node) {
        this.service = service;
        this.instance = instance;
        this.node = node;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        // 下一节点
        return service.nextNode(instance, node);
    }

}
