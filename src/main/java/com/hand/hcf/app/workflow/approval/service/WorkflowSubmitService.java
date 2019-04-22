package com.hand.hcf.app.workflow.approval.service;

//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.workflow.approval.constant.ErrorConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.implement.WorkflowNextNodeAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowSubmitInstanceAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalDocumentCO;
import com.hand.hcf.app.workflow.dto.ApprovalResultCO;
import com.hand.hcf.app.workflow.dto.UserApprovalDTO;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * 工作流提交逻辑
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowSubmitService {
    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private WorkflowMainService workflowMainService;

    @Autowired
    private WorkflowBaseService workflowBaseService;

    @Autowired
    private WorkflowMoveNodeService workflowMoveNodeService;

    /**
     * 提交工作流
     *
     * @param approvalDocumentCO
     * @return
     */
    //@LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResultCO submitWorkflow(ApprovalDocumentCO approvalDocumentCO) {
        WorkFlowDocumentRef workFlowDocumentRef = toDomain(approvalDocumentCO);
        Integer entityType = workFlowDocumentRef.getDocumentCategory();
        UUID entityOid = workFlowDocumentRef.getDocumentOid();
        UUID submitterOid = workFlowDocumentRef.getSubmittedBy();


        WorkFlowDocumentRef workFlowDocumentRefPO = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(entityOid, entityType);
        // 不能提交审批中和已通过的实例
        if (workFlowDocumentRefPO != null) {
            Integer status = workFlowDocumentRefPO.getStatus();
            if (DocumentOperationEnum.APPROVAL.getId().equals(status)
                    || DocumentOperationEnum.APPROVAL_PASS.getId().equals(status)) {
                throw new BizException(ErrorConstants.INSTANCE_STATUS_CANNOT_SUBMIT);
            }
        }

        // 保存实例
        if (workFlowDocumentRefPO != null) {
            workFlowDocumentRef.setId(workFlowDocumentRefPO.getId());
            workFlowDocumentRef.setVersionNumber(workFlowDocumentRefPO.getVersionNumber());
            workFlowDocumentRef = workFlowDocumentRefService.updateSysWorkFlowDocumentRef(workFlowDocumentRef);
        } else {
            workFlowDocumentRef = workFlowDocumentRefService.createSysWorkFlowDocumentRef(workFlowDocumentRef);
        }

        // WorkflowInstance
        WorkflowInstance instance = new WorkflowInstance(workFlowDocumentRef);
        // WorkflowUser
        WorkflowUser user = new WorkflowUser(submitterOid);
        // WorkflowSubmitInstanceAction
        WorkflowSubmitInstanceAction action = new WorkflowSubmitInstanceAction(this, instance, user, null);

        // 同个实例的提交/撤回/通过/驳回等操作不支持并发
        workflowBaseService.lockInstance(instance);

        // 提交实例
        workflowMainService.runWorkflow(action);
        // 获取状态
        workFlowDocumentRef = workFlowDocumentRefService.selectById(workFlowDocumentRef.getId());
        Integer status = workFlowDocumentRef.getStatus();

        // 返回结果
        ApprovalResultCO approvalResultCO = new ApprovalResultCO();
        approvalResultCO.setSuccess(true);
        approvalResultCO.setStatus(status);
        approvalResultCO.setError(null);
        return approvalResultCO;
    }

    /**
     * 提交实例
     *
     * @param instance
     * @param user
     * @param remark
     * @return
     */
    public WorkflowResult submitInstance(WorkflowInstance instance, WorkflowUser user, String remark) {
        Assert.notNull(instance, "instance null");
        Assert.notNull(instance.getApprovalStatus(), "instance.approvalStatus null");
        Integer status = instance.getApprovalStatus();

        // 不能提交审批中和已经通过的实例
        if (WorkflowInstance.APPROVAL_STATUS_APPROVAL.equals(status)
                || WorkflowInstance.APPROVAL_STATUS_PASS.equals(status)) {
            throw new BizException(ErrorConstants.INSTANCE_STATUS_CANNOT_SUBMIT);
        }

        // 更新实例的状态
        instance.setApprovalStatus(WorkflowInstance.APPROVAL_STATUS_APPROVAL);
        workflowBaseService.updateInstance(instance);

        // 保存提交的历史
        workflowBaseService.saveHistory(instance, user, WorkflowSubmitInstanceAction.ACTION_NAME, remark);

        WorkflowResult result = new WorkflowResult();
        // 下个动作是移到下个节点
        WorkflowNextNodeAction action = new WorkflowNextNodeAction(workflowMoveNodeService, instance, null);
        result.setStatus(WorkflowSubmitInstanceAction.RESULT_SUBMIT_SUCCESS);
        result.setEntity(instance);
        result.setNext(action);
        return result;
    }

    /**
     * @author mh.z
     * @date 2019/04/06
     *
     * @param approvalDocumentCO
     * @return
     */
    protected WorkFlowDocumentRef toDomain(ApprovalDocumentCO approvalDocumentCO) {
        Assert.notNull(approvalDocumentCO, "approvalDocumentCO null");
        Assert.notNull(approvalDocumentCO.getDocumentId(), "approvalDocumentCO.documentId null");
        Assert.notNull(approvalDocumentCO.getDocumentOid(), "approvalDocumentCO.documentOid null");
        Assert.notNull(approvalDocumentCO.getDocumentCategory(), "approvalDocumentCO.documentCategory null");
        Assert.notNull(approvalDocumentCO.getSubmittedBy(), "approvalDocumentCO.submittedBy null");
        Assert.notNull(approvalDocumentCO.getFormOid(), "approvalDocumentCO.formOid null");
        //jiu.zhao application name
		//Assert.notNull(approvalDocumentCO.getDestinationService(), "approvalDocumentCO.destinationService null");

        WorkFlowDocumentRef workFlowDocumentRef = new WorkFlowDocumentRef();
        BeanUtils.copyProperties(approvalDocumentCO, workFlowDocumentRef);
        UUID applicantOid = workFlowDocumentRef.getApplicantOid();
        UUID unitOid = workFlowDocumentRef.getUnitOid();
        Long companyId = workFlowDocumentRef.getCompanyId();
        ZonedDateTime now = ZonedDateTime.now();

        // 申请人
        if (applicantOid != null) {
            UserApprovalDTO userApprovalDTO = baseClient.getUserByUserOid(applicantOid);
            workFlowDocumentRef.setApplicantCode(userApprovalDTO.getEmployeeCode());
            workFlowDocumentRef.setApplicantName(userApprovalDTO.getFullName());
            workFlowDocumentRef.setUserOid(applicantOid);
        }

        // 部门
        if (unitOid != null) {
            DepartmentCO departmentCO = baseClient.getDepartmentByDepartmentOid(unitOid);
            workFlowDocumentRef.setUnitName(departmentCO.getName());
            workFlowDocumentRef.setUnitCode(departmentCO.getDepartmentCode());
        }

        // 公司
        if (companyId != null) {
            CompanyCO companyCO = baseClient.getCompanyById(companyId);
            workFlowDocumentRef.setCompanyOid(companyCO.getCompanyOid());
            workFlowDocumentRef.setCompanyCode(companyCO.getCompanyCode());
            workFlowDocumentRef.setCompanyName(companyCO.getName());
            workFlowDocumentRef.setTenantId(companyCO.getTenantId());
        }

        // 单据名称
        workFlowDocumentRef.setContractName(approvalDocumentCO.getDocumentName());
        // 提交日期
        workFlowDocumentRef.setSubmitDate(now);
        // 单据状态
        workFlowDocumentRef.setStatus(DocumentOperationEnum.GENERATE.getId());

        workFlowDocumentRef.setFilterFlag(false);
        workFlowDocumentRef.setEventId("");
        workFlowDocumentRef.setEventConfirmStatus(false);
        workFlowDocumentRef.setLastRejectType("");
        workFlowDocumentRef.setRejectType("");
        workFlowDocumentRef.setRejectReason("");
        workFlowDocumentRef.setCreatedDate(now);
        workFlowDocumentRef.setLastUpdatedDate(now);

        return workFlowDocumentRef;
    }

}
