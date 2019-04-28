package com.hand.hcf.app.workflow.approval.service;

//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.workflow.approval.constant.ErrorConstants;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.implement.WorkflowWithdrawInstanceAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalReqDTO;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import com.hand.hcf.app.core.exception.BizException;
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
    private WorkflowApprovalNotificationService workflowApprovalNotificationService;

    @Autowired
    private WorkflowActionService workflowActionService;

    /**
     * 撤回工作流
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param userOid 审批人oid
     * @param approvalReqDTO 要撤回的单据
     * @return 撤回的结果
     */
    //@LcnTransaction
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
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param entityType 单据大类
     * @param entityOid 单据oid
     * @param userOid 审批人oid
     * @param approvalText 审批意见
     */
    protected void doWithdrawWorkflow(Integer entityType, UUID entityOid, UUID userOid, String approvalText) {
        Assert.notNull(entityType, "entityType null");
        Assert.notNull(entityOid, "entityOid null");
        Assert.notNull(userOid, "userOid null");

        // 查找要撤回的实例
        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(entityOid, entityType);
        if (workFlowDocumentRef == null) {
            throw new BizException(MessageConstants.NOT_FIND_THE_INSTANCE);
        }

        UUID submitterOid = workFlowDocumentRef.getSubmittedBy();
        // 暂定只能提交人可以撤回实例
        if (!userOid.equals(submitterOid)) {
            throw new BizException(MessageConstants.NOT_FIND_THE_INSTANCE);
        }

        WorkflowInstance instance = new WorkflowInstance(workFlowDocumentRef);
        WorkflowUser user = new WorkflowUser(userOid);
        WorkflowWithdrawInstanceAction action = new WorkflowWithdrawInstanceAction(workflowActionService, instance, user, approvalText);

        // 对同个实例的操作不支持并发
        workflowBaseService.lockInstance(instance);
        // 撤回实例
        workflowMainService.runWorkflow(action);

        // 刷新实例
        workFlowDocumentRef = workFlowDocumentRefService.selectById(workFlowDocumentRef.getId());
        // 通知审批结果
        workflowApprovalNotificationService.sendMessage(workFlowDocumentRef);
    }

}
