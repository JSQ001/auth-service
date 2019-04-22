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

    /** 用户审批节点 */
    public static final String RESULT_USER_NODE = "userNode";
    /** 机器人节点 */
    public static final String RESULT_ROBOT_NODE = "robotNode";
    /** 通知节点 */
    public static final String RESULT_NOTICE_NODE = "noticeNode";
    /** 结束节点 */
    public static final String RESULT_END_NODE = "endNode";
    /** 跳过节点 */
    public static final String RESULT_SKIP_NODE = "skipNode";

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
