package com.hand.hcf.app.workflow.approval.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.co.WorkFlowDocumentRefCO;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.workflow.brms.service.BrmsService;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.domain.ApprovalHistory;
import com.hand.hcf.app.workflow.domain.ApprovalNodeEnum;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalDocumentCO;
import com.hand.hcf.app.workflow.dto.ApprovalResultCO;
import com.hand.hcf.app.workflow.dto.BuildApprovalChainResult;
import com.hand.hcf.app.workflow.dto.UserApprovalDTO;
import com.hand.hcf.app.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.enums.ApprovalOperationTypeEnum;
import com.hand.hcf.app.workflow.enums.ApprovalPathModeEnum;
import com.hand.hcf.app.workflow.service.ApprovalHistoryService;
import com.hand.hcf.app.workflow.service.DefaultWorkflowIntegrationServiceImpl;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import com.hand.hcf.app.workflow.service.WorkFlowEventPublishService;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.exception.core.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApprovalSubmitService {
    public static Logger logger = LoggerFactory.getLogger(ApprovalSubmitService.class);

    @Autowired
    private DefaultWorkflowIntegrationServiceImpl defaultWorkflowIntegrationService;

    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private ApprovalRuleService approvalRuleService;

    @Autowired
    private BrmsService brmsService;

    @Autowired
    private ApprovalHistoryService approvalHistoryRepository;

    @Autowired
    private BaseClient baseClient;

    /**
     * 工作流单据提交统一入口方法
     * <p>
     * applicantOid: 申请人Oid,
     * userOid: 用户Oid,
     * formOid: 表单Oid,
     * documentOid: 单据Oid,
     * documentCategory: 单据大类 （如801003),
     * countersignApproverOids: 加签审批人Oid,
     * documentNumber:单据编号 ,
     * remark:描述说明 ,
     * companyId:公司ID,
     * unitOid:部门Oid,
     * amount:金额,
     * currencyCode:币种,
     * documentTypeId:单据类型ID
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public String submitWorkflow(@RequestBody WorkFlowDocumentRefCO workFlowDocumentRefDTO) {
        WorkFlowDocumentRef workFlowDocumentRef = WorkFlowDocumentRef.builder().build();
        BeanUtils.copyProperties(workFlowDocumentRefDTO, workFlowDocumentRef);
        // 设置FilterFlag
        if (workFlowDocumentRef.getDocumentOid() == null) {
            throw new BizException("提交失败，单据Oid不允许为空!");
        }

        UUID documentOid = workFlowDocumentRef.getDocumentOid();
        UUID userOid = workFlowDocumentRef.getApplicantOid();
        Boolean filterFlag = approvalRuleService.findFilterRuleNew(workFlowDocumentRef);
        workFlowDocumentRef.setFilterFlag(filterFlag);
        //获取公司信息
        CompanyCO company = baseClient.getCompanyById(workFlowDocumentRef.getCompanyId());
        //查询审批人
        UserApprovalDTO user = baseClient.getUserByUserOid(userOid);
        if (workFlowDocumentRef.getUnitOid() != null) {
            DepartmentCO oneByDepartmentOid = baseClient.getDepartmentByDepartmentOid(workFlowDocumentRef.getUnitOid());
            workFlowDocumentRef.setUnitName(oneByDepartmentOid.getName());
            workFlowDocumentRef.setUnitCode(oneByDepartmentOid.getDepartmentCode());
        }
        workFlowDocumentRef.setCompanyCode(company.getCompanyCode());
        workFlowDocumentRef.setCompanyName(company.getName());
        workFlowDocumentRef.setCompanyOid(company.getCompanyOid());
        workFlowDocumentRef.setApplicantCode(user.getEmployeeCode());
        workFlowDocumentRef.setApplicantName(user.getFullName());
        workFlowDocumentRef.setTenantId(company.getTenantId());
        workFlowDocumentRef.setSubmitDate(ZonedDateTime.now());
        //
        if (workFlowDocumentRef.getContractName() == null) {
            workFlowDocumentRef.setContractName(workFlowDocumentRefDTO.getDocumentName());
        }
        workFlowDocumentRef.setUserOid(workFlowDocumentRefDTO.getApplicantOid());
        workFlowDocumentRef.setStatus(DocumentOperationEnum.APPROVAL.getId());
        workFlowDocumentRef.setEventConfirmStatus(false);
        workFlowDocumentRef.setLastRejectType("");
        workFlowDocumentRef.setRejectType("");
        workFlowDocumentRef.setRejectReason("");
        workFlowDocumentRef.setEventId("");

        //设置审批历史
        ApprovalHistory approvalHistory = new ApprovalHistory();
        approvalHistory.setEntityType(workFlowDocumentRefDTO.getDocumentCategory());
        approvalHistory.setEntityOid(documentOid);
        approvalHistory.setOperationType(ApprovalOperationTypeEnum.SELF.getId());
        approvalHistory.setOperation(ApprovalOperationEnum.SUBMIT_FOR_APPROVAL.getId());
        approvalHistory.setOperatorOid(userOid);
        approvalHistory.setApprovalNodeName(ApprovalNodeEnum.SUBMIT_NODE.getName());
        approvalHistoryRepository.save(approvalHistory);
        try {
            // 审批流
            BuildApprovalChainResult buildApprovalChainResult = null;
            //判断是否是自定义审批流
            if (brmsService.isEnableRule( workFlowDocumentRef.getFormOid())) {
                buildApprovalChainResult = approvalRuleService.buildNewApprovalChainResultByRuleOrApproverOids(workFlowDocumentRef.getDocumentCategory(), null, ApprovalNodeEnum.SUBMIT_NODE.getName(), workFlowDocumentRef);
            } else {
                List<String> approverList = null;
                ApprovalPathModeEnum companyApprovalPathMode = defaultWorkflowIntegrationService.getCompanyApprovalPathMode(company.getCompanyOid());
                if (companyApprovalPathMode == ApprovalPathModeEnum.FULL) {
                    approverList = defaultWorkflowIntegrationService.getWorkflowApprovalPath(userOid, documentOid, workFlowDocumentRef.getDocumentCategory());
                } else {
                    approverList = defaultWorkflowIntegrationService.getWorkflowNextApprovalPath(workFlowDocumentRef.getApplicantOid(), documentOid, null, workFlowDocumentRef.getDocumentCategory());
                }
                approvalRuleService.buildApprovalChain(documentOid, workFlowDocumentRef.getDocumentCategory(), approverList, false);
            }
            /**
             * 满足自审批要求
             * 申请人自审批
             */
            if (buildApprovalChainResult != null && buildApprovalChainResult.getAutoSelfApproval()) {
                logger.debug("submitWorkflow param entityOid : {} , entityType : {} , self approval ", documentOid, workFlowDocumentRef.getDocumentCategory());
                approvalRuleService.selfApproval(userOid, "", workFlowDocumentRef);
            }
            if (buildApprovalChainResult != null && buildApprovalChainResult.getApprovalChains().size() > 0) {
                //保存关联单据信息
                workFlowDocumentRefService.saveOrUpdate(workFlowDocumentRef);
                // 发送事件消息 修改各单据的审批状态 提交的时候不发布消息，撤回和审批，拒绝时，发布消息
                //workflowEventPublishService.publishEvent(workFlowDocumentRef);
            }
        } catch (ValidationException e) {
            e.printStackTrace();

            throw e;
        }
        return "SUCCESS";
    }

    /**
     * @author mh.z
     * @date 2019/03/22
     * @description 提交工作流
     *
     * @param approvalDocumentCO 提交的单据
     * @return 提交结果
     */
    public ApprovalResultCO submitWorkflow(ApprovalDocumentCO approvalDocumentCO) {
        WorkFlowDocumentRefCO workFlowDocumentRefCO = new WorkFlowDocumentRefCO();
        BeanUtils.copyProperties(approvalDocumentCO, workFlowDocumentRefCO);

        try {
            WorkFlowEventPublishService.enablePublish(false);
            // 提交工作流
            submitWorkflow(workFlowDocumentRefCO);
        } finally {
            WorkFlowEventPublishService.enablePublish(true);
        }

        // 获取提交后单据的状态
        UUID documentOid = approvalDocumentCO.getDocumentOid();
        Integer documentCategory = approvalDocumentCO.getDocumentCategory();
        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService
                .getByDocumentOidAndDocumentCategory(documentOid, documentCategory);
        Integer documentStatus = workFlowDocumentRef.getStatus();

        // 返回审批结果
        ApprovalResultCO approvalResultCO = new ApprovalResultCO();
        approvalResultCO.setSuccess(true);
        approvalResultCO.setStatus(documentStatus); // 单据状态
        approvalResultCO.setError(null);
        return approvalResultCO;
    }

}
