package com.hand.hcf.app.workflow.approval.service;

//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.workflow.approval.constant.ErrorConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowRule;
import com.hand.hcf.app.workflow.approval.dto.WorkflowTask;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.implement.WorkflowRejectInstanceAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowRejectNodeAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowRejectTaskAction;
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
 * 工作流驳回逻辑
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowRejectService {
    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private WorkflowBaseService workflowBaseService;

    @Autowired
    private WorkflowMainService workflowMainService;

    @Autowired
    private WorkflowApprovalNotificationService workflowApprovalNotificationService;

    /**
     * 驳回审批
     *
     * @param userOid
     * @param approvalReqDTO
     * @return
     */
    //@LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResDTO rejectWorkflow(UUID userOid, ApprovalReqDTO approvalReqDTO) {
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
            // 驳回审批
            doRejectWorkflow(entityType, entityOid, userOid, approvalText);
            // 累加成功数
            approvalResDTO.setSuccessNum(approvalResDTO.getSuccessNum() + 1);
        }

        return approvalResDTO;
    }

    /**
     * 驳回审批
     *
     * @param entityType
     * @param entityOid
     * @param userOid
     * @param approvalText
     */
    protected void doRejectWorkflow(Integer entityType, UUID entityOid, UUID userOid, String approvalText) {
        Assert.notNull(entityType, "entityType null");
        Assert.notNull(entityOid, "entityOid null");
        Assert.notNull(userOid, "userOid null");

        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(entityOid, entityType);
        WorkflowInstance instance = new WorkflowInstance(workFlowDocumentRef);
        WorkflowUser user = new WorkflowUser(userOid);
        WorkflowRejectTaskAction action = new WorkflowRejectTaskAction(this, instance, user, approvalText);

        // 同个实例的提交/撤回/通过/驳回等操作不支持并发
        workflowBaseService.lockInstance(instance);

        // 驳回任务
        workflowMainService.runWorkflow(action);
        // 刷新实例
        workFlowDocumentRef = workFlowDocumentRefService.selectById(workFlowDocumentRef.getId());
        // 通知审批结果
        workflowApprovalNotificationService.sendMessage(workFlowDocumentRef);
    }

    /**
     * 驳回任务
     *
     * @param instance
     * @param user
     * @param remark
     * @return
     */
    public WorkflowResult rejectTask(WorkflowInstance instance, WorkflowUser user, String remark) {
        Assert.notNull(instance, "instance null");
        Assert.notNull(user, "user null");

        // 查找任务
        WorkflowTask task = workflowBaseService.findTask(instance, user);
        if (task == null) {
            throw new BizException(ErrorConstants.NOT_FIND_THE_TASK);
        }

        // 更新任务的状态成驳回
        task.setApprovalStatus(WorkflowTask.APPROVAL_STATUS_APPROVED);
        task.setStatus(WorkflowTask.STATUS_INVALID);
        workflowBaseService.updateTask(task);
        // 保存撤回的历史
        workflowBaseService.saveHistory(task, WorkflowRejectTaskAction.ACTION_NAME, remark);

        WorkflowRejectNodeAction action = new WorkflowRejectNodeAction(this, task.getNode(), null, null);
        WorkflowResult result = new WorkflowResult();
        result.setStatus(WorkflowRejectTaskAction.RESULT_REJECT_SUCCESS);
        result.setEntity(task);
        result.setNext(action);
        return result;
    }

    /**
     * 驳回节点
     *
     * @param node
     * @param user
     * @param remark
     * @return
     */
    public WorkflowResult rejectNode(WorkflowNode node, WorkflowUser user, String remark) {
        Assert.notNull(node, "node null");
        Assert.notNull(node.getInstance(), "node.instance null");
        Assert.notNull(node.getRule(), "node.rule null");
        WorkflowInstance instance = node.getInstance();
        WorkflowRule rule = node.getRule();
        Integer countersign = rule.getCountersignRule();

        boolean canRejectNode = true;
        // 这个会签规则要求同个节点上所有人都驳回才能驳回节点
        if (WorkflowRule.COUNTERSIGN_ANY_PASS_OR_ALL_REJECT.equals(countersign)) {
            int unfinishedTotal = workflowBaseService.countTasks(node, WorkflowTask.APPROVAL_STATUS_APPROVAL);
            canRejectNode = unfinishedTotal == 0;
        }

        WorkflowResult result = new WorkflowResult();
        result.setEntity(node);

        if (canRejectNode) {
            // 清除跟节点关联的所有任务
            workflowBaseService.clearAllTasks(node);

            // 下个动作是驳回实例
            WorkflowRejectInstanceAction action = new WorkflowRejectInstanceAction(this, instance, null, null);
            result.setStatus(WorkflowRejectNodeAction.RESULT_REJECT_SUCCESS);
            result.setNext(action);
        } else {
            result.setStatus(WorkflowRejectNodeAction.RESULT_REJECT_PEND);
        }

        return result;
    }

    /**
     * 驳回实例
     *
     * @param instance
     * @param user
     * @param remark
     * @return
     */
    public WorkflowResult rejectInstance(WorkflowInstance instance, WorkflowUser user, String remark) {
        Assert.notNull(instance, "instance null");

        // 清除跟该实例关联的所有任务
        workflowBaseService.clearAllTasks(instance);

        // 更新实例的状态成驳回
        instance.setApprovalStatus(WorkflowInstance.APPROVAL_STATUS_REJECT);
        workflowBaseService.updateInstance(instance);

        WorkflowResult result = new WorkflowResult();
        result.setStatus(WorkflowRejectInstanceAction.RESULT_REJECT_SUCCESS);
        result.setEntity(instance);
        return result;
    }

}
