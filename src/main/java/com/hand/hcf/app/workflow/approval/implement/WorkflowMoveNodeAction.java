package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.service.WorkflowInitNodeService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.util.CheckUtil;

/**
 * 移到指定节点动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowMoveNodeAction implements WorkflowAction {
    private WorkflowActionService service;
    private WorkflowBaseService workflowBaseService;
    private WorkflowInitNodeService workflowInitNodeService;
    /** 当前的实例 */
    private WorkflowInstance instance;
    /** 移到的节点 */
    private WorkflowNode node;

    /** 动作名称 */
    public static final String ACTION_NAME = "move node";

    public WorkflowMoveNodeAction(WorkflowActionService service, WorkflowInstance instance, WorkflowNode node) {
        this.service = service;
        this.workflowBaseService = service.getWorkflowBaseService();
        this.workflowInitNodeService = service.getWorkflowInitNodeService();
        this.instance = instance;
        this.node = node;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        // 移动节点
        return moveNode(this, instance, node);
    }


    /**
     * 移到指定节点
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param action 动作
     * @param instance 当前的实例
     * @param node 移到的节点
     * @return 操作结果
     */
    public WorkflowResult moveNode(WorkflowMoveNodeAction action, WorkflowInstance instance, WorkflowNode node) {
        CheckUtil.notNull(action, "action null");
        CheckUtil.notNull(instance, "instance null");
        CheckUtil.notNull(node, "node null");

        // 移到指定节点的逻辑：
        // 1.更新实例的当前节点
        // 2.初始节点（生成任务等）

        // 更新实例的当前节点
        instance.setLastNodeOid(node.getNodeOid());
        instance.setLastNodeName(node.getName());
        workflowBaseService.updateInstance(instance);

        // 初始节点
        WorkflowResult result = workflowInitNodeService.initNode(node);
        return result;
    }

}
