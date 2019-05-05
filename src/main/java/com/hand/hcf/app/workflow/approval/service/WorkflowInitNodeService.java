package com.hand.hcf.app.workflow.approval.service;

import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.*;
import com.hand.hcf.app.workflow.approval.implement.WorkflowAutoApproveAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowNextNodeAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowPassInstanceAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.util.CheckUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作流初始节点的逻辑
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowInitNodeService {
    @Autowired
    private WorkflowBaseService workflowBaseService;

    @Autowired
    private WorkflowRobotApproveService workflowRobotApproveService;

    @Autowired
    private WorkflowActionService workflowActionService;

    @Autowired
    private WorkflowRepeatApproveService workflowRepeatApproveService;

    @Autowired
    private WorkflowFindUserService workflowFindUserService;

    /** 结果-用户节点 */
    public static final String RESULT_USER_NODE = "user node";
    /** 结果-机器人节点 */
    public static final String RESULT_ROBOT_NODE = "robot node";
    /** 结果-通知节点 */
    public static final String RESULT_NOTICE_NODE = "notice node";
    /** 结果-结束节点 */
    public static final String RESULT_END_NODE = "end node";
    /** 结果-空节点 */
    public static final String RESULT_EMPTY_NODE = "empty node";

    /**
     * 初始节点
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 节点
     * @return 初始结果
     */
    public WorkflowResult initNode(WorkflowNode node) {
        CheckUtil.notNull(node, "node null");
        Integer nodeType = CheckUtil.notNull(node.getType(), "node.type null");
        WorkflowInstance instance = CheckUtil.notNull(node.getInstance(), "instance null");

        // 如果是结束节点则下一个动作是通过实例
        if (WorkflowNode.TYPE_END.equals(nodeType)) {
            WorkflowPassInstanceAction action = new WorkflowPassInstanceAction(workflowActionService, instance, null, null);
            WorkflowResult result = new WorkflowResult();
            result.setEntity(node);
            result.setStatus(RESULT_END_NODE);
            // 下一个动作是通过实例
            result.setNext(action);
            return result;
        }

        WorkflowResult result = null;
        // 获取满足条件的审批人
        List<WorkflowUser> userList = workflowFindUserService.findUsers(node);

        if (WorkflowNode.TYPE_USER.equals(nodeType)) {
            // 初始用户节点
            result = initUserNode(node, userList);
        } else if (WorkflowNode.TYPE_ROBOT.equals(nodeType)) {
            // 初始机器人节点
            result = initRobotNode(node, userList);
        } else if (WorkflowNode.TYPE_NOTICE.equals(nodeType)) {
            // 初始通知节点
            result = initNoticeNode(node, userList);
        } else {
            throw new IllegalArgumentException(String.format("node.type(%s) invalid", nodeType));
        }

        return result;
    }

    /**
     * 初始审批节点
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 节点
     * @param userList 满足审批条件的审批人
     * @return 初始结果
     */
    protected WorkflowResult initUserNode(WorkflowNode node, List<WorkflowUser> userList) {
        CheckUtil.notNull(node, "node null");
        CheckUtil.notNull(userList, "userList null");
        WorkflowInstance instance = CheckUtil.notNull(node.getInstance(), "node.instance null");
        WorkflowRule rule = CheckUtil.notNull(node.getRule(), "node.rule null");

        // 初始审批节点的逻辑：
        // 有满足条件的审批人
        //   1.创建并保存审批任务
        //   下一动作：节点设置无需重复审批且有重复的审批则下一个动作是自动审批，
        //              否则下一个动作是null
        // 没有满足条件的审批人并且设置了为空跳过
        //   下一动作：移到下一节点
        // 没有满足条件的审批人并且设置了为空不跳过
        //   抛出错误

        String returnStatus = null;
        Object nextAction = null;

        if (!userList.isEmpty()) {
            // 情况：有满足条件的审批人
            //
            returnStatus = RESULT_USER_NODE;

            List<WorkflowTask> newTaskList = new ArrayList<WorkflowTask>();
            // 获取下一个任务组编号
            int group = workflowBaseService.nextGroup(instance);
            // 创建并保存审批任务
            for (WorkflowUser user : userList) {
                WorkflowTask newTask = workflowBaseService.createTask(node, user, group);
                workflowBaseService.saveTask(newTask);
                newTaskList.add(newTask);
            }

            List<WorkflowApproval> approvalList = null;
            // 获取重复的审批
            if (WorkflowRule.REPEAT_SKIP.equals(rule.getRepeatRule())) {
                approvalList = workflowRepeatApproveService.getRepeatApprovals(newTaskList);
            }

            if (CollectionUtils.isNotEmpty(approvalList)) {
                // 下一个动作是自动审批
                nextAction = createWorkflowAutoApproveActions(workflowActionService, approvalList);
            } else {
                // 没有下一个动作
                nextAction = null;
            }
        } else if (WorkflowRule.EMPTY_NODE_SKIP.equals(rule.getEmptyNodeRule())) {
            // 情况：没有满足条件的审批人并且设置了为空跳过
            //
            returnStatus = RESULT_EMPTY_NODE;
            // 下一个动作是移到下一节点
            nextAction = new WorkflowNextNodeAction(workflowActionService, instance, node);
        } else {
            // 情况：没有满足条件的审批人并且设置了为空不跳过
            //
            throw new BizException(MessageConstants.NODE_EMPTY_NOT_SKIP);
        }

        WorkflowResult result = new WorkflowResult();
        result.setEntity(node);
        result.setStatus(returnStatus);
        // 设置下一个动作
        result.setNext(nextAction);
        return result;
    }

    /**
     * 初始机器人节点
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 节点
     * @param userList 满足审批条件的机器人
     * @return 初始结果
     */
    protected WorkflowResult initRobotNode(WorkflowNode node, List<WorkflowUser> userList) {
        CheckUtil.notNull(node, "node null");
        CheckUtil.notNull(userList, "userList null");

        // 初始机器人节点的逻辑：
        // 满足审批条件
        //   1.获取机器人审批操作
        //   下一动作：自动审批
        // 不满足审批条件
        //   下一动作：移到下一节点

        // 如果没有满足条件的机器人，则跳过该机器人节点（没有满足审批条件跳过）
        boolean skipNode = userList.isEmpty();
        if (skipNode) {
            WorkflowAction nextAction = new WorkflowNextNodeAction(workflowActionService, node.getInstance(), node);
            WorkflowResult result = new WorkflowResult();
            result.setEntity(node);
            result.setStatus(RESULT_ROBOT_NODE);
            // 下一个动作是移到下一节点
            result.setNext(nextAction);
            return result;
        }

        // 获取机器人审批操作
        WorkflowApproval approval = workflowRobotApproveService.getRobotApproval(node);
        // 下一个动作是自动审批
        WorkflowAction nextAction = new WorkflowAutoApproveAction(workflowActionService, approval);

        WorkflowResult result = new WorkflowResult();
        result.setEntity(node);
        result.setStatus(RESULT_ROBOT_NODE);
        result.setNext(nextAction);
        return result;
    }

    /**
     * 初始通知节点
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 节点
     * @param userList 满足条件的通知人
     * @return 初始结果
     */
    protected WorkflowResult initNoticeNode(WorkflowNode node, List<WorkflowUser> userList) {
        CheckUtil.notNull(node, "node null");
        CheckUtil.notNull(userList, "userList null");
        WorkflowInstance instance = CheckUtil.notNull(node.getInstance(), "instance null");

        // 初始通知节点的逻辑：
        // 1.创建并保存通知任务
        // 下一动作：移到下一节点

        // 获取下一个任务组编号
        int group = workflowBaseService.nextGroup(instance);
        // 创建并保存通知任务
        for (WorkflowUser user : userList) {
            workflowBaseService.saveTask(node, user, group);
        }

        WorkflowNextNodeAction nextAction = new WorkflowNextNodeAction(workflowActionService, node.getInstance(), node);
        WorkflowResult result = new WorkflowResult();
        result.setEntity(node);
        result.setStatus(RESULT_NOTICE_NODE);
        // 下一个动作是移到下一节点
        result.setNext(nextAction);
        return result;
    }

    /**
     * 创建自动审批动作并返回
     * @verson 1.0
     * @author mh.z
     * @date 2019/04/27
     *
     * @param workflowActionService
     * @param approvalList 审批操作
     * @return 自动审批动作
     */
    protected List<WorkflowAutoApproveAction> createWorkflowAutoApproveActions(WorkflowActionService workflowActionService, List<WorkflowApproval> approvalList) {
        CheckUtil.notNull(workflowActionService, "workflowActionService null");
        CheckUtil.notNull(approvalList, "approvalList null");

        List<WorkflowAutoApproveAction> actionList = new ArrayList<WorkflowAutoApproveAction>();
        WorkflowAutoApproveAction action = null;

        for (WorkflowApproval approval : approvalList) {
            action = new WorkflowAutoApproveAction(workflowActionService, approval);
            actionList.add(action);
        }

        return actionList;
    }

}
