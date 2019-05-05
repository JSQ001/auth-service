package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.*;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.service.WorkflowRepeatApproveService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.enums.*;
import com.hand.hcf.app.workflow.service.ApprovalChainService;
import com.hand.hcf.app.workflow.util.CheckUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 工作流加签动作
 * @author mh.z
 * @date 2019/04/28
 */
public class WorkflowAddSignAction implements WorkflowAction {
    private WorkflowActionService service;
    private WorkflowBaseService workflowBaseService;
    private ApprovalChainService approvalChainService;
    private WorkflowRepeatApproveService workflowRepeatApproveService;
    /** 操作的实例 */
    private WorkflowInstance instance;
    /** 操作的用户 */
    private WorkflowUser user;
    /** 加签人 */
    private Collection<WorkflowUser> signers;
    /** 加签顺序 */
    private Integer countersignOrder;
    /** 审批顺序 */
    private Integer approvalOrder;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "add sign";

    /** 结果-加签成功 */
    public static final String RESULT_ADD_SUCCESS = "add success";

    public WorkflowAddSignAction(WorkflowActionService service, WorkflowInstance instance, WorkflowUser user,
                                 Collection<WorkflowUser> signers, Integer countersignOrder, Integer approvalOrder, String remark) {
        this.service = service;
        this.workflowBaseService = service.getWorkflowBaseService();
        this.approvalChainService = service.getApprovalChainService();
        this.workflowRepeatApproveService = service.getWorkflowRepeatApproveService();
        this.instance = instance;
        this.user = user;
        this.signers = signers;
        this.countersignOrder = countersignOrder;
        this.approvalOrder = approvalOrder;
        this.remark = remark;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        return addTask(this, instance, user, signers, countersignOrder, approvalOrder, remark);
    }

    /**
     * 加签
     * @version 1.0
     * @author mh.z
     * @date 2019/04/28
     *
     * @param action 动作
     * @param instance 操作的实例
     * @param user 操作的用户
     * @param signers 加签人
     * @param countersignOrder 加签顺序
     * @param approvalOrder 审批顺序
     * @param remark 备注
     * @return 操作的结果
     */
    protected WorkflowResult addTask(WorkflowAddSignAction action, WorkflowInstance instance, WorkflowUser user,
                                     Collection<WorkflowUser> signers, Integer countersignOrder, Integer approvalOrder, String remark) {
        CheckUtil.notNull(action, "action null");
        CheckUtil.notNull(instance, "instance null");
        CheckUtil.notNull(user, "user null");
        CheckUtil.notNull(signers, "signers null");
        CheckUtil.notNull(countersignOrder, "countersignOrder null");
        CheckUtil.notNull(approvalOrder, "approvalOrder null");

        // 节点前加签：
        // 获取跟当前任务同任务组同审批顺序的任务
        // 克隆审批中和已审批过的任务（新的任务组编号，审批状态是待激活）
        // 把原来任务（未完成的任务）的状态修改成无效
        // 创建加签的任务（新的任务组）
        //
        // 平行节点加签：
        // 创建加签的任务（跟当前任务同任务组）
        //
        // 节点后加签：
        // 创建加签的任务（新的任务组）

        // 根据实例和用户获取任务
        WorkflowTask task = workflowBaseService.findTask(instance, user);
        if (task == null) {
            throw new BizException(MessageConstants.NOT_FIND_THE_TASK);
        }

        // 检查加签
        checkSign(task, signers, countersignOrder, approvalOrder);

        // 更改当前的任务
        updateCurrentTasks(task, countersignOrder, approvalOrder);

        // 创建加签任务
        List<WorkflowTask> newTaskList = createNewTasks(task, signers, countersignOrder, approvalOrder);
        for (WorkflowTask newTask : newTaskList) {
            workflowBaseService.saveTask(newTask);
        }

        // 保存加签操作的历史
        String operation = ApprovalOperationEnum.ADD_COUNTERSIGN.getId().toString();
        workflowBaseService.saveHistory(task, operation, remark);

        WorkflowNode node = task.getNode();
        WorkflowRule rule = node.getRule();
        List<WorkflowApproval> approvalList = null;
        // 获取要重复审批的操作
        if (WorkflowRule.REPEAT_SKIP.equals(rule.getRepeatRule())) {
            approvalList = getRepeatApprovals(newTaskList);
        }

        Object nextAction = null;
        String returnStatus = null;

        if (CollectionUtils.isNotEmpty(approvalList)) {
            List<WorkflowAutoApproveAction> actionList = new ArrayList<WorkflowAutoApproveAction>();
            WorkflowAutoApproveAction newAction = null;

            for (WorkflowApproval approval : approvalList) {
                newAction = new WorkflowAutoApproveAction(service, approval);
                actionList.add(newAction);
            }

            returnStatus = RESULT_ADD_SUCCESS;
            // 下一个动作是自动审批
            nextAction = actionList;
        } else {
            returnStatus = RESULT_ADD_SUCCESS;
            // 没有下一个动作
            nextAction = null;
        }

        WorkflowResult result = new WorkflowResult();
        result.setEntity(task);
        result.setStatus(returnStatus);
        result.setNext(nextAction);
        return result;
    }
    
    /**
     * 检查加签
     * @version 1.0
     * @author mh.z
     * @date 2019/04/29
     *
     * @param task
     * @param signers
     * @param countersignOrder
     * @param approvalOrder
     */
    protected void checkSign(WorkflowTask task, Collection<WorkflowUser> signers,
                             Integer countersignOrder, Integer approvalOrder) {
        CheckUtil.notNull(task, "task null");
        CheckUtil.notNull(signers, "signers null");
        CheckUtil.notNull(countersignOrder, "countersignOrder null");
        CheckUtil.notNull(approvalOrder, "approvalOrder null");
        WorkflowInstance instance = CheckUtil.notNull(task.getInstance(), "task.instance null");
        WorkflowUser user = CheckUtil.notNull(task.getUser(), "task.user null");
        UUID userOid = user.getUserOid();
        Integer approvalStatus = CheckUtil.notNull(
                instance.getApprovalStatus(), "task.instance.approvalStatus null");

        // 只能对审批中的单据操作
        if (!WorkflowInstance.APPROVAL_STATUS_APPROVAL.equals(approvalStatus)) {
            throw new BizException(MessageConstants.INSTANCE_STATUS_CANNOT_ADDSIGN);
        }

        // 加签人不能为空
        if (signers.isEmpty()) {
            throw new BizException(MessageConstants.ADDSIGN_USER_IS_EMPTY);
        }

        WorkflowNode node = task.getNode();
        WorkflowRule rule = node.getRule();
        // 只有节点设置允许加签才能加签
        if (!Boolean.TRUE.equals(rule.getAddSignFlag())) {
            throw new BizException(MessageConstants.NODE_RULE_CANNOT_ADDSIGN);
        }

        // 不能加签给自己
        for (WorkflowUser signer : signers) {
            if (userOid.equals(signer.getUserOid())) {
                throw new BizException(MessageConstants.CANNOT_ADDSIGN_TO_ME);
            }
        }
    }

    /**
     * 更改当前的任务
     * @version 1.0
     * @author mh.z
     * @date 2019/05/03
     *
     * @param task 任务
     * @param countersignOrder 加签顺序
     * @param approvalOrder 审批顺序
     */
    protected void updateCurrentTasks(WorkflowTask task, Integer countersignOrder, Integer approvalOrder) {
        CheckUtil.notNull(task, "task null");
        CheckUtil.notNull(countersignOrder, "countersignOrder null");
        CheckUtil.notNull(approvalOrder, "approvalOrder null");
        WorkflowInstance instance = CheckUtil.notNull(task.getInstance(), "task.instance null");
        WorkflowNode node = CheckUtil.notNull(task.getNode(), "node.node null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        // 只有节点前加签才需要更改当前的任务
        if (!CounterSignOrderEnum.BEFORE.getValue().equals(countersignOrder)) {
            return;
        }

        ApprovalChain approvalChain = task.getApprovalChain();
        Integer groupNumber = approvalChain.getGroupNumber();
        Integer approvalNumber = approvalChain.getApprovalOrder();
        // 获取跟当前任务同任务组同审批顺序的任务
        List<ApprovalChain> approvalChainList = approvalChainService.listByGroupNumberAndApprovalOrder(
                entityType, entityOid, groupNumber, approvalNumber);
        // 获取下一个任务组编号
        Integer newGroupNumber = workflowBaseService.nextGroup(instance);

        for (ApprovalChain sourceApprovalChain : approvalChainList) {
            // 克隆审批中和已审批过的任务（新的任务组编号，审批状态是待激活）
            if (ApprovalChainStatusEnum.NORMAL.getId().equals(sourceApprovalChain.getStatus())
                    || Boolean.TRUE.equals(sourceApprovalChain.getFinishFlag())) {
                WorkflowUser user = new WorkflowUser(sourceApprovalChain.getApproverOid());
                WorkflowTask newTask = workflowBaseService.createTask(node, user, newGroupNumber);
                ApprovalChain newApprovalChain = newTask.getApprovalChain();
                newApprovalChain.setAddSign(sourceApprovalChain.getAddSign());
                newApprovalChain.setSourceApprovalChainId(sourceApprovalChain.getSourceApprovalChainId());
                newApprovalChain.setCurrentFlag(false);
                workflowBaseService.saveTask(newTask);
            }

            // 把原来任务（未完成的任务）的状态修改成无效
            if (ApprovalChainStatusEnum.NORMAL.getId().equals(sourceApprovalChain.getStatus())
                    && Boolean.FALSE.equals(sourceApprovalChain.getFinishFlag())) {
                sourceApprovalChain.setStatus(ApprovalChainStatusEnum.INVALID.getId());
                approvalChainService.updateById(sourceApprovalChain);
            }
        }
    }

    /**
     * 创建加签任务
     * @version 1.0
     * @author mh.z
     * @date 2019/05/02
     *
     * @param task 源任务
     * @param signers 加签人
     * @param countersignOrder 加签顺序
     * @param approvalOrder 审批顺序
     * @return 创建的加签任务
     */
    protected List<WorkflowTask> createNewTasks(WorkflowTask task, Collection<WorkflowUser> signers,
                                                Integer countersignOrder, Integer approvalOrder) {
        CheckUtil.notNull(task, "task null");
        CheckUtil.notNull(signers, "signers null");
        CheckUtil.notNull(countersignOrder, "countersignOrder null");
        CheckUtil.notNull(approvalOrder, "approvalOrder null");
        WorkflowInstance instance = CheckUtil.notNull(task.getInstance(), "task.instance null");
        WorkflowNode node = CheckUtil.notNull(task.getNode(), "task.node null");
        ApprovalChain sourceApprovalChain = task.getApprovalChain();
        Long sourceApprovalChainId = sourceApprovalChain.getId();

        Integer group = null;
        if (CounterSignOrderEnum.PARALLEL.getValue().equals(countersignOrder)) {
            // 平行加签的新任务跟源任务同个任务组
            group = task.getGroup();
        } else {
            group = workflowBaseService.nextGroup(instance);
        }

        List<WorkflowTask> newTaskList = new ArrayList<WorkflowTask>();
        for (WorkflowUser signer : signers) {
            WorkflowTask newTask = workflowBaseService.createTask(node, signer, group);
            ApprovalChain newApprovalChain = newTask.getApprovalChain();
            newApprovalChain.setSourceApprovalChainId(sourceApprovalChainId);
            newApprovalChain.setAddSign(IsAddSignEnum.SIGN_YES.getId());

            boolean currentFlag = true;
            if (CounterSignOrderEnum.BEFORE.getValue().equals(countersignOrder)) {
                // 节点前加签
                if (ApprovalOrderEnum.ORDER.getValue().equals(approvalOrder)
                        && newTaskList.size() > 0) {
                    // 顺序审批
                    currentFlag = false;
                }
            } else if (CounterSignOrderEnum.AFTER.getValue().equals(countersignOrder)) {
                // 节点后加签
                currentFlag = false;
            }
            newApprovalChain.setCurrentFlag(currentFlag);

            if (ApprovalOrderEnum.ORDER.getValue().equals(approvalOrder)) {
                // 顺序审批
                newApprovalChain.setApprovalOrder(newTaskList.size());
            }

            newTaskList.add(newTask);
        }

        return newTaskList;
    }

    /**
     * 返回要重复审批的操作
     * @version 1.0
     * @author mh.z
     * @date 2019/05/04
     *
     * @param taskList 任务列表
     * @return 要重复审批的操作
     */
    protected List<WorkflowApproval> getRepeatApprovals(List<WorkflowTask> taskList) {
        CheckUtil.notNull(taskList, "taskList null");

        List<WorkflowTask> currentTaskList = new ArrayList<WorkflowTask>();
        for (WorkflowTask task : taskList) {
            if (WorkflowTask.APPROVAL_STATUS_APPROVAL.equals(task.getApprovalStatus())) {
                currentTaskList.add(task);
            }
        }

        // 只需要获取当前任务要重复审批的操作
        List<WorkflowApproval> approvalList = workflowRepeatApproveService.getRepeatApprovals(currentTaskList);
        return approvalList;
    }

}
