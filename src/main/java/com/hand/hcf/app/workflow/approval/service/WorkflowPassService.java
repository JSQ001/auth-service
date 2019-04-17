package com.hand.hcf.app.workflow.approval.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.workflow.approval.constant.ErrorConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowRule;
import com.hand.hcf.app.workflow.approval.dto.WorkflowTask;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.implement.WorkflowNextNodeAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowPassInstanceAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowPassNodeAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowPassTaskAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalReqDTO;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

/**
 * 工作流通过逻辑
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowPassService {
    @Autowired
    private WorkflowBaseService workflowBaseService;

    @Autowired
    private WorkflowMoveNodeService workflowMoveNodeService;

    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private WorkflowMainService workflowMainService;

    @Autowired
    private WorkflowApprovalNotificationService workflowApprovalNotificationService;

    /**
     * 通过审批
     *
     * @param userOid
     * @param approvalReqDTO
     * @return
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResDTO passWorkflow(UUID userOid, ApprovalReqDTO approvalReqDTO) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        approvalResDTO.setFailNum(0);

        List<ApprovalReqDTO.Entity> entityList = approvalReqDTO.getEntities();
        if (CollectionUtils.isEmpty(entityList)) {
            return approvalResDTO;
        }

        Integer entityType = null;
        UUID entityOid = null;
        String approvalText = approvalReqDTO.getApprovalTxt();

        for (ApprovalReqDTO.Entity entity : entityList) {
            entityType = entity.getEntityType();
            entityOid = UUID.fromString(entity.getEntityOid());
            // 通过审批
            doPassWorkflow(entityType, entityOid, userOid, approvalText);
            // 累加成功数
            approvalResDTO.setSuccessNum(approvalResDTO.getSuccessNum() + 1);
        }

        return approvalResDTO;
    }

    /**
     * 通过审批
     *
     * @param entityType
     * @param entityOid
     * @param userOid
     * @param approvalText
     */
    protected void doPassWorkflow(Integer entityType, UUID entityOid, UUID userOid, String approvalText) {
        Assert.notNull(entityType, "entityType null");
        Assert.notNull(entityOid, "entityOid null");
        Assert.notNull(userOid, "userOid null");

        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(entityOid, entityType);
        WorkflowInstance instance = new WorkflowInstance(workFlowDocumentRef);
        WorkflowUser user = new WorkflowUser(userOid);
        WorkflowPassTaskAction action = new WorkflowPassTaskAction(this, instance, user, approvalText);

        // 同个实例的提交/撤回/通过/驳回等操作不支持并发
        workflowBaseService.lockInstance(instance);

        // 通过任务
        workflowMainService.runWorkflow(action);
        // 刷新实例
        workFlowDocumentRef = workFlowDocumentRefService.selectById(workFlowDocumentRef.getId());
        // 通知审批结果
        workflowApprovalNotificationService.sendMessage(workFlowDocumentRef);
    }

    /**
     * 通过任务
     *
     * @param instance
     * @param user
     * @param remark
     * @return
     */
    public WorkflowResult passTask(WorkflowInstance instance, WorkflowUser user, String remark) {
        Assert.notNull(instance, "instance null");
        Assert.notNull(user, "user null");

        // 查找任务
        WorkflowTask task = workflowBaseService.findTask(instance, user);
        if (task == null) {
            throw new BizException(ErrorConstants.NOT_FIND_THE_TASK);
        }

        // 更新任务的状态成通过
        task.setApprovalStatus(WorkflowTask.APPROVAL_STATUS_APPROVED);
        workflowBaseService.updateTask(task);
        // 保存通过的历史
        workflowBaseService.saveHistory(task, WorkflowPassTaskAction.ACTION_NAME, remark);

        WorkflowResult result = new WorkflowResult();
        result.setEntity(task);
        result.setStatus(WorkflowPassTaskAction.RESULT_PASS_SUCCESS);

        WorkflowNode node = task.getNode();
        if (WorkflowNode.TYPE_ROBOT.equals(node.getType())) {
            // 机器人通过则当作实例通过了
            WorkflowPassInstanceAction action = new WorkflowPassInstanceAction(this, task.getInstance(), null, null);
            result.setNext(action);
        } else {
            WorkflowPassNodeAction action = new WorkflowPassNodeAction(this, task.getNode(), null, null);
            result.setNext(action);
        }

        return result;
    }

    /**
     * 通过节点
     *
     * @param node
     * @param user
     * @param remark
     * @return
     */
    public WorkflowResult passNode(WorkflowNode node, WorkflowUser user, String remark) {
        Assert.notNull(node, "node null");
        Assert.notNull(node.getInstance(), "node.instance null");
        Assert.notNull(node.getRule(), "node.rule null");
        WorkflowInstance instance = node.getInstance();
        WorkflowRule rule = node.getRule();
        Integer countersign = rule.getCountersignRule();

        boolean canPassNode = true;
        // 这个会签规则要求同个节点上所有人都通过才能通过节点
        if (WorkflowRule.COUNTERSIGN_ALL_PASS_OR_ANY_REJECT.equals(countersign)) {
            int unfinishedTotal = workflowBaseService.countTasks(node, WorkflowTask.APPROVAL_STATUS_APPROVAL);
            canPassNode = unfinishedTotal == 0;
        }

        WorkflowResult result = new WorkflowResult();
        result.setEntity(node);

        if (canPassNode) {
            // 清除跟节点关联的所有未完成任务
            workflowBaseService.clearUnfinishedTasks(node);

            // 下个动作是移到下个节点
            WorkflowNextNodeAction action = new WorkflowNextNodeAction(workflowMoveNodeService, instance, node);
            result.setStatus(WorkflowPassNodeAction.RESULT_PASS_SUCCESS);
            result.setNext(action);
        } else {
            result.setStatus(WorkflowPassNodeAction.RESULT_PASS_PEND);
        }

        return result;
    }

    /**
     * 通过实例
     *
     * @param instance
     * @param user
     * @param remark
     * @return
     */
    public WorkflowResult passInstance(WorkflowInstance instance, WorkflowUser user, String remark) {
        Assert.notNull(instance, "instance null");
        Assert.notNull(instance.getId(), "instance.id null");

        // 若有节点审批通过则单据通过（当前判断有完成的任务则单据通过）
        int taskTotal = workflowBaseService.countTasks(instance, WorkflowTask.APPROVAL_STATUS_APPROVED);
        if (taskTotal == 0) {
            throw new BizException(ErrorConstants.CHAIN_NOT_EXISTS_TASK);
        }

        // 清除跟该实例关联的所有未完成任务
        workflowBaseService.clearUnfinishedTasks(instance);

        // 更新实例的状态成通过
        instance.setApprovalStatus(WorkflowInstance.APPROVAL_STATUS_PASS);
        workflowBaseService.updateInstance(instance);

        WorkflowResult result = new WorkflowResult();
        result.setStatus(WorkflowPassInstanceAction.RESULT_PASS_SUCCESS);
        result.setEntity(instance);
        return result;
    }

}
