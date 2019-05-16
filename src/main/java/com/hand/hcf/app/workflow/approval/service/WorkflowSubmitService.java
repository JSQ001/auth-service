package com.hand.hcf.app.workflow.approval.service;

//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.co.ApprovalDocumentCO;
import com.hand.hcf.app.common.co.ApprovalResultCO;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.implement.WorkflowSubmitInstanceAction;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.domain.ApprovalForm;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.chain.UserApprovalDTO;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.service.ApprovalFormService;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.ZonedDateTime;
import java.util.List;
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
    private ApprovalFormService approvalFormService;

    @Autowired
    private WorkflowActionService workflowActionService;

    /**
     * 提交工作流
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param approvalDocumentCO 提交的数据
     * @return 提交的结果
     */
    //@LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResultCO submitWorkflow(ApprovalDocumentCO approvalDocumentCO) {
        WorkFlowDocumentRef workFlowDocumentRef = toDomain(approvalDocumentCO);
        Integer entityType = workFlowDocumentRef.getDocumentCategory();
        UUID entityOid = workFlowDocumentRef.getDocumentOid();
        UUID submitterOid = workFlowDocumentRef.getSubmittedBy();

        //不能通过禁用的审批链
        ApprovalForm approvalForm = approvalFormService.getByOid(workFlowDocumentRef.getFormOid());
        Boolean valid = approvalForm.getValid();
        if (!Boolean.TRUE.equals(valid)) {
            throw new BizException(MessageConstants.FORM_RULE_PROHIBIT);
        }

        WorkFlowDocumentRef workFlowDocumentRefPO = workFlowDocumentRefService
                .getByDocumentOidAndDocumentCategory(entityOid, entityType);

        if (workFlowDocumentRefPO != null) {
            // 不能提交审批中和已通过的实例
            Integer status = workFlowDocumentRefPO.getStatus();
            if (DocumentOperationEnum.APPROVAL.getId().equals(status)
                    || DocumentOperationEnum.APPROVAL_PASS.getId().equals(status)) {
                throw new BizException(MessageConstants.INSTANCE_STATUS_CANNOT_SUBMIT);
            }
        }

        // 保存要提交的实例
        if (workFlowDocumentRefPO != null) {
            workFlowDocumentRef.setId(workFlowDocumentRefPO.getId());
            workFlowDocumentRef.setVersionNumber(workFlowDocumentRefPO.getVersionNumber());

            // 保留最后驳回类型
            workFlowDocumentRef.setLastRejectType(workFlowDocumentRefPO.getLastRejectType());
            // 保留提交后跳到的节点id
            workFlowDocumentRef.setJumpNodeId(workFlowDocumentRefPO.getJumpNodeId());
            // 更新实例
            workFlowDocumentRef = workFlowDocumentRefService.updateSysWorkFlowDocumentRef(workFlowDocumentRef);
        } else {
            workFlowDocumentRef = workFlowDocumentRefService.createSysWorkFlowDocumentRef(workFlowDocumentRef);
        }

        // WorkflowInstance
        WorkflowInstance instance = new WorkflowInstance(workFlowDocumentRef);
        // WorkflowUser
        WorkflowUser user = new WorkflowUser(submitterOid);
        // WorkflowSubmitInstanceAction
        WorkflowSubmitInstanceAction action = new WorkflowSubmitInstanceAction(workflowActionService, instance, user, null);

        // 提交实例
        workflowMainService.runWorkflow(instance, action);

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
     * 创建实例对象
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param approvalDocumentCO 提交的数据
     * @return 实例对象
     */
    protected WorkFlowDocumentRef toDomain(ApprovalDocumentCO approvalDocumentCO) {
        Assert.notNull(approvalDocumentCO, "approvalDocumentCO null");
        Assert.notNull(approvalDocumentCO.getDocumentId(), "approvalDocumentCO.documentId null");
        Assert.notNull(approvalDocumentCO.getDocumentOid(), "approvalDocumentCO.documentOid null");
        Assert.notNull(approvalDocumentCO.getDocumentCategory(), "approvalDocumentCO.documentCategory null");
        Assert.notNull(approvalDocumentCO.getSubmittedBy(), "approvalDocumentCO.submittedBy null");
        Assert.notNull(approvalDocumentCO.getDestinationService(), "approvalDocumentCO.destinationService null");

        WorkFlowDocumentRef workFlowDocumentRef = new WorkFlowDocumentRef();
        BeanUtils.copyProperties(approvalDocumentCO, workFlowDocumentRef);
        UUID applicantOid = workFlowDocumentRef.getApplicantOid();
        UUID unitOid = workFlowDocumentRef.getUnitOid();
        Long companyId = workFlowDocumentRef.getCompanyId();
        ZonedDateTime now = ZonedDateTime.now();

        // 如果提交参数里没有formOid则根据单据大类获取（没有或多于一个表单则报错）
        UUID formOid = workFlowDocumentRef.getFormOid();
        if (formOid == null) {
            Long tenantId = OrgInformationUtil.getCurrentTenantId();
            Integer formTypeId = approvalDocumentCO.getDocumentCategory();
            List<ApprovalForm> approvalFormList = approvalFormService.listByTenantAndType(tenantId, formTypeId, RuleApprovalEnum.VALID.getId());

            if (approvalFormList.size() != 1) {
                throw new BizException(MessageConstants.FAIL_TO_GET_FORM_BY_TYPE);
            }

            ApprovalForm approvalForm = approvalFormList.get(0);
            formOid = approvalForm.getFormOid();
            workFlowDocumentRef.setFormOid(formOid);
        }

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
            workFlowDocumentRef.setSetOfBooksId(companyCO.getSetOfBooksId());
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
