package com.hand.hcf.app.workflow.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.base.implement.web.CommonControllerImpl;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.common.co.WorkFlowDocumentRefCO;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.brms.dto.DroolsRuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.service.BrmsService;
import com.hand.hcf.app.workflow.constant.FormConstants;
import com.hand.hcf.app.workflow.constant.SyncLockPrefix;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.util.RespCode;
import com.hand.hcf.app.workflow.util.StringUtil;
import com.hand.hcf.app.workflow.workflow.domain.*;
import com.hand.hcf.app.workflow.workflow.dto.*;
import com.hand.hcf.app.workflow.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.workflow.enums.ApprovalOperationTypeEnum;
import com.hand.hcf.app.workflow.workflow.enums.ApprovalPathModeEnum;
import com.hand.hcf.app.workflow.workflow.enums.DocumentCategoryEnum;
import com.hand.hcf.app.workflow.workflow.persistence.WorkFlowDocumentRefMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.exception.core.ServiceUnavailableException;
import com.hand.hcf.core.exception.core.ValidationException;
import com.hand.hcf.core.redisLock.annotations.SyncLock;
import com.hand.hcf.core.redisLock.enums.CredentialTypeEnum;
import com.hand.hcf.core.service.MessageService;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/12.
 * 工作流方法调用 统一入口方法
 */
@Service
public class WorkFlowApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(WorkFlowApprovalService.class);
    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;
    @Autowired
    private BaseClient baseClient;

    @Autowired
    private ApprovalHistoryService approvalHistoryRepository;
    @Autowired
    private BrmsService brmsService;

    @Autowired
    private WorkFlowEventPublishService workflowEventPublishService;
    @Autowired
    private MessageService exceptionService;

    @Autowired
    private DefaultWorkflowIntegrationServiceImpl defaultWorkflowIntegrationService;

    @Autowired
    private WorkFlowApprovalItemService workflowApprovalItemService;

    @Autowired
    private WorkFlowDocumentRefMapper workFlowDocumentRefMapper;

    @Autowired
    private ApprovalFormService approvalFormService;
    @Autowired
    private WorkFlowRefApproversService workFlowRefApproversService;
    @Autowired
    private CommonControllerImpl organizationInterface;

    @Autowired
    private MapperFacade mapperFacade;
    @Autowired
    private ApprovalChainService approvalChainService;

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
        Boolean filterFlag = workflowApprovalItemService.findFilterRuleNew(workFlowDocumentRef);
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
        workFlowDocumentRef.setSetOfBooksId(company.getSetOfBooksId());
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
            if (brmsService.isEnableRule(company.getCompanyOid(), workFlowDocumentRef.getFormOid())) {
                buildApprovalChainResult = workflowApprovalItemService.buildNewApprovalChainResultByRuleOrApproverOids(workFlowDocumentRef.getDocumentCategory(), null, ApprovalNodeEnum.SUBMIT_NODE.getName(), workFlowDocumentRef);
            } else {
                List<String> approverList = null;
                ApprovalPathModeEnum companyApprovalPathMode = defaultWorkflowIntegrationService.getCompanyApprovalPathMode(company.getCompanyOid());
                if (companyApprovalPathMode == ApprovalPathModeEnum.FULL) {
                    approverList = defaultWorkflowIntegrationService.getWorkflowApprovalPath(userOid, documentOid, workFlowDocumentRef.getDocumentCategory());
                } else {
                    approverList =defaultWorkflowIntegrationService.getWorkflowNextApprovalPath(workFlowDocumentRef.getApplicantOid(),documentOid, null,workFlowDocumentRef.getDocumentCategory());
                }
                workflowApprovalItemService.buildApprovalChain(documentOid, workFlowDocumentRef.getDocumentCategory(), approverList, false);
            }
            /**
             * 满足自审批要求
             * 申请人自审批
             */
            if (buildApprovalChainResult != null && buildApprovalChainResult.getAutoSelfApproval()) {
                logger.debug("submitWorkflow param entityOid : {} , entityType : {} , self approval ", documentOid, workFlowDocumentRef.getDocumentCategory());
                workflowApprovalItemService.selfApproval(userOid, "", workFlowDocumentRef);
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

    /**
     * 审批驳回
     *
     * @param approverOid
     * @param approvalReqDTO
     * @return
     */
    @SyncLock(lockPrefix = SyncLockPrefix.APPROVAL, credential = CredentialTypeEnum.USER_OID)
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResDTO rejectWorkflow(UUID approverOid, ApprovalReqDTO approvalReqDTO) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        approvalResDTO.setFailNum(0);
        if (CollectionUtils.isNotEmpty(approvalReqDTO.getEntities())) {
            for (ApprovalReqDTO.Entity entity : approvalReqDTO.getEntities()) {
                try {
                    boolean canRejectDocument = workflowApprovalItemService.rejectWorkflow(approverOid, entity.getEntityType(), UUID.fromString(entity.getEntityOid()), entity.getApproverOid() == null ? null : UUID.fromString(entity.getApproverOid()), approvalReqDTO.getApprovalTxt());
                    approvalResDTO.setSuccessNum(approvalResDTO.getSuccessNum() + 1);
                    // 发送事件消息 修改各单据的审批状态 提交的时候不发布消息，撤回和审批，拒绝时，发布消息
                    WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(UUID.fromString(entity.getEntityOid()), entity.getEntityType());

                    // 只有提交状态的单据才可以审批通过和拒绝和撤回
                    if (!DocumentOperationEnum.APPROVAL.getId().equals(workFlowDocumentRef.getStatus())) {
                        throw new BizException(RespCode.STATUS_ERROR_200003);
                    }

                    // 支持任一人（任一审批人审批通过则单据被审批通过，所有审批人都审批驳回则单据才被驳回）会签规则
                    if (canRejectDocument) {
                        workFlowDocumentRef.setStatus(DocumentOperationEnum.APPROVAL_REJECT.getId());
                        workFlowDocumentRef.setRejectReason(approvalReqDTO.getApprovalTxt());
                        workFlowDocumentRef.setLastRejectType(DocumentOperationEnum.APPROVAL_REJECT.getId().toString());
                        workFlowDocumentRef.setRejectType(DocumentOperationEnum.APPROVAL_REJECT.getId().toString());
                        workFlowDocumentRef.setLastApproverOid(approverOid);
                        workFlowDocumentRef.setRejectReason(approvalReqDTO.getApprovalTxt());// 审批意见
                        workflowEventPublishService.publishEvent(workFlowDocumentRef);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("approval.rejectWorkflow.error", e);
                    approvalResDTO.setFailNum(approvalResDTO.getFailNum() + 1);
                    if (e instanceof BizException) {
                        String messageDetailByCode = exceptionService.getMessageDetailByCode(((BizException) e).getCode());
                        approvalResDTO.getFailReason().put(entity.getEntityOid(), messageDetailByCode);
                    } else if (e instanceof ServiceUnavailableException) {
                        approvalResDTO.getFailReason().put(entity.getEntityOid(), exceptionService.getMessageDetailByCode(RespCode.SERVICE_6001, e.getMessage()));
                    } else {
                        approvalResDTO.getFailReason().put(entity.getEntityOid(), e.getMessage());
                    }
                }
            }
        }
        return approvalResDTO;
    }

    /**
     * 审批通过
     *
     * @param approverOid
     * @param approvalReqDTO
     * @return
     */
    @SyncLock(lockPrefix = SyncLockPrefix.APPROVAL, credential = CredentialTypeEnum.USER_OID)
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResDTO passWorkflow(UUID approverOid, ApprovalReqDTO approvalReqDTO, UUID formOid) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        approvalResDTO.setFailNum(0);
        if (CollectionUtils.isNotEmpty(approvalReqDTO.getEntities())) {
            for (ApprovalReqDTO.Entity entity : approvalReqDTO.getEntities()) {
                try {
                    WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(UUID.fromString(entity.getEntityOid()), entity.getEntityType());
                    // 只有提交状态的单据才可以审批通过和拒绝和撤回
                    if (!DocumentOperationEnum.APPROVAL.getId().equals(workFlowDocumentRef.getStatus())) {
                        throw new BizException(RespCode.STATUS_ERROR_200003);
                    }
                    workflowApprovalItemService.passWorkflow(approverOid, entity.getApproverOid() == null ? null : UUID.fromString(entity.getApproverOid()), approvalReqDTO.getApprovalTxt(), false, entity.isPriceAuditor(), approvalResDTO, workFlowDocumentRef);
                    approvalResDTO.setSuccessNum(approvalResDTO.getSuccessNum() + 1);

                    workFlowDocumentRef.setRejectReason(approvalReqDTO.getApprovalTxt());// 审批意见
                    // 发送事件消息 修改各单据的审批状态 提交的时候不发布消息，撤回和审批，拒绝时，发布消息
                    workflowEventPublishService.publishEvent(workFlowDocumentRef);

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("approval.pass.error:", e);
                    approvalResDTO.setFailNum(approvalResDTO.getFailNum() + 1);
                    if (e instanceof BizException) {
                        String messageDetailByCode = exceptionService.getMessageDetailByCode(((BizException) e).getCode(), ((BizException) e).getArgs());
                        if (StringUtils.isEmpty(messageDetailByCode)) {
                            approvalResDTO.getFailReason().put(entity.getEntityOid(), e.getMessage());
                        } else {
                            approvalResDTO.getFailReason().put(entity.getEntityOid(), messageDetailByCode);
                        }
                    } else if (e instanceof ServiceUnavailableException) {
                        approvalResDTO.getFailReason().put(entity.getEntityOid(), exceptionService.getMessageDetailByCode(RespCode.SERVICE_6001, e.getMessage()));
                    } else {
                        approvalResDTO.getFailReason().put(entity.getEntityOid(), e.getMessage());
                    }
                }
            }
        }
        return approvalResDTO;
    }

    /**
     * 审批撤回
     *
     * @param userOid
     * @param approvalReqDTO
     * @return
     */
    @Transactional
    @LcnTransaction
    @SyncLock(lockPrefix = SyncLockPrefix.APPROVAL, credential = CredentialTypeEnum.USER_OID)
    public ApprovalResDTO withdrawWorkflow(UUID userOid, ApprovalReqDTO approvalReqDTO) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        approvalResDTO.setFailNum(0);
        if (approvalReqDTO.getEntities() != null) {
            List<WorkFlowDocumentRef> documentRefList = new ArrayList<>();
            approvalReqDTO.getEntities().stream().forEach(entity -> {
                WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(UUID.fromString(entity.getEntityOid()), entity.getEntityType());
                if (workFlowDocumentRef != null) {
                    try {
                        // 只有提交状态的单据才可以审批通过和拒绝和撤回
                        if (!DocumentOperationEnum.APPROVAL.getId().equals(workFlowDocumentRef.getStatus())) {
                            throw new BizException(RespCode.STATUS_ERROR_200003);
                        }
                        workFlowDocumentRef.setLastApproverOid(userOid);
                        documentRefList.add(workFlowDocumentRef);
                        if (DocumentOperationEnum.APPROVAL_PASS.getId().equals(workFlowDocumentRef.getStatus())) {
                            throw new RuntimeException("已审批通过的单据，不允许撤回，单据Oid：" + workFlowDocumentRef.getDocumentOid());
                        }
                        // 将审批流数据设置为无效
                        workflowApprovalItemService.withdrawBase(workFlowDocumentRef, approvalReqDTO.getApprovalTxt());
                        approvalResDTO.setSuccessNum(approvalResDTO.getSuccessNum() + 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        approvalResDTO.setFailNum(approvalResDTO.getFailNum() + 1);
                        approvalResDTO.getFailReason().put(entity.getEntityOid(), "Cannot parse entity type: " + entity.getEntityType());
                        if (e instanceof BizException) {
                            BizException biz = (BizException) e;
                            throw new BizException(biz.getCode());
                        } else {
                            throw new BizException("", e.getMessage());
                        }
                    }
                }
            });
            if (documentRefList != null) {
                // 发布消息事件，修改单据的状态为 撤回
                documentRefList.stream().forEach(doc -> {
                    doc.setStatus(DocumentOperationEnum.WITHDRAW.getId());
                    doc.setRejectType(DocumentOperationEnum.WITHDRAW.getId().toString());
                    doc.setLastRejectType(DocumentOperationEnum.WITHDRAW.getId().toString());
                    doc.setRejectReason(approvalReqDTO.getApprovalTxt());
                    this.workflowEventPublishService.publishEvent(doc);
                });
            }
        }
        return approvalResDTO;
    }

    public Map<String, Set<UUID>> getRuleApproverUserOIDs(DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO) {
        return defaultWorkflowIntegrationService.getApproverUserOids(droolsRuleApprovalNodeDTO.getRuleApproverDTOs(),
                        droolsRuleApprovalNodeDTO.getFormValues(),
                        droolsRuleApprovalNodeDTO.getApplicantOid(),
                        droolsRuleApprovalNodeDTO.getRuleApprovalNodeOid(),
                        droolsRuleApprovalNodeDTO);
    }


    /**
     * 【仪表盘】-我的单据
     * tabNumber=1(被退回的单据)
     * tabNumber=2(未完成的单据)
     * @param tabNumber
     * @return
     */
    public List<WorkflowDocumentDTO> listMyDocument(Integer tabNumber){
        List<WorkflowDocumentDTO> list = new ArrayList<>();
        List<Integer> statusList = new ArrayList<>();
        if(tabNumber == 1){
            statusList.add(DocumentOperationEnum.APPROVAL_REJECT.getId());//1005审批驳回
            statusList.add(2001);//2001审核驳回
        }else if(tabNumber == 2){
            statusList.add(DocumentOperationEnum.APPROVAL.getId());//1002审批中
            //statusList.add(DocumentOperationEnum.APPROVAL_PASS.getId());//1004审批通过
        }
        List<WorkFlowDocumentRef> workFlowDocumentRefList = workFlowDocumentRefMapper.selectList(
                new EntityWrapper<WorkFlowDocumentRef>()
                        .in(CollectionUtils.isNotEmpty(statusList), "status", statusList)
                        .eq("applicant_oid", OrgInformationUtil.getCurrentUserOid())
                        .orderBy("document_number", false));

        //封装数据
        if(workFlowDocumentRefList.size() > 0){
            for(WorkFlowDocumentRef workFlowDocumentRef : workFlowDocumentRefList) {
                WorkflowDocumentDTO documentDTO = new WorkflowDocumentDTO();
                documentDTO.setId(workFlowDocumentRef.getDocumentId());
                documentDTO.setType(workFlowDocumentRef.getDocumentCategory());
                documentDTO.setCode(workFlowDocumentRef.getDocumentNumber());
                //documentDTO.setName(EntityTypeDescEnum.parse(type).getDes());
                ApprovalForm approvalForm = approvalFormService.getByOid(workFlowDocumentRef.getFormOid());
                if (approvalForm != null){
                    documentDTO.setName(approvalForm.getFormName());
                }
                documentDTO.setRemark(workFlowDocumentRef.getRemark());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                ZonedDateTime applicantDate = workFlowDocumentRef.getApplicantDate();

                if (applicantDate != null) {
                    documentDTO.setCreatedTime(applicantDate.format(formatter));
                }

                documentDTO.setCurrency(workFlowDocumentRef.getCurrencyCode());
                documentDTO.setAmount(workFlowDocumentRef.getAmount());
                documentDTO.setStatusCode(workFlowDocumentRef.getStatus());
                String statusName = null;
                if(DocumentOperationEnum.APPROVAL_REJECT.getId().equals(workFlowDocumentRef.getStatus())){
                    statusName = "审批驳回";
                }else if(new Integer(2001).equals(workFlowDocumentRef.getStatus())){
                    statusName = "审核驳回";
                }else if(DocumentOperationEnum.APPROVAL.getId().equals(workFlowDocumentRef.getStatus())){
                    statusName = "审核中";
                }else if(DocumentOperationEnum.APPROVAL_PASS.getId().equals(workFlowDocumentRef.getStatus())){
                    statusName = "审批通过";
                }
                documentDTO.setStatusName(statusName);

                if(tabNumber == 1) {
                    //对于退回单据，rejecterName是指驳回人
                    if (workFlowDocumentRef.getLastApproverOid() !=null) {
                        //最后审批人
                        UserApprovalDTO userDTO = baseClient.getUserByUserOid(workFlowDocumentRef.getLastApproverOid());
                        if (userDTO != null) {
                            documentDTO.setRejecterName(userDTO.getFullName());
                        }
                    }
                }else if(tabNumber == 2){
                    //对于未完成单据，rejecterName是指当前审批人
                    List<WorkFlowApprovers> workFlowApprovers = workFlowRefApproversService.getWorkflowApproversByRefIdAndNodeOid(workFlowDocumentRef.getId(),workFlowDocumentRef.getApprovalNodeOid());
                    StringBuilder sb = new StringBuilder();
                    for(WorkFlowApprovers workFlowApproversl : workFlowApprovers){
                        UserApprovalDTO userDTO =baseClient.getUserByUserOid(workFlowApproversl.getApproverOid());
                        if (userDTO != null) {
                            sb.append(userDTO.getFullName()+",");
                        }
                    }
                    documentDTO.setRejecterName(sb.toString());
                }
//                documentDTO.setNodeName();
                list.add(documentDTO);
            }
        }
        return list;
    }

    public ApprovalDashboardDTO getApprovalDashboardDetailDTOList(){
        UUID userOid = OrgInformationUtil.getCurrentUserOid();
        List<ApprovalDashboardDetailDTO>  approvalDashboardDetailDTOList = workFlowDocumentRefMapper.getApprovalListDashboard(userOid);
        List<ApprovalDashboardDetailDTO> list = new LinkedList<>();
        Integer totalCount = 0;
        Map<String, String> categoryMap = new HashMap<String, String>();

        if (approvalDashboardDetailDTOList != null && approvalDashboardDetailDTOList.size() > 0) {
            List<SysCodeValueCO> sysCodeValueCOList = organizationInterface.listSysValueByCodeConditionByEnabled(FormConstants.SYS_CODE_FORM_TYPE, true);

            for (SysCodeValueCO sysCodeValueCO : sysCodeValueCOList) {
                categoryMap.put(sysCodeValueCO.getValue(), sysCodeValueCO.getName());
            }
        }

        for (ApprovalDashboardDetailDTO approvalDashboardDetailDTO : approvalDashboardDetailDTOList) {
            ApprovalDashboardDetailDTO detailDTO = new ApprovalDashboardDetailDTO();
            detailDTO.setCount(approvalDashboardDetailDTO.getCount());
            detailDTO.setType(approvalDashboardDetailDTO.getType());
            String name = null;
            if(approvalDashboardDetailDTO.getType() != null){
                //name = EntityTypeDescEnum.parse(Integer.valueOf(approvalDashboardDetailDTO.getType())).getDes();
                name = categoryMap.get(approvalDashboardDetailDTO.getType());
            }
            detailDTO.setName(name);
            totalCount += approvalDashboardDetailDTO.getCount();
            list.add(detailDTO);
        }
        ApprovalDashboardDTO approvalDashboardDTO = new ApprovalDashboardDTO();
        approvalDashboardDTO.setTotalCount(totalCount);
        approvalDashboardDTO.setApprovalDashboardDetailDTOList(list);
        return approvalDashboardDTO;
    }

    /**
     * 待办事项-待审批单据-单据列表
     * @param documentCategory 单据大类
     * @param documentTypeId 单据类型id
     * @param beginDate 提交日期从
     * @param endDate 提交日期至
     * @param amountFrom 本币金额从
     * @param amountTo 本币金额至
     * @param remark 备注
     * @param documentNumber 单据编号
     * @param mybatisPage 分页信息
     * @return
     */
    public List<WorkFlowDocumentRefDTO> getApprovalToPendDeatil(Integer documentCategory, Long documentTypeId, String applicantName, ZonedDateTime beginDate, ZonedDateTime endDate, Double amountFrom, Double amountTo, String remark, String documentNumber, Page mybatisPage) {

        List<WorkFlowDocumentRefDTO> list = new LinkedList<>();

        //获取当前登录用户(审批人)
        UUID userOid = OrgInformationUtil.getCurrentUserOid();

        // 过滤特殊字符
        if (StringUtils.isNotEmpty(documentNumber)) {
            documentNumber = StringUtil.escapeSpecialCharacters(documentNumber);
            //单据编号模糊查询
            documentNumber = '%' + documentNumber + '%';
        }

        //申请人模糊查询
        if (StringUtils.isNotEmpty(applicantName)) {
            applicantName = '%' + applicantName + '%';
        }

        // 备注模糊查询
        if (StringUtils.isNotEmpty(remark)) {
            remark = '%' + remark + '%';
        }

        //单据编号模糊查询
        if (StringUtils.isNotEmpty(documentNumber)) {
            documentNumber = '%' + documentNumber + '%';
        }

        List<WorkFlowDocumentRef> workFlowDocumentRefList = workFlowDocumentRefMapper.getApprovalToPendDeatil(userOid,documentCategory,documentTypeId,applicantName,beginDate,endDate,amountFrom, amountTo,remark,documentNumber,mybatisPage);
        for (WorkFlowDocumentRef workFlowDocumentRef : workFlowDocumentRefList) {

            //设置相同属性
            WorkFlowDocumentRefDTO workFlowDocumentRefDTO = mapperFacade.map(workFlowDocumentRef, WorkFlowDocumentRefDTO.class);
            //转化其他属性
            if (workFlowDocumentRef.getDocumentCategory() != null) {
                workFlowDocumentRefDTO.setDocumentCategoryName(DocumentCategoryEnum.getDescById(workFlowDocumentRef.getDocumentCategory()));
            }

            list.add(workFlowDocumentRefDTO);
        }


        return list;
    }

    /**
     * 待办事项-待审批单据-分类信息
     * @param documentCategory 单据大类
     * @param documentTypeId 单据类型id
     * @param beginDate 提交日期从
     * @param endDate 提交日期至
     * @param amountFrom 本币金额从
     * @param amountTo 本币金额至
     * @param remark 备注
     * @param documentNumber 单据编号
     * @return
     */
    public List<ApprovalDashboardDetailDTO> getApprovalToPendTotal(Integer documentCategory, Long documentTypeId, String applicantName, ZonedDateTime beginDate, ZonedDateTime endDate, Double amountFrom, Double amountTo, String remark, String documentNumber) {

        //获取当前登录用户(审批人)
        UUID userOid = OrgInformationUtil.getCurrentUserOid();

        // 过滤特殊字符
        if (StringUtils.isNotEmpty(documentNumber)) {
            documentNumber = StringUtil.escapeSpecialCharacters(documentNumber);
            //单据编号模糊查询
            documentNumber = '%' + documentNumber + '%';
        }

        //申请人模糊查询
        if (StringUtils.isNotEmpty(applicantName)) {
            applicantName = '%' + applicantName + '%';
        }

        // 备注模糊查询
        if (StringUtils.isNotEmpty(remark)) {
            remark = '%' + remark + '%';
        }

        //单据编号模糊查询
        if (StringUtils.isNotEmpty(documentNumber)) {
            documentNumber = '%' + documentNumber + '%';
        }

        List<ApprovalDashboardDetailDTO>  approvalDashboardDetailDTOList = workFlowDocumentRefMapper.getApprovalToPendTotal(userOid,documentCategory,documentTypeId,applicantName,beginDate,endDate,amountFrom, amountTo,remark,documentNumber);
        if (approvalDashboardDetailDTOList.size() > 0 && approvalDashboardDetailDTOList != null) {
            for (ApprovalDashboardDetailDTO approvalDashboardDetailDTO : approvalDashboardDetailDTOList) {
                if (approvalDashboardDetailDTO.getType() != null) {
                    approvalDashboardDetailDTO.setName(DocumentCategoryEnum.getDescById(Integer.valueOf(approvalDashboardDetailDTO.getType())));
                }
            }
        }

        return approvalDashboardDetailDTOList;
    }

    /**
     * 待办事项-被退回单据/未完成单据
     * @param tabNumber tabNumber=1(被退回的单据) tabNumber=2(未完成的单据)
     * @param documentCategory 单据大类
     * @param documentTypeId 单据类型id
     * @param beginDate 提交日期从
     * @param endDate 提交日期至
     * @param amountFrom 本币金额从
     * @param amountTo 本币金额至
     * @param lastApproverOid 当前审批人oid
     * @param approvalNodeName 当前审批节点名称
     * @param remark 备注
     * @param documentNumber 单据编号
     * @param mybatisPage 分页信息
     * @return
     */
    public List<WorkFlowDocumentRefDTO> listMyDocumentDetail(Integer tabNumber,Integer documentCategory, Long documentTypeId, String applicantName, ZonedDateTime beginDate, ZonedDateTime endDate, Double amountFrom, Double amountTo, UUID lastApproverOid, String approvalNodeName, String remark, String documentNumber, Page mybatisPage){
        List<WorkFlowDocumentRefDTO> list = new ArrayList<>();
        List<Integer> statusList = new ArrayList<>();
        List<WorkFlowDocumentRef> workFlowDocumentRefList = null;
        if(tabNumber == 1){
            statusList.add(DocumentOperationEnum.APPROVAL_REJECT.getId());//1005审批驳回
            statusList.add(2001);//2001审核驳回
            workFlowDocumentRefList = workFlowDocumentRefMapper.selectPage(mybatisPage,
                    new EntityWrapper<WorkFlowDocumentRef>()
                            .in(CollectionUtils.isNotEmpty(statusList), "status", statusList)
                            .eq("applicant_oid", OrgInformationUtil.getCurrentUserOid())
                            .eq(documentCategory != null, "document_category", documentCategory)
                            .eq(documentTypeId != null, "document_type_id", documentTypeId)
                            .like(applicantName != null, "applicant_name", applicantName)
                            .ge(beginDate != null, "submit_date", beginDate)
                            .le(endDate != null, "submit_date", endDate)
                            .ge(amountFrom != null, "function_amount", amountFrom)
                            .le(amountTo != null, "function_amount", amountTo)
                            .eq(lastApproverOid != null, "last_approver_oid", lastApproverOid) //驳回人
                            .like(approvalNodeName != null, "approval_node_name", approvalNodeName)
                            .like(remark != null, "remark", remark)
                            .like(documentNumber != null, "document_number", documentNumber)
                            .orderBy("last_updated_date", true));
        }else if(tabNumber == 2){
            statusList.add(DocumentOperationEnum.APPROVAL.getId());//1002审批中
            statusList.add(DocumentOperationEnum.APPROVAL_PASS.getId());//1004审批通过

            //审批节点名称模糊查询
            if (StringUtils.isNotEmpty(approvalNodeName)) {
                approvalNodeName = '%' + approvalNodeName + '%';
            }

            //申请人名称模糊查询
            if (StringUtils.isNotEmpty(applicantName)) {
                applicantName = '%' + applicantName + '%';
            }

            //单据编号模糊查询
            if (StringUtils.isNotEmpty(documentNumber)) {
                documentNumber = '%' + documentNumber + '%';
            }

            workFlowDocumentRefList = workFlowDocumentRefMapper.getUnFinishedList(documentCategory,documentTypeId,applicantName,beginDate,endDate,amountFrom, amountTo,lastApproverOid,approvalNodeName,remark,documentNumber,mybatisPage);
        }


        //封装数据
        if(workFlowDocumentRefList.size() > 0){
            for(WorkFlowDocumentRef workFlowDocumentRef : workFlowDocumentRefList) {
                //设置相同属性
                WorkFlowDocumentRefDTO documentDTO = mapperFacade.map(workFlowDocumentRef, WorkFlowDocumentRefDTO.class);

                //转化其他属性
                if (workFlowDocumentRef.getDocumentCategory() != null) {
                    documentDTO.setDocumentCategoryName(DocumentCategoryEnum.getDescById(workFlowDocumentRef.getDocumentCategory()));
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (workFlowDocumentRef.getLastUpdatedDate() != null) {
                    documentDTO.setRejectTime(workFlowDocumentRef.getLastUpdatedDate().format(formatter));
                }

                if(tabNumber == 1) {
                    //对于退回单据，rejecterName是指驳回人
                    if (workFlowDocumentRef.getLastApproverOid() !=null) {
                        //最后审批人
                        UserApprovalDTO userDTO = baseClient.getUserByUserOid(workFlowDocumentRef.getLastApproverOid());
                        if (userDTO != null) {
                            documentDTO.setRejecterName(userDTO.getFullName());
                        }
                    }
                }else if(tabNumber == 2){
                    //对于未完成单据，rejecterName是指当前审批人
                    List<WorkFlowApprovers> workFlowApprovers = workFlowRefApproversService.getWorkflowApproversByRefIdAndNodeOid(workFlowDocumentRef.getId(),workFlowDocumentRef.getApprovalNodeOid());
                    StringBuilder sb = new StringBuilder();
                    for(WorkFlowApprovers workFlowApproversl : workFlowApprovers){
                        UserApprovalDTO userDTO =baseClient.getUserByUserOid(workFlowApproversl.getApproverOid());
                        if (userDTO != null) {
                            sb.append(userDTO.getFullName()+",");
                        }
                    }
                    documentDTO.setRejecterName(sb.toString());
                }
                documentDTO.setNodeName(workFlowDocumentRef.getApprovalNodeName());
                list.add(documentDTO);
            }
        }
        return list;
    }

}
