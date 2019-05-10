package com.hand.hcf.app.workflow.approval.service;

import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.ContactControllerImpl;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.implement.WorkflowTransferTaskAction;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.dto.transfer.TransferDTO;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import com.hand.hcf.app.workflow.util.CheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 工作流转交逻辑
 * @author ly
 * @date 2019/04/18
 */
@Service
public class WorkflowApprovalTransferService {
    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private ContactControllerImpl contactClient;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private WorkflowMainService workflowMainService;

    @Autowired
    private WorkflowActionService workflowActionService;

    /**
     * 转交审批
     * @author ly
     * @date 2019/94/18
     *
     * @param userOid 用户oid
     * @param transferDTO 转交信息
     * @return 转交的结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResDTO transferWorkflow(UUID userOid, TransferDTO transferDTO) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        approvalResDTO.setFailNum(0);

        Integer entityType = transferDTO.getEntityType();
        UUID entityOid = transferDTO.getEntityOid();
        UUID assigneeOid = transferDTO.getUserOid();
        String approvalText = transferDTO.getRemark();

        // 转交审批
        doTransferWorkflow(entityType, entityOid, userOid, assigneeOid, approvalText);
        return approvalResDTO;
    }

    /**
     * 转交审批
     * @version 1.0
     * @author ly
     * @date 2019/04/18
     *
     * @param entityType 单据大类
     * @param entityOid 单据Oid
     * @param userOid 操作人oid
     * @param assigneeOid 受理人（代理人）oid
     * @param approvalText 转交理由
     */
    protected void doTransferWorkflow(Integer entityType, UUID entityOid, UUID userOid, UUID assigneeOid, String approvalText) {
        CheckUtil.notNull(entityType, "entityType null");
        CheckUtil.notNull(entityOid, "entityOid null");
        CheckUtil.notNull(userOid, "userOid null");
        CheckUtil.notNull(assigneeOid, "assigneeOid null");

        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService
                .getByDocumentOidAndDocumentCategory(entityOid, entityType);
        if (workFlowDocumentRef == null) {
            throw new BizException(MessageConstants.NOT_FIND_THE_INSTANCE);
        }

        ContactCO contactCO = contactClient.getByUserOid(assigneeOid);
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        // 只能转交给同租户下的用户
        if (tenantId == null || !tenantId.equals(contactCO.getTenantId())) {
            throw new BizException(MessageConstants.WORKFLOW_TENANT_NOT_OPENING);
        }

        WorkflowInstance instance = new WorkflowInstance(workFlowDocumentRef);
        WorkflowUser user = new WorkflowUser(userOid);
        WorkflowUser assignee = new WorkflowUser(assigneeOid);
        String remark = getTransferDetail(contactCO, approvalText);

        WorkflowTransferTaskAction action = new WorkflowTransferTaskAction(workflowActionService,
                instance, user, assignee, remark);
        // 执行转交审批的工作流逻辑
        workflowMainService.runWorkflowAndSendMessage(instance, action);
    }

    /**
     * 返回转交历史记录
     * @version 1.0
     * @author ly
     * @date 2019/04/18
     *
     * @param contactCO 受理人（代理人）
     * @param approvalText 转交理由
     * @return
     */
    public String getTransferDetail(ContactCO contactCO, String approvalText) {
        CheckUtil.notNull(contactCO, "contactCO null");
        String fullName = contactCO.getFullName();
        String employeeCode = contactCO.getEmployeeCode();

        if (approvalText == null) {
            approvalText = "";
        }

        String transferDetail = baseClient.getMessageDetailByCode(MessageConstants.DELIVER_REMARK, true,
                fullName, employeeCode, approvalText);
        return transferDetail;
    }

}
