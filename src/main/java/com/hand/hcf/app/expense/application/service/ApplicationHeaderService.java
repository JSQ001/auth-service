package com.hand.hcf.app.expense.application.service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.expense.application.domain.*;
import com.hand.hcf.app.expense.application.enums.ClosedTypeEnum;
import com.hand.hcf.app.expense.application.persistence.ApplicationHeaderMapper;
import com.hand.hcf.app.expense.application.web.dto.*;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.dto.BudgetCheckResultDTO;
import com.hand.hcf.app.expense.common.dto.DimensionDTO;
import com.hand.hcf.app.expense.common.dto.DocumentLineDTO;
import com.hand.hcf.app.expense.common.externalApi.ContractService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.externalApi.PrepaymentService;
import com.hand.hcf.app.expense.common.service.CommonService;
import com.hand.hcf.app.expense.common.utils.*;
import com.hand.hcf.app.expense.policy.dto.DynamicFieldDTO;
import com.hand.hcf.app.expense.policy.dto.ExpensePolicyMatchDimensionDTO;
import com.hand.hcf.app.expense.policy.dto.PolicyCheckResultDTO;
import com.hand.hcf.app.expense.policy.service.ExpensePolicyService;
import com.hand.hcf.app.expense.report.domain.ExpenseReportHeader;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.service.ExpenseReportHeaderService;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeService;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.domain.ExpenseDocumentField;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum;
import com.hand.hcf.app.expense.type.service.ExpenseDimensionService;
import com.hand.hcf.app.expense.type.service.ExpenseDocumentFieldService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.dto.ApprovalDocumentCO;
import com.hand.hcf.app.workflow.dto.ApprovalResultCO;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;

import com.hand.hcf.core.domain.ExportConfig;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.handler.ExcelExportHandler;
import com.hand.hcf.core.redisLock.annotations.LockedObject;
import com.hand.hcf.core.redisLock.annotations.SyncLock;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.ExcelExportService;
import com.hand.hcf.core.util.DateUtil;
import com.hand.hcf.core.util.TypeConversionUtils;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/21
 */
@Service
@Slf4j
public class ApplicationHeaderService extends BaseService<ApplicationHeaderMapper, ApplicationHeader> {
    @Autowired
    private ApplicationTypeService typeService;
    @Autowired
    private ExpenseDimensionService dimensionService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ApplicationLineService lineService;
    @Autowired
    private ExpenseDocumentFieldService documentFieldService;
    @Autowired
    private ContractService contractClient;
    @Autowired
    private ApplicationLineDistService distService;
    @Autowired
    private WorkflowControllerImpl workflowClient;
    @Autowired
    private ExpenseTypeService expenseTypeService;
    @Autowired
    private ExpensePolicyService expensePolicyService;
    @Autowired
    private  ExcelExportService excelExportService;
    @Autowired
    private PrepaymentService prepaymentService;
    @Autowired
    private ExpenseReportHeaderService expenseReportHeaderService;
    @Autowired
    private ExpenseReportTypeService expenseReportTypeService;
    @Autowired
    private MapperFacade mapperFacade;

    @Value("${spring.application.name:}")
    private String applicationName;

    //jiu.zhao 预算
    /*@Autowired
    private BudgetClient budgetClient;*/



    /**
     * 提交数据发送工作流模块
     *
     * @param header              单据头信息
     * @param type                单据类型
     * @param workFlowDocumentRef 提交信息
     */
    private void sendWorkflow(ApplicationHeader header,
                              ApplicationType type,
                              WorkFlowDocumentRefCO workFlowDocumentRef) {
        String documentOidStr = header.getDocumentOid();
        UUID documentOid = documentOidStr != null ? UUID.fromString(documentOidStr) : null;
        String unitOidStr = header.getDepartmentOid();
        UUID unitOid = unitOidStr != null ? UUID.fromString(unitOidStr) : null;
        String applicantOidStr = header.getApplicationOid();
        UUID applicantOid = applicantOidStr != null ? UUID.fromString(applicantOidStr) : null;
        String formOidStr = type.getFormOid();
        UUID formOid = formOidStr != null ? UUID.fromString(formOidStr) : null;

        // 设置单据类型的名称和代码
        ApprovalDocumentCO submitData = new ApprovalDocumentCO();
        submitData.setDocumentId(header.getId()); // 单据id
        submitData.setDocumentOid(documentOid); // 单据oid
        submitData.setDocumentNumber(header.getDocumentNumber()); // 单据编号
        submitData.setDocumentName(null); // 单据名称
        submitData.setDocumentCategory(header.getDocumentType()); // 单据类别
        submitData.setDocumentTypeId(header.getTypeId()); // 单据类型id
        submitData.setDocumentTypeCode(type.getTypeCode()); // 单据类型代码
        submitData.setDocumentTypeName(type.getTypeName()); // 单据类型名称
        submitData.setCurrencyCode(header.getCurrencyCode()); // 币种
        submitData.setAmount(header.getAmount()); // 原币金额
        submitData.setFunctionAmount(header.getFunctionalAmount()); // 本币金额
        submitData.setCompanyId(header.getCompanyId()); // 公司id
        submitData.setUnitOid(unitOid); // 部门oid
        submitData.setApplicantOid(applicantOid); // 申请人oid
        submitData.setApplicantDate(header.getRequisitionDate()); // 申请日期
        submitData.setRemark(header.getRemarks()); // 备注
        submitData.setSubmittedBy(OrgInformationUtil.getCurrentUserOid()); // 提交人
        submitData.setFormOid(formOid); // 表单oid
        submitData.setDestinationService(applicationName); // 注册到Eureka中的名称

        //审批中
        ApprovalResultCO submitResult = workflowClient.submitWorkflow(submitData);

        try {
            if (Boolean.TRUE.equals(submitResult.getSuccess())){
                Integer approvalStatus = submitResult.getStatus();

                if (DocumentOperationEnum.APPROVAL.getId().equals(approvalStatus)) {
                    updateById(header);
                } else {
                    updateDocumentStatus(header.getId(), approvalStatus, "");
                }
            } else {
                throw new BizException(submitResult.getError());
            }
        } catch (Exception e) {
            // 提交工作流失败则回滚预算
            if (header.getBudgetFlag()) {
                rollBackBudget(header.getId());
            }
            throw new BizException(RespCode.EXPENSE_APPLICATION_SUBMIT_ERROR);
        }
    }

    /**
     * 费用申请单提交
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    @SyncLock(lockPrefix = SyncLockPrefix.EXP_APPLICATION)
    public BudgetCheckResultDTO submit(@LockedObject(value = {"documentId"}) WorkFlowDocumentRefCO workFlowDocumentRef,
                                       Boolean ignoreWarningFlag) {
        // 给单据加上排他锁，否则可能会出现以下几种错误，
        // 1当存在多线程修改单据状态，可能导致最终单据的状态不正确。
        lockByDocumentId(workFlowDocumentRef.getDocumentId());

        long start = System.currentTimeMillis();
        ApplicationHeader header = this.selectById(workFlowDocumentRef.getDocumentId());
        if (header == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        //校验状态
        checkDocumentStatus(DocumentOperationEnum.APPROVAL.getId(), header.getStatus());
        // 提交设置申请日期为当前日期
        header.setRequisitionDate(ZonedDateTime.now());
        header.setStatus(DocumentOperationEnum.APPROVAL.getId());
        // 只有提交时才把form_oid 更新为单据类型新的form_oid, 预算管控标识也更新
        ApplicationType applicationType = typeService.selectById(header.getTypeId());
        header.setFormOid(applicationType.getFormOid());
        header.setBudgetFlag(applicationType.getBudgetFlag());
        // 分摊行创建
        List<ApplicationLineDist> distList = createDistLine(header);
        if (CollectionUtils.isEmpty(distList)) {
            throw new BizException(RespCode.EXPENSE_APPLICATION_LINE_IS_NULL);
        }
        BudgetCheckResultDTO budgetCheckResultDTO;
        //校验预算
        if (header.getBudgetFlag()) {
            // 只有当启用预算
            budgetCheckResultDTO = checkBudget(ignoreWarningFlag == null ? true : ignoreWarningFlag,
                    header, distList);
        } else {
            budgetCheckResultDTO = BudgetCheckResultDTO.ok();
        }
        // 预算校验成功才提交工作流
        if (budgetCheckResultDTO.getPassFlag()) {
            sendWorkflow(header, applicationType, workFlowDocumentRef);
        } else {
            //校验失败，回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        log.info("申请单整体提交,耗时:{}ms", System.currentTimeMillis() - start);
        return budgetCheckResultDTO;
    }
    /**
     * @param documentId 单据id
     */
    public void lockByDocumentId(Long documentId) {
        if (documentId == null) {
            throw new IllegalArgumentException("documentId null");
        }

        EntityWrapper<ApplicationHeader> wrapper = new EntityWrapper<ApplicationHeader>();
        wrapper.eq("id", documentId);
        updateForSet("version_number = version_number", wrapper);
    }

    /**
     * @param headerId     申请单头ID
     * @param status       状态
     * @param approvalText 审批意见
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDocumentStatus(Long headerId, Integer status, String approvalText) {
        ApplicationHeader header = this.selectById(headerId);
        if (header == null) {
            throw new BizException(RespCode.EXPENSE_APPLICATION_HEADER_ID_IS_NULL);
        }

        Long documentId = header.getId();
        // 给单据加上排他锁，否则可能会出现以下几种错误，
        // 1当存在多线程修改单据状态，可能导致最终单据的状态不正确。
        lockByDocumentId(documentId);
        // 这里重新通过id获取单据是为了保证接下来修改的是最新版本记录
        header = selectById(documentId);

        header.setStatus(status);
        // 保存
        this.updateById(header);
        if (DocumentOperationEnum.WITHDRAW.getId().equals(status) || DocumentOperationEnum.APPROVAL_REJECT.getId().equals(status)) {
            //审批拒绝  撤回 如果有预算需要释放，
            rollBackBudget(headerId);
        }
    }


    /**
     * 创建单据头
     *
     * @param dto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApplicationHeader createHeader(ApplicationHeaderWebDTO dto) {
        // 先查询类型 和维度
        ApplicationTypeDimensionDTO applicationTypeDimensionDTO = typeService.queryTypeAndDimensionById(dto.getTypeId(), false);
        Boolean contractRequireInput = applicationTypeDimensionDTO.getRequireInput();
        DepartmentCO units = organizationService.getDepartmentById(dto.getDepartmentId());
        if (units == null) {
            throw new BizException(RespCode.EXPENSE_DEPARTMENT_IS_NULL);
        }
        List<ExpenseDimension> dimensions = applicationTypeDimensionDTO.getDimensions();
        ContactCO userCO = organizationService.getUserById(dto.getEmployeeId());
        dto.setApplicationOid(userCO != null ? userCO.getUserOid() : OrgInformationUtil.getCurrentUserOid().toString());

        checkCreateHeader(contractRequireInput, dto, dimensions);
        dto.setId(null);

        if (!applicationTypeDimensionDTO.getAssociateContract()) {
            dto.setContractHeaderId(null);
        }
        // 设置默认值
        dto.setClosedFlag(ClosedTypeEnum.NOT_CLOSED);
        dto.setAmount(BigDecimal.ZERO);
        dto.setFunctionalAmount(BigDecimal.ZERO);
        dto.setBudgetStatus(false);
        dto.setTenantId(dto.getTenantId() != null ? dto.getTenantId() : OrgInformationUtil.getCurrentTenantId());
        dto.setSetOfBooksId(dto.getSetOfBooksId() != null ? dto.getSetOfBooksId() : OrgInformationUtil.getCurrentSetOfBookId());
        // 取自单据类型，后续不可修改
        dto.setRequireInput(contractRequireInput);
        dto.setBudgetFlag(applicationTypeDimensionDTO.getBudgetFlag());
        dto.setAssociateContract(applicationTypeDimensionDTO.getAssociateContract());
        dto.setEmployeeId(dto.getEmployeeId() != null ? dto.getEmployeeId() : OrgInformationUtil.getCurrentUserId());
        // 设置单据一些信息
        dto.setStatus(DocumentOperationEnum.GENERATE.getId());
        dto.setDocumentType(ExpenseDocumentTypeEnum.EXP_REQUISITION.getKey());
        dto.setFormOid(applicationTypeDimensionDTO.getFormOid());
        dto.setDocumentOid(UUID.randomUUID().toString());
        dto.setDocumentNumber(commonService.getCoding("EXPENSE_APPLICATION", dto.getCompanyId(), null));
        ApplicationHeader applicationHeader = new ApplicationHeader();
        BeanUtils.copyProperties(dto, applicationHeader);
        applicationHeader.setDepartmentOid(units.getDepartmentOid().toString());
        this.insert(applicationHeader);
        if (com.baomidou.mybatisplus.toolkit.CollectionUtils.isNotEmpty(dimensions)) {
            dimensions.stream().forEach(e -> {
                e.setHeaderId(applicationHeader.getId());
                e.setId(null);
                e.setDocumentType(ExpenseDocumentTypeEnum.EXP_REQUISITION.getKey());
            });
            dimensionService.insertBatch(dimensions);
        }
        return applicationHeader;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApplicationHeader updateHeader(ApplicationHeaderWebDTO dto) {
        // 先查询。然后将前端的值赋值给查询出来的，再更新
        if (dto.getId() == null) {
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        ApplicationHeader queryData = this.selectById(dto.getId());
        // 版本不一致
        if (!queryData.getVersionNumber().equals(dto.getVersionNumber())) {
            throw new BizException(RespCode.SYS_VERSION_IS_ERROR);
        }
        checkDocumentStatus(0, queryData.getStatus());
        List<ExpenseDimension> dimensions = dimensionService.listDimensionByHeaderIdAndType(dto.getId(), dto.getDocumentType(), true);
        checkUpdateHeader(queryData, dto, dimensions);
        DepartmentCO units = organizationService.getDepartmentById(dto.getDepartmentId());
        if (units == null) {
            throw new BizException(RespCode.EXPENSE_DEPARTMENT_IS_NULL);
        }
        queryData.setDepartmentOid(units.getDepartmentOid().toString());
        this.updateById(queryData);
        if (!CollectionUtils.isEmpty(dimensions)) {
            dimensionService.updateBatchById(dimensions);
        }
        return queryData;
    }

    /**
     * 校验单据头信息
     *
     * @param contractRequireInput 合同是否必输
     * @param dto                  单据dto
     * @param dimensions           单据类型所有维度
     */
    private void checkCreateHeader(Boolean contractRequireInput,
                                   ApplicationHeaderWebDTO dto,
                                   List<ExpenseDimension> dimensions) {
        // 判断合同是否必输
        if (contractRequireInput) {
            if (dto.getContractHeaderId() == null) {
                throw new BizException(RespCode.EXPENSE_ASSOICATE_CONTRACT_IS_NULL);
            }
        }
        // 如果维度不为空
        if (!CollectionUtils.isEmpty(dimensions)) {
            // 判断是不是单据头的维度
            List<ExpenseDimension> headerDimensions = dimensions
                    .stream()
                    .filter(ExpenseDimension::getHeaderFlag)
                    .collect(Collectors.toList());
            // 如果是单据头上的维度就将值赋值给对应的维度
            if (!CollectionUtils.isEmpty(headerDimensions)) {
                if (CollectionUtils.isEmpty(dto.getDimensions()) && dto.getDimensions().size() != headerDimensions.size()) {
                    throw new BizException(RespCode.EXPENSE_DIMENSIONS_IS_NULL);
                }
                dto.getDimensions().forEach(headerDimension -> {
                    for (ExpenseDimension dimension : dimensions) {
                        if (headerDimension.getDimensionId().equals(dimension.getDimensionId())) {
                            dimension.setHeaderFlag(true);
                            dimension.setValue(headerDimension.getValue());
                            break;
                        }
                    }
                });
            }
        }
        // 设置汇率
        String currencyCode = organizationService.getUserSetOfBooksCurrencyCode(dto.getApplicationOid());
        if (!StringUtils.hasText(currencyCode)){
            throw new BizException(RespCode.EXPENSE_APPLICATION_CURRENCY_CODE_IS_NULL);
        }
        if (currencyCode.equals(dto.getCurrencyCode())) {
            dto.setExchangeRate(BigDecimal.valueOf(1));
        } else {
            CurrencyRateCO currencyRateDTO = organizationService.getForeignCurrencyByCode(currencyCode,
                    dto.getCurrencyCode(),
                    OrgInformationUtil.getCurrentSetOfBookId());
            dto.setExchangeRate(BigDecimal.valueOf(currencyRateDTO.getRate()));
        }
    }

    /**
     * 根据ID查询单据头信息含头的维度信息
     *
     * @param id
     * @return
     */
    public ApplicationHeaderWebDTO getHeaderInfoById(Long id) {
        ApplicationHeaderWebDTO applicationHeaderWebDTO = baseMapper.getHeaderWebDTOById(id, 1);
        //设置typeName
        ApplicationType applicationType = typeService.selectById(applicationHeaderWebDTO.getTypeId());
        applicationHeaderWebDTO.setTypeName(null != applicationType ? applicationType.getTypeName() : null);
        // 编辑时设置保存的值，如果保存的值不存在，则为空，同时设置可以选到的维值
        commonService.setDimensionValueNameAndOptions(
                applicationHeaderWebDTO.getDimensions(),
                applicationHeaderWebDTO.getCompanyId(),
                applicationHeaderWebDTO.getDepartmentId(),
                applicationHeaderWebDTO.getEmployeeId()
        );
        setCompanyAndDepartmentAndEmployee(Collections.singletonList(applicationHeaderWebDTO), true);
        setAttachments(applicationHeaderWebDTO);
        //jiu.zhao 合同
        /*if (applicationHeaderWebDTO.getContractHeaderId() != null) {
            List<ContractHeaderCO> contractHeaderList = contractClient.listContractHeadersByIds(Collections.singletonList(applicationHeaderWebDTO.getContractHeaderId()));
            if (!CollectionUtils.isEmpty(contractHeaderList)) {
                applicationHeaderWebDTO.setContractNumber(contractHeaderList.get(0).getContractNumber());
            }
        }*/
        return applicationHeaderWebDTO;
    }

    public List<ApplicationHeaderWebDTO> listHeaderDTOsByCondition(Page page,
                                                                   String documentNumber,
                                                                   Long typeId,
                                                                   ZonedDateTime requisitionDateFrom,
                                                                   ZonedDateTime requisitionDateTo,
                                                                   BigDecimal amountFrom,
                                                                   BigDecimal amountTo,
                                                                   Integer status,
                                                                   String currencyCode,
                                                                   String remarks,
                                                                   Long employeeId,
                                                                   ClosedTypeEnum closedFlag) {
        Wrapper<ApplicationHeader> wrapper = new EntityWrapper<ApplicationHeader>()
                .eq("t.created_by", OrgInformationUtil.getCurrentUserId())
                .eq(typeId != null, "t.type_id", typeId)
                .eq(status != null, "t.status", status)
                .eq(employeeId != null, "t.employee_id", employeeId)
                .ge(amountFrom != null, "t.amount", amountFrom)
                .le(amountTo != null, "t.amount", amountTo)
                .ge(requisitionDateFrom != null, "t.requisition_date", requisitionDateFrom)
                .lt(requisitionDateTo != null, "t.requisition_date", requisitionDateTo)
                .like(StringUtils.hasText(documentNumber), "t.document_number", documentNumber)
                .eq(StringUtils.hasText(currencyCode), "t.currency_code", currencyCode)
                .like(StringUtils.hasText(remarks), "t.remarks", remarks)
                .eq(closedFlag != null, "t.closed_flag", closedFlag)
                .orderBy("t.id", false);
        List<ApplicationHeaderWebDTO> headers = baseMapper.listByCondition(page, wrapper);
        setCompanyAndDepartmentAndEmployee(headers, true);
        return headers;
    }

    private void setCompanyAndDepartmentAndEmployee(List<ApplicationHeaderWebDTO> headers,
                                                    boolean isSetEmployee) {
        if (!CollectionUtils.isEmpty(headers)) {
            Set<Long> companyIds = new HashSet<>();
            Set<Long> departmentIds = new HashSet<>();
            Set<Long> employeeIds = new HashSet<>();
            headers.forEach(e -> {
                companyIds.add(e.getCompanyId());
                departmentIds.add(e.getDepartmentId());
                employeeIds.add(e.getEmployeeId());
                employeeIds.add(e.getCreatedBy());
            });
            // 查询公司
            Map<Long, CompanyCO> companyMap = organizationService.getCompanyMapByCompanyIds(new ArrayList<>(companyIds));
            // 查询部门
            Map<Long, DepartmentCO> departmentMap = organizationService.getDepartmentMapByDepartmentIds(new ArrayList<>(departmentIds));
            // 查询员工
            Map<Long, ContactCO> usersMap = new HashMap<>(16);
            if (isSetEmployee) {
                usersMap = organizationService.getUserMapByUserIds(new ArrayList<>(employeeIds));
            }

            Map<Long, ContactCO> finalUsersMap = usersMap;
            headers
                    .forEach(e -> {
                        if (companyMap.containsKey(e.getCompanyId())) {
                            e.setCompanyName(companyMap.get(e.getCompanyId()).getName());
                        }
                        if (departmentMap.containsKey(e.getDepartmentId())) {
                            e.setDepartmentName(departmentMap.get(e.getDepartmentId()).getName());
                        }
                        if (isSetEmployee) {
                            if (finalUsersMap.containsKey(e.getEmployeeId())) {
                                e.setEmployeeName(finalUsersMap.get(e.getEmployeeId()).getFullName());
                            }
                            if (finalUsersMap.containsKey(e.getCreatedBy())) {
                                e.setCreatedName(finalUsersMap.get(e.getCreatedBy()).getFullName());
                            }
                        }
                    });
        }
    }

    private void setAttachments(ApplicationHeaderWebDTO dto) {
        if (dto != null) {
            if (StringUtils.hasText(dto.getAttachmentOid())) {
                String[] strings = dto.getAttachmentOid().split(",");
                List<String> attachmentOidList = Arrays.asList(strings);
                List<AttachmentCO> attachments = organizationService.listAttachmentsByOids(attachmentOidList);
                dto.setAttachmentOidList(attachmentOidList);
                dto.setAttachments(attachments);
            }
        }
    }

    private void checkUpdateHeader(ApplicationHeader queryData,
                                   ApplicationHeaderWebDTO dto,
                                   List<ExpenseDimension> dimensions) {
        // 判断合同是否必输
        if (queryData.getRequireInput()) {
            if (dto.getContractHeaderId() == null) {
                throw new BizException(RespCode.EXPENSE_ASSOICATE_CONTRACT_IS_NULL);
            }
        }
        // 如果是关联合同
        if (queryData.getAssociateContract()) {
            queryData.setContractHeaderId(dto.getContractHeaderId());
        }
        // 如果维度不为空 将前端的维度值赋值给查询出来的
        if (!CollectionUtils.isEmpty(dimensions)) {
            List<ExpenseDimension> dtoDimensions = dto.getDimensions();
            if (dimensions.size() != dtoDimensions.size()) {
                throw new BizException(RespCode.EXPENSE_DIMENSIONS_IS_NULL);
            }
            Map<Long, ExpenseDimension> valueMap = dtoDimensions
                    .stream()
                    .collect(Collectors.toMap(ExpenseDimension::getDimensionId, e -> e));
            dimensions.forEach(e -> {
                e.setValue(valueMap.get(e.getDimensionId()).getValue());
            });
        }
        // 币种不可以修改
        // 设置传过来的公司，部门 备注、附件
        queryData.setCompanyId(dto.getCompanyId());
        queryData.setDepartmentId(dto.getDepartmentId());
        queryData.setAttachmentOid(dto.getAttachmentOid());
        queryData.setRemarks(dto.getRemarks());
    }


    /**
     * 创建/更新单据行时查询维度、field信息
     *
     * @param headerId
     * @param id
     * @param isNew
     * @return
     */
    public ApplicationLineWebDTO queryLineInfo(Long headerId, Long id, Boolean isNew) {
        ApplicationLineWebDTO lineDto = new ApplicationLineWebDTO();
        ApplicationHeaderWebDTO headerDTO = baseMapper.getHeaderWebDTOById(headerId, null);
        List<ExpenseDimension> dimensions = headerDTO.getDimensions();
        if (isNew) {
            setCompanyAndDepartmentAndEmployee(Collections.singletonList(headerDTO), false);
            lineDto.setCompanyName(headerDTO.getCompanyName());
            lineDto.setCompanyId(headerDTO.getCompanyId());
            lineDto.setDepartmentName(headerDTO.getDepartmentName());
            lineDto.setDepartmentId(headerDTO.getDepartmentId());
            lineDto.setFields(new ArrayList<>());
        } else {
            if (id == null) {
                throw new BizException(RespCode.SYS_ID_IS_NULL);
            }
            ApplicationLine line = lineService.selectById(id);
            BeanUtils.copyProperties(line, lineDto);
            if(line.getResponsibilityCenterId() != null){
                ResponsibilityCenterCO responsibilityCenterCO = organizationService.getResponsibilityCenterById(line.getResponsibilityCenterId());
                if(responsibilityCenterCO != null) {
                    lineDto.setResponsibilityCenterCodeName(responsibilityCenterCO.getResponsibilityCenterCodeName());
                }
            }
            lineService.setOtherInfo(Collections.singletonList(lineDto));
            lineService.getDimensionList(dimensions, line);
            ExpenseType expenseType = expenseTypeService.selectById(line.getExpenseTypeId());
            lineDto.setExpenseTypeName(null != expenseType ? expenseType.getName() : null);
            lineDto.setEntryMode(null != expenseType ? expenseType.getEntryMode() : null);
            List<ExpenseFieldDTO> fields = lineService.getFields(line);
            lineDto.setFields(fields);
        }
        commonService.setDimensionValueNameAndOptions(
                dimensions,
                isNew ? headerDTO.getCompanyId() : lineDto.getCompanyId(),
                isNew ? headerDTO.getDepartmentId() : lineDto.getDepartmentId(),
                headerDTO.getEmployeeId()
        );
        lineDto.setDimensions(dimensions);
        return lineDto;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean createLine(ApplicationLineWebDTO dto) {
        ApplicationHeader header = checkLineInfo(dto);
        // 创建行
        lineService.createLine(header, dto);
        // 更新头金额
        updateHeaderAmount(header);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateLine(ApplicationLineWebDTO dto) {
        ApplicationHeader header = checkLineInfo(dto);
        // 更新行
        lineService.updateLine(header, dto);
        // 更新头金额
        updateHeaderAmount(header);
        return true;
    }

    private ApplicationHeader checkLineInfo(ApplicationLineWebDTO dto) {
        if (dto.getHeaderId() == null) {
            throw new BizException(RespCode.EXPENSE_APPLICATION_HEADER_ID_IS_NULL);
        }
        ApplicationHeader header = this.selectById(dto.getHeaderId());
        if (header == null) {
            throw new BizException(RespCode.EXPENSE_APPLICATION_HEADER_ID_IS_NULL);
        }
        checkDocumentStatus(0, header.getStatus());
        return header;
    }

    /**
     * 校验单据状态
     *
     * @param operateType 操作类型
     * @param status      单据状态
     */
    private void checkDocumentStatus(Integer operateType, Integer status) {
        switch (operateType) {
            //点击删除
            case -1:
                if (!status.equals(DocumentOperationEnum.GENERATE.getId()) && !status.equals(DocumentOperationEnum.APPROVAL_REJECT.getId())
                        && !status.equals(DocumentOperationEnum.CANCEL.getId()) && !status.equals(DocumentOperationEnum.WITHDRAW.getId())) {
                    throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
                }
                break;
            //更改
            case 0:
                if (!status.equals(DocumentOperationEnum.GENERATE.getId()) && !status.equals(DocumentOperationEnum.APPROVAL_REJECT.getId())
                        && !status.equals(DocumentOperationEnum.CANCEL.getId()) && !status.equals(DocumentOperationEnum.WITHDRAW.getId())) {
                    throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
                }
                break;
            // 提交 至审核中
            case 1002:
                if (!status.equals(DocumentOperationEnum.GENERATE.getId()) && !status.equals(DocumentOperationEnum.APPROVAL_REJECT.getId())
                        && !status.equals(DocumentOperationEnum.CANCEL.getId()) && !status.equals(DocumentOperationEnum.WITHDRAW.getId())
                        && !status.equals(DocumentOperationEnum.HOLD.getId())) {
                    throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
                }
                break;
            // 审核
            case 1004:
                if (!status.equals(DocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
                }
                break;

            // 撤回
            case 1003:
                if (!status.equals(DocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
                }
                break;
            // 审批驳回
            case 1005:
                if (!status.equals(DocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 根据ID删除单据行
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLineByLineId(Long id) {
        ApplicationLine line = lineService.selectById(id);
        if (line == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        ApplicationHeader applicationHeader = this.selectById(line.getHeaderId());
        checkDocumentStatus(-1, applicationHeader.getStatus());
        lineService.deleteById(id);
        // 删除fields
        documentFieldService.delete(new EntityWrapper<ExpenseDocumentField>()
                .eq("header_id", applicationHeader.getId())
                .eq("line_id", line.getId())
                .eq("document_type", applicationHeader.getDocumentType()));
        updateHeaderAmount(applicationHeader);
        return true;
    }

    /**
     * 更新头金额
     *
     * @param header
     */
    private void updateHeaderAmount(ApplicationHeader header) {
        ApplicationLine line = lineService.getTotalAmount(header.getId());
        if (line == null) {
            header.setAmount(BigDecimal.ZERO);
            header.setFunctionalAmount(BigDecimal.ZERO);
        } else {
            header.setAmount(line.getAmount());
            header.setFunctionalAmount(line.getFunctionalAmount());
        }
        this.updateById(header);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteHeader(Long id) {

        ApplicationHeader applicationHeader = this.selectById(id);
        if (null == applicationHeader) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        checkDocumentStatus(-1, applicationHeader.getStatus());
        // 删除行
        lineService.delete(new EntityWrapper<ApplicationLine>().eq("header_id", id));
        // 删除field
        documentFieldService.delete(new EntityWrapper<ExpenseDocumentField>()
                .eq("header_id", id)
                .eq("document_type", applicationHeader.getDocumentType()));
        // 删除维度
        dimensionService.delete(new EntityWrapper<ExpenseDimension>()
                .eq("header_id", id)
                .eq("document_type", applicationHeader.getDocumentType()));
        // 判断是否存在附件
        if (StringUtils.hasText(applicationHeader.getAttachmentOid())) {
            String[] strings = applicationHeader.getAttachmentOid().split(",");
            organizationService.deleteAttachmentsByOids(Arrays.asList(strings));
        }
        // 删除头信息
        this.deleteById(id);
        return true;
    }

    public List<DimensionDTO> queryDimensionColumn(Long id) {
        List<ExpenseDimension> dimensions = dimensionService.listDimensionByHeaderIdAndType(id, ExpenseDocumentTypeEnum.EXP_REQUISITION.getKey(), null);
        if (CollectionUtils.isEmpty(dimensions)) {
            return new ArrayList<>();
        }else{
            List<Long> ids = dimensions.stream().map(ExpenseDimension::getDimensionId).collect(Collectors.toList());
            List<DimensionCO> dimensionCOS = organizationService.listDimensionsByIds(ids);
            Map<Long, String> map = dimensionCOS.stream().collect(Collectors.toMap(DimensionCO::getId, DimensionCO::getDimensionName));
            return dimensions.stream().map(e -> {
                        DimensionDTO dto = new DimensionDTO();
                        dto.setTitle(map.get(e.getDimensionId()));
                        String[] split = e.getDimensionField().split("Id");
                        dto.setDataIndex(split[0] + "Name");
                        return dto;
            }).collect(Collectors.toList());
        }
    }

    /**
     * 根据ID查询单据头详情
     *
     * @param id 单据头id
     * @return
     */
    public ApplicationHeaderWebDTO getHeaderDetailInfo(Long id) {

        ApplicationHeaderWebDTO dto = baseMapper.getHeaderWebDTOById(id, 1);
        if (dto == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        //设置typeName
        ApplicationType applicationType = typeService.selectById(dto.getTypeId());
        dto.setTypeName(null != applicationType ? applicationType.getTypeName() : null);
        commonService.setDimensionValueName(dto.getDimensions(), dto.getCompanyId(),dto.getDepartmentId(),
                dto.getEmployeeId());
        setCompanyAndDepartmentAndEmployee(Collections.singletonList(dto), true);
        //jiu.zhao 合同
        /*if (dto.getContractHeaderId() != null) {
            List<ContractHeaderCO> contractHeaderList = contractClient.listContractHeadersByIds(Collections.singletonList(dto.getContractHeaderId()));
            if (!CollectionUtils.isEmpty(contractHeaderList)) {
                dto.setContractNumber(contractHeaderList.get(0).getContractNumber());
            }
        }*/
        setAttachments(dto);
        return dto;
    }


    public DocumentLineDTO<ApplicationLineWebDTO> getLinesByHeaderId(Long id, Page page, boolean closeFlag) {
        return lineService.getLinesByHeaderId(id, page, closeFlag);
    }

    private List<ApplicationLineDist> createDistLine(ApplicationHeader header) {
        // 删除然后新增
        distService.delete(new EntityWrapper<ApplicationLineDist>().eq("header_id", header.getId()));
        return distService.createDist(header.getId());
    }

    /**
     * 暂用预算
     *
     * @param ignoreWarningFlag 是否忽略警告
     * @param header            申请单头信息
     * @param distLine          申请单分摊行信息
     * @return
     */
    private BudgetCheckResultDTO checkBudget(boolean ignoreWarningFlag,
                                             ApplicationHeader header,
                                             List<ApplicationLineDist> distLine) {
        log.info("进入预算校验");
        //封装请求参数
        BudgetCheckMessageCO param = new BudgetCheckMessageCO();
        //忽略警告标志
        param.setIgnoreWarningFlag(ignoreWarningFlag ? "Y" : "");
        //租户id
        param.setTenantId(header.getTenantId());
        //账套id
        param.setSetOfBooksId(header.getSetOfBooksId());
        List<BudgetReserveCO> details = new ArrayList<>();
        // 获取会计期间 如果是单据关闭，则根据参数指定获取是当前期间还是原期间
        ZonedDateTime periodDate = ZonedDateTime.now();

        PeriodCO period = organizationService.getPeriodsByIDAndTime(header.getSetOfBooksId(), DateUtil.ZonedDateTimeToString(periodDate));
        if (period == null) {
            throw new BizException(RespCode.EXPENSE_PERIODS_ERROR);
        }
        //组装请求参数 (从分摊行取)
        for (ApplicationLineDist lineDist : distLine) {
            BudgetReserveCO detail = new BudgetReserveCO();
            //会计期间名称
            detail.setPeriodName(period.getPeriodName());
            detail.setPeriodNumber(period.getPeriodNum());
            detail.setPeriodQuarter(period.getQuarterNum());
            detail.setPeriodYear(period.getPeriodYear());
            //数量 默认1
            detail.setQuantity(lineDist.getQuantity() == null ? 1 : lineDist.getQuantity());
            detail.setReleaseMsg(null);
            // 申请单冻结
            detail.setReserveFlag("R");
            //费用分摊金额
            detail.setAmount(lineDist.getAmount());
            detail.setDocumentItemSourceId(lineDist.getExpenseTypeId());
            detail.setDocumentItemSourceType("APPLICATION_TYPE");
            detail.setBusinessType("EXP_REQUISITION");
            detail.setStatus("N");
            detail.setManualFlag("N");
            // 公司 部门 员工
            detail.setCompanyId(lineDist.getCompanyId());
            detail.setUnitId(lineDist.getDepartmentId());
            detail.setEmployeeId(header.getEmployeeId());
            //费用币种 汇率 金额
            detail.setCurrency(lineDist.getCurrencyCode());
            detail.setExchangeRate(lineDist.getExchangeRate().doubleValue());
            detail.setFunctionalAmount(lineDist.getFunctionalAmount());
            //申请单头ID /费用id
            detail.setDocumentId(lineDist.getHeaderId());
            detail.setDocumentLineId(lineDist.getId());
            //单位
            detail.setUom(lineDist.getPriceUnit());
            //创建人
            detail.setCreatedBy(lineDist.getCreatedBy());
            DimensionUtils.setDimensionIdByObject(lineDist, detail, ApplicationLineDist.class, BudgetReserveCO.class);
            details.add(detail);
        }
        //预算保留行数据
        log.info("预算保留行数据设置公司，部门，维度等信息");
        commonService.setBudgetReserveOtherInfo(details);
        log.info("组装参数成功");
        param.setBudgetReserveDtoList(details);
        BudgetCheckResultDTO resultDTO = commonService.checkBudget(param);
        // result passFlag 为 true 说明没有超预算 反之表示超预算
        header.setBudgetStatus(!resultDTO.getPassFlag());
        header.setBudgetErrorMessage(resultDTO.getMessage());

        this.updateById(header);

        return resultDTO;
    }

    public ApplicationHeaderCO getHeaderByDocumentOid(String documentOid) {
        return baseMapper.getHeaderByDocumentOid(documentOid);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(OperateDocumentCO operateDocumentDTO) {
        ApplicationHeader header = baseMapper.getHeaderByOid(operateDocumentDTO.getDocumentOid());
        checkDocumentStatus(operateDocumentDTO.getStatus(), header.getStatus());

        if (DocumentOperationEnum.APPROVAL.getId().equals(operateDocumentDTO.getStatus())) {
            // 提交
            if (header.getBudgetFlag() && header.getBudgetStatus()) {
                // 只有当启用预算，并且是超预算
                List<ApplicationLineDist> distList = distService.selectList(new EntityWrapper<ApplicationLineDist>().eq("header_id", header.getId()));
                BudgetCheckResultDTO budgetCheckResultDTO = checkBudget(operateDocumentDTO.getIgnoreWarningFlag() == null ? true : operateDocumentDTO.getIgnoreWarningFlag(),
                        header, distList);
                if (!budgetCheckResultDTO.getPassFlag()) {
                    throw new BizException(RespCode.EXPENSE_BUDGET_CHECK_ERROR);
                }
                // 提交时修改日期
                header.setRequisitionDate(ZonedDateTime.now());
            }
        } else if (DocumentOperationEnum.APPROVAL_PASS.getId().equals(operateDocumentDTO.getStatus())) {
            log.debug("申请单通过！");
        } else if (DocumentOperationEnum.APPROVAL_REJECT.getId().equals(operateDocumentDTO.getStatus()) ||
                DocumentOperationEnum.WITHDRAW.getId().equals(operateDocumentDTO.getStatus())) {
            // 拒绝或者撤回 释放预算
            if (header.getBudgetFlag()) {
                rollBackBudget(header.getId());
            }
        } else {
            throw new BizException(RespCode.EXPENSE_APPLICATION_STATUS_ERROR);
        }

        header.setStatus(operateDocumentDTO.getStatus());
        this.updateById(header);

    }


    @Transactional(rollbackFor = Exception.class)
    public void updateFilterRule(String documentOid) {
        ApplicationHeader header = baseMapper.getHeaderByOid(documentOid);
        this.updateById(header);
    }

    @Transactional(rollbackFor = Exception.class)
    public void rollBackBudget(Long id) {
        List<ApplicationLineDist> distList = distService.selectList(new EntityWrapper<ApplicationLineDist>().eq("header_id", id));
        List<BudgetReverseRollbackCO> rollbackDTOList = distList.stream().map(e -> {
            BudgetReverseRollbackCO rollbackDTO = new BudgetReverseRollbackCO();
            rollbackDTO.setBusinessType("EXP_REQUISITION");
            rollbackDTO.setDocumentId(e.getHeaderId());
            rollbackDTO.setDocumentLineId(e.getId());
            return rollbackDTO;
        }).collect(Collectors.toList());
        commonService.rollbackBudget(rollbackDTOList);
    }

    public List<ApplicationHeaderCO> queryByConditionByWorkFlow(DocumentQueryParamCO paramDTO) {
        List<ApplicationHeaderCO> result = baseMapper.listConditionByWorkFlow(
                paramDTO.getSubmitDateFrom(),
                paramDTO.getSubmitDateTo(),
                paramDTO.getAmountFrom(),
                paramDTO.getAmountTo(),
                paramDTO.getBusinessCode(),
                paramDTO.getTypeId(),
                paramDTO.getCurrencyCode(),
                paramDTO.getDescription(),
                paramDTO.getCompanyId());
        return result;
    }

    /**
     * 获取申请单关闭查询的Wrapper
     */
    public Wrapper<ApplicationHeader> getClosedQueryWrapper(String documentNumber,
                                                            Long typeId,
                                                            ZonedDateTime requisitionDateFrom,
                                                            ZonedDateTime requisitionDateTo,
                                                            BigDecimal amountFrom,
                                                            BigDecimal amountTo,
                                                            ClosedTypeEnum closedFlag,
                                                            String currencyCode,
                                                            String remarks,
                                                            Long employeeId,
                                                            List<Long> companyId) {
        Wrapper<ApplicationHeader> wrapper = new EntityWrapper<ApplicationHeader>()
                .eq("t.status", DocumentOperationEnum.APPROVAL_PASS.getId())
                .ne("t.closed_flag", ClosedTypeEnum.CLOSED)
                .eq(typeId != null, "t.type_id", typeId)
                .eq(closedFlag != null, "t.closed_flag", closedFlag)
                .eq(employeeId != null, "t.employee_id", employeeId)
                .ge(amountFrom != null, "t.amount", amountFrom)
                .le(amountTo != null, "t.amount", amountTo)
                .ge(requisitionDateFrom != null, "t.requisition_date", requisitionDateFrom)
                .lt(requisitionDateTo != null, "t.requisition_date", requisitionDateTo)
                .like(StringUtils.hasText(documentNumber), "t.document_number", documentNumber)
                .eq(StringUtils.hasText(currencyCode), "t.currency_code", currencyCode)
                .like(StringUtils.hasText(remarks), "t.remarks", remarks)
                .in(!CollectionUtils.isEmpty(companyId), "t.company_id", companyId)
                .orderBy("t.id", false);
        log.debug("申请单关闭查询的查询条件为：{}", wrapper.getSqlSegment());
        return wrapper;

    }

    public List<ApplicationHeaderWebDTO> listClosedByCondition(Page page,
                                                               Wrapper<ApplicationHeader> wrapper) {
        List<ApplicationHeaderWebDTO> headers = baseMapper.listCloseByCondition(page, wrapper);
        setCompanyAndDepartmentAndEmployee(headers, true);
        return headers;
    }

    /**
     * 申请单关闭导出
     */
    public void exportClosedExcel(String documentNumber,
                                  Long typeId,
                                  ZonedDateTime requisitionDateFrom,
                                  ZonedDateTime requisitionDateTo,
                                  BigDecimal amountFrom,
                                  BigDecimal amountTo,
                                  ClosedTypeEnum closedFlag,
                                  String currencyCode,
                                  String remarks,
                                  Long employeeId,
                                  List<Long> companyId,
                                  HttpServletResponse response,
                                  HttpServletRequest request,
                                  ExportConfig exportConfig) throws IOException {
        // 获取查询条件SQL
        Wrapper<ApplicationHeader> wrapper = getClosedQueryWrapper(documentNumber, typeId, requisitionDateFrom,
                requisitionDateTo, amountFrom, amountTo, closedFlag, currencyCode, remarks, employeeId, companyId);
        int total = baseMapper.getCountByCondition(wrapper);
        int availProcessors = Runtime.getRuntime().availableProcessors() / 2;
        ExcelExportHandler<ApplicationHeaderWebDTO, ApplicationHeaderClosedDTO> handler = new ExcelExportHandler<ApplicationHeaderWebDTO, ApplicationHeaderClosedDTO>() {
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<ApplicationHeaderWebDTO> queryDataByPage(Page page) {
                return listClosedByCondition(page, wrapper);
            }
            @Override
            public ApplicationHeaderClosedDTO toDTO(ApplicationHeaderWebDTO t) {
                ApplicationHeaderClosedDTO dto = ApplicationHeaderClosedDTO.builder()
                        .documentNumber(t.getDocumentNumber())
                        .typeName(t.getTypeName())
                        .companyName(t.getCompanyName())
                        .employeeName(t.getEmployeeName())
                        .amount(t.getAmount())
                        .currencyCode(t.getCurrencyCode())
                        .functionalAmount(t.getFunctionalAmount())
                        .closedFlag(t.getClosedFlag().getDesc())
                        .remarks(t.getRemarks())
                        .canCloseAmount(t.getCanCloseAmount())
                        .requisitionDate(t.getRequisitionDate()).build();
                return dto;
            }

            @Override
            public Class<ApplicationHeaderClosedDTO> getEntityClass() {
                return ApplicationHeaderClosedDTO.class;
            }
        };
        excelExportService.exportAndDownloadExcel(exportConfig, handler, availProcessors, request, response);
    }

    /**
     * 批量关闭申请单头信息
     *
     * @param closedDTO 关闭的申请单Id集合和意见
     */
    @Transactional(rollbackFor = Exception.class)
    //jiu.zhao 分布式锁
    //@Lock(name = SyncLockPrefix.EXP_APPLICATION, lockType = LockType.TRY_LOCK_LIST, listKey = "#closedDTO.headerIds")
    public boolean closedHeader(ClosedDTO closedDTO) {
        if (CollectionUtils.isEmpty(closedDTO.getHeaderIds())) {
            return true;
        }
        // 查询尚未关闭完全的
        List<ApplicationHeader> applicationHeaders = this.selectList(new EntityWrapper<ApplicationHeader>()
                .ne("closed_flag", ClosedTypeEnum.CLOSED)
                .in("id", closedDTO.getHeaderIds())
                .eq("status", DocumentOperationEnum.APPROVAL_PASS.getId()));


        if (!CollectionUtils.isEmpty(applicationHeaders)) {
            List<ApplicationAssociateDTO> associateDTOS = baseMapper.listAssociateInfo(closedDTO.getHeaderIds());
            Map<Long, List<ApplicationAssociateDTO>> appAssociateMap = new HashMap<>(16);
            if (!CollectionUtils.isEmpty(associateDTOS)){
                appAssociateMap = associateDTOS.stream().collect(Collectors.groupingBy(ApplicationAssociateDTO::getApplicationHeaderId));

            }
            // 更改行金额和释放预算
            updateLinesClosedAmount(applicationHeaders, appAssociateMap);
            // 插入日志
            List<CommonApprovalHistoryCO> collect = applicationHeaders.stream().map(e -> {
                CommonApprovalHistoryCO historyCO = new CommonApprovalHistoryCO();
                historyCO.setEntityOid(UUID.fromString(e.getDocumentOid()));
                historyCO.setEntityType(ExpenseDocumentTypeEnum.EXP_REQUISITION.getKey());
                historyCO.setOperatorOid(OrgInformationUtil.getCurrentUserOid());
                historyCO.setOperation(7001);
                historyCO.setOperationDetail(closedDTO.getMessages());
                return historyCO;
            }).collect(Collectors.toList());
            workflowClient.saveBatchHistory(collect);
            return this.updateBatchById(applicationHeaders);
        }
        return true;
    }

    /**
     * 更改行上的关闭金额
     *
     * @param applicationHeaders 申请单头信息
     */
    private void updateLinesClosedAmount(List<ApplicationHeader> applicationHeaders,
                                         Map<Long, List<ApplicationAssociateDTO>> appAssociateMap) {
        applicationHeaders.forEach(header -> {
            // 查询报账单关联的申请单的金额
            Map<Long, BigDecimal> associateAmount = new HashMap<>(16);
            //校验报账单状态和金额
            List<ApplicationAssociateDTO> applicationAssociateDTOS = appAssociateMap.get(header.getId());
            if (!CollectionUtils.isEmpty(applicationAssociateDTOS)){
                applicationAssociateDTOS.forEach(e -> {
                    if (Constants.AUDIT_FLAG.equals(e.getAuditFlag())){
                        throw new BizException(RespCode.EXPENSE_APPLICATION_HEADER_CLOSE_CHECK_ERROR, new Object[]{header.getDocumentNumber(), e.getReportNumber()});
                    }
                    if (associateAmount.get(e.getApplicationLineId()) == null) {
                        associateAmount.put(e.getApplicationLineId(), e.getAmount());
                    }else{
                        associateAmount.put(e.getApplicationLineId(), associateAmount.get(e.getApplicationLineId()).add(e.getAmount()));
                    }
                });
            }
            header.setClosedFlag(ClosedTypeEnum.CLOSED);
            // 每个单据行本次需要关闭的金额 (用于释放预算)
            List<ApplicationLine> lines = lineService.getLinesByHeaderId(header.getId());
            if (CollectionUtils.isEmpty(lines)) {
                return;
            }
            lines.forEach(e -> {
                // 报账单关联金额
                BigDecimal reportAmount = associateAmount.get(e.getId()) == null ? BigDecimal.ZERO : associateAmount.get(e.getId());
                e.setClosedFlag(ClosedTypeEnum.CLOSED);
                // 设置金额和状态
                e.setClosedAmount(e.getAmount().subtract(reportAmount));
                e.setClosedFunctionalAmount(TypeConversionUtils.roundHalfUp(e.getClosedAmount().multiply(header.getExchangeRate())));
            });
            lineService.updateBatchById(lines);
            double sumClosedAmount = lines.stream().mapToDouble(e -> e.getClosedAmount().doubleValue()).sum();
            double sumClosedFunctionAmount = lines.stream().mapToDouble(e -> e.getClosedFunctionalAmount().doubleValue()).sum();
            header.setClosedAmount(BigDecimal.valueOf(sumClosedAmount));
            header.setClosedFunctionalAmount(BigDecimal.valueOf(sumClosedFunctionAmount));
            if (null != header.getBudgetFlag() && header.getBudgetFlag()) {
                // 预算释放
                releaseBudgetByClose(header, null);
            }
        });
    }

    /**
     * 申请单关闭时释放预算
     */
    private void releaseBudgetByClose(ApplicationHeader header, Long lineDistId) {
        ZonedDateTime periodDate = ZonedDateTime.now();
        String parameterValue = organizationService.getParameterValue(header.getCompanyId(),
                header.getSetOfBooksId(), ParameterConstant.BGT_CLOSED_PERIOD);
        if (!ParameterConstant.CURRENT_PERIOD.equals(parameterValue)) {
            periodDate = header.getRequisitionDate();
        }
        PeriodCO period = organizationService.getPeriodsByIDAndTime(header.getSetOfBooksId(), DateUtil.ZonedDateTimeToString(periodDate));
        if (period == null) {
            throw new BizException(RespCode.EXPENSE_PERIODS_ERROR);
        }
        //jiu.zhao 预算
        //budgetClient.closeRequisition(period, "EXP_REQUISITION", header.getId(), lineDistId);
    }

    /**
     * 根据单据头ID校验费用政策
     *
     * @param id
     * @return
     */
    public PolicyCheckResultDTO checkPolicy(Long id) {

        ApplicationHeader header = this.selectById(id);
        if (header == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        List<ApplicationLine> lines = lineService.getLinesByHeaderId(id);

        for (ApplicationLine line : lines) {
            List<ExpenseDocumentField> fieldList = documentFieldService.selectList(new EntityWrapper<ExpenseDocumentField>()
                    .eq("header_id", header.getId())
                    .eq("line_id", line.getId())
                    .eq("document_type", header.getDocumentType())
            );
            List<DynamicFieldDTO> dynamicFields = new ArrayList<>();
            fieldList.forEach(f ->
                    dynamicFields.add(
                            DynamicFieldDTO.builder().fieldId(f.getId()).fieldName(f.getName()).fieldDataType(f.getFieldDataType()).fieldValue(f.getValue()).build()
                    )
            );
            ExpensePolicyMatchDimensionDTO matchDimensionDTO = ExpensePolicyMatchDimensionDTO
                    .builder()
                    .expenseTypeFlag(0)
                    .companyId(header.getCompanyId())
                    .applicationId(header.getEmployeeId())
                    .currencyCode(line.getCurrencyCode())
                    .expenseTypeId(line.getExpenseTypeId())
                    .amount(line.getAmount())
                    .quantity(line.getQuantity())
                    .price(line.getPrice())
                    .dynamicFields(dynamicFields)
                    .build();
            PolicyCheckResultDTO result = expensePolicyService.checkExpensePolicy(matchDimensionDTO);
            if (!result.getPassFlag()) {
                return result;
            }

        }
        return PolicyCheckResultDTO.ok();
    }

    public  List<ApplicationFinancRequsetDTO> listHeaderDTOsByfincancies(Page page,
                                                                         String documentNumber,
                                                                         Long companyId,
                                                                         Long typeId,
                                                                         ZonedDateTime requisitionDateFrom,
                                                                         ZonedDateTime requisitionDateTo,
                                                                         BigDecimal amountFrom,
                                                                         BigDecimal amountTo,
                                                                         Long status,
                                                                         String currencyCode,
                                                                         String remarks,
                                                                         Long employeeId,
                                                                         Long unitId,
                                                                         Long closedFlag,
                                                                         BigDecimal associatedAmountFrom,
                                                                         BigDecimal associatedAmountTo,
                                                                         BigDecimal relevanceAmountFrom,
                                                                         BigDecimal relevanceAmountTo
    ){

        Wrapper<ApplicationHeader> wrapper = new EntityWrapper<ApplicationHeader>()
                //.eq("t.created_by", OrgInformationUtil.getCurrentUserId())
                .like(StringUtils.hasText(documentNumber),"t.document_number",documentNumber)
                .eq(typeId != null, "t.type_id", typeId)
                .eq(unitId != null, "t.department_id", unitId)
                .eq(companyId != null, "t.company_id", companyId)
                .eq(status != null, "t.status", status)
                .eq(closedFlag != null, "t.closed_flag", closedFlag)
                .eq(employeeId != null, "t.employee_id", employeeId)
                .ge(amountFrom != null, "t.amount", amountFrom)
                .le(amountTo != null, "t.amount", amountTo)
                .ge(requisitionDateFrom != null, "t.requisition_date", requisitionDateFrom)
                .lt(requisitionDateTo != null, "t.requisition_date", requisitionDateTo)
                .eq(StringUtils.hasText(currencyCode), "t.currency_code", currencyCode)
                .like(StringUtils.hasText(remarks), "t.remarks", remarks)
                .orderBy("t.id", false);
        List<ApplicationFinancRequsetDTO> headers = baseMapper.listByfincancies(page, wrapper,associatedAmountFrom,associatedAmountTo,relevanceAmountFrom,relevanceAmountTo);


        headers.forEach(applicationFinancRequsetDTO -> {
            applicationFinancRequsetDTO.setRelevanceAmount(new BigDecimal(String.valueOf(applicationFinancRequsetDTO.getAmount().subtract(applicationFinancRequsetDTO.getAssociatedAmount()))));
            DepartmentCO department = organizationService.getDepartmentById(applicationFinancRequsetDTO.getDepartmentId());
            //设置部门
            if (department != null) {
                applicationFinancRequsetDTO.setDepartmentName(department.getName());
            }
        });
        setCompanyAndDepartmentAndEmployee1(headers);


        return  headers;
    }


    public List<ContactCO> listUsersByCreatedApplications() {
        List<Long> userList = baseMapper.selectList(
                new EntityWrapper<ApplicationHeader>()
                        .eq("set_of_books_id", OrgInformationUtil.getCurrentSetOfBookId())
                        .eq("created_by", OrgInformationUtil.getCurrentUserId())
        ).stream().map(ApplicationHeader::getEmployeeId).distinct().filter(Objects::nonNull).collect(Collectors.toList());
        if (userList.size() == 0) {
            return new ArrayList<>();
        }
        return organizationService.listUsersByIds(userList);
    }

    public Page listByFinancial(Long companyId,
                                Long typeId,
                                Long employeeId,
                                Integer status,
                                Long departmentId,
                                String dateFrom,
                                String dateTo,
                                String currencyCode,
                                BigDecimal amountFrom,
                                BigDecimal amountTo,
                                BigDecimal reportAmountFrom,
                                BigDecimal reportAmountTo,
                                BigDecimal reportAbleAmountFrom,
                                BigDecimal reportAbleAmountTo,
                                ClosedTypeEnum closedFlag,
                                String remarks,
                                Page page) {
        Wrapper wrapper = new EntityWrapper<ApplicationHeader>()
                .eq("t.created_by", OrgInformationUtil.getCurrentUserId())
                .eq(typeId != null, "t.type_id", typeId)
                .eq(status != null, "t.status", status)
                .eq(employeeId != null, "t.employee_id", employeeId)
                .eq(departmentId != null, "t.department_id", departmentId)
                .ge(amountFrom != null, "t.amount", amountFrom)
                .le(amountTo != null, "t.amount", amountTo)
                .ge(dateFrom != null, "requisition_date", TypeConversionUtils.getStartTimeForDayYYMMDD(dateFrom))
                .le(dateTo != null, "requisition_date", TypeConversionUtils.getEndTimeForDayYYMMDD(dateTo))
//                .ge(reportAmountFrom != null, "amount", reportAmountFrom)
//                .le(reportAmountTo != null, "amount", reportAmountTo)
//                .ge(reportAbleAmountFrom != null, "amount", reportAbleAmountFrom)
//                .le(reportAbleAmountTo != null, "amount", reportAbleAmountTo)
                .eq(closedFlag != null, "t.closed_flag", closedFlag)
                .eq(StringUtils.hasText(currencyCode), "t.currency_code", currencyCode)
                .like(StringUtils.hasText(remarks), "t.remarks", remarks)
                .orderBy("t.id", false);
        List<ApplicationHeaderWebDTO> headers = baseMapper.listByFinancial(page, wrapper);
        setCompanyAndDepartmentAndEmployee(headers, true);
        page.setRecords(headers);
        return page;

    }


    public Wrapper<ApplicationHeader> getFDformQueryWrapper(String documentNumber,
                                                            Long typeId,
                                                            ZonedDateTime requisitionDateFrom,
                                                            ZonedDateTime requisitionDateTo,
                                                            BigDecimal amountFrom,
                                                            BigDecimal amountTo,
                                                            Integer closedFlag,
                                                            String currencyCode,
                                                            String remarks,
                                                            Long employeeId,
                                                            Long companyId) {
        Wrapper<ApplicationHeader> wrapper = new EntityWrapper<ApplicationHeader>()
                .eq(typeId != null, "t.type_id", typeId)
                .eq(closedFlag != null, "t.closed_flag", closedFlag)
                .eq(employeeId != null, "t.employee_id", employeeId)
                .ge(amountFrom != null, "t.amount", amountFrom)
                .le(amountTo != null, "t.amount", amountTo)
                .ge(requisitionDateFrom != null, "t.requisition_date", requisitionDateFrom)
                .lt(requisitionDateTo != null, "t.requisition_date", requisitionDateTo)
                .like(StringUtils.hasText(documentNumber), "t.document_number", documentNumber)
                .eq(StringUtils.hasText(currencyCode), "t.currency_code", currencyCode)
                .like(StringUtils.hasText(remarks), "t.remarks", remarks)
                .eq(companyId!=null, "t.company_id", companyId)
                .orderBy("t.id", false);
        log.debug("申请单财务查询的查询条件为：{}", wrapper.getSqlSegment());
        return wrapper;

    }
    public List<ApplicationFinancRequsetDTO> listFDformdByCondition(Page page,
                                                                    Wrapper<ApplicationHeader> wrapper,
                                                                    BigDecimal associatedAmountFrom,
                                                                    BigDecimal associatedAmountTo,
                                                                    BigDecimal relevanceAmountFrom,
                                                                    BigDecimal relevanceAmountTo){
        List<ApplicationFinancRequsetDTO> headers = baseMapper.listByfincancies(page, wrapper,associatedAmountFrom,associatedAmountTo,relevanceAmountFrom,relevanceAmountTo);
        setCompanyAndDepartmentAndEmployee1(headers);
        return headers;
    }
    private void setCompanyAndDepartmentAndEmployee1(List<ApplicationFinancRequsetDTO> headers) {
        if (!CollectionUtils.isEmpty(headers)){
            Set<Long> companyIds = new HashSet<>();
            Set<Long> departmentIds = new HashSet<>();
            Set<Long> employeeIds = new HashSet<>();
            headers.forEach(e -> {
                companyIds.add(e.getCompanyId());
                departmentIds.add(e.getDepartmentId());
                employeeIds.add(e.getEmployeeId());
                employeeIds.add(e.getCreatedBy());
            });
            // 查询公司
            Map<Long, CompanyCO> companyMap = organizationService.getCompanyMapByCompanyIds(new ArrayList<>(companyIds));
            // 查询部门
            Map<Long, DepartmentCO> departmentMap = organizationService.getDepartmentMapByDepartmentIds(new ArrayList<>(departmentIds));
            // 查询员工
            Map<Long, ContactCO> usersMap = organizationService.getUserMapByUserIds(new ArrayList<>(employeeIds));
            headers
                    .forEach(e ->{
                        if (companyMap.containsKey(e.getCompanyId())){
                            e.setCompanyName(companyMap.get(e.getCompanyId()).getName());
                        }
                        if (departmentMap.containsKey(e.getDepartmentId())){
                            e.setDepartmentName(departmentMap.get(e.getDepartmentId()).getName());
                        }
                        if(usersMap.containsKey(e.getEmployeeId())){
                            e.setEmployeeName(usersMap.get(e.getEmployeeId()).getFullName());
                        }
                        if(usersMap.containsKey(e.getCreatedBy())){
                            e.setCreatedName(usersMap.get(e.getCreatedBy()).getFullName());
                        }
                    });
        }
    }
    public void exportFormExcel(String documentNumber,
                                Long typeId,
                                ZonedDateTime requisitionDateFrom,
                                ZonedDateTime requisitionDateTo,
                                BigDecimal amountFrom,
                                BigDecimal amountTo,
                                Integer closedFlag,
                                String currencyCode,
                                String remarks,
                                Long employeeId,
                                Long companyId,
                                BigDecimal associatedAmountFrom,
                                BigDecimal associatedAmountTo,
                                BigDecimal relevanceAmountFrom,
                                BigDecimal relevanceAmountTo,
                                HttpServletResponse response,
                                HttpServletRequest request,
                                ExportConfig exportConfig) throws IOException {
        // 获取查询条件SQL
        Wrapper<ApplicationHeader> wrapper = getFDformQueryWrapper(documentNumber, typeId, requisitionDateFrom,
                requisitionDateTo, amountFrom, amountTo, closedFlag, currencyCode, remarks, employeeId, companyId);

        int total = baseMapper.getCountByCondition(wrapper);

        int availProcessors = Runtime.getRuntime().availableProcessors() / 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<ApplicationFinancRequsetDTO, ApplicationFinancRequsetDTO>() {
            @Override
            public int getTotal() {
                return total;
            }
            @Override
            public List<ApplicationFinancRequsetDTO> queryDataByPage(Page page) {

                return listFDformdByCondition(page, wrapper,associatedAmountFrom,associatedAmountTo,relevanceAmountFrom,relevanceAmountTo);
            }
            @Override
            public ApplicationFinancRequsetDTO toDTO(ApplicationFinancRequsetDTO t) {
                return t;
            }
            @Override
            public Class<ApplicationFinancRequsetDTO> getEntityClass() {
                return ApplicationFinancRequsetDTO.class;
            }
        }, availProcessors, request, response);
    }


    @Transactional(rollbackFor = Exception.class)
    //jiu.zhao 分布式锁
    //@Lock(name = SyncLockPrefix.EXP_APPLICATION, keys = "#headerId")
    public boolean closedLine(Long id, String message, Long headerId) {
        ApplicationLine line = lineService.selectById(id);
        if (null == line || ClosedTypeEnum.CLOSED.equals(line.getClosedFlag())){
            return true;
        }
        ApplicationHeader header = this.selectById(headerId);
        List<ApplicationAssociateDTO> associateDTOS = baseMapper.listAssociateInfo(Collections.singletonList(headerId));
        BigDecimal reportAmount = BigDecimal.valueOf(0L);
        if (!CollectionUtils.isEmpty(associateDTOS)){
            Map<Long, List<ApplicationAssociateDTO>> appAssociateMap = associateDTOS.stream().collect(Collectors.groupingBy(ApplicationAssociateDTO::getApplicationHeaderId));
            List<ApplicationAssociateDTO> applicationAssociateDTOS = appAssociateMap.get(headerId);
            if (!CollectionUtils.isEmpty(applicationAssociateDTOS)){
                for (int i = 0; i < applicationAssociateDTOS.size(); i++) {
                    ApplicationAssociateDTO e = applicationAssociateDTOS.get(i);
                    if (Constants.AUDIT_FLAG.equals(e.getAuditFlag())){
                        throw new BizException(RespCode.EXPENSE_APPLICATION_HEADER_CLOSE_CHECK_ERROR, new Object[]{header.getDocumentNumber(), e.getReportNumber()});
                    }
                    reportAmount = reportAmount.add(e.getAmount());
                }
            }
        }
        line.setClosedFlag(ClosedTypeEnum.CLOSED);
        BigDecimal closedAmount = line.getAmount()
                .subtract(line.getClosedAmount() == null ? BigDecimal.ZERO : line.getClosedAmount())
                .subtract(reportAmount);
        line.setClosedAmount(line.getAmount().subtract(reportAmount));
        line.setClosedFunctionalAmount(TypeConversionUtils.roundHalfUp(line.getClosedAmount().multiply(header.getExchangeRate())));

        lineService.updateById(line);
        header.setClosedAmount(header.getClosedAmount() == null ? closedAmount : header.getClosedAmount().add(closedAmount));
        header.setClosedFunctionalAmount(header.getClosedFunctionalAmount() == null ?
                TypeConversionUtils.roundHalfUp(closedAmount.multiply(header.getExchangeRate()))
                : TypeConversionUtils.roundHalfUp(closedAmount.multiply(header.getExchangeRate())).add(header.getClosedFunctionalAmount()));

        List<ApplicationLine> lines = lineService.selectList(new EntityWrapper<ApplicationLine>().eq("header_id", header.getId()));
        boolean allMatch = lines.stream().allMatch(e -> ClosedTypeEnum.CLOSED.equals(e.getClosedFlag()));
        if (!allMatch){
            header.setClosedFlag(ClosedTypeEnum.PARTIAL_CLOSED);
        }else{
            header.setClosedFlag(ClosedTypeEnum.CLOSED);
        }
        if (null != header.getBudgetFlag() && header.getBudgetFlag()) {
            // 预算释放
            releaseBudgetByClose(header, line.getId());
        }
        CommonApprovalHistoryCO historyCO = new CommonApprovalHistoryCO();
        historyCO.setEntityOid(UUID.fromString(header.getDocumentOid()));
        historyCO.setEntityType(ExpenseDocumentTypeEnum.EXP_REQUISITION.getKey());
        historyCO.setOperatorOid(OrgInformationUtil.getCurrentUserOid());
        historyCO.setOperation(7001);
        historyCO.setOperationDetail(message);
        workflowClient.saveHistory(historyCO);
        return this.updateById(header);
    }

    public List<ApplicationAssociatePrepaymentDTO> listInfoByCondition(Long prepaymentTypeId,
                                                                       Long companyId,
                                                                       Long unitId,
                                                                       Long applicantId,
                                                                       String currencyCode,
                                                                       String applicationNumber,
                                                                       String applicationType,
                                                                       Page page) {

        CashPayRequisitionTypeCO prepaymentType = prepaymentService.getPaymentRequisitionTypeById(prepaymentTypeId);
        boolean needApply = prepaymentType.getNeedApply();
        Integer allType = prepaymentType.getAllType().getId();
        Integer formBasis = prepaymentType.getApplicationFormBasis().getId();
        String sourceDocumentCategory = ExpenseDocumentTypeEnum.EXP_REQUISITION.name(); //单据大类
        Integer status = DocumentOperationEnum.APPROVAL_PASS.getId(); //单据状态 通过
        List<Long> typeIdList = null;

        if (!needApply||allType == 0||formBasis == 0) {
            return Arrays.asList();
        } else {
            Wrapper wrapper = new EntityWrapper();
            //关联公司和部门
            if (formBasis == 1) {
                wrapper.eq( "t.company_id", companyId)
                        .eq("t.department_id", unitId);
            } else if (formBasis == 2) {  //关联申请人
                wrapper.eq( "t.employee_id", applicantId);
            }
            //筛选类型id
            if (allType == 2) {
                CashPayRequisitionTypeSummaryCO cashPayRequisitionTypeSummaryCO = prepaymentService.getCashPayRequisitionTypeById(prepaymentTypeId);
                typeIdList = cashPayRequisitionTypeSummaryCO.getRequisitionTypeIdList();
            }

            List<ApplicationAssociatePrepaymentDTO> result = baseMapper.listInfoByCondition(page,wrapper,sourceDocumentCategory,
                    currencyCode,typeIdList,status,applicationNumber,applicationType);

            for(ApplicationAssociatePrepaymentDTO dto:result) {
                dto.setAssociableAmount(dto.getAmount().subtract(dto.getAssociatedAmount()));
            }

            return result;
        }
    }

    /**
     * 关联合同
     * @param contractHeaderId
     */
    public List<ApplicationHeaderWebDTO> getContractById(Long contractHeaderId, Page myPage) {

        Wrapper<ApplicationHeader> wrapper = new EntityWrapper<ApplicationHeader>()
          .eq("contract_header_id", contractHeaderId
        );
        List<ApplicationHeaderWebDTO> headers = baseMapper.listByCondition(myPage, wrapper);
        setCompanyAndDepartmentAndEmployee(headers, true);
        return headers;
    }

    /**
     * 报账单费用行关联申请单
     * @param expenseTypeId 报账单费用行费用类型ID
     * @param currencyCode  费用行币种
     * @param expReportHeaderId   此次操作的报账单
     * @param page
     * @return
     */
    public List<ApplicationHeaderAbbreviateDTO> selectApplicationAndApportionment(Long expenseTypeId,
                                                                                  String currencyCode,
                                                                                  Long expReportHeaderId,
                                                                                  String documentNumber,
                                                                                  Page page){
        ExpenseReportHeader expenseReportHeader = expenseReportHeaderService.selectById(expReportHeaderId);
        // 1.报账单关联申请相关设置
        ExpenseReportType expenseReportType = expenseReportTypeService.selectById(expenseReportHeader.getDocumentTypeId());
        ExpenseType expenseTypeById = expenseTypeService.selectById(expenseTypeId);
        //无需关联申请
        if("NO_NEED".equals(expenseTypeById.getApplicationModel())){
            return Arrays.asList();
        }
        //创建用于查询的dto
        ApplicationHeaderAbbreviateDTO dto = new ApplicationHeaderAbbreviateDTO();
        dto.setDocumentNumber(documentNumber);
        //关联申请单依据
        String applicationFormBasis = expenseReportType.getApplicationFormBasis();
        switch(applicationFormBasis){
            //1 报账单头公司=申请单头公司
            case "HEADER_COM" :
                if(TypeConversionUtils.isEmpty(expenseReportHeader.getCompanyId())){
                    return Arrays.asList();
                }
                dto.setHeaderCompanyId(expenseReportHeader.getCompanyId());
                break;
            //2 报账单头部门=申请单头部门
            case "HEADER_DEPARTMENT" :
                if(TypeConversionUtils.isEmpty(expenseReportHeader.getDepartmentId())){
                    return Arrays.asList();
                }
                dto.setHeaderDepartmentId(expenseReportHeader.getDepartmentId());
                break;
            //3 报账单头公司+头部门=申请单头公司+头部门
            case "COM_DEP" :
                if(TypeConversionUtils.isEmpty(expenseReportHeader.getCompanyId()) || TypeConversionUtils.isEmpty(expenseReportHeader.getDepartmentId())){
                    return Arrays.asList();
                }
                dto.setHeaderCompanyId(expenseReportHeader.getCompanyId());
                dto.setHeaderDepartmentId(expenseReportHeader.getDepartmentId());
                break;
            //4 报账单头申请人=申请单头申请人
            case "HEADER_EMPLOYEE" :
                if(TypeConversionUtils.isEmpty(expenseReportHeader.getApplicantId())){
                    return null;
                }
                dto.setEmployeeId(expenseReportHeader.getApplicantId());
                break;
            default:
                return Arrays.asList();
        }

        // 2.申请单分摊行的费用类型
        dto.setExpenseTypeId(expenseTypeById.getSourceTypeId());
        // 3.相同币种，申请单未完全报销
        dto.setCurrencyCode(currencyCode);

        //只涉及报账单关联
        dto.setRelatedDocumentCategory(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory());
        dto.setExpReportHeaderId(expReportHeaderId);

        dto.setSourceDocumentCategory(ExpenseDocumentTypeEnum.EXP_REQUISITION.getCategory());
        // 审批通过
        dto.setStatus(DocumentOperationEnum.APPROVAL_PASS.getId());
        List<ApplicationHeaderAbbreviateDTO> applicationHeaderDtos = baseMapper.selectRelateExpenseReportApplications(page, dto);
        applicationHeaderDtos.stream().forEach(applicationHeaderDTO -> {
            applicationHeaderDTO.setTypeName(typeService.selectById(applicationHeaderDTO.getTypeId()).getTypeName());
            ContactCO userById = organizationService.getUserById(applicationHeaderDTO.getEmployeeId());
            applicationHeaderDTO.setEmployeeCode(userById.getEmployeeCode());
            applicationHeaderDTO.setEmployeeName(userById.getFullName());
            dto.setId(applicationHeaderDTO.getId());
            List<ApplicationLineAbbreviateDTO> applicationLineAbbreviateDTOS = distService.selectByApplicationHeaderId(dto);
            applicationLineAbbreviateDTOS.stream().forEach(applicationLineAbbreviateDTO -> {
                applicationLineAbbreviateDTO.setUsableAmount(applicationLineAbbreviateDTO.getAmount().subtract(applicationLineAbbreviateDTO.getUsedAmount()));
                //设置公司信息
                CompanyCO company = organizationService.getCompanyById(applicationLineAbbreviateDTO.getCompanyId());
                if (company != null) {
                    applicationLineAbbreviateDTO.setCompanyCode(company.getCompanyCode());
                    applicationLineAbbreviateDTO.setCompanyName(company.getName());
                }
                //设置部门信息
                DepartmentCO department = organizationService.getDepartmentById(applicationLineAbbreviateDTO.getDepartmentId());
                if (department != null) {
                    applicationLineAbbreviateDTO.setDepartmentCode(department.getDepartmentCode());
                    applicationLineAbbreviateDTO.setDepartmentName(department.getName());
                }
                //设置责任中心信息
                if(applicationLineAbbreviateDTO.getResponsibilityCenterId() != null){
                    ResponsibilityCenterCO responsibilityCenterCO =
                            organizationService.getResponsibilityCenterById(applicationLineAbbreviateDTO.getResponsibilityCenterId());
                    if (responsibilityCenterCO != null) {
                        applicationLineAbbreviateDTO.setResponsibilityCenterCode(responsibilityCenterCO.getResponsibilityCenterCode());
                        applicationLineAbbreviateDTO.setResponsibilityCenterName(responsibilityCenterCO.getResponsibilityCenterName());
                    }
                }

                //费用类型
                ExpenseType expenseType = expenseTypeService.selectById(applicationLineAbbreviateDTO.getApplicationTypeId());
                if (expenseType != null) {
                    applicationLineAbbreviateDTO.setApplicationTypeName(expenseType.getName());
                }
                applicationLineAbbreviateDTO.setExpenseTypeId(expenseTypeId);
                //费用类型
                expenseType = expenseTypeService.selectById(expenseTypeId);
                if (expenseType != null) {
                    applicationLineAbbreviateDTO.setExpenseTypeName(expenseType.getName());
                }
                //维度
                DimensionUtils.setDimensionName(applicationLineAbbreviateDTO,organizationService);
            });
            applicationHeaderDTO.setLines(applicationLineAbbreviateDTOS);
        });
        return applicationHeaderDtos;
    }

    /**
     * 获取总金额，可关联金额，用于提交校验
     * @param applicationId
     * @return
     */
    public List<ApplicationAmountCO> getApplicationAmountById(Long applicationId) {
        return baseMapper.getApplicationAmountById(applicationId);
    }

    /**
     * 获取申请单关联的预付款单的单号
     */
    public List<PrepaymentRequisitionReleaseCO> getPrepaymentByDocumentNumber(String documentNumber){
        List<PrepaymentRequisitionReleaseCO>  result = baseMapper.getPrepaymentBydocumentNumber(documentNumber,new EntityWrapper<ExpenseRequisitionReqRelease>()
                .eq("err.source_doc_category", ExpenseDocumentTypeEnum.EXP_REQUISITION.name()));
        return result ;
    }

    /**
     * 根据单据id获取申请单信息
     */
    public ApplicationCO getApplicationByDocumentId(Long documentId){
        ApplicationHeaderCO header = baseMapper.getHeaderByDocumentId(documentId);
        ApplicationType applicationType = typeService.getApplicationTypeById(header.getTypeId());
        header.setTypeName(applicationType.getTypeName());
        List<ApplicationLineCO> lines = lineService.getLinesByHeaderId(documentId).stream().map(item -> {
            ApplicationLineCO lineCO = new ApplicationLineCO();
            mapperFacade.map(item, lineCO);
            return lineCO;
        }).collect(Collectors.toList());
        ApplicationCO applicationCO = new ApplicationCO();
        applicationCO.setApplicationHeader(header);
        applicationCO.setApplicationLines(lines);
        return applicationCO;
    }
}
