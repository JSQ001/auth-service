package com.hand.hcf.app.workflow.approval.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.workflow.approval.constant.ErrorConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.implement.WorkflowWithdrawInstanceAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalReqDTO;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import com.hand.hcf.core.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

/**
 * 工作流撤回逻辑
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowWithdrawService {
    @Autowired
    private WorkflowMainService workflowMainService;

    @Autowired
    private WorkflowBaseService workflowBaseService;

    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private WorkflowMessageService workflowMessageService;

    /**
     * 撤回工作流
     *
     * @param userOid
     * @param approvalReqDTO
     * @return
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResDTO withdrawWorkflow(UUID userOid, ApprovalReqDTO approvalReqDTO) {
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
            // 撤回工作流
            doWithdrawWorkflow(entityType, entityOid, userOid, approvalText);
            // 累加成功数
            approvalResDTO.setSuccessNum(approvalResDTO.getSuccessNum() + 1);
        }

        return approvalResDTO;
    }

    /**
     * 撤回工作流
     *
     * @param entityType
     * @param entityOid
     * @param userOid
     * @param approvalText
     */
    protected void doWithdrawWorkflow(Integer entityType, UUID entityOid, UUID userOid, String approvalText) {
        Assert.notNull(entityType, "entityType null");
        Assert.notNull(entityOid, "entityOid null");
        Assert.notNull(userOid, "userOid null");

        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(entityOid, entityType);
        UUID submittorOid = workFlowDocumentRef.getSubmittedBy();
        Integer approvalStatus = workFlowDocumentRef.getStatus();
        if (workFlowDocumentRef == null) {
            throw new BizException(ErrorConstants.NOT_FIND_THE_INSTANCE);
        } else if (!userOid.equals(submittorOid)) {
            // 暂定只能提交人可以撤回实例
            throw new BizException(ErrorConstants.NOT_FIND_THE_INSTANCE);
        } else if (!DocumentOperationEnum.APPROVAL.getId().equals(approvalStatus)) {
            // 只能撤回审批中的实例
            throw new BizException(ErrorConstants.INSTANCE_STATUS_CANNOT_WITHDRAW);
        }

        WorkflowInstance instance = WorkflowInstance.toInstance(workFlowDocumentRef);
        WorkflowUser user = new WorkflowUser(userOid);
        WorkflowWithdrawInstanceAction action = new WorkflowWithdrawInstanceAction(this, instance, user, approvalText);

        // 同个实例的提交/撤回/通过/驳回等操作不支持并发
        workflowBaseService.lockInstance(instance);

        // 撤回实例
        workflowMainService.runWorkflow(action);
        // 刷新实例
        workFlowDocumentRef = workFlowDocumentRefService.selectById(workFlowDocumentRef.getId());
        // 发送消息
        workflowMessageService.sendMessage(workFlowDocumentRef);
    }

    /**
     * 撤回实例
     *
     * @param instance
     * @param user
     * @param remark
     * @return
     */
    public WorkflowResult withdrawInstance(WorkflowInstance instance, WorkflowUser user, String remark) {
        Assert.notNull(instance, "instance null");
        Assert.notNull(user, "user null");

        // 清除跟实例关联的所有任务
        workflowBaseService.clearAllTasks(instance);
        // 更新实例的状态成撤回
        instance.setStatus(WorkflowInstance.STATUS_WITHDRAW);
        workflowBaseService.updateInstance(instance);
        // 保存撤回的历史
        workflowBaseService.saveHistory(instance, user, WorkflowWithdrawInstanceAction.ACTION_NAME, remark);

        WorkflowResult result = new WorkflowResult();
        result.setStatus(WorkflowWithdrawInstanceAction.RESULT_WITHDRAW_SUCCESS);
        result.setEntity(instance);
        return result;
    }

}
