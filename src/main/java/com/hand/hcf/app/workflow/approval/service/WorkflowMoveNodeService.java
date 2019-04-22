package com.hand.hcf.app.workflow.approval.service;

import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.workflow.approval.constant.ErrorConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowRule;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.implement.WorkflowAutoApproveAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowNextNodeAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowPassInstanceAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 工作流移动节点的逻辑
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowMoveNodeService {
    @Autowired
    private WorkflowBaseService workflowBaseService;

    @Autowired
    private WorkflowAutoTaskService workflowAutoTaskService;

    @Autowired
    private WorkflowPassService workflowPassService;

    /**
     * 移到下个节点
     *
     * @param instance
     * @param node
     * @return
     */
    public WorkflowResult nextNode(WorkflowInstance instance, WorkflowNode node) {
        Assert.notNull(instance, "instance null");

        WorkflowNode nextNode = workflowBaseService.findNext(instance, node);
        WorkflowResult result = initNode(nextNode);
        return result;
    }

    /**
     * 初始节点
     *
     * @param node
     * @return
     */
    public WorkflowResult initNode(WorkflowNode node) {
        Assert.notNull(node, "node null");
        Assert.notNull(node.getType(), "node.type null");
        Assert.notNull(node.getInstance(), "instance null");

        Integer nodeType = node.getType();
        WorkflowInstance instance = node.getInstance();
        WorkflowResult result = null;

        if (!WorkflowNode.TYPE_END.equals(nodeType)) {
            // 获取满足条件的审批人
            List<WorkflowUser> userList = workflowBaseService.findUsers(node);

            if (WorkflowNode.TYPE_USER.equals(nodeType)) {
                // 用户节点
                result = initUserNode(node, userList);
            } else if (WorkflowNode.TYPE_ROBOT.equals(nodeType)) {
                // 机器人节点
                result = initRobotNode(node, userList);
            } else if (WorkflowNode.TYPE_NOTICE.equals(nodeType)) {
                // 通知节点
                result = initNoticeNode(node, userList);
            } else {
                throw new IllegalArgumentException(String.format("node.type(%s) invalid", nodeType));
            }
        } else {
            // 结束节点
            WorkflowPassInstanceAction action = new WorkflowPassInstanceAction(workflowPassService, instance, null, null);
            result = new WorkflowResult();
            result.setStatus(WorkflowNextNodeAction.RESULT_END_NODE);
            result.setEntity(node);
            result.setNext(action);
        }

        return result;
    }

    /**
     * 初始审批节点
     *
     * @param node
     * @param userList
     * @return
     */
    protected WorkflowResult initUserNode(WorkflowNode node, List<WorkflowUser> userList) {
        Assert.notNull(node, "node null");
        Assert.notNull(userList, "userList null");
        Assert.notNull(node.getInstance(), "node.instance null");
        Assert.notNull(node.getRule(), "node.rule null");

        WorkflowInstance instance = node.getInstance();
        WorkflowRule rule = node.getRule();
        String status = null;
        WorkflowAction action = null;

        if (!userList.isEmpty()) {
            status = WorkflowNextNodeAction.RESULT_USER_NODE;

            // 保存审批任务
            for (WorkflowUser user : userList) {
                workflowBaseService.saveTask(node, user);
            }
        } else if (WorkflowRule.EMPTY_NODE_SKIP.equals(rule.getEmptyNodeRule())) {
            status = WorkflowNextNodeAction.RESULT_SKIP_NODE;
            action = new WorkflowNextNodeAction(this, instance, node);
        } else {
            throw new BizException(ErrorConstants.NODE_EMPTY_NOT_SKIP);
        }

        WorkflowResult result = new WorkflowResult();
        result.setStatus(status);
        result.setEntity(node);
        result.setNext(action);
        return result;
    }

    /**
     * 初始机器人节点
     *
     * @param node
     * @param userList
     * @return
     */
    protected WorkflowResult initRobotNode(WorkflowNode node, List<WorkflowUser> userList) {
        Assert.notNull(node, "node null");
        Assert.notNull(userList, "userList null");

        boolean skipNode = userList.isEmpty();
        WorkflowAutoApproveAction action = new WorkflowAutoApproveAction(workflowAutoTaskService, node, skipNode);

        WorkflowResult result = new WorkflowResult();
        result.setEntity(node);
        result.setStatus(WorkflowNextNodeAction.RESULT_ROBOT_NODE);
        result.setNext(action);
        return result;
    }

    /**
     * 初始通知节点
     *
     * @param node
     * @param userList
     * @return
     */
    protected WorkflowResult initNoticeNode(WorkflowNode node, List<WorkflowUser> userList) {
        Assert.notNull(node, "node null");
        Assert.notNull(userList, "userList null");

        // 保存通知任务
        for (WorkflowUser user : userList) {
            workflowBaseService.saveTask(node, user);
        }

        WorkflowNextNodeAction action = new WorkflowNextNodeAction(this, node.getInstance(), node);
        WorkflowResult result = new WorkflowResult();
        result.setStatus(WorkflowNextNodeAction.RESULT_NOTICE_NODE);
        result.setEntity(node);
        result.setNext(action);
        return result;
    }

}
