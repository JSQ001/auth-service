package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.*;
import com.hand.hcf.app.workflow.approval.enums.ApprovalOrderEnum;
import com.hand.hcf.app.workflow.approval.enums.CounterSignOrderEnum;
import com.hand.hcf.app.workflow.approval.enums.IsAddSignEnum;
import com.hand.hcf.app.workflow.approval.service.WorkflowActionService;
import com.hand.hcf.app.workflow.approval.service.WorkflowBaseService;
import com.hand.hcf.app.workflow.approval.service.WorkflowRepeatApproveService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.enums.ApprovalChainStatusEnum;
import com.hand.hcf.app.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.service.ApprovalChainService;
import com.hand.hcf.app.workflow.util.CheckUtil;
import com.hand.hcf.app.workflow.util.StringUtil;
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
        String returnMessage = null;

        // 节点前加签：
        // 1.获取跟当前任务同任务组同审批顺序的任务
        // 2.克隆步骤1中审批状态是审批中和已审批过的任务（新的任务组编号，审批状态是待激活）
        // 3.把步骤1中状态是未完成的任务修改成无效
        // 4.创建加签的任务（新的任务组）
        //
        // 平行节点加签：
        // 1.创建加签的任务（跟当前任务同任务组）
        //
        // 节点后加签：
        // 1.创建加签的任务（新的任务组）

        // 根据实例和用户获取任务
        WorkflowTask task = workflowBaseService.findTask(instance, user);
        if (task == null) {
            throw new BizException(MessageConstants.NOT_FIND_THE_TASK);
        }

        // 检查加签
        checkAddSign(task, signers, countersignOrder, approvalOrder);
        // 更改当前任务
        updateCurrentTasks(task, countersignOrder, approvalOrder);
        // 创建加签任务
        List<WorkflowTask> newTaskList = createNewTasks(task, signers, countersignOrder, approvalOrder);
        workflowBaseService.saveTasks(newTaskList);
        // 保存加签操作的历史
        String operation = ApprovalOperationEnum.ADD_COUNTERSIGN.getId().toString();
        workflowBaseService.saveHistory(task, operation, remark);

        WorkflowNode node = task.getNode();
        WorkflowRule rule = node.getRule();
        // 获取自动审批动作（根据是否无需重复审批规则）
        List<WorkflowAutoApproveAction> actionList = workflowRepeatApproveService.getAutoActionsByRule(rule, newTaskList);
        // 设置要返回的信息
        returnMessage = StringUtil.concat("repeat approval total is ", actionList.size());

        Object nextAction = null;
        String returnStatus = null;

        if (CollectionUtils.isNotEmpty(actionList)) {
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
        result.setMessage(returnMessage);
        return result;
    }

    /**
     * 检查加签
     * @version 1.0
     * @author mh.z
     * @date 2019/04/29
     *
     * @param task 当前任务
     * @param signers 加签人
     * @param countersignOrder 加签顺序
     * @param approvalOrder 审批顺序
     */
    protected void checkAddSign(WorkflowTask task, Collection<WorkflowUser> signers,
                                Integer countersignOrder, Integer approvalOrder) {
        CheckUtil.notNull(task, "task null");
        CheckUtil.notNull(signers, "signers null");
        CheckUtil.notNull(countersignOrder, "countersignOrder null");
        CheckUtil.notNull(approvalOrder, "approvalOrder null");

        WorkflowInstance instance = CheckUtil.notNull(task.getInstance(), "task.instance null");
        Integer approvalStatus = CheckUtil.notNull(instance.getApprovalStatus(), "task.instance.approvalStatus null");
        WorkflowNode node = CheckUtil.notNull(task.getNode(), "task.node null");
        WorkflowRule rule = CheckUtil.notNull(node.getRule(), "task.node.rule null");
        WorkflowUser user = CheckUtil.notNull(task.getUser(), "task.user null");
        UUID userOid = CheckUtil.notNull(user.getUserOid(), "task.user.userOid null");

        // 只能对审批中的单据操作
        if (!WorkflowInstance.APPROVAL_STATUS_APPROVAL.equals(approvalStatus)) {
            throw new BizException(MessageConstants.INSTANCE_STATUS_CANNOT_ADDSIGN);
        }

        // 加签人不能为空
        if (signers.isEmpty()) {
            throw new BizException(MessageConstants.ADDSIGN_USER_IS_EMPTY);
        }

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
     * 更改当前任务
     * @version 1.0
     * @author mh.z
     * @date 2019/05/03
     *
     * @param task 当前任务
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
        List<ApprovalChain> approvalChainList = approvalChainService
                .listByGroupNumberAndApprovalOrder(entityType, entityOid, groupNumber, approvalNumber);
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

            // 把原来未完成任务的状态修改成无效
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
     * @param sourceTask 源任务
     * @param signers 加签人
     * @param countersignOrder 加签顺序
     * @param approvalOrder 审批顺序
     * @return 创建的加签任务
     */
    protected List<WorkflowTask> createNewTasks(WorkflowTask sourceTask, Collection<WorkflowUser> signers,
                                                Integer countersignOrder, Integer approvalOrder) {
        CheckUtil.notNull(sourceTask, "sourceTask null");
        CheckUtil.notNull(signers, "signers null");
        CheckUtil.notNull(countersignOrder, "countersignOrder null");
        CheckUtil.notNull(approvalOrder, "approvalOrder null");
        WorkflowInstance instance = CheckUtil.notNull(sourceTask.getInstance(), "task.instance null");
        WorkflowNode node = CheckUtil.notNull(sourceTask.getNode(), "task.node null");
        ApprovalChain sourceApprovalChain = sourceTask.getApprovalChain();
        Long sourceApprovalChainId = sourceApprovalChain.getId();

        Integer group = null;
        List<WorkflowTask> newTaskList = new ArrayList<WorkflowTask>();

        if (CounterSignOrderEnum.PARALLEL.getValue().equals(countersignOrder)) {
            // 平行加签的新任务跟源任务同个任务组
            group = sourceTask.getGroup();
        } else {
            // 不是平行加签的新任务用新的任务组
            group = workflowBaseService.nextGroup(instance);
        }

        for (WorkflowUser signer : signers) {
            // 创建加签任务
            WorkflowTask newTask = workflowBaseService.createTask(node, signer, group);
            ApprovalChain newApprovalChain = newTask.getApprovalChain();
            // 记录来源任务
            newApprovalChain.setSourceApprovalChainId(sourceApprovalChainId);
            // 标记为加签任务
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

}
