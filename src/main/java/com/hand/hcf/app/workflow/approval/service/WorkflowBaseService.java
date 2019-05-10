package com.hand.hcf.app.workflow.approval.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.workflow.approval.dto.*;
import com.hand.hcf.app.workflow.approval.enums.RejectTypeEnum;
import com.hand.hcf.app.workflow.approval.implement.WorkflowPassTaskAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowRejectTaskAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowSubmitInstanceAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowWithdrawInstanceAction;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.service.RuleApprovalNodeService;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.domain.ApprovalHistory;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.enums.ApprovalChainStatusEnum;
import com.hand.hcf.app.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.enums.ApprovalOperationTypeEnum;
import com.hand.hcf.app.workflow.service.ApprovalChainService;
import com.hand.hcf.app.workflow.service.ApprovalHistoryService;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import com.hand.hcf.app.workflow.util.CheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author mh.z
 */
@Service
public class WorkflowBaseService {
    /*
    只提供通用的方法
     */

    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private ApprovalChainService approvalChainService;

    @Autowired
    private ApprovalHistoryService approvalHistoryService;

    @Autowired
    private RuleApprovalNodeService ruleApprovalNodeService;

    /**
     * 根据实例和用户查找任务并返回
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance 实例
     * @param user 审批人
     * @return 没有找到任务则返回null
     */
    public WorkflowTask findTask(WorkflowInstance instance, WorkflowUser user) {
        CheckUtil.notNull(instance, "instance null");
        CheckUtil.notNull(user, "user null");
        Integer entityType = CheckUtil.notNull(instance.getEntityType(), "instance.entityType null");
        UUID entityOid = CheckUtil.notNull(instance.getEntityOid(), "instance.entityOid null");
        UUID userOid = CheckUtil.notNull(user.getUserOid(), "user.userOid null");

        // 根据实例和用户获取任务
        ApprovalChain approvalChain = approvalChainService.getApproverApprovalChain(entityType, entityOid, userOid);
        WorkflowTask task = null;

        if (approvalChain != null) {
            // 获取节点
            UUID ruleApprovalNodeOid = approvalChain.getRuleApprovalNodeOid();
            RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(ruleApprovalNodeOid);
            WorkflowNode node = new WorkflowNode(ruleApprovalNode, instance);
            // 创建任务对象
            task = new WorkflowTask(approvalChain, instance, node, user);
        }

        return task;
    }

    /**
     * 统计任务数
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 节点
     * @param approvalStatus 任务的审批状态
     * @return 任务数
     */
    public int countTasks(WorkflowNode node, Integer approvalStatus) {
        CheckUtil.notNull(node, "node null");
        CheckUtil.notNull(approvalStatus, "approvalStatus null");
        UUID nodeOid = CheckUtil.notNull(node.getNodeOid(), "node.nodeOid null");
        WorkflowInstance instance = CheckUtil.notNull(node.getInstance(), "node.instance null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        int taskTotal = approvalChainService.countApprovalChain(entityType, entityOid, nodeOid, approvalStatus);
        return taskTotal;
    }

    /**
     * 统计任务数
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance 实例
     * @param approvalStatus 任务的审批状态
     * @return 任务数
     */
    public int countTasks(WorkflowInstance instance, Integer approvalStatus) {
        CheckUtil.notNull(instance, "instance null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        int taskTotal = approvalChainService.countApprovalChain(entityType, entityOid, null, approvalStatus);
        return taskTotal;
    }

    /**
     * 清除跟节点关联的所有当前任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 节点
     */
    public void clearCurrentTasks(WorkflowNode node) {
        CheckUtil.notNull(node, "node null");
        UUID nodeOid = CheckUtil.notNull(node.getNodeOid(), "node.nodeOid null");
        WorkflowInstance instance = CheckUtil.notNull(node.getInstance(), "node.instance null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        approvalChainService.clearApprovalChain(entityType, entityOid, nodeOid, true, null);
    }

    /**
     * 清除跟节点关联的所有未完成任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 节点
     */
    public void clearUnfinishedTasks(WorkflowNode node) {
        CheckUtil.notNull(node, "node null");
        UUID nodeOid = CheckUtil.notNull(node.getNodeOid(), "node.nodeOid null");
        WorkflowInstance instance = CheckUtil.notNull(node.getInstance(), "node.instance null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        approvalChainService.clearApprovalChain(entityType, entityOid, nodeOid, null, false);
    }

    /**
     * 清除跟节点关联的所有任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 节点
     */
    public void clearAllTasks(WorkflowNode node) {
        CheckUtil.notNull(node, "node null");
        UUID nodeOid = CheckUtil.notNull(node.getNodeOid(), "node.nodeOid null");
        WorkflowInstance instance = CheckUtil.notNull(node.getInstance(), "node.instance null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        approvalChainService.clearApprovalChain(entityType, entityOid, nodeOid, null, null);
    }

    /**
     * 清除跟单据关联的所有未完成任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance 实例
     */
    public void clearUnfinishedTasks(WorkflowInstance instance) {
        CheckUtil.notNull(instance, "instance null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        approvalChainService.clearApprovalChain(entityType, entityOid, null, null, false);
    }

    /**
     * 清除跟单据关联的所有任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance 实例
     */
    public void clearAllTasks(WorkflowInstance instance) {
        CheckUtil.notNull(instance, "instance null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        approvalChainService.clearApprovalChain(entityType, entityOid, null, null, null);
    }

    /**
     * 更新任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param task 任务
     */
    public void updateTask(WorkflowTask task) {
        CheckUtil.notNull(task, "task null");
        ApprovalChain approvalChain = CheckUtil.notNull(task.getApprovalChain(), "task.approvalChain null");

        Long approvalChainId = approvalChain.getId();
        ApprovalChain approvalChainPO = approvalChainService.selectById(approvalChainId);

        // 更新版本号
        approvalChain.setVersionNumber(approvalChainPO.getVersionNumber());
        // 更新任务
        approvalChainService.save(approvalChain);
    }

    /**
     * 更新实例
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance 实例
     */
    public void updateInstance(WorkflowInstance instance) {
        CheckUtil.notNull(instance, "instance null");
        Integer approvalStatus = CheckUtil.notNull(
                instance.getApprovalStatus(), "instance.approvalStatus null");
        WorkFlowDocumentRef workFlowDocumentRef = CheckUtil.notNull(
                instance.getWorkFlowDocumentRef(), "instance.workFlowDocumentRef null");

        Long workFlowDocumentRefId = workFlowDocumentRef.getId();
        WorkFlowDocumentRef workFlowDocumentRefPO = workFlowDocumentRefService.selectById(workFlowDocumentRefId);

        if (WorkflowInstance.APPROVAL_STATUS_REJECT.equals(approvalStatus)) {
            // 驳回情况下记录驳回的原因
            String rejectType = RejectTypeEnum.APPROVAL_REJECT.getId().toString();
            workFlowDocumentRef.setLastRejectType(rejectType);
            workFlowDocumentRef.setRejectType(rejectType);
        } else if (WorkflowInstance.APPROVAL_STATUS_WITHDRAW.equals(approvalStatus)) {
            // 撤销情况下记录撤销的原因
            String rejectType = RejectTypeEnum.WITHDRAW.getId().toString();
            workFlowDocumentRef.setLastRejectType(rejectType);
            workFlowDocumentRef.setRejectType(rejectType);
        }

        // 更新版本号
        workFlowDocumentRef.setVersionNumber(workFlowDocumentRefPO.getVersionNumber());
        // 更新实例
        workFlowDocumentRefService.updateById(workFlowDocumentRef);
    }

    /**
     * 创建任务
     * @author mh.z
     * @date 2019/04/21
     *
     * @param node 节点
     * @param user 审批人
     * @param group 组编号
     * @return 任务
     */
    public WorkflowTask createTask(WorkflowNode node, WorkflowUser user, int group) {
        CheckUtil.notNull(node, "node null");
        CheckUtil.notNull(user, "user null");
        CheckUtil.notNull(node.getInstance(), "node.instance null");
        CheckUtil.notNull(node.getType(), "node.type null");
        CheckUtil.notNull(node.getRule(), "node.rule null");

        WorkflowInstance instance = node.getInstance();
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();
        UUID nodeOid = node.getNodeOid();
        Integer nodeSequence = node.getRuleApprovalNode().getSequenceNumber();
        WorkflowRule rule = node.getRule();
        Integer countersignRule = rule.getCountersignRule();
        Integer nodeType = node.getType();
        UUID userOid = user.getUserOid();
        ZonedDateTime now = ZonedDateTime.now();
        CheckUtil.notNull(entityType, "node.instance.entityType null");
        CheckUtil.notNull(entityOid, "node.instance.entityOid null");
        CheckUtil.notNull(nodeOid, "node.nodeOid null");
        CheckUtil.notNull(nodeSequence, "node.sequence null");
        CheckUtil.notNull(countersignRule, "node.countersignRule null");
        CheckUtil.notNull(userOid, "user.userOid null");

        ApprovalChain approvalChain = new ApprovalChain();
        approvalChain.setEntityType(entityType);
        approvalChain.setEntityOid(entityOid);
        approvalChain.setSequence(nodeSequence);
        approvalChain.setCountersignType(null);
        // 会签规则
        approvalChain.setCountersignRule(countersignRule);

        approvalChain.setApproverOid(userOid);
        approvalChain.setCurrentFlag(true);
        approvalChain.setFinishFlag(false);
        approvalChain.setStatus(ApprovalChainStatusEnum.NORMAL.getId());

        if (WorkflowNode.TYPE_NOTICE.equals(nodeType)) {
            approvalChain.setNoticed(true);
        } else {
            approvalChain.setNoticed(false);
        }

        approvalChain.setGroupNumber(group);
        approvalChain.setApprovalOrder(0);
        approvalChain.setApportionmentFlag(false);
        approvalChain.setRuleApprovalNodeOid(nodeOid);
        approvalChain.setProxyFlag(false);
        approvalChain.setAddSign(0);
        approvalChain.setInvoiceAllowUpdateType(RuleApprovalEnum.NODE_INVOICE_ALLOW_UPDATE_TYPE_NOT_ALLOW.getId());
        approvalChain.setSourceApprovalChainId(null);
        approvalChain.setAllFinished(false);
        approvalChain.setCreatedDate(now);
        approvalChain.setLastUpdatedDate(now);

        WorkflowTask task = new WorkflowTask(approvalChain, instance, node, user);
        return task;
    }

    /**
     * 保存任务
     * @author mh.z
     * @date 2019/05/05
     *
     * @param taskList 任务列表
     */
    public void saveTasks(List<WorkflowTask> taskList) {
        CheckUtil.notNull(taskList, "taskList null");

        for (WorkflowTask task : taskList) {
            saveTask(task);
        }
    }

    /**
     * 保存任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param task 任务
     */
    public void saveTask(WorkflowTask task) {
        CheckUtil.notNull(task, "task null");
        ApprovalChain approvalChain = CheckUtil.notNull(task.getApprovalChain(), "task.approvalChain null");

        // 保存任务
        approvalChainService.save(approvalChain);
    }

    /**
     * 保存任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 节点
     * @param user 用户
     * @param group 组编号
     */
    public void saveTask(WorkflowNode node, WorkflowUser user, int group) {
        WorkflowTask task = createTask(node, user, group);
        saveTask(task);
    }

    /**
     * 保存历史
     * @author mh.z
     * @date 2019/04/07
     *
     * @param task 任务
     * @param operation 操作
     * @param remark 备注
     */
    public void saveHistory(WorkflowTask task, String operation, String remark) {
        CheckUtil.notNull(task, "task null");
        CheckUtil.notNull(operation, "operation null");
        CheckUtil.notNull(task.getInstance(), "task.instance null");
        CheckUtil.notNull(task.getUser(), "task.user null");
        CheckUtil.notNull(task.getNode(), "task.node null");

        WorkflowInstance instance = task.getInstance();
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();
        WorkflowNode node = task.getNode();
        UUID nodeOid = node.getNodeOid();
        WorkflowRule rule = node.getRule();
        WorkflowUser user = task.getUser();
        UUID userOid = user.getUserOid();
        CheckUtil.notNull(entityType, "task.instance.entityType null");
        CheckUtil.notNull(entityOid, "task.instance.entityOid null");
        CheckUtil.notNull(nodeOid, "task.node.nodeOid null");
        CheckUtil.notNull(rule, "task.node.rule null");
        CheckUtil.notNull(userOid, "task.user.userOid null");

        ApprovalHistory approvalHistory = new ApprovalHistory();
        ZonedDateTime now = ZonedDateTime.now();
        approvalHistory.setEntityType(entityType);
        approvalHistory.setEntityOid(entityOid);

        if (WorkflowPassTaskAction.ACTION_NAME.equals(operation)) {
            // 审批通过
            approvalHistory.setOperationType(ApprovalOperationTypeEnum.APPROVAL.getId());
            approvalHistory.setOperation(ApprovalOperationEnum.APPROVAL_PASS.getId());
        } else if (WorkflowRejectTaskAction.ACTION_NAME.equals(operation)) {
            // 审批驳回
            approvalHistory.setOperationType(ApprovalOperationTypeEnum.APPROVAL.getId());
            approvalHistory.setOperation(ApprovalOperationEnum.APPROVAL_REJECT.getId());
        } else if (ApprovalOperationEnum.APPROVAL_TRANSFER.getId().toString().equals(operation)) {
            // 转交
            approvalHistory.setOperationType(ApprovalOperationTypeEnum.APPROVAL.getId());
            approvalHistory.setOperation(ApprovalOperationEnum.APPROVAL_TRANSFER.getId());
        } else if (ApprovalOperationEnum.APPROVAL_RETURN.getId().toString().equals(operation)) {
            // 退回
            approvalHistory.setOperationType(ApprovalOperationTypeEnum.APPROVAL.getId());
            approvalHistory.setOperation(ApprovalOperationEnum.APPROVAL_RETURN.getId());
        } else if (ApprovalOperationEnum.ADD_COUNTERSIGN.getId().toString().equals(operation)) {
            // 加签
            approvalHistory.setOperationType(ApprovalOperationTypeEnum.APPROVAL.getId());
            approvalHistory.setOperation(ApprovalOperationEnum.ADD_COUNTERSIGN.getId());
        } else if (ApprovalOperationEnum.APPROVAL_JUMP.getId().toString().equals(operation)){
            //跳转
            approvalHistory.setOperationType(ApprovalOperationTypeEnum.APPROVAL.getId());
            approvalHistory.setOperation(ApprovalOperationEnum.APPROVAL_JUMP.getId());
        } else {
            throw new IllegalArgumentException(String.format("operation(%s) invalid", operation));
        }

        approvalHistory.setRefApprovalChainId(task.getId());
        // 申请人oid
        approvalHistory.setCurrentApplicantOid(instance.getApplicantOid());
        // 节点信息
        approvalHistory.setRuleApprovalNodeOid(nodeOid);
        approvalHistory.setApprovalNodeOid(nodeOid);
        approvalHistory.setApprovalNodeName(node.getName());
        // 操作描述
        approvalHistory.setOperationDetail(remark);
        // 操作人oid
        approvalHistory.setOperatorOid(userOid);
        approvalHistory.setCreatedDate(now);
        approvalHistory.setLastUpdatedDate(now);
        approvalHistoryService.insert(approvalHistory);
    }

    /**
     * 保存历史
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance 实例
     * @param user 操作人
     * @param operation 操作
     * @param remark 备注
     */
    public void saveHistory(WorkflowInstance instance, WorkflowUser user, String operation, String remark) {
        CheckUtil.notNull(instance, "instance null");
        CheckUtil.notNull(user, "user null");
        Integer entityType = CheckUtil.notNull(instance.getEntityType(), "instance.entityType null");
        UUID entityOid = CheckUtil.notNull(instance.getEntityOid(), "instance.entityOid null");
        UUID userOid = CheckUtil.notNull(user.getUserOid(), "user.userOid null");

        ApprovalHistory approvalHistory = new ApprovalHistory();
        ZonedDateTime now = ZonedDateTime.now();
        approvalHistory.setEntityType(entityType);
        approvalHistory.setEntityOid(entityOid);

        if (WorkflowSubmitInstanceAction.ACTION_NAME.equals(operation)) {
            // 提交工作流
            approvalHistory.setOperationType(ApprovalOperationTypeEnum.SELF.getId());
            approvalHistory.setOperation(ApprovalOperationEnum.SUBMIT_FOR_APPROVAL.getId());
        } else if (WorkflowWithdrawInstanceAction.ACTION_NAME.equals(operation)) {
            // 撤回工作流
            approvalHistory.setOperationType(ApprovalOperationTypeEnum.SELF.getId());
            approvalHistory.setOperation(ApprovalOperationEnum.WITHDRAW.getId());
        } else {
            throw new IllegalArgumentException(String.format("operation(%s) invalid", operation));
        }

        // 申请人oid
        approvalHistory.setCurrentApplicantOid(instance.getApplicantOid());
        // 操作描述
        approvalHistory.setOperationDetail(remark);
        // 操作人oid
        approvalHistory.setOperatorOid(userOid);
        approvalHistory.setCreatedDate(now);
        approvalHistory.setLastUpdatedDate(now);
        approvalHistoryService.insert(approvalHistory);
    }

    /**
     * 返回下一个任务组编号
     * @version 1.0
     * @author mh.z
     * @date 2019/05/01
     *
     * @param instance 实例
     * @return 下一个任务组编号
     */
    public Integer nextGroup(WorkflowInstance instance) {
        Integer entityType = CheckUtil.notNull(instance.getEntityType(), "instance.entityType null");
        UUID entityOid = CheckUtil.notNull(instance.getEntityOid(), "instance.entityOid null");

        Integer group = approvalChainService.getNextGroupNumber(entityType, entityOid);
        return group;
    }

    /**
     * 加上写锁
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance 要加锁的实例
     */
    public void lockInstance(WorkflowInstance instance) {
        CheckUtil.notNull(instance, "instance null");
        Integer entityType = CheckUtil.notNull(instance.getEntityType(), "instance.entityType null");
        UUID entityOid = CheckUtil.notNull(instance.getEntityOid(), "instance.entityOid null");

        EntityWrapper<WorkFlowDocumentRef> wrapper = new EntityWrapper<WorkFlowDocumentRef>();
        wrapper.eq("document_oid", entityOid);
        wrapper.eq("document_category", entityType);
        workFlowDocumentRefService.updateForSet("version_number = version_number", wrapper);
    }

}
