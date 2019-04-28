package com.hand.hcf.app.workflow.approval.dto;

import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * 工作流实例
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowInstance {
    /*
    只提供获取/设置通用的字段
     */

    private WorkFlowDocumentRef workFlowDocumentRef;

    /** 审批状态-未提交 */
    public static final Integer APPROVAL_STATUS_GENERAL = DocumentOperationEnum.GENERATE.getId();
    /** 审批状态-审批中 */
    public static final Integer APPROVAL_STATUS_APPROVAL = DocumentOperationEnum.APPROVAL.getId();
    /** 审批状态-已通过 */
    public static final Integer APPROVAL_STATUS_PASS = DocumentOperationEnum.APPROVAL_PASS.getId();
    /** 审批状态-已驳回 */
    public static final Integer APPROVAL_STATUS_REJECT = DocumentOperationEnum.APPROVAL_REJECT.getId();
    /** 审批状态-已撤回 */
    public static final Integer APPROVAL_STATUS_WITHDRAW = DocumentOperationEnum.WITHDRAW.getId();

    public WorkflowInstance(WorkFlowDocumentRef workFlowDocumentRef) {
        this.workFlowDocumentRef = workFlowDocumentRef;
    }

    public WorkFlowDocumentRef getWorkFlowDocumentRef() {
        return workFlowDocumentRef;
    }

    public void setWorkFlowDocumentRef(WorkFlowDocumentRef workFlowDocumentRef) {
        this.workFlowDocumentRef = workFlowDocumentRef;
    }

    /**
     * 返回实例id
     *
     * @return
     */
    public Long getId() {
        return workFlowDocumentRef.getId();
    }

    /**
     * 返回关联的实体类型
     *
     * @return
     */
    public Integer getEntityType() {
        return workFlowDocumentRef.getDocumentCategory();
    }

    /**
     * 返回关联的实体oid
     *
     * @return
     */
    public UUID getEntityOid() {
        return workFlowDocumentRef.getDocumentOid();
    }

    /**
     * 返回审批状态
     *
     * @return
     */
    public Integer getApprovalStatus() {
        return workFlowDocumentRef.getStatus();
    }

    /**
     * 设置审批状态
     *
     * @param status
     */
    public void setApprovalStatus(Integer status) {
        workFlowDocumentRef.setStatus(status);
    }

    /**
     * 返回申请人oid
     *
     * @return
     */
    public UUID getApplicantOid() {
        return workFlowDocumentRef.getApplicantOid();
    }

    /**
     * 返回提交人oid
     *
     * @return
     */
    public UUID getSubmitterOid() {
        return workFlowDocumentRef.getSubmittedBy();
    }

    /**
     * 返回提交日期
     *
     * @return
     */
    public ZonedDateTime getSubmitDate() {
        return workFlowDocumentRef.getSubmitDate();
    }

    /**
     * 返回最后审批人oid
     *
     * @return
     */
    public UUID getLastApproverOid() {
        return workFlowDocumentRef.getLastApproverOid();
    }

    /**
     * 设置最后审批人oid
     *
     * @param lastApproverOid
     */
    public void setLastApproverOid(UUID lastApproverOid) {
        workFlowDocumentRef.setLastApproverOid(lastApproverOid);
    }

    /**
     * 返回最后审批节点oid
     *
     * @return
     */
    public UUID getLastNodeOid() {
        String approvalNodeOidStr = workFlowDocumentRef.getApprovalNodeOid();
        UUID approvalNodeOid = approvalNodeOidStr != null ? UUID.fromString(approvalNodeOidStr) : null;
        return approvalNodeOid;
    }

    /**
     * 设置最后审批节点oid
     *
     * @param approvalNodeOid
     */
    public void setLastNodeOid(UUID approvalNodeOid) {
        String approvalNodeOidStr = approvalNodeOid != null ? approvalNodeOid.toString() : null;
        workFlowDocumentRef.setApprovalNodeOid(approvalNodeOidStr);
    }

    /**
     * 返回最后审批节点名称
     *
     * @return
     */
    public String getLastNodeName() {
        return workFlowDocumentRef.getApprovalNodeName();
    }

    /**
     * 设置最后审批节点名称
     *
     * @param approvalNodeName
     */
    public void setLastNodeName(String approvalNodeName) {
        workFlowDocumentRef.setApprovalNodeName(approvalNodeName);
    }

    /**
     * 返回驳回/撤回的原因
     *
     * @return
     */
    public String getRejectReason() {
        return workFlowDocumentRef.getRejectReason();
    }

    /**
     * 设置驳回/撤回的原因
     *
     * @param rejectReason
     */
    public void setRejectReason(String rejectReason) {
        workFlowDocumentRef.setRejectReason(rejectReason);
    }

}
