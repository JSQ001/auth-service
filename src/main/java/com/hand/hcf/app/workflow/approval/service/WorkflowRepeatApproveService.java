package com.hand.hcf.app.workflow.approval.service;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.*;
import com.hand.hcf.app.workflow.approval.implement.WorkflowAutoApproveAction;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.service.RuleApprovalNodeService;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.domain.ApprovalHistory;
import com.hand.hcf.app.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.service.ApprovalChainService;
import com.hand.hcf.app.workflow.service.ApprovalHistoryService;
import com.hand.hcf.app.workflow.util.CheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 工作流重复审批逻辑
 * @author mh.z
 * @date 2019/04/25
 */
@Service
public class WorkflowRepeatApproveService {
    @Autowired
    private ApprovalChainService approvalChainService;

    @Autowired
    private ApprovalHistoryService approvalHistoryService;

    @Autowired
    private RuleApprovalNodeService ruleApprovalNodeService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private WorkflowActionService workflowActionService;

    /**
     * 根据规则获取自动审批动作并返回
     * @version 1.0
     * @author mh.z
     * @date 2019/05/05
     *
     * @param rule 规则
     * @param taskList 任务列表
     * @return 审批动作列表
     */
    public List<WorkflowAutoApproveAction> getAutoActionsByRule(WorkflowRule rule, List<WorkflowTask> taskList) {
        CheckUtil.notNull(rule, "rule null");
        CheckUtil.notNull(taskList, "taskList null");

        // 获取当前审批中的任务
        List<WorkflowTask> currentTaskList = new ArrayList<WorkflowTask>();
        for (WorkflowTask task : taskList) {
            if (WorkflowTask.APPROVAL_STATUS_APPROVAL.equals(task.getApprovalStatus())) {
                currentTaskList.add(task);
            }
        }

        List<WorkflowApproval> approvalList = null;
        // 获取重复上次操作的审批
        if (WorkflowRule.REPEAT_SKIP.equals(rule.getRepeatRule())) {
            approvalList = getRepeatApprovals(currentTaskList);
        }

        if (CollectionUtils.isEmpty(approvalList)) {
            return new ArrayList<WorkflowAutoApproveAction>();
        }

        // 创建自动审批动作
        List<WorkflowAutoApproveAction> actionList = WorkflowAutoApproveAction
                .createActions(workflowActionService, approvalList);

        return actionList;
    }

    /**
     * 返回要重复审批的操作
     * @author mh.z
     * @date 2019/04/25
     *
     * @param taskList 任务
     * @return 要重复审批的操作
     */
    public List<WorkflowApproval> getRepeatApprovals(List<WorkflowTask> taskList) {
        CheckUtil.notNull(taskList, "taskList null");

        List<WorkflowApproval> approvalList = new ArrayList<WorkflowApproval>();
        String approvalRemark = null;
        Long approvalSequence = null;
        WorkflowApproval newApproval = null;
        WorkflowApproval lastApproval = null;
        Integer lastOperation = null;

        for (WorkflowTask task : taskList) {
            // 查找最近一次的审批
            lastApproval = getLastApproval(task);

            if (lastApproval != null) {
                lastOperation = lastApproval.getOperation();
                approvalSequence = lastApproval.getApprovalSequence();

                if (approvalRemark == null) {
                    approvalRemark = getApprovalRemark();
                }

                newApproval = new WorkflowApproval(task, lastOperation, approvalRemark);
                newApproval.setApprovalSequence(approvalSequence);
                approvalList.add(newApproval);
            }
        }

        // 按照最近审批顺序升序
        Stream<WorkflowApproval> stream = approvalList.stream();
        stream = stream.sorted(Comparator.comparing(WorkflowApproval::getApprovalSequence));
        approvalList = stream.collect(Collectors.toList());
        return approvalList;
    }

    /**
     * 查找最近一次的审批
     * @author mh.z
     * @date 2019/04/25
     *
     * @param task 任务
     * @return 没有最近一次的审批返回null
     */
    protected WorkflowApproval getLastApproval(WorkflowTask task) {
        CheckUtil.notNull(task, "task null");

        WorkflowInstance instance = task.getInstance();
        WorkflowUser user = task.getUser();
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();
        ZonedDateTime submitDate = instance.getSubmitDate();
        UUID userOid = user.getUserOid();
        CheckUtil.notNull(entityType, "task.instance.entityType null");
        CheckUtil.notNull(entityOid, "task.instance.entityOid null");
        CheckUtil.notNull(submitDate, "task.instance.submitDate null");
        CheckUtil.notNull(userOid, "task.user.userOid null");

        // 查找最近一次审批过的任务
        ApprovalChain lastApprovalChain = approvalChainService.getLastApprovalChain(entityType, entityOid, userOid, submitDate);
        if (lastApprovalChain == null) {
            return null;
        }

        Long lastApprovalChainId = lastApprovalChain.getId();
        // 获取任务对应的审批历史
        ApprovalHistory approvalHistory = approvalHistoryService.getByApprovalChainId(lastApprovalChainId);
        Integer approvalOperation = approvalHistory.getOperation();
        String approvalRemark = approvalHistory.getOperationDetail();
        ZonedDateTime approvalTime = approvalHistory.getCreatedDate();
        // 获取任务对应的审批节点
        UUID ruleApprovalNodeOid = lastApprovalChain.getRuleApprovalNodeOid();
        RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(ruleApprovalNodeOid);

        WorkflowNode lastNode = new WorkflowNode(ruleApprovalNode, instance);
        WorkflowTask lastTask = new WorkflowTask(lastApprovalChain, instance, lastNode, user);
        Integer lastOperation = null;

        if (ApprovalOperationEnum.APPROVAL_PASS.getId().equals(approvalOperation)) {
            // 通过任务的动作
            lastOperation = WorkflowApproval.OPERATION_PASS;
        } else if (ApprovalOperationEnum.APPROVAL_REJECT.getId().equals(approvalOperation)) {
            // 驳回任务的动作
            lastOperation = WorkflowApproval.OPERATION_REJECT;
        } else {
            throw new IllegalArgumentException(String.format("ApprovalHistory.operation(%s) invalid", approvalOperation));
        }


        WorkflowApproval lastApproval = new WorkflowApproval(lastTask, lastOperation, approvalRemark);
        Long approvalSequence = approvalTime.toEpochSecond();
        lastApproval.setApprovalSequence(approvalSequence);
        return lastApproval;
    }

    /**
     * 返回审批备注
     * @author mh.z
     * @date 2019/04/25
     *
     * @return 审批意见
     */
    protected String getApprovalRemark() {
        String approvalRemark = baseClient.getMessageDetailByCode(
                MessageConstants.NO_REPEAT_APPROVE_REMARK, true);
        return approvalRemark;
    }

}
