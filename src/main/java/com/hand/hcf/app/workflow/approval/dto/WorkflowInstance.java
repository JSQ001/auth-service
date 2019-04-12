package com.hand.hcf.app.workflow.approval.dto;

import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;

import java.util.UUID;

/**
 * 工作流实例
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowInstance {
    private WorkFlowDocumentRef workFlowDocumentRef;

    /** 未提交 */
    public static final Integer APPROVAL_STATUS_GENERAL = DocumentOperationEnum.GENERATE.getId();
    /** 审批中 */
    public static final Integer APPROVAL_STATUS_APPROVAL = DocumentOperationEnum.APPROVAL.getId();
    /** 已通过 */
    public static final Integer APPROVAL_STATUS_PASS = DocumentOperationEnum.APPROVAL_PASS.getId();
    /** 已驳回 */
    public static final Integer APPROVAL_STATUS_REJECT = DocumentOperationEnum.APPROVAL_REJECT.getId();
    /** 已撤回 */
    public static final Integer APPROVAL_STATUS_WITHDRAW = DocumentOperationEnum.WITHDRAW.getId();


    public WorkflowInstance(WorkFlowDocumentRef workFlowDocumentRef) {
        this.workFlowDocumentRef = workFlowDocumentRef;
    }

    public WorkFlowDocumentRef getWorkFlowDocumentRef() {
        return workFlowDocumentRef;
    }

    public Long getId() {
        return workFlowDocumentRef.getId();
    }

    public Integer getEntityType() {
        return workFlowDocumentRef.getDocumentCategory();
    }

    public UUID getEntityOid() {
        return workFlowDocumentRef.getDocumentOid();
    }

    public Integer getApprovalStatus() {
        return workFlowDocumentRef.getStatus();
    }

    public void setApprovalStatus(Integer status) {
        workFlowDocumentRef.setStatus(status);
    }

    public UUID getFormOid() {
        return workFlowDocumentRef.getFormOid();
    }

    public UUID getApplicantOid() {
        return workFlowDocumentRef.getApplicantOid();
    }

}
