package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.service.SysCodeService;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.service.MessageService;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.core.web.dto.MessageDTO;
import com.hand.hcf.app.expense.application.domain.ApplicationLineDist;
import com.hand.hcf.app.expense.application.domain.ExpenseRequisitionRelease;
import com.hand.hcf.app.expense.application.service.ApplicationLineDistService;
import com.hand.hcf.app.expense.application.service.ExpenseRequisitionReleaseService;
import com.hand.hcf.app.expense.book.domain.ExpenseBook;
import com.hand.hcf.app.expense.book.service.ExpenseBookService;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.dto.BudgetCheckResultDTO;
import com.hand.hcf.app.expense.common.externalApi.*;
import com.hand.hcf.app.expense.common.service.CommonService;
import com.hand.hcf.app.expense.common.utils.ParameterConstant;
import com.hand.hcf.app.expense.common.utils.PolicyCheckConstant;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.invoice.domain.*;
import com.hand.hcf.app.expense.invoice.dto.InvoiceDTO;
import com.hand.hcf.app.expense.invoice.service.*;
import com.hand.hcf.app.expense.policy.dto.DynamicFieldDTO;
import com.hand.hcf.app.expense.policy.dto.ExpensePolicyMatchDimensionDTO;
import com.hand.hcf.app.expense.policy.dto.PolicyCheckResultDTO;
import com.hand.hcf.app.expense.policy.service.ExpensePolicyService;
import com.hand.hcf.app.expense.report.domain.*;
import com.hand.hcf.app.expense.report.dto.ExpenseReportAutoCreateDTO;
import com.hand.hcf.app.expense.report.dto.ExpenseReportHeaderDTO;
import com.hand.hcf.app.expense.report.dto.ExpenseReportInvoiceMatchResultDTO;
import com.hand.hcf.app.expense.report.dto.ExpenseReportTypeDTO;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportHeaderMapper;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.domain.ExpenseDocumentField;
import com.hand.hcf.app.expense.type.domain.ExpenseField;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.service.ExpenseDimensionService;
import com.hand.hcf.app.expense.type.service.ExpenseDocumentFieldService;
import com.hand.hcf.app.expense.type.service.ExpenseFieldService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

//import com.codingapi.txlcn.tc.annotation.LcnTransaction;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 14:39
 * @remark
 */
@Service
@Slf4j
public class ExpenseReportHeaderService extends BaseService<ExpenseReportHeaderMapper, ExpenseReportHeader> {

    @Autowired
    private ExpenseDimensionService expenseDimensionService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ExpenseReportLineService expenseReportLineService;

    @Autowired
    private ExpenseReportDistService expenseReportDistService;

    @Autowired
    private ExpenseReportTaxDistService expenseReportTaxDistService;

    @Autowired
    private ExpenseReportPaymentScheduleService expenseReportPaymentScheduleService;

    @Autowired
    private ExpenseDocumentFieldService documentFieldService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ExpenseReportTypeService expenseReportTypeService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ExpenseRequisitionReleaseService expenseRequisitionReleaseService;

    @Autowired
    private WorkBenchService workBenchService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private WorkflowControllerImpl workflowClient;

    //@Value("${spring.application.name:}")
    private String applicationName;

    @Autowired
    private InvoiceLineExpenceService invoiceLineExpenceService;

    @Autowired
    private InvoiceLineDistService invoiceLineDistService;

    @Autowired
    private InvoiceHeadService invoiceHeadService;

    @Autowired
    private InvoiceLineService invoiceLineService;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ApplicationLineDistService applicationLineDistService;

    @Autowired
    private AccountingService accountingService;
    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private InvoiceExpenseTypeRulesService invoiceExpenseTypeRulesService;

    @Autowired
    private SysCodeService sysCodeService;

    @Autowired
    private ExpenseTypeService expenseTypeService;

    @Autowired
    private ExpenseBookService expenseBookService;

    @Autowired
    private ExpenseFieldService expenseFieldService;

    @Autowired
    private ExpensePolicyService expensePolicyService;

    /**
     * 保存费用类型
     *
     * @param expenseReportHeaderDTO
     * @param expenseReportType
     * @param autoCreate  是否为系统自动生成
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExpenseReportHeader saveExpenseReportHeader(ExpenseReportHeaderDTO expenseReportHeaderDTO, ExpenseReportType expenseReportType, Boolean autoCreate) {
        if(expenseReportType == null){
            expenseReportType = expenseReportTypeService.selectById(expenseReportHeaderDTO.getDocumentTypeId());
        }
        if(BooleanUtils.isNotTrue(autoCreate)){
            if (!expenseReportType.getMultiPayee()) {
                if (expenseReportHeaderDTO.getAccountNumber() == null || expenseReportHeaderDTO.getAccountName() == null
                        || expenseReportHeaderDTO.getPayeeId() == null || expenseReportHeaderDTO.getPayeeCategory() == null) {
                    throw new BizException(RespCode.EXPENSE_REPORT_ACCOUNT_INFO_IS_NOT_COMPLETE);
                }
            }
            if (BooleanUtils.isTrue(expenseReportType.getAssociateContract()) && BooleanUtils.isTrue(expenseReportType.getContractRequired())) {
                if (expenseReportHeaderDTO.getContractHeaderId() == null) {
                    throw new BizException(RespCode.EXPENSE_REPORT_CONTRACT_IS_NULL);
                }
            }
        }
        ExpenseReportHeader expenseReportHeader = new ExpenseReportHeader();
        //设置默认值
        if (expenseReportHeaderDTO.getId() == null) {
            expenseReportHeaderDTO.setTenantId(
                    expenseReportHeaderDTO.getTenantId() != null ? expenseReportHeaderDTO.getTenantId() : OrgInformationUtil.getCurrentTenantId());
            expenseReportHeaderDTO.setSetOfBooksId(
                    expenseReportHeaderDTO.getSetOfBooksId() != null ? expenseReportHeaderDTO.getSetOfBooksId() : OrgInformationUtil.getCurrentSetOfBookId());
            expenseReportHeaderDTO.setRequisitionNumber(
                    commonService.getCoding(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory(), expenseReportHeaderDTO.getCompanyId(), null));
            expenseReportHeaderDTO.setTotalAmount(BigDecimal.ZERO);
            expenseReportHeaderDTO.setFunctionalAmount(BigDecimal.ZERO);
            expenseReportHeaderDTO.setRequisitionDate(
                    expenseReportHeaderDTO.getRequisitionDate() == null ? ZonedDateTime.now() : expenseReportHeaderDTO.getRequisitionDate());
            expenseReportHeaderDTO.setStatus(DocumentOperationEnum.GENERATE.getId());
            expenseReportHeaderDTO.setAuditFlag("N");
            expenseReportHeaderDTO.setDocumentOid(UUID.randomUUID().toString());
            expenseReportHeaderDTO.setJeCreationStatus(false);
        } else {
            //校验状态
            checkDocumentStatus(0, expenseReportHeaderDTO.getStatus());
        }
        // 更新计划付款行信息
        if (expenseReportHeaderDTO.getId() != null) {
            updateScheduleLine(selectById(expenseReportHeaderDTO.getId()), expenseReportHeaderDTO);
        }
        BeanUtils.copyProperties(expenseReportHeaderDTO, expenseReportHeader);
        insertOrUpdate(expenseReportHeader);
        // 维护维度布局
        List<ExpenseDimension> expenseDimensions = expenseReportHeaderDTO.getExpenseDimensions();
        if (CollectionUtils.isNotEmpty(expenseDimensions)) {
            expenseDimensions.stream().forEach(e -> {
                e.setHeaderId(expenseReportHeader.getId());
                e.setDocumentType(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getKey());
            });
            expenseDimensionService.insertOrUpdateBatch(expenseDimensions);
        }
        return expenseReportHeader;
    }

    /**
     * 动态修改计划付款行信息
     *
     * @param oldExpenseReportHeader
     * @param expenseReportDTO
     */
    private void updateScheduleLine(ExpenseReportHeader oldExpenseReportHeader, ExpenseReportHeaderDTO expenseReportDTO) {
        List<ExpenseReportPaymentSchedule> paymentScheduleList =
                expenseReportPaymentScheduleService.selectList(new EntityWrapper<ExpenseReportPaymentSchedule>()
                        .eq("exp_report_header_id", oldExpenseReportHeader.getId()));
        if (CollectionUtils.isNotEmpty(paymentScheduleList)) {
            //收款方变更
            boolean accountChange = (!StringUtils.isEmpty(oldExpenseReportHeader.getAccountNumber()) &&
                    !oldExpenseReportHeader.getAccountNumber().equals(expenseReportDTO.getAccountNumber())) ||
                    (!(oldExpenseReportHeader.getPayeeCategory() == null ? "" : oldExpenseReportHeader.getPayeeCategory()).equals(expenseReportDTO.getPayeeCategory())
                            || oldExpenseReportHeader.getPayeeId() != expenseReportDTO.getPayeeId());
            //合同变更
            boolean contractChange = (!StringUtils.isEmpty(oldExpenseReportHeader.getContractHeaderId()) &&
                    !oldExpenseReportHeader.getContractHeaderId().equals(expenseReportDTO.getContractHeaderId()))
                    || (StringUtils.isEmpty(oldExpenseReportHeader.getContractHeaderId()) && !StringUtils.isEmpty(expenseReportDTO.getContractHeaderId()));
            //收款方变动
            if (accountChange) {
                paymentScheduleList.forEach(schedule -> {
                    schedule.setPayeeCategory(expenseReportDTO.getPayeeCategory());
                    schedule.setPayeeId(expenseReportDTO.getPayeeId());
                    schedule.setAccountNumber(expenseReportDTO.getAccountNumber());
                    schedule.setAccountName(expenseReportDTO.getAccountName());
                    schedule.setConPaymentScheduleLineId(null);
                });
            }
            //收款方未变动，合同头变动
            if (!accountChange && contractChange) {
                paymentScheduleList.forEach(schedule -> {
                    schedule.setConPaymentScheduleLineId(null);
                });
            }
            //收款方未变动或者合同头变动才执行更新
            if (accountChange || contractChange) {
                expenseReportPaymentScheduleService.updateAllColumnBatchById(paymentScheduleList);
            }
        }
    }

    /**
     * 根据ID删除报账单
     *
     * @param headerId
     */
    @Transactional(rollbackFor = Exception.class)
   //@SyncLock(lockPrefix = SyncLockPrefix.PUBLIC_REPORT)
    public void deleteExpenseReportHeaderById(Long headerId) {
        ExpenseReportHeader expenseReportHeader = selectById(headerId);
        if (expenseReportHeader != null) {
            // 判断单据状态 非编辑中、撤回、拒绝的单据，都不能删除
            if (!(expenseReportHeader.getStatus().equals(DocumentOperationEnum.GENERATE.getId())
                    || expenseReportHeader.getStatus().equals(DocumentOperationEnum.WITHDRAW.getId())
                    || expenseReportHeader.getStatus().equals(DocumentOperationEnum.APPROVAL_REJECT.getId())
                    || expenseReportHeader.getStatus().equals(DocumentOperationEnum.CANCEL.getId()))) {
                throw new BizException(RespCode.EXPENSE_REPORT_CANNOT_DELETED, new String[]{expenseReportHeader.getRequisitionNumber()});
            }
            // 删除field
            documentFieldService.delete(new EntityWrapper<ExpenseDocumentField>()
                    .eq("header_id", headerId)
                    .eq("document_type", ExpenseDocumentTypeEnum.PUBLIC_REPORT.getKey()));
            // 删除维度
            expenseDimensionService.delete(new EntityWrapper<ExpenseDimension>()
                    .eq("header_id", headerId)
                    .eq("document_type", ExpenseDocumentTypeEnum.PUBLIC_REPORT.getKey()));
            // 删除头信息
            deleteById(headerId);
            // 删除行信息
            expenseReportLineService.deleteExpenseReportLineByHeaderId(headerId);
            // 删除分摊行信息
            expenseReportDistService.deleteExpenseReportDistByHeaderId(headerId);
            // 删除税金分摊行信息
            expenseReportTaxDistService.deleteExpenseReportTaxDistByHeaderId(headerId);
            // 删除计划付款行信息
            expenseReportPaymentScheduleService.deleteExpenseReportPaymentScheduleByHeaderId(headerId);
            //删除审批流实例
            Integer entityType = ExpenseDocumentTypeEnum.PUBLIC_REPORT.getKey();
            UUID entityOid = UUID.fromString(expenseReportHeader.getDocumentOid());
            workflowClient.deleteApprovalDocument(entityType, entityOid);
        }
    }

    /**
     * 获取单据头信息
     *
     * @param headerId
     * @return
     */
    public ExpenseReportHeaderDTO getExpenseReportById(Long headerId) {
        ExpenseReportHeader expenseReportHeader = selectById(headerId);
        ExpenseReportHeaderDTO expenseReportHeaderDTO = new ExpenseReportHeaderDTO();
        BeanUtils.copyProperties(expenseReportHeader, expenseReportHeaderDTO);
        List<ExpenseDimension> expenseDimensions =
                expenseDimensionService.listDimensionByHeaderIdAndType(headerId, ExpenseDocumentTypeEnum.PUBLIC_REPORT.getKey(), null);
        expenseReportHeaderDTO.setExpenseDimensions(expenseDimensions);
        setExpenseReportHeaderDto(expenseReportHeaderDTO);

        List<ExpenseReportPaymentSchedule> paymentScheduleList = expenseReportPaymentScheduleService.selectList(
                new EntityWrapper<ExpenseReportPaymentSchedule>()
                        .eq("exp_report_header_id", headerId));
        BigDecimal totalMoney = paymentScheduleList.stream().map(ExpenseReportPaymentSchedule::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        expenseReportHeaderDTO.setPaymentTotalAmount(totalMoney);

        return expenseReportHeaderDTO;
    }

    /**
     * 我的报账单
     *
     * @param documentTypeId
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param applicantId
     * @param status
     * @param amountFrom
     * @param amountTo
     * @param remark
     * @param page
     * @return
     */
    public List<ExpenseReportHeader> getMyExpenseReports(Long documentTypeId,
                                                         ZonedDateTime requisitionDateFrom,
                                                         ZonedDateTime requisitionDateTo,
                                                         Long applicantId,
                                                         Long demanderId,
                                                         Integer status,
                                                         Long companyId,
                                                         BigDecimal amountFrom,
                                                         BigDecimal amountTo,
                                                         String remark,
                                                         String requisitionNumber,
                                                         Boolean editor,
                                                         Boolean passed,
                                                         Page page) {
        Long currentUserId = OrgInformationUtil.getCurrentUserId();
        Wrapper<ExpenseReportHeader> wrapper =new EntityWrapper<ExpenseReportHeader>()
                .eq("created_by", currentUserId)
                .eq(documentTypeId != null, "document_type_id", documentTypeId)
                .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                .eq(applicantId != null, "applicant_id", applicantId)
                .eq(demanderId != null, "demander_id", demanderId)
                .eq(companyId != null, "company_id", companyId)
                .ge(amountFrom != null, "total_amount", amountFrom)
                .le(amountTo != null, "total_amount", amountTo)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(remark), "description", remark)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(requisitionNumber), "requisition_number", requisitionNumber)
                .orderBy("requisition_number", false);
        if (editor) {
            wrapper = wrapper.in("status", "1001,1003,1005,2001");
        } else  if (passed) {
            wrapper = wrapper.in("status", "1004,2002");
        } else {
            wrapper = wrapper.eq(status != null, "status", status);
        }
        List<ExpenseReportHeader> expenseReportHeaders = baseMapper.selectPage(page, wrapper);

        //更加id或code取name
        expenseReportHeaders.stream().forEach(expenseReportHeader -> {
            //申请人
            ContactCO applicantById = organizationService.getUserById(expenseReportHeader.getApplicantId());
            expenseReportHeader.setApplicantCode(applicantById.getEmployeeCode());
            expenseReportHeader.setApplicantName(applicantById.getFullName());
            //需求方
            ContactCO demanderById = organizationService.getUserById(expenseReportHeader.getDemanderId());
            expenseReportHeader.setDemanderCode(demanderById.getEmployeeCode());
            expenseReportHeader.setDemanderName(demanderById.getFullName());
            //公司
            CompanyCO companyById = organizationService.getCompanyById(expenseReportHeader.getCompanyId());
            expenseReportHeader.setCompanyCode(companyById.getCompanyCode());
            expenseReportHeader.setCompanyName(companyById.getName());
            // 单据类型
            ExpenseReportType expenseReportType = expenseReportTypeService.selectById(expenseReportHeader.getDocumentTypeId());
            expenseReportHeader.setDocumentTypeName(expenseReportType.getReportTypeName());
            expenseReportHeader.setFormId(expenseReportType.getFormId());
            ApprovalFormCO approvalFormById = organizationService.getApprovalFormById(expenseReportType.getFormId());
            expenseReportHeader.setFormOid(approvalFormById.getFormOid());
        });
        return expenseReportHeaders;
    }

    /**
     * 调用预算模块预算校验接口
     *
     * @param expenseReportHeader 报账单头信息
     * @return
     */
    public BudgetCheckResultDTO checkBudget(ExpenseReportHeader expenseReportHeader,
                                            Boolean ignoreWarningFlag,
                                            String expTaxDist) {
        BudgetCheckMessageCO param = new BudgetCheckMessageCO();
        List<BudgetReserveCO> budgetReserveDtoList;
        List<ExpenseReportDist> expenseReportDistList = expenseReportDistService.selectList(new EntityWrapper<ExpenseReportDist>().eq("exp_report_header_id", expenseReportHeader.getId()));
        //jiu.zhao 预算
        //budgetReserveDtoList = expenseReportDistToBudgetReserveCO(expenseReportHeader,expenseReportDistList, expTaxDist);
        param.setTenantId(expenseReportHeader.getTenantId());
        param.setSetOfBooksId(expenseReportHeader.getSetOfBooksId());
        //param.setBudgetReserveDtoList(budgetReserveDtoList);
        param.setIgnoreWarningFlag(BooleanUtils.isTrue(ignoreWarningFlag) ? "Y" : "N");
        param.setIncludeReleaseFlag("Y");
        return commonService.checkBudget(param);
    }

    /**
     * expenseReportDist转换为BudgetReserveCO
     *
     * @param expenseReportDistList
     * @return
     */
    //jiu.zhao 预算
    /*private List<BudgetReserveCO> expenseReportDistToBudgetReserveCO(ExpenseReportHeader expenseReportHeader,
                                                                     List<ExpenseReportDist> expenseReportDistList,
                                                                     String expTaxDist){
        List<BudgetReserveCO> budgetReserveCOList = new ArrayList<>();
        for (ExpenseReportDist expenseReportDist : expenseReportDistList) {
            BudgetReserveCO budgetReserveCO = new BudgetReserveCO();
            budgetReserveCO.setCompanyId(expenseReportDist.getCompanyId());
            budgetReserveCO.setCompanyCode(organizationService.getCompanyById(expenseReportDist.getCompanyId()).getCompanyCode());
            budgetReserveCO.setBusinessType(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory());
            budgetReserveCO.setReserveFlag("U");
            budgetReserveCO.setStatus("N");
            budgetReserveCO.setManualFlag("N");
            budgetReserveCO.setDocumentId(expenseReportDist.getExpReportHeaderId());
            budgetReserveCO.setDocumentLineId(expenseReportDist.getId());
            budgetReserveCO.setCurrency(expenseReportDist.getCurrencyCode());
            budgetReserveCO.setExchangeRate(expenseReportDist.getExchangeRate().doubleValue());
            if(ParameterConstant.TAX_IN.equals(expTaxDist)){
                budgetReserveCO.setAmount(expenseReportDist.getAmount());
                budgetReserveCO.setFunctionalAmount(expenseReportDist.getFunctionAmount());
            }else if(ParameterConstant.TAX_OFF.equals(expTaxDist)){
                budgetReserveCO.setAmount(expenseReportDist.getNoTaxDistAmount());
                budgetReserveCO.setFunctionalAmount(expenseReportDist.getNoTaxDistFunctionAmount());
            }
            budgetReserveCO.setQuantity(1);
            budgetReserveCO.setUnitId(expenseReportDist.getDepartmentId());
            budgetReserveCO.setUnitCode(organizationService.getDepartmentById(expenseReportDist.getDepartmentId()).getDepartmentCode());
            budgetReserveCO.setEmployeeId(expenseReportHeader.getApplicantId());
            budgetReserveCO.setEmployeeCode(organizationService.getUserById(expenseReportHeader.getApplicantId()).getEmployeeCode());
            if(expenseReportDist.getResponsibilityCenterId() != null){
                budgetReserveCO.setResponsibilityCenterId(expenseReportDist.getResponsibilityCenterId());
                budgetReserveCO.setResponsibilityCenterCode(
                        organizationService.getResponsibilityCenterById(expenseReportDist.getResponsibilityCenterId()).getResponsibilityCenterCode());
            }
            budgetReserveCO.setCreatedBy(expenseReportDist.getCreatedBy());
            DimensionUtils.setDimensionMessage(expenseReportDist,budgetReserveCO,organizationService,true,true,false);
            budgetReserveCO.setDocumentItemSourceType("EXPENSE_TYPE");
            budgetReserveCO.setDocumentItemSourceId(expenseReportDist.getExpenseTypeId());
            // 申请单信息
            List<ExpenseRequisitionRelease> expenseRequisitionReleaseByRelatedDocumentMsg = expenseRequisitionReleaseService.getExpenseRequisitionReleaseByRelatedDocumentMsg(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory(),
                    expenseReportDist.getExpReportHeaderId(),
                    null,
                    expenseReportDist.getId());
            if(CollectionUtils.isNotEmpty(expenseRequisitionReleaseByRelatedDocumentMsg)){
                BudgetReportRequisitionReleaseCO budgetReportRequisitionReleaseCO = expenseRequisitionReleaseByRelatedDocumentMsg.stream().map(e -> {
                    BudgetReportRequisitionReleaseCO releaseCO = new BudgetReportRequisitionReleaseCO();
                    releaseCO.setReleaseBusinessType(ExpenseDocumentTypeEnum.EXP_REQUISITION.getCategory());
                    releaseCO.setReleaseDocumentId(e.getSourceDocumentId());
                    releaseCO.setReleaseDocumentLineId(e.getSourceDocumentDistId());
                    releaseCO.setReleaseID(e.getId());
                    return releaseCO;
                }).collect(Collectors.toList()).get(0);
                budgetReserveCO.setReleaseMsg(budgetReportRequisitionReleaseCO);
            }
            // 期间信息
            ZonedDateTime periodDate = ZonedDateTime.now();
            String parameterValue = organizationService.getParameterValue(expenseReportDist.getCompanyId(),
                    expenseReportDist.getSetOfBooksId(), ParameterConstant.BGT_OCCUPY_DATE);
            if (ParameterConstant.EXPENSE_DATE.equals(parameterValue)) {
                periodDate = expenseReportHeader.getRequisitionDate();
            }
            PeriodCO period = organizationService.getPeriodsByIDAndTime(expenseReportDist.getSetOfBooksId(), DateUtil.ZonedDateTimeToString(periodDate));
            //会计期间名称
            budgetReserveCO.setPeriodName(period.getPeriodName());
            budgetReserveCO.setPeriodNumber(period.getPeriodNum());
            budgetReserveCO.setPeriodQuarter(period.getQuarterNum());
            budgetReserveCO.setPeriodYear(period.getPeriodYear());
            budgetReserveCOList.add(budgetReserveCO);

        }
        return budgetReserveCOList;
    }*/

    /**
     * 费用对接工作台
     *
     * @param expenseReportHeader
     */
    public void pushToWorkBranchByHeaderId(ExpenseReportHeader expenseReportHeader) {
        if (expenseReportHeader == null) {
            throw new BizException(RespCode.EXPENSE_REPORT_HEADER_IS_NUTT);
        }
        List<ExpenseReportLine> reportLineList = expenseReportLineService.selectList(new EntityWrapper<ExpenseReportLine>()
                .eq("exp_report_header_id", expenseReportHeader.getId()));
        BusinessDataCO businessDataCO = new BusinessDataCO();
        businessDataCO.setOtherLines(null);
        businessDataCO.setTenantId(expenseReportHeader.getTenantId());
        businessDataCO.setSobId(expenseReportHeader.getSetOfBooksId());
        businessDataCO.setBusinessTypeCode("EXP_REPORT");
        businessDataCO.setDocumentId(expenseReportHeader.getId());
        businessDataCO.setDocumentNumber(expenseReportHeader.getRequisitionNumber());
        businessDataCO.setCompanyId(expenseReportHeader.getCompanyId());
        CompanyCO company = organizationService.getCompanyById(expenseReportHeader.getCompanyId());
        if (company != null) {
            businessDataCO.setCompanyCode(company.getCompanyCode());
        }
        businessDataCO.setDepartmentId(expenseReportHeader.getDepartmentId());
        DepartmentCO department = organizationService.getDepartmentById(expenseReportHeader.getDepartmentId());
        if (department != null) {
            businessDataCO.setDepartmentCode(department.getDepartmentCode());
        }
        businessDataCO.setEmployeeId(expenseReportHeader.getApplicantId());
        businessDataCO.setCurrencyCode(expenseReportHeader.getCurrencyCode());
        businessDataCO.setExchangeRate(expenseReportHeader.getExchangeRate());
        businessDataCO.setAmount(expenseReportHeader.getTotalAmount());
        businessDataCO.setFunctionalAmount(expenseReportHeader.getFunctionalAmount());
        businessDataCO.setDocumentTypeId(expenseReportHeader.getDocumentTypeId());
        businessDataCO.setDocumentCategory(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getKey().toString());
        businessDataCO.setDocumentOid(expenseReportHeader.getDocumentOid());
        //workBenchService.pushReportDataToWorkBranch(businessDataCO);
    }

    /**
     * 费用对接支付
     *
     * @param expenseReportHeader
     */
    public void saveDataToPayment(ExpenseReportHeader expenseReportHeader) {
        if (expenseReportHeader == null) {
            throw new BizException(RespCode.EXPENSE_REPORT_HEADER_IS_NUTT);
        }
        List<ExpenseReportPaymentSchedule> expenseReportPaymentSchedules = expenseReportPaymentScheduleService.selectList(new EntityWrapper<ExpenseReportPaymentSchedule>()
                .eq("exp_report_header_id", expenseReportHeader.getId()).orderBy("created_date"));
        if (CollectionUtils.isEmpty(expenseReportPaymentSchedules)) {
            return;
        }
        List<CashTransactionDataCreateCO> result = new ArrayList<>();
        expenseReportPaymentSchedules.forEach(paymentSchedule -> {
            CashTransactionDataCreateCO co = new CashTransactionDataCreateCO();
            co.setTenantId(paymentSchedule.getTenantId());
            co.setDocumentCategory(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory());
            co.setDocumentHeaderId(paymentSchedule.getExpReportHeaderId());
            co.setDocumentTypeId(expenseReportHeader.getDocumentTypeId());
            co.setDocumentNumber(expenseReportHeader.getRequisitionNumber());
            co.setEmployeeId(paymentSchedule.getApplicantId());
            ContactCO contact = organizationService.getUserById(expenseReportHeader.getApplicantId());
            if (contact != null) {
                co.setEmployeeName(contact.getFullName());
            }
            co.setDocumentLineId(paymentSchedule.getId());
            co.setRequisitionDate(paymentSchedule.getCreatedDate());
            co.setCompanyId(paymentSchedule.getCompanyId());
            co.setPaymentCompanyId(paymentSchedule.getCompanyId());
            co.setAmount(paymentSchedule.getAmount());
            co.setCurrency(paymentSchedule.getCurrencyCode());
            co.setExchangeRate(paymentSchedule.getExchangeRate().doubleValue());
            co.setPartnerCategory(paymentSchedule.getPayeeCategory());
            co.setPartnerId(paymentSchedule.getPayeeId());
            //收款对象
            String payeeName = null;
            String payeeCode = null;
            if (paymentSchedule.getPayeeCategory() != null) {
                if (paymentSchedule.getPayeeCategory().equals("EMPLOYEE")) {
                    ContactCO user = organizationService.getUserById(paymentSchedule.getPayeeId());
                    if (user != null) {
                        payeeName = user.getFullName();
                        payeeCode = user.getEmployeeCode();
                    }
                } else if (paymentSchedule.getPayeeCategory().equals("VENDER")) {
                    VendorInfoCO info = organizationService.getOneVendorInfoById(paymentSchedule.getPayeeId());
                    if (info != null) {
                        payeeCode = info.getVenderCode();
                        payeeName = info.getVenNickname();
                    }
                }
            }
            co.setPartnerCode(payeeCode);
            co.setPartnerName(payeeName);
            co.setAccountNumber(paymentSchedule.getAccountNumber());
            co.setAccountName(paymentSchedule.getAccountName());
            //现金事务类型代码
            co.setCshTransactionTypeCode("PAYMENT");
            co.setCshTransactionClassId(paymentSchedule.getCshTransactionClassId());
            co.setCshFlowItemId(paymentSchedule.getCashFlowItemId());
            co.setContractHeaderId(expenseReportHeader.getContractHeaderId());
            co.setPaymentMethodCategory(paymentSchedule.getPaymentMethod());
            co.setPaymentType(paymentSchedule.getPaymentType());
            co.setPropFlag(paymentSchedule.getPropFlag());
            co.setRemark(paymentSchedule.getDescription());
            co.setFrozenFlag("Y".equals(paymentSchedule.getFrozenFlag()) ? true : false);
            //单据OID
            co.setEntityOid(expenseReportHeader.getDocumentOid());
            //实体类型
            co.setEntityType(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getKey());
            co.setRequisitionPaymentDate(paymentSchedule.getPaymentScheduleDate());
            result.add(co);
        });
        //jiu.zhao 支付
        //paymentService.saveDataToPayment(result);
    }


    /**
     * 校验单据状态
     *
     * @param operateType 操作类型
     * @param status      单据状态
     */
    public void checkDocumentStatus(Integer operateType, Integer status) {
        switch (operateType) {
            //点击删除
            case -1:
                if (!status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.GENERATE.getId()) && !status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.APPROVAL_REJECT.getId())
                        && !status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.CANCEL.getId()) && !status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.WITHDRAW.getId())) {
                    throw new BizException(RespCode.EXPENSE_REPORT_STATUS_ERROR);
                }
                break;
            //更改
            case 0:
                if (!status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.GENERATE.getId()) && !status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.APPROVAL_REJECT.getId())
                        && !status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.CANCEL.getId()) && !status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.WITHDRAW.getId())) {
                    throw new BizException(RespCode.EXPENSE_REPORT_STATUS_ERROR);
                }
                break;
            // 提交 至审核中
            case 1002:
                if (!status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.GENERATE.getId()) && !status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.APPROVAL_REJECT.getId())
                        && !status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.CANCEL.getId()) && !status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.WITHDRAW.getId())
                        && !status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.HOLD.getId())) {
                    throw new BizException(RespCode.EXPENSE_REPORT_STATUS_ERROR);
                }
                break;
            // 审批通过
            case 1004:
                if (!status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.EXPENSE_REPORT_STATUS_ERROR);
                }
                break;

            // 撤回
            case 1003:
                if (!status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.EXPENSE_REPORT_STATUS_ERROR);
                }
                break;
            // 审批驳回
            case 1005:
                if (!status.equals(com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.EXPENSE_REPORT_STATUS_ERROR);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 根据报账单单据头ID校验费用政策
     *
     * @param id
     * @return
     */
    public PolicyCheckResultDTO checkPolicy(Long id) {

        ExpenseReportHeader header = this.selectById(id);
        if (header == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        List<ExpenseReportLine> lines = expenseReportLineService.getExpenseReportLinesByHeaderId(id);

        PolicyCheckResultDTO waringPolicyResult = null;

        for (ExpenseReportLine line : lines) {
            List<ExpenseDocumentField> fieldList = documentFieldService.selectList(new EntityWrapper<ExpenseDocumentField>()
                    .eq("header_id", header.getId())
                    .eq("line_id", line.getId())
                    .eq("document_type", 801001)
            );
            List<DynamicFieldDTO> dynamicFields = new ArrayList<>();
            fieldList.forEach(f -> {
                        ExpenseField expenseField = expenseFieldService.selectOne(
                                new EntityWrapper<ExpenseField>().eq("field_oid", f.getFieldOid())
                        );
                        dynamicFields.add(
                                DynamicFieldDTO
                                        .builder()
                                        .fieldId(expenseField.getId())
                                        .fieldName(f.getName())
                                        .fieldDataType(f.getFieldDataType())
                                        .fieldValue(f.getValue())
                                        .fieldTypeId(f.getFieldTypeId())
                                        .build()
                        );
                    }
            );
            ExpensePolicyMatchDimensionDTO matchDimensionDTO = ExpensePolicyMatchDimensionDTO
                    .builder()
                    //0为申请政策，1为报销政策
                    .expenseTypeFlag(1)
                    .companyId(header.getCompanyId())
                    .applicationId(header.getApplicantId())
                    .currencyCode(line.getCurrencyCode())
                    .expenseTypeId(line.getExpenseTypeId())
                    .amount(line.getAmount())
                    .quantity(line.getQuantity())
                    .price(line.getPrice())
                    .dynamicFields(dynamicFields)
                    .build();
            PolicyCheckResultDTO result = expensePolicyService.checkExpensePolicy(matchDimensionDTO);
            //禁止
            if (PolicyCheckConstant.CONTROL_STRATEGY_CODE_FORBIDDEN.equals(result.getCode())) {
                return result;
            }
            if (waringPolicyResult == null && PolicyCheckConstant.CONTROL_STRATEGY_CODE_WARNING.equals(result.getCode())) {
                waringPolicyResult = result;
            }
        }

        if (waringPolicyResult != null) {
            //警告
            return waringPolicyResult;
        } else {
            //通过
            return PolicyCheckResultDTO.ok();
        }
    }


    /**
     * 报账单提交
     *
     * @param workFlowDocumentRef 工作流信息
     * @param ignoreWarningFlag   是否忽略警告标志
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    //@LcnTransaction
   //@SyncLock(lockPrefix = SyncLockPrefix.PUBLIC_REPORT)
    public BudgetCheckResultDTO submit( WorkFlowDocumentRefCO workFlowDocumentRef,
                                       Boolean ignoreWarningFlag) {

        ExpenseReportHeader expenseReportHeader = selectById(workFlowDocumentRef.getDocumentId());
        if (expenseReportHeader == null) {
            throw new BizException(RespCode.EXPENSE_REPORT_HEADER_IS_NUTT);
        }
        //税金分摊方式
        String expTaxDist = organizationService.getParameterValue(expenseReportHeader.getCompanyId(),
                expenseReportHeader.getSetOfBooksId(), ParameterConstant.EXP_TAX_DIST);
        //校验状态
        checkDocumentStatus(DocumentOperationEnum.APPROVAL.getId(), expenseReportHeader.getStatus());
        expenseReportHeader.setStatus(DocumentOperationEnum.APPROVAL.getId());
        ExpenseReportTypeDTO expenseReportType = expenseReportTypeService.getExpenseReportType(expenseReportHeader.getDocumentTypeId(), expenseReportHeader.getId());
        if (BooleanUtils.isTrue(expenseReportType.getAssociateContract()) && BooleanUtils.isTrue(expenseReportType.getContractRequired())) {
            if (expenseReportHeader.getContractHeaderId() == null) {
                throw new BizException(RespCode.EXPENSE_REPORT_CONTRACT_IS_NULL);
            }
        }
        //计划付款行校验
//        List<ExpenseReportPaymentSchedule> expenseReportPaymentSchedules = checkPaymentSchedule(expenseReportHeader);
        //校验费用行及分摊行
//        checkExpenseReportLineAndDist(expenseReportHeader);
        //关联申请单校验
//        checkReleaseRequisition(expenseReportHeader, expTaxDist);
        // 发送合同信息
//        sendContract(expenseReportHeader, expenseReportPaymentSchedules);
        //核销记录生效
        //jiu.zhao 支付
        //paymentService.saveWriteOffTakeEffect(ExpenseDocumentTypeEnum.PUBLIC_REPORT.name(),expenseReportHeader.getId(),Arrays.asList(),LoginInformationUtil.getCurrentUserId());

        //校验预算
        BudgetCheckResultDTO budgetCheckResultDTO;

        if (BooleanUtils.isTrue(expenseReportType.getBudgetFlag())) {
            // 只有当启用预算
            budgetCheckResultDTO = checkBudget(
                    expenseReportHeader, ignoreWarningFlag, expTaxDist);
        } else {
            budgetCheckResultDTO = BudgetCheckResultDTO.ok();
        }
        if (budgetCheckResultDTO.getPassFlag()) {
            // 发送工作流并更新单据状态
            sendWorkFlow(expenseReportHeader, expenseReportType, workFlowDocumentRef);
        } else {
            //校验失败，回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return budgetCheckResultDTO;
    }

    private void checkExpenseReportLineAndDist(ExpenseReportHeader expenseReportHeader) {
        List<ExpenseReportLine> expenseReportLines = expenseReportLineService.selectList(new EntityWrapper<ExpenseReportLine>().eq("exp_report_header_id", expenseReportHeader.getId()));
        if (CollectionUtils.isEmpty(expenseReportLines)) {
            throw new BizException(RespCode.EXPENSE_REPORT_LINE_NOT_NULL);
        }
        expenseReportLines.stream().forEach(line -> {
            List<ExpenseReportDist> expenseReportDists = expenseReportDistService.selectList(new EntityWrapper<ExpenseReportDist>().eq("exp_report_line_id", line.getId()));
            if (CollectionUtils.isEmpty(expenseReportDists)) {
                throw new BizException(RespCode.EXPENSE_REPORT_DIST_NOT_NULL);
            }
            BigDecimal reduce = expenseReportDists.stream().map(ExpenseReportDist::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal noTaxReduce = expenseReportDists.stream().map(ExpenseReportDist::getNoTaxDistAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (!reduce.equals(line.getAmount()) || !noTaxReduce.equals(line.getExpenseAmount())) {
                throw new BizException(RespCode.EXPENSE_REPORT_DIST_TOTAL_AMOUNT_ERROR);
            }
        });
    }

    /**
     * 校验关联申请单
     *
     * @param expenseReportHeader
     * @param expTaxDist          分摊方式
     */
    private void checkReleaseRequisition(ExpenseReportHeader expenseReportHeader,
                                         String expTaxDist) {
        List<ExpenseReportDist> expenseReportDists = expenseReportDistService.selectList(new EntityWrapper<ExpenseReportDist>().eq("exp_report_header_id", expenseReportHeader.getId()));
        //校验关联的申请单
        expenseReportDists.stream()
                .filter(m -> m.getSourceDocumentDistId() != null)
                .forEach(expenseReportDist -> {
                    ApplicationLineDist applicationLineDist = applicationLineDistService.selectById(expenseReportDist.getSourceDocumentDistId());
                    // 获取分摊行对应的申请单释放记录
                    List<ExpenseRequisitionRelease> expenseRequisitionReleaseBySourceDocumentMsg = expenseRequisitionReleaseService.getExpenseRequisitionReleaseBySourceDocumentMsg(ExpenseDocumentTypeEnum.EXP_REQUISITION.getCategory(),
                            expenseReportDist.getSourceDocumentId(),
                            null,
                            expenseReportDist.getSourceDocumentDistId());

                    BigDecimal sumAmount = expenseRequisitionReleaseBySourceDocumentMsg.stream().map(m -> m.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    // 分摊行金额
                    BigDecimal distAmount = null;
                    BigDecimal distFunctionAmount = null;
                    if (ParameterConstant.TAX_IN.equals(expTaxDist)) {
                        distAmount = expenseReportDist.getAmount();
                        distFunctionAmount = expenseReportDist.getFunctionAmount();
                    } else if (ParameterConstant.TAX_OFF.equals(expTaxDist)) {
                        distAmount = expenseReportDist.getNoTaxDistAmount();
                        distFunctionAmount = expenseReportDist.getNoTaxDistFunctionAmount();
                    }
                    sumAmount = sumAmount.add(distAmount);
                    if (applicationLineDist.getAmount().compareTo(sumAmount) < 0) {
                        throw new BizException(RespCode.EXPENSE_REPORT_LINE_AMOUNT_TOO_BIG);
                    }
                    ExpenseRequisitionRelease release = new ExpenseRequisitionRelease();
                    release.setTenantId(expenseReportDist.getTenantId());
                    release.setSetOfBooksId(expenseReportDist.getSetOfBooksId());
                    release.setSourceDocumentCategory(ExpenseDocumentTypeEnum.EXP_REQUISITION.getCategory());
                    release.setSourceDocumentId(expenseReportDist.getSourceDocumentId());
                    release.setSourceDocumentDistId(expenseReportDist.getSourceDocumentDistId());
                    release.setRelatedDocumentCategory(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory());
                    release.setRelatedDocumentId(expenseReportDist.getExpReportHeaderId());
                    release.setRelatedDocumentLineId(expenseReportDist.getExpReportLineId());
                    release.setRelatedDocumentDistId(expenseReportDist.getId());
                    release.setCurrencyCode(expenseReportDist.getCurrencyCode());
                    release.setExchangeRate(expenseReportDist.getExchangeRate().doubleValue());
                    release.setAmount(distAmount);
                    release.setFunctionalAmount(distFunctionAmount);
                    expenseRequisitionReleaseService.saveExpenseRequisitionRelease(release);
                });
    }

    /**
     * 计划付款行校验
     * ... 合同金额未校验
     *
     * @param expenseReportHeader
     */
    private List<ExpenseReportPaymentSchedule> checkPaymentSchedule(ExpenseReportHeader expenseReportHeader) {
        List<ExpenseReportPaymentSchedule> paymentScheduleList =
                expenseReportPaymentScheduleService.getExpensePaymentScheduleByReportHeaderId(expenseReportHeader.getId());
        if (CollectionUtils.isEmpty(paymentScheduleList)) {
            throw new BizException(RespCode.EXPENSE_REPORT_SCHEDULE_NOT_NULL);
        }
        //jiu.zhao 合同
        /*if(expenseReportHeader.getContractHeaderId() != null){
            List<ContractHeaderCO> contractHeaderCOS = contractService.listContractHeadersByIds(Arrays.asList(expenseReportHeader.getContractHeaderId()));
            if(CollectionUtils.isNotEmpty(contractHeaderCOS)) {
                Integer contractStatus = contractHeaderCOS.get(0).getStatus();
                if (PaymentDocumentOperationEnum.HOLD.getId().equals(contractStatus)) {
                    //HOLD(6001),暂挂中
                    throw new BizException(RespCode.EXPENSE_REPORT_CONTRACT_STATUS, new Object[]{contractHeaderCOS.get(0).getContractNumber(), messageService.getMessages(RespCode.CONTRACT_STATUS_HOLD)});
                } else if (PaymentDocumentOperationEnum.CANCEL.getId().equals(contractStatus)) {
                    //CANCEL(6002),已取消
                    throw new BizException(RespCode.EXPENSE_REPORT_CONTRACT_STATUS, new Object[]{contractHeaderCOS.get(0).getContractNumber(), messageService.getMessages(RespCode.CONTRACT_STATUS_CANCEL)});
                } else if (PaymentDocumentOperationEnum.FINISH.getId().equals(contractStatus)) {
                    //FINISH(6003),已完成
                    throw new BizException(RespCode.EXPENSE_REPORT_CONTRACT_STATUS, new Object[]{contractHeaderCOS.get(0).getContractNumber(), messageService.getMessages(RespCode.CONTRACT_STATUS_FINISH)});
                }
            }
        }*/
        BigDecimal totalAmount = paymentScheduleList.stream().map(m -> m.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (expenseReportHeader.getTotalAmount().compareTo(totalAmount) != 0) {
            throw new BizException(RespCode.EXPENSE_REPORT_SCHEDULE_TOTAL_AMOUNT_ERROR);
        }
        //默认创建的分摊数据不完整
        for (ExpenseReportPaymentSchedule expensePaymentSchedule : paymentScheduleList) {
            if (expensePaymentSchedule.getAccountName() == null || expensePaymentSchedule.getAccountNumber() == null
                    || expensePaymentSchedule.getCshTransactionClassId() == null) {
                throw new BizException(RespCode.EXPENSE_REPORT_PAYMENT_INFO_ERROR);
            }
        }
        return paymentScheduleList;
    }

    /**
     * 发送合同资金计划行
     *
     * @param expenseReportHeader
     */
    private void sendContract(ExpenseReportHeader expenseReportHeader,
                              List<ExpenseReportPaymentSchedule> paymentScheduleList) {
        if (expenseReportHeader.getContractHeaderId() != null) {
            //建立报账单和合同关联关系
            List<ContractDocumentRelationCO> list = new ArrayList<>();
            paymentScheduleList.stream().filter(m -> m.getConPaymentScheduleLineId() != null)
                    .forEach(schedule -> {
                        ContractDocumentRelationCO relation = new ContractDocumentRelationCO();
                        relation.setAmount(schedule.getAmount());
                        relation.setContractHeadId(expenseReportHeader.getContractHeaderId());
                        relation.setContractLineId(schedule.getConPaymentScheduleLineId());
                        relation.setCreatedBy(LoginInformationUtil.getCurrentUserId());
                        relation.setCurrencyCode(schedule.getCurrencyCode());
                        relation.setDocumentType(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory());
                        relation.setExchangeRate(schedule.getExchangeRate().doubleValue());
                        relation.setDocumentHeadId(expenseReportHeader.getId());
                        relation.setDocumentLineId(schedule.getId());
                        relation.setFunctionAmount(schedule.getFunctionAmount());
                        list.add(relation);
                    });
            //jiu.zhao 合同
            /*if(CollectionUtils.isNotEmpty(list)){
                contractService.saveOrUpdateContractDocumentRelationBatch(list);
            }*/
        }
    }

    /**
     * 推送至工作流
     *
     * @param expenseReportHeader
     * @param expenseReportType
     * @param workFlowDocumentRef
     */
    private void sendWorkFlow(ExpenseReportHeader expenseReportHeader,
                              ExpenseReportTypeDTO expenseReportType,
                              WorkFlowDocumentRefCO workFlowDocumentRef) {
        if (expenseReportType == null) {
            return;
        }
        DepartmentCO departmentCO = organizationService.getDepartmentById(expenseReportHeader.getDepartmentId());
        UUID unitOid = null;
        if (departmentCO != null) {
            unitOid = departmentCO.getDepartmentOid();
        }

        CompanyCO companyCO = organizationService.getCompanyById(expenseReportHeader.getCompanyId());
        Long companyId = null;
        if (companyCO != null) {
            companyId = companyCO.getId();
        }

        ContactCO contactCO = organizationService.getUserById(expenseReportHeader.getApplicantId());
        UUID applicantOid = null;
        if (contactCO != null) {
            applicantOid = UUID.fromString(contactCO.getUserOid());
        }

        ApprovalFormCO approvalFormCO = organizationService.getApprovalFormById(expenseReportType.getFormId());
        UUID formOid = null;
        if (approvalFormCO != null) {
            formOid = approvalFormCO.getFormOid();
        }

        // 设置提交的参数
        ApprovalDocumentCO submitData = new ApprovalDocumentCO();
        submitData.setDocumentId(expenseReportHeader.getId()); // 单据id
        submitData.setDocumentOid(UUID.fromString(expenseReportHeader.getDocumentOid())); // 单据oid
        submitData.setDocumentNumber(expenseReportHeader.getRequisitionNumber()); // 单据编号
        submitData.setDocumentName(null); // 单据名称
        submitData.setDocumentCategory(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getKey()); // 单据类别
        submitData.setDocumentTypeId(expenseReportType.getId()); // 单据类型id
        submitData.setDocumentTypeCode(expenseReportType.getReportTypeCode()); // 单据类型代码
        submitData.setDocumentTypeName(expenseReportType.getReportTypeName()); // 单据类型名称
        submitData.setCurrencyCode(expenseReportHeader.getCurrencyCode()); // 币种
        submitData.setAmount(expenseReportHeader.getTotalAmount()); // 原币金额
        submitData.setFunctionAmount(expenseReportHeader.getFunctionalAmount()); // 本币金额
        submitData.setCompanyId(companyId); // 公司id
        submitData.setUnitOid(unitOid); // 部门oid
        submitData.setApplicantOid(applicantOid); // 申请人oid
        submitData.setApplicantDate(expenseReportHeader.getCreatedDate()); // 申请日期
        submitData.setRemark(expenseReportHeader.getDescription()); // 备注
        submitData.setSubmittedBy(OrgInformationUtil.getCurrentUserOid()); // 提交人
        submitData.setFormOid(formOid); // 表单oid
        submitData.setDestinationService("expense"); // 注册到Eureka中的名称

        //调用工作流的三方接口进行提交
        ApprovalResultCO submitResult = workflowClient.submitWorkflow(submitData);

        if (Boolean.TRUE.equals(submitResult.getSuccess())) {
            Integer approvalStatus = submitResult.getStatus();

            if (DocumentOperationEnum.APPROVAL.getId().equals(approvalStatus)) {
                //提交成功，更新单据的状态
                updateById(expenseReportHeader);
            } else {
                updateDocumentStatus(expenseReportHeader, approvalStatus, "");
            }
        } else {
            throw new BizException(submitResult.getError());
        }
    }

    /**
     * @param headerId 申请单头ID
     * @param status   状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDocumentStatus(Long headerId, Integer status, String approvalText) {
        ExpenseReportHeader header = this.selectById(headerId);
        updateDocumentStatus(header,status,approvalText);
    }

    /**
     * @param header 申请单头
     * @param status   状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDocumentStatus(ExpenseReportHeader header, Integer status, String approvalText) {
        if (header == null) {
            throw new BizException(RespCode.EXPENSE_REPORT_HEADER_IS_NUTT);
        }
        if (DocumentOperationEnum.WITHDRAW.getId().equals(status) || DocumentOperationEnum.APPROVAL_REJECT.getId().equals(status)) {
            checkDocumentStatus(status,header.getStatus());
            header.setStatus(status);
            //审批拒绝  撤回 如果有预算需要释放，
            rollBackBudget(header.getId());
            //删除费用申请释放信息
            expenseRequisitionReleaseService.deleteExpenseRequisitionReleaseMsg(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory(), header.getId(), null, null);
            //核销回滚
            //jiu.zhao 支付
            //paymentService.updateWriteOffRollback(ExpenseDocumentTypeEnum.PUBLIC_REPORT.name(),header.getId(),Arrays.asList(),LoginInformationUtil.getCurrentUserId());
            //释放合同关联信息
            deleteContractDocumentRelations(header);
        }
        // 审批通过，报账单进入共享池
        if (DocumentOperationEnum.APPROVAL_PASS.getId().equals(status)) {
            checkDocumentStatus(status,header.getStatus());
            header.setStatus(status);
            pushToWorkBranchByHeaderId(header);
        }
        // 复核通过，报账单发送支付平台
        if (DocumentOperationEnum.AUDIT_PASS.getId().equals(status)) {
            if("Y".equals(header.getAuditFlag())){
                throw new BizException(RespCode.EXPENSE_REPORT_STATUS_ERROR);
            }
            saveDataToPayment(header);
            header.setAuditFlag("Y");
            header.setAuditDate(ZonedDateTime.now());
            //paymentService.updateAuditChangeWriteOffStatus(DocumentTypeEnum.PUBLIC_REPORT.getCategory(), headerId,LoginInformationUtil.getCurrentUserId(),1);
            //accountingService.updateAccountPosting(DocumentTypeEnum.PUBLIC_REPORT.getCategory(),headerId,null,null,LoginInformationUtil.getCurrentUserId());
            expenseReportLineService.updateExpenseReportLineAduitStatusByHeaderId(header.getId(), "Y", ZonedDateTime.now());
            invoiceHeadService.updateInvoiceAccountingFlagByHeaderId(header.getId(),"Y");
            expenseReportDistService.updateExpenseReportDistAduitStatusByHeaderId(header.getId(), "Y", ZonedDateTime.now());
            expenseReportTaxDistService.updateExpenseReportTaxDistAduitStatusByHeaderId(header.getId(), "Y", ZonedDateTime.now());
            expenseReportPaymentScheduleService.updateExpenseReportScheduleAduitStatusByHeaderId(header.getId(), "Y", ZonedDateTime.now());
        }
        // 复核拒绝
        if (DocumentOperationEnum.AUDIT_REJECT.getId().equals(status)) {
            if(! DocumentOperationEnum.APPROVAL_PASS.getId().equals(header.getStatus())
                    || "Y".equals(header.getAuditFlag())){
                throw new BizException(RespCode.EXPENSE_REPORT_STATUS_ERROR);
            }
            //审批拒绝  撤回 如果有预算需要释放，
            rollBackBudget(header.getId());
            //删除费用申请释放信息
            expenseRequisitionReleaseService.deleteExpenseRequisitionReleaseMsg(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory(), header.getId(), null, null);
            //释放合同关联信息
            deleteContractDocumentRelations(header);
            //核销回滚
            //jiu.zhao 支付
            //paymentService.updateWriteOffRollback(DocumentTypeEnum.PUBLIC_REPORT.name(),header.getId(),Arrays.asList(),LoginInformationUtil.getCurrentUserId());
            //删除凭证
            if (BooleanUtils.isTrue(header.getJeCreationStatus())) {
                //jiu.zhao 合同
                //accountingService.deleteExpReportGeneralLedgerJournalDataByHeaderId(header.getId());
                header.setJeCreationStatus(false);
                header.setJeCreationDate(null);
            }
            header.setStatus(DocumentOperationEnum.APPROVAL_REJECT.getId());
            // 删除工作流实列
            workflowClient.updateDocumentStatus(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getKey(),
                    UUID.fromString(header.getDocumentOid()), DocumentOperationEnum.AUDIT_REJECT);
        }
        // 保存
        this.updateAllColumnById(header);
    }

    private void deleteContractDocumentRelations(ExpenseReportHeader expenseReportHeader) {
        if (expenseReportHeader.getContractHeaderId() != null) {
            List<ExpenseReportPaymentSchedule> paymentScheduleList =
                    expenseReportPaymentScheduleService.getExpensePaymentScheduleByReportHeaderId(expenseReportHeader.getId());
            List<ContractDocumentRelationCO> collect = paymentScheduleList.stream().filter(e -> e.getConPaymentScheduleLineId() != null).map(paymentSchedule -> {
                ContractDocumentRelationCO contractDocumentRelationCO = new ContractDocumentRelationCO();
                contractDocumentRelationCO.setContractHeadId(expenseReportHeader.getContractHeaderId());
                contractDocumentRelationCO.setContractLineId(paymentSchedule.getConPaymentScheduleLineId());
                contractDocumentRelationCO.setDocumentType(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory());
                contractDocumentRelationCO.setDocumentHeadId(expenseReportHeader.getId());
                contractDocumentRelationCO.setDocumentLineId(paymentSchedule.getId());
                return contractDocumentRelationCO;
            }).collect(Collectors.toList());
            //jiu.zhao 合同
            /*if(CollectionUtils.isNotEmpty(collect)){
                contractService.deleteContractDocumentRelationBatch(collect);
            }*/
        }
    }

    private void rollBackBudget(Long id) {
        List<ExpenseReportDist> distList = expenseReportDistService.selectList(new EntityWrapper<ExpenseReportDist>().eq("exp_report_header_id", id));
        List<BudgetReverseRollbackCO> rollbackDTOList = distList.stream().map(e -> {
            BudgetReverseRollbackCO rollbackDTO = new BudgetReverseRollbackCO();
            rollbackDTO.setBusinessType(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory());
            rollbackDTO.setDocumentId(id);
            rollbackDTO.setDocumentLineId(e.getId());
            return rollbackDTO;
        }).collect(Collectors.toList());
        commonService.rollbackBudget(rollbackDTOList);
    }

    @Transactional(rollbackFor = Exception.class)
    //@LcnTransaction
    public String saveInitializeExpReportGeneralLedgerJournalLine(Long reportHeaderId, String accountingDate) {
        ExpenseReportCO reportCO = new ExpenseReportCO();
        //报销单头
        ExpenseReportHeader reportHeader = this.selectById(reportHeaderId);
        //报销单行
        List<ExpenseReportLine> reportLines = expenseReportLineService.getExpenseReportLinesByHeaderId(reportHeaderId);
        //分摊行
        List<ExpenseReportDist> expenseReportDists = expenseReportDistService.getExpenseReportDistByHeaderId(reportHeaderId);

        List<InvoiceLineExpence> lineExpenses = invoiceLineExpenceService.selectList(new EntityWrapper<InvoiceLineExpence>().eq("exp_expense_head_id", reportHeaderId));
        //分配行id
        List<Long> invoiceDistIds = lineExpenses.stream().map(InvoiceLineExpence::getInvoiceDistId).collect(Collectors.toList());
        //发票分配行
        if (CollectionUtils.isNotEmpty(invoiceDistIds)) {
            List<InvoiceLineDist> invoiceLineDists = invoiceLineDistService.selectBatchIds(invoiceDistIds);
            //发票行id
            List<Long> invoiceLineIds = invoiceLineDists.stream().map(InvoiceLineDist::getInvoiceLineId).collect(Collectors.toList());
            //发票行
            List<InvoiceLine> invoiceLines = invoiceLineService.selectBatchIds(invoiceLineIds);
            //发票头
            List<Long> expenseReportHeaderIds = invoiceLines.stream()
                    .map(InvoiceLine::getInvoiceHeadId)
                    .distinct().collect(Collectors.toList());
            List<InvoiceHead> invoiceHeaders = invoiceHeadService.selectBatchIds(expenseReportHeaderIds);
            List<ExpenseReportInvoiceHeaderCO> expenseReportInvoiceHeaderCOS = invoiceHeaderToInvoiceHeaderCO(invoiceHeaders);
            List<ExpenseReportInvoiceLineCO> expenseReportInvoiceLineCOS = invoiceLineToInvoiceLineCO(invoiceLines);
            List<ExpenseReportInvoiceLineDistCO> expenseReportInvoiceLineDistCOS = invoiceLineDistToInvoiceLineDistCO(invoiceLineDists);
            reportCO.setExpenseReportInvoiceHeaders(expenseReportInvoiceHeaderCOS);
            reportCO.setExpenseReportInvoiceLines(expenseReportInvoiceLineCOS);
            reportCO.setExpenseReportInvoiceLineDists(expenseReportInvoiceLineDistCOS);
        }

        //分摊税行
        List<ExpenseReportTaxDist> reportTaxDists = expenseReportTaxDistService.getExpenseReportTaxDistByHeaderId(reportHeaderId);
        //计划付款行
        List<ExpenseReportPaymentSchedule> expenseReportSchedules = expenseReportPaymentScheduleService.getExpensePaymentScheduleByReportHeaderId(reportHeaderId);
        //转换
        ExpenseReportHeaderCO expenseReportHeader = reportHeaderToReportHeaderCO(reportHeader, accountingDate);
        List<ExpenseReportLineCO> expenseReportLineCOS = reportLineToReportLineCO(reportLines);
        List<ExpenseReportDistCO> expenseReportDistCOS = reportDistToReportDistCO(expenseReportDists);
        List<ExpenseReportTaxDistCO> expenseReportTaxDistCOS = reportTaxDistToReportTaxDistCO(reportTaxDists);
        List<ExpenseReportScheduleCO> expenseReportScheduleCOS = reportScheduleToReportScheduleCO(expenseReportSchedules);

        reportCO.setExpenseReportHeader(expenseReportHeader);
        reportCO.setExpenseReportLines(expenseReportLineCOS);
        reportCO.setExpenseReportDists(expenseReportDistCOS);
        reportCO.setExpenseReportTaxDistS(expenseReportTaxDistCOS);
        reportCO.setExpenseReportSchedules(expenseReportScheduleCOS);
        //jiu.zhao 核算
		/*accountingService.saveInitializeExpReportGeneralLedgerJournalLine(reportCO);
        log.info("====核算模块创建凭证成功，开始组装核销报文===");
        CashWriteOffAccountCO writeOffAccountCO = new CashWriteOffAccountCO();
        writeOffAccountCO.setDocumentHeaderId(reportHeader.getId());
        writeOffAccountCO.setDocumentType(DocumentTypeEnum.PUBLIC_REPORT.getCategory());
        writeOffAccountCO.setTenantId(reportHeader.getTenantId());
        writeOffAccountCO.setOperatorId(LoginInformationUtil.getCurrentUserId());
        writeOffAccountCO.setAccountDate(expenseReportHeader.getAccountDate());
        writeOffAccountCO.setAccountPeriod(expenseReportHeader.getAccountPeriod());
        String writeOffResult = paymentService.saveWriteOffJournalLines(writeOffAccountCO);
        if("NO_WRITE_OFF_DATA".equals(writeOffResult)){
            log.info("====支付模块返回成功，没有核销，无须生成凭证===");
        }else if ("SUCCESS".equals(writeOffResult)){
            log.info("====创建核销凭证成功===");
        }*/
        reportHeader.setJeCreationStatus(true);
        reportHeader.setJeCreationDate(ZonedDateTime.now());
        updateById(reportHeader);
        return "SUCCESS";
    }

    private ExpenseReportHeaderCO reportHeaderToReportHeaderCO(ExpenseReportHeader reportHeader,
                                                               String accountingDate) {
        ZonedDateTime accountDate = TypeConversionUtils.getStartTimeForDayYYMMDD(accountingDate);
        PeriodCO period = organizationService.getPeriodsByIDAndTime(reportHeader.getSetOfBooksId(), DateUtil.ZonedDateTimeToString(accountDate));
        return ExpenseReportHeaderCO.builder()
                .id(reportHeader.getId())
                .tenantId(reportHeader.getTenantId())
                .setOfBooksId(reportHeader.getSetOfBooksId())
                .currencyCode(reportHeader.getCurrencyCode())
                .rate(reportHeader.getExchangeRate().doubleValue())
                .businessCode(reportHeader.getRequisitionNumber())
                .companyId(reportHeader.getCompanyId())
                .unitId(reportHeader.getDepartmentId())
                .applicationId(reportHeader.getApplicantId())
                .formId(reportHeader.getFormId())
                .remark(reportHeader.getDescription())
                .reportDate(reportHeader.getRequisitionDate())
                .totalAmount(reportHeader.getTotalAmount())
                .functionalAmount(reportHeader.getFunctionalAmount())
                .reportStatus(reportHeader.getStatus().toString())
                .accountDate(accountDate)
                .accountPeriod(period.getPeriodName()).build();
    }

    private List<ExpenseReportLineCO> reportLineToReportLineCO(List<ExpenseReportLine> reportLines) {
        List<ExpenseReportLineCO> expenseReportLines = new ArrayList<>();
        reportLines.forEach(reportLine -> {
            ExpenseReportLineCO expenseReportLine = ExpenseReportLineCO.builder()
                    .id(reportLine.getId())
                    .headerId(reportLine.getExpReportHeaderId())
                    .companyId(reportLine.getCompanyId())
                    .expenseTypeId(reportLine.getExpenseTypeId())
                    .expenseDate(reportLine.getExpenseDate())
                    .quantity(reportLine.getQuantity())
                    .price(reportLine.getPrice())
                    .uom(reportLine.getUom())
                    .currencyCode(reportLine.getCurrencyCode())
                    .rate(reportLine.getExchangeRate().doubleValue())
                    .amount(reportLine.getAmount())
                    .functionAmount(reportLine.getFunctionAmount())
                    .expenseAmount(reportLine.getExpenseAmount())
                    .expenseFunctionAmount(reportLine.getExpenseFunctionAmount())
                    .taxAmount(reportLine.getTaxAmount())
                    .taxFunctionAmount(reportLine.getTaxFunctionAmount())
                    .installmentDeductionFlag(reportLine.getInstallmentDeductionFlag())
                    .inputTaxFlag(reportLine.getInputTaxFlag())
                    .useType(reportLine.getUseType())
                    .description(reportLine.getDescription())
                    .build();
            expenseReportLines.add(expenseReportLine);
        });
        return expenseReportLines;
    }

    private List<ExpenseReportInvoiceHeaderCO> invoiceHeaderToInvoiceHeaderCO(List<InvoiceHead> invoiceHeads) {
        List<ExpenseReportInvoiceHeaderCO> expenseReportInvoiceHeaders = new ArrayList<>();
        invoiceHeads.forEach(invoiceHead -> {
            ExpenseReportInvoiceHeaderCO invoiceHeaderCO = ExpenseReportInvoiceHeaderCO.builder()
                    .id(invoiceHead.getInvoiceTypeId())
                    .invoiceTypeId(invoiceHead.getInvoiceTypeId())
                    .tenantId(invoiceHead.getTenantId())
                    .setOfBooksId(invoiceHead.getSetOfBooksId())
                    .invoiceDate(invoiceHead.getInvoiceDate())
                    .invoiceNo(invoiceHead.getInvoiceNo())
                    .invoiceCode(invoiceHead.getInvoiceCode())
                    .machineNo(invoiceHead.getMachineNo())
                    .checkCode(invoiceHead.getCheckCode())
                    .totalAmount(invoiceHead.getTotalAmount())
                    .taxTotalAmount(invoiceHead.getTaxTotalAmount())
                    .currencyCode(invoiceHead.getCurrencyCode())
                    .exchangeRate(invoiceHead.getExchangeRate())
                    .remark(invoiceHead.getRemark())
                    .buyerName(invoiceHead.getBuyerName())
                    .buyerTaxNo(invoiceHead.getBuyerTaxNo())
                    .buyerAddPh(invoiceHead.getBuyerAddPh())
                    .buyerAccount(invoiceHead.getBuyerAccount())
                    .salerName(invoiceHead.getSalerName())
                    .salerTaxNo(invoiceHead.getSalerTaxNo())
                    .salerAddPh(invoiceHead.getSalerAddPh())
                    .salerAccount(invoiceHead.getSalerAccount())
                    .cancelFlag(invoiceHead.getCancelFlag())
                    .redInvoiceFlag(invoiceHead.getRedInvoiceFlag())
                    .checkResult(invoiceHead.getCheckResult())
                    .build();
            expenseReportInvoiceHeaders.add(invoiceHeaderCO);
        });
        return expenseReportInvoiceHeaders;
    }

    private List<ExpenseReportInvoiceLineCO> invoiceLineToInvoiceLineCO(List<InvoiceLine> invoiceLines) {
        List<ExpenseReportInvoiceLineCO> expenseReportInvoiceLines = new ArrayList<>();
        invoiceLines.forEach(invoiceLine -> {
            ExpenseReportInvoiceLineCO invoiceLineCO = ExpenseReportInvoiceLineCO.builder()
                    .id(invoiceLine.getId())
                    .invoiceHeaderId(invoiceLine.getInvoiceHeadId())
                    .invoiceLineNum(invoiceLine.getInvoiceLineNum())
                    .goodsName(invoiceLine.getGoodsName())
                    .specificationModel(invoiceLine.getSpecificationModel())
                    .unit(invoiceLine.getUnit())
                    .num(invoiceLine.getNum())
                    .detailAmount(invoiceLine.getDetailAmount())
                    .taxRate(invoiceLine.getTaxRate())
                    .taxAmount(invoiceLine.getTaxAmount())
                    .currencyCode(invoiceLine.getCurrencyCode())
                    .exchangeRate(invoiceLine.getExchangeRate())
                    .build();
            expenseReportInvoiceLines.add(invoiceLineCO);
        });
        return expenseReportInvoiceLines;
    }

    private List<ExpenseReportInvoiceLineDistCO> invoiceLineDistToInvoiceLineDistCO(List<InvoiceLineDist> invoiceLineDists) {
        List<ExpenseReportInvoiceLineDistCO> expenseReportInvoiceLineDists = new ArrayList<>();
        invoiceLineDists.forEach(invoiceLineDist -> {
            InvoiceLine invoiceLine = invoiceLineService.selectById(invoiceLineDist.getInvoiceLineId());
            ExpenseReportInvoiceLineDistCO invoiceLineDistCO = ExpenseReportInvoiceLineDistCO.builder()
                    .id(invoiceLineDist.getId())
                    .invoiceLineId(invoiceLineDist.getInvoiceLineId())
                    .invoiceHeaderId(invoiceLine.getInvoiceHeadId())
                    .goodsName(invoiceLineDist.getGoodsName())
                    .specificationModel(invoiceLineDist.getSpecificationModel())
                    .unit(invoiceLineDist.getUnit())
                    .num(invoiceLineDist.getNum())
                    .detailAmount(invoiceLineDist.getDetailAmount())
                    .taxRate(invoiceLineDist.getTaxRate())
                    .taxAmount(invoiceLineDist.getTaxAmount())
                    .currencyCode(invoiceLineDist.getCurrencyCode())
                    .exchangeRate(invoiceLineDist.getExchangeRate())
                    .build();
            expenseReportInvoiceLineDists.add(invoiceLineDistCO);
        });
        return expenseReportInvoiceLineDists;
    }

    private List<ExpenseReportDistCO> reportDistToReportDistCO(List<ExpenseReportDist> reportDists) {
        List<ExpenseReportDistCO> expenseReportDists = new ArrayList<>();
        reportDists.forEach(reportDist -> {
            ExpenseReportDistCO expenseReportDist = ExpenseReportDistCO.builder().companyId(reportDist.getCompanyId())
                    .currencyCode(reportDist.getCurrencyCode())
                    .dimension1Id(reportDist.getDimension1Id())
                    .dimension2Id(reportDist.getDimension2Id())
                    .dimension3Id(reportDist.getDimension3Id())
                    .dimension4Id(reportDist.getDimension4Id())
                    .dimension5Id(reportDist.getDimension5Id())
                    .dimension6Id(reportDist.getDimension6Id())
                    .dimension7Id(reportDist.getDimension7Id())
                    .dimension8Id(reportDist.getDimension8Id())
                    .dimension9Id(reportDist.getDimension9Id())
                    .dimension10Id(reportDist.getDimension10Id())
                    .dimension11Id(reportDist.getDimension11Id())
                    .dimension12Id(reportDist.getDimension12Id())
                    .dimension13Id(reportDist.getDimension13Id())
                    .dimension14Id(reportDist.getDimension14Id())
                    .dimension15Id(reportDist.getDimension15Id())
                    .dimension16Id(reportDist.getDimension16Id())
                    .dimension17Id(reportDist.getDimension17Id())
                    .dimension18Id(reportDist.getDimension18Id())
                    .dimension19Id(reportDist.getDimension19Id())
                    .dimension20Id(reportDist.getDimension20Id())
                    .expenseTypeId(reportDist.getExpenseTypeId())
                    .taxFunctionAmount(reportDist.getTaxDistFunctionAmount())
                    .headerId(reportDist.getExpReportHeaderId())
                    .id(reportDist.getId())
                    .lineId(reportDist.getExpReportLineId())
                    .resCenterId(reportDist.getResponsibilityCenterId())
                    .noTaxFunctionAmount(reportDist.getNoTaxDistFunctionAmount())
                    .noTaxAmount(reportDist.getNoTaxDistAmount())
                    .taxAmount(reportDist.getTaxDistAmount())
                    .amount(reportDist.getAmount())
                    .functionAmount(reportDist.getFunctionAmount())
                    .unitId(reportDist.getDepartmentId()).build();
            expenseReportDists.add(expenseReportDist);
        });
        return expenseReportDists;
    }

    private List<ExpenseReportTaxDistCO> reportTaxDistToReportTaxDistCO(List<ExpenseReportTaxDist> reportTaxDists) {
        List<ExpenseReportTaxDistCO> expenseReportTaxDistS = new ArrayList<>();
        if (CollectionUtils.isEmpty(reportTaxDists)) {
            new ArrayList<>();
        }
        reportTaxDists.forEach(reportTaxDist -> {
            InvoiceLineDist invoiceLineDist = invoiceLineDistService.selectById(reportTaxDist.getInvoiceDistId());
            InvoiceLine invoiceLine = invoiceLineService.selectById(invoiceLineDist.getInvoiceLineId());
            ExpenseReportDist expenseReportDist = expenseReportDistService.selectById(reportTaxDist.getExpReportDistId());
            ExpenseReportTaxDistCO reportTaxDistCO = ExpenseReportTaxDistCO.builder().companyId(reportTaxDist.getCompanyId())
                    .currencyCode(reportTaxDist.getCurrencyCode())
                    .dimension1Id(reportTaxDist.getDimension1Id())
                    .dimension2Id(reportTaxDist.getDimension2Id())
                    .dimension3Id(reportTaxDist.getDimension3Id())
                    .dimension4Id(reportTaxDist.getDimension4Id())
                    .dimension5Id(reportTaxDist.getDimension5Id())
                    .dimension6Id(reportTaxDist.getDimension6Id())
                    .dimension7Id(reportTaxDist.getDimension7Id())
                    .dimension8Id(reportTaxDist.getDimension8Id())
                    .dimension9Id(reportTaxDist.getDimension9Id())
                    .dimension10Id(reportTaxDist.getDimension10Id())
                    .dimension11Id(reportTaxDist.getDimension11Id())
                    .dimension12Id(reportTaxDist.getDimension12Id())
                    .dimension13Id(reportTaxDist.getDimension13Id())
                    .dimension14Id(reportTaxDist.getDimension14Id())
                    .dimension15Id(reportTaxDist.getDimension15Id())
                    .dimension16Id(reportTaxDist.getDimension16Id())
                    .dimension17Id(reportTaxDist.getDimension17Id())
                    .dimension18Id(reportTaxDist.getDimension18Id())
                    .dimension19Id(reportTaxDist.getDimension19Id())
                    .dimension20Id(reportTaxDist.getDimension20Id())
                    .distId(reportTaxDist.getExpReportDistId())
                    .expenseTypeId(reportTaxDist.getExpenseTypeId())
                    .headerId(reportTaxDist.getExpReportHeaderId())
                    .id(reportTaxDist.getId())
                    .invoiceDistId(reportTaxDist.getInvoiceDistId())
                    .invoiceHeaderId(invoiceLine.getInvoiceHeadId())
                    .invoiceLineId(invoiceLine.getId())
                    .lineId(expenseReportDist.getExpReportLineId())
                    .resCenterId(reportTaxDist.getResponsibilityCenterId())
                    .taxAmount(reportTaxDist.getTaxAmount())
                    .taxFunctionAmount(reportTaxDist.getFunctionAmount())
                    .unitId(reportTaxDist.getDepartmentId()).build();
            expenseReportTaxDistS.add(reportTaxDistCO);
        });
        return expenseReportTaxDistS;
    }

    private List<ExpenseReportScheduleCO> reportScheduleToReportScheduleCO(List<ExpenseReportPaymentSchedule> reportSchedules) {
        List<ExpenseReportScheduleCO> expenseReportSchedules = new ArrayList<>();
        reportSchedules.forEach(reportSchedule -> {
            ExpenseReportScheduleCO reportScheduleCO = ExpenseReportScheduleCO.builder()
                    .id(reportSchedule.getId())
                    .headerId(reportSchedule.getExpReportHeaderId())
                    .companyId(reportSchedule.getCompanyId())
                    .unitId(reportSchedule.getDepartmentId())
                    .description(reportSchedule.getDescription())
                    .currency(reportSchedule.getCurrencyCode())
                    .rate(reportSchedule.getExchangeRate().doubleValue())
                    .amount(reportSchedule.getAmount())
                    .functionalAmount(reportSchedule.getFunctionAmount())
                    .schedulePaymentDate(reportSchedule.getPaymentScheduleDate())
                    .paymentMethod(reportSchedule.getPaymentMethod())
                    .cshTransactionClassId(reportSchedule.getCshTransactionClassId())
                    .cashFlowItemId(reportSchedule.getCashFlowItemId())
                    .payeeCategory(reportSchedule.getPayeeCategory())
                    .payeeId(reportSchedule.getPayeeId())
                    .accountNumber(reportSchedule.getAccountNumber())
                    .accountName(reportSchedule.getAccountName())
                    .frozenResult(reportSchedule.getFrozenFlag())
                    .build();
            if ("EMPLOYEE".equals(reportScheduleCO.getPayeeCategory())) {
                ContactCO user = organizationService.getUserById(reportScheduleCO.getPayeeId());
                if (user != null) {
                    reportScheduleCO.setPayeeCode(user.getEmployeeCode());
                }
            } else {
                VendorInfoCO vendorInfo = organizationService.getOneVendorInfoById(reportScheduleCO.getPayeeId());
                if (vendorInfo != null) {
                    reportScheduleCO.setPayeeCode(vendorInfo.getVenderCode());
                }
            }
            expenseReportSchedules.add(reportScheduleCO);
        });
        return expenseReportSchedules;
    }

    public List<ExpenseReportHeaderDTO> queryExpenseReportsFinance(Long companyId,
                                                                   Long setOfBooksId,
                                                                   Long documentTypeId,
                                                                   ZonedDateTime reqDateFrom,
                                                                   ZonedDateTime reqDateTo,
                                                                   Long applicantId,
                                                                   Integer status,
                                                                   String currencyCode,
                                                                   BigDecimal amountFrom,
                                                                   BigDecimal amountTo,
                                                                   String remark,
                                                                   String requisitionNumber,
                                                                   Long unitId,
                                                                   BigDecimal paidAmountFrom,
                                                                   BigDecimal paidAmountTo,
                                                                   ZonedDateTime cDateFrom,
                                                                   ZonedDateTime cDateTo,
                                                                   String backlashFlag,
                                                                   Long tenantId,
                                                                   boolean dataAuthFlag,
                                                                   Page page) {
        //要么费用模块查询出来相对应的 要么就是支付模块查询出来再给费用。
        //采用先查询支付模块 再将数据放入到费用模块来进行查询。
        List<ExpenseReportHeaderDTO> reportHeaderDTOS = new ArrayList<>();
        //jiu.zhao 支付
        List<CashTransactionDetailCO> coList = new ArrayList<>();

        //coList = paymentService.queryCashTransactionDetailByReport(paidAmountFrom, paidAmountTo, backlashFlag);

            //如果查询出来为零的话 也要分情况， 只要已支付金额 核销金额  反冲标志 其中只要一个不为null 那就返回为Null
               List<Long>ids = new ArrayList<>();
                     //在判断传入的参数是否为空 如果不为空则
        String dataAuthLabel = null;
        if(dataAuthFlag){
            Map<String,String> map = new HashMap<>();
            map.put(DataAuthorityUtil.TABLE_NAME,"exp_report_header");
            map.put(DataAuthorityUtil.TABLE_ALIAS,"t");
            map.put(DataAuthorityUtil.SOB_COLUMN,"set_of_books_id");
            map.put(DataAuthorityUtil.COMPANY_COLUMN,"company_id");
            map.put(DataAuthorityUtil.UNIT_COLUMN,"department_id");
            map.put(DataAuthorityUtil.EMPLOYEE_COLUMN,"applicant_id");
            dataAuthLabel = DataAuthorityUtil.getDataAuthLabel(map);
        }
            if (paidAmountFrom!=null||paidAmountTo!=null||!StringUtils.isEmpty(backlashFlag)) {
                         // 判断返回的结果是否为空， 如果不为空
                         //获取到id。
                    if(!CollectionUtils.isEmpty(coList)) {
                        ids = coList.stream()
                                .map(CashTransactionDetailCO::getDocumentId)
                                .collect(Collectors.toList());
                        reportHeaderDTOS = baseMapper.queryReportHeaderByids(getHearderWrapper(ids, companyId, documentTypeId, reqDateFrom, reqDateTo
                                         , applicantId, status, currencyCode, amountFrom, amountTo,
                                         remark, requisitionNumber, unitId, cDateFrom, cDateTo,tenantId).and(dataAuthLabel !=  null,dataAuthLabel),
                                         page);
                        for (ExpenseReportHeaderDTO expenseReportHeaderDTO : reportHeaderDTOS) {
                            // 返回账套code、账套name
                            SetOfBooksInfoCO setOfBooksInfoCOById = organizationService.getSetOfBooksInfoCOById(expenseReportHeaderDTO.getSetOfBooksId(), false);
                            if (setOfBooksInfoCOById != null) {
                                expenseReportHeaderDTO.setSetOfBooksCode(setOfBooksInfoCOById.getSetOfBooksCode());
                                expenseReportHeaderDTO.setSetOfBooksName(setOfBooksInfoCOById.getSetOfBooksName());
                            }
                        }
                    }
                         //为空则 返回前台空数据。
            }else{
                    reportHeaderDTOS = baseMapper.queryReportHeaderByids(getHearderWrapper(ids, companyId, documentTypeId, reqDateFrom, reqDateTo
                        , applicantId, status, currencyCode, amountFrom, amountTo,
                        remark, requisitionNumber, unitId, cDateFrom, cDateTo,tenantId).and(dataAuthLabel !=  null,dataAuthLabel),
                        page);
                for (ExpenseReportHeaderDTO expenseReportHeaderDTO : reportHeaderDTOS) {
                    // 返回账套code、账套name
                    SetOfBooksInfoCO setOfBooksInfoCOById = organizationService.getSetOfBooksInfoCOById(expenseReportHeaderDTO.getSetOfBooksId(), false);
                    if (setOfBooksInfoCOById != null) {
                        expenseReportHeaderDTO.setSetOfBooksCode(setOfBooksInfoCOById.getSetOfBooksCode());
                        expenseReportHeaderDTO.setSetOfBooksName(setOfBooksInfoCOById.getSetOfBooksName());
                    }
                }
            }

              //拼接数据
                setReportHeaderDTOSAmount(coList,reportHeaderDTOS);
        return reportHeaderDTOS;
    }
        //查询相关属性
        public  void  setExpenseReportHeaderDto(ExpenseReportHeaderDTO expenseReportHeaderDTO ){
            CompanyCO companyById = organizationService.getCompanyById(expenseReportHeaderDTO.getCompanyId());
            expenseReportHeaderDTO.setCompanyCode(companyById.getCompanyCode());
            expenseReportHeaderDTO.setCompanyName(companyById.getName());
            // 受益部门信息
            DepartmentCO departmentById = organizationService.getDepartmentById(expenseReportHeaderDTO.getDepartmentId());
            expenseReportHeaderDTO.setDepartmentCode(departmentById.getDepartmentCode());
            expenseReportHeaderDTO.setDepartmentName(departmentById.getName());
            // 预算部门信息
            DepartmentCO budgetDepById = organizationService.getDepartmentById(expenseReportHeaderDTO.getBudgetDepId());
            expenseReportHeaderDTO.setBudgetDepCode(budgetDepById.getDepartmentCode());
            expenseReportHeaderDTO.setBudgetDepName(budgetDepById.getName());
            // 申请人信息
            ContactCO userById = organizationService.getUserById(expenseReportHeaderDTO.getApplicantId());
            expenseReportHeaderDTO.setApplicantCode(userById.getEmployeeCode());
            expenseReportHeaderDTO.setApplicantName(userById.getFullName());
            // 需求方信息
            ContactCO demanderUserById = organizationService.getUserById(expenseReportHeaderDTO.getDemanderId());
            expenseReportHeaderDTO.setDemanderCode(demanderUserById.getEmployeeCode());
            expenseReportHeaderDTO.setDemanderName(demanderUserById.getFullName());
            // 单据类型
            ExpenseReportType expenseReportType = expenseReportTypeService.selectById(expenseReportHeaderDTO.getDocumentTypeId());
            expenseReportHeaderDTO.setDocumentTypeName(expenseReportType.getReportTypeName());
            expenseReportHeaderDTO.setFormId(expenseReportType.getFormId());
            ApprovalFormCO approvalFormById = organizationService.getApprovalFormById(expenseReportType.getFormId());
            expenseReportHeaderDTO.setFormOid(approvalFormById.getFormOid());
            // 币种
            CurrencyRateCO currencyRate = organizationService.getForeignCurrencyByCode(null, expenseReportHeaderDTO.getCurrencyCode(), expenseReportHeaderDTO.getSetOfBooksId());
            expenseReportHeaderDTO.setCurrencyName(currencyRate.getCurrencyCode() + "-" + currencyRate.getCurrencyName());
            //区域
            SysCodeValue sysCodeValueByCode = sysCodeService.getValueBySysCodeAndValue("REPORT_AREA", expenseReportHeaderDTO.getAreaCode());
            String AreaName = Optional
                    .ofNullable(sysCodeValueByCode)
                    .map(u -> TypeConversionUtils.parseString(u.getName()))
                    .orElseThrow(() -> new BizException("SYS_CODE_REPORT_AREA_NOT_EXISTS"));
            expenseReportHeaderDTO.setAreaName(AreaName);
            //附件
            if(com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(expenseReportHeaderDTO.getAttachmentOid())){
                List<String> strings = Arrays.asList(expenseReportHeaderDTO.getAttachmentOid().split(","));
                expenseReportHeaderDTO.setAttachmentOidList(strings);
                List<AttachmentCO> attachmentCOS = organizationService.listAttachmentsByOids(strings);
                expenseReportHeaderDTO.setAttachments(attachmentCOS);
            }else{
                expenseReportHeaderDTO.setAttachmentOidList(Arrays.asList());
                expenseReportHeaderDTO.setAttachments(Arrays.asList());
            }
            // 创建人信息
            if (expenseReportHeaderDTO.getApplicantId().equals(expenseReportHeaderDTO.getCreatedBy())) {
                expenseReportHeaderDTO.setCreatedCode(userById.getEmployeeCode());
                expenseReportHeaderDTO.setCreatedName(userById.getFullName());
            } else {
                userById = organizationService.getUserById(expenseReportHeaderDTO.getCreatedBy());
                expenseReportHeaderDTO.setCreatedCode(userById.getEmployeeCode());
                expenseReportHeaderDTO.setCreatedName(userById.getFullName());
            }
        }

    public Wrapper<ExpenseReportHeader> getHearderWrapper(List<Long>ids,
                                                       Long companyId,
                                                       Long documentTypeId,
                                                       ZonedDateTime reqDateFrom,
                                                       ZonedDateTime reqDateTo,
                                                       Long applicantId,
                                                       Integer status,
                                                       String currencyCode,
                                                       BigDecimal amountFrom,
                                                       BigDecimal amountTo,
                                                       String remark,
                                                       String requisitionNumber,
                                                       Long unitId,
                                                       ZonedDateTime cDateFrom,
                                                       ZonedDateTime cDateTo,
                                                       Long tenantId){
        Wrapper<ExpenseReportHeader> wrapper= new EntityWrapper<ExpenseReportHeader>()
                    .eq(tenantId!=null,"t.tenant_id",tenantId)
                    .in(!CollectionUtils.isEmpty(ids),"t.id",ids)
                    .eq(companyId!=null,"t.company_id",companyId)
                    .eq(documentTypeId!=null,"t.document_type_id",documentTypeId)
                    .gt(reqDateFrom!=null,"t.requisition_date",reqDateFrom)
                    .lt(reqDateTo!=null,"t.requisition_date",reqDateTo)
                    .eq(applicantId!=null,"t.applicant_id",applicantId)
                    .eq(status!=null,"t.status",status)
                    .eq(!StringUtils.isEmpty(currencyCode),"t.currency_code",currencyCode)
                    .gt(amountFrom!=null,"t.total_amount",amountFrom)
                    .lt(amountTo!=null,"t.total_amount",amountTo)
                    .like(!StringUtils.isEmpty(remark),"t.description",remark)
                    .like(!StringUtils.isEmpty(requisitionNumber),"t.requisition_number",requisitionNumber)
                    .eq(unitId!=null,"t.department_id",unitId)
                    .gt(cDateFrom!=null,"t.audit_date",cDateFrom)
                    .lt(cDateTo!=null,"t.audit_date",cDateTo);

        return wrapper;
    }

    public void exportFormExcel(Long companyId,
                                Long documentTypeId,
                                ZonedDateTime reqDateFrom,
                                ZonedDateTime reqDateTo,
                                Long applicantId,
                                Integer status,
                                String currencyCode,
                                BigDecimal amountFrom,
                                BigDecimal amountTo,
                                String remark,
                                String requisitionNumber,
                                Long unitId,
                                BigDecimal paidAmountFrom,
                                BigDecimal paidAmountTo,
                                ZonedDateTime cDateFrom,
                                ZonedDateTime cDateTo,
                                String backlashFlag,
                                Long tenantId,
                                HttpServletResponse response,
                                HttpServletRequest request,
                                ExportConfig exportConfig) throws IOException {
        //jiu.zhao 支付
        /*List<CashTransactionDetailCO> coList = new ArrayList<>();
        coList= paymentService.queryCashTransactionDetailByReport(paidAmountFrom,paidAmountTo,backlashFlag);*/
        List<Long> ids = new ArrayList<>();
        int total = 0;
        Wrapper<ExpenseReportHeader> wrapper = new EntityWrapper<>();
        if (paidAmountFrom != null || paidAmountTo != null || !StringUtils.isEmpty(backlashFlag)) {
            // 判断返回的结果是否为空， 如果不为空
            //获取到id。
            /*if(!CollectionUtils.isEmpty(coList)) {
                ids = coList.stream()
                        .map(CashTransactionDetailCO::getDocumentId)
                        .collect(Collectors.toList());
                
                wrapper=getHearderWrapper(ids,companyId,documentTypeId,reqDateFrom,reqDateTo
                                                ,applicantId,status,currencyCode,amountFrom,amountTo,
                                                remark,requisitionNumber,unitId,cDateFrom,cDateTo,tenantId);
                
            }*/
            
        } else {
            wrapper = getHearderWrapper(ids, companyId, documentTypeId, reqDateFrom, reqDateTo
                    , applicantId, status, currencyCode, amountFrom, amountTo,
                    remark, requisitionNumber, unitId, cDateFrom, cDateTo, tenantId);
            
        }
        total = baseMapper.getCountByCondition(wrapper);
        int total1 = total;
        int availProcessors = Runtime.getRuntime().availableProcessors() / 2;
        Wrapper<ExpenseReportHeader> wrapper1 = wrapper;
        //bo.liu 支付
        /*List<CashTransactionDetailCO> finalCoList = coList;*/
        excelExportService.exportAndDownloadExcel(exportConfig,new ExcelExportHandler<ExpenseReportHeaderDTO,ExpenseReportHeaderDTO>(){
            @Override
            public int getTotal() {
                return total1;
            }

            @Override
            public List<ExpenseReportHeaderDTO> queryDataByPage(Page page) {
                List<ExpenseReportHeaderDTO> reportHeaderDTOS  =   baseMapper.queryReportHeaderByids(wrapper1,page);
                //bo.liu 支付
                /*setReportHeaderDTOSAmount(finalCoList,reportHeaderDTOS);*/
                return reportHeaderDTOS;
            }

            @Override
            public ExpenseReportHeaderDTO toDTO(ExpenseReportHeaderDTO t) {
                return t;
            }

            @Override
            public Class<ExpenseReportHeaderDTO> getEntityClass() {
                return ExpenseReportHeaderDTO.class;
            }
        }, availProcessors, request, response);

    }
        //拼接数据
    public void setReportHeaderDTOSAmount( List<CashTransactionDetailCO> finalCoList,List<ExpenseReportHeaderDTO> reportHeaderDTOS){
        if(!CollectionUtils.isEmpty(finalCoList)){
            for (CashTransactionDetailCO cashTransactionDetailCO : finalCoList
            ) {
                reportHeaderDTOS.stream().forEach(e->{
                    if (cashTransactionDetailCO.getDocumentId().longValue()==e.getId().longValue()){
                        e.setPaidAmount(cashTransactionDetailCO.getAmount());
                        e.setReversedFlag(cashTransactionDetailCO.getReservedStatus());
                        e.setWriteOffAmount(cashTransactionDetailCO.getWriteOffAmount());

                    }
                    setExpenseReportHeaderDto(e);
                });

             }
        }else{
            reportHeaderDTOS.stream().forEach(e->{
                setExpenseReportHeaderDto(e);
            });
        }
    }

    /**
     *  单据邮寄，分页查询已审核且未比对通过的报账单信息
     *
     * @param documentTypeId
     * @param auditDateFrom
     * @param auditDateTo
     * @param applicantId
     * @param status
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param remark
     * @param page
     * @return
     */
    public List<ExpenseReportHeader> getEmailExpenseReports(Long documentTypeId,
                                                            ZonedDateTime auditDateFrom,
                                                            ZonedDateTime auditDateTo,
                                                            Long applicantId,
                                                            Integer status,
                                                            String currencyCode,
                                                            BigDecimal amountFrom,
                                                            BigDecimal amountTo,
                                                            String remark,
                                                            String requisitionNumber,
                                                            Page page) {
//        Long currentUserId = OrgInformationUtil.getCurrentUserId();
        Wrapper<ExpenseReportHeader> wrapper =new EntityWrapper<ExpenseReportHeader>()
//                .eq("created_by", currentUserId)
                .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                .eq("set_of_books_id", OrgInformationUtil.getCurrentSetOfBookId())
                .ne("comparison_flag", "Y")
                .ne("receipt_documents_flag","Y")
                .eq(documentTypeId != null, "document_type_id", documentTypeId)
                .ge(auditDateFrom != null, "audit_date", auditDateFrom)
                .le(auditDateTo != null, "audit_date", auditDateTo)
                .eq(applicantId != null, "applicant_id", applicantId)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                .eq("audit_flag","Y")
                .ge(amountFrom != null, "total_amount", amountFrom)
                .le(amountTo != null, "total_amount", amountTo)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(remark), "description", remark)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(requisitionNumber), "requisition_number", requisitionNumber)
                .orderBy("requisition_number", false);
        List<ExpenseReportHeader> expenseReportHeaders = baseMapper.selectPage(page, wrapper);
        expenseReportHeaders.stream().forEach(expenseReportHeader -> {
            ContactCO userById = organizationService.getUserById(expenseReportHeader.getApplicantId());
            expenseReportHeader.setApplicantCode(userById.getEmployeeCode());
            expenseReportHeader.setApplicantName(userById.getFullName());
            // 单据类型
            ExpenseReportType expenseReportType = expenseReportTypeService.selectById(expenseReportHeader.getDocumentTypeId());
            expenseReportHeader.setDocumentTypeName(expenseReportType.getReportTypeName());
            expenseReportHeader.setFormId(expenseReportType.getFormId());
            ApprovalFormCO approvalFormById = organizationService.getApprovalFormById(expenseReportType.getFormId());
            expenseReportHeader.setFormOid(approvalFormById.getFormOid());
            if(expenseReportHeader.getReceiptDocumentsFlag() == null || "N".equals(expenseReportHeader.getReceiptDocumentsFlag())){
                expenseReportHeader.setReceiptDocumentsFlagDesc("未签收");
            }else{
                expenseReportHeader.setReceiptDocumentsFlagDesc("已签收");
            }
        });
        return expenseReportHeaders;
    }

    /**
     * 根据报销单头ID保存发票行报销记录中的发票袋号码
     * @param expenseReportHeaderIdList 报销单头
     * @param invoiceBagNo 发票袋号码
     */
    public void saveInvoiceLineExpenceInvoiceBagNo(List<Long> expenseReportHeaderIdList, String invoiceBagNo){
        for(Long expenseReportHeaderId : expenseReportHeaderIdList){
            List<InvoiceLineExpence> invoiceLineExpences = invoiceLineExpenceService
                    .getInvoiceLineExpenseByReportHeaderIdAndInvoiceBagConfirmFlag(expenseReportHeaderId, "N", false);
            List<InvoiceLineExpence> list = new ArrayList<InvoiceLineExpence>();
            for(InvoiceLineExpence invoiceLineExpence : invoiceLineExpences){
                invoiceLineExpence.setInvoiceBagNo(invoiceBagNo);
                list.add(invoiceLineExpence);
            }
            if(list.size() > 0){
                invoiceLineExpenceService.updateBatchById(list);
            }
        }
    }

    /**
     * 根据报销单头ID更新报销单确认邮寄
     * @param expenseReportHeaderIdList 报销单头
     */
    public void confirmInvoiceLineExpenceEmail(List<Long> expenseReportHeaderIdList){
        for(Long expenseReportHeaderId : expenseReportHeaderIdList){
            List<InvoiceLineExpence> invoiceLineExpences = invoiceLineExpenceService
                    .getInvoiceLineExpenseByReportHeaderIdAndInvoiceBagConfirmFlag(expenseReportHeaderId, "N", true);
            List<InvoiceLineExpence> list = new ArrayList<InvoiceLineExpence>();
            for(InvoiceLineExpence invoiceLineExpence : invoiceLineExpences){
                invoiceLineExpence.setInvoiceBagConfirmFlag("Y");
                list.add(invoiceLineExpence);
            }
            if(list.size() > 0){
                invoiceLineExpenceService.updateBatchById(list);
            }
        }
    }

    /**
     *  单据签收，分页查询当前员工已经扫描过发票袋号码的报账单信息
     *
     * @param documentTypeId
     * @param applicantId
     * @param status
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param remark
     * @param requisitionNumber
     * @param invoiceBagNo
     * @param receiptDocumentsFlag
     * @param sheerMateFlag
     * @param dealUserId
     * @param page
     * @return
     */
    public List<ExpenseReportHeader> getSignExpenseReports(Long documentTypeId,
                                                            Long applicantId,
                                                            Integer status,
                                                            String currencyCode,
                                                            BigDecimal amountFrom,
                                                            BigDecimal amountTo,
                                                            String remark,
                                                            String requisitionNumber,
                                                            String invoiceBagNo,
                                                            String receiptDocumentsFlag,
                                                            String sheerMateFlag,
                                                            Long dealUserId,
                                                            Page page) {
        Long currentUserId = OrgInformationUtil.getCurrentUserId();
        Wrapper<ExpenseReportHeader> wrapper =new EntityWrapper<ExpenseReportHeader>()
                .eq("ibns.created_by", currentUserId)
                .eq("r.tenant_id", OrgInformationUtil.getCurrentTenantId())
                .eq("r.set_of_books_id", OrgInformationUtil.getCurrentSetOfBookId())
                .eq(documentTypeId != null, "r.document_type_id", documentTypeId)
                .eq(applicantId != null, "r.applicant_id", applicantId)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "r.currency_code", currencyCode)
                .eq("r.audit_flag","Y")
                .ge(amountFrom != null, "r.total_amount", amountFrom)
                .le(amountTo != null, "r.total_amount", amountTo)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(remark), "r.description", remark)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(requisitionNumber), "r.requisition_number", requisitionNumber)
//                .eq(invoiceBagNo != null,"ibns.invoice_bag_no", invoiceBagNo)
                .eq(receiptDocumentsFlag != null,"r.receipt_documents_flag", receiptDocumentsFlag)
                .eq(sheerMateFlag != null,"r.sheer_mate_flag", sheerMateFlag)
                .eq(dealUserId != null,"r.deal_user_id", dealUserId)
                .orderBy("r.requisition_number", false);
        List<ExpenseReportHeader> expenseReportHeaders = baseMapper.getSignExpenseReports(wrapper, page);
        expenseReportHeaders.stream().forEach(expenseReportHeader -> {
            ContactCO userById = organizationService.getUserById(expenseReportHeader.getApplicantId());
            expenseReportHeader.setApplicantCode(userById.getEmployeeCode());
            expenseReportHeader.setApplicantName(userById.getFullName());
            if (expenseReportHeader.getDealUserId() != null){
                ContactCO dealUserById = organizationService.getUserById(expenseReportHeader.getDealUserId());
                expenseReportHeader.setDealUserIdName(dealUserById.getFullName());
            }
            // 单据类型
            ExpenseReportType expenseReportType = expenseReportTypeService.selectById(expenseReportHeader.getDocumentTypeId());
            expenseReportHeader.setDocumentTypeName(expenseReportType.getReportTypeName());
            expenseReportHeader.setFormId(expenseReportType.getFormId());
            ApprovalFormCO approvalFormById = organizationService.getApprovalFormById(expenseReportType.getFormId());
            expenseReportHeader.setFormOid(approvalFormById.getFormOid());
            if(expenseReportHeader.getReceiptDocumentsFlag() == null || "N".equals(expenseReportHeader.getReceiptDocumentsFlag())){
                expenseReportHeader.setReceiptDocumentsFlagDesc("未签收");
            }else{
                expenseReportHeader.setReceiptDocumentsFlagDesc("已签收");
            }
            if(expenseReportHeader.getSheerMateFlag() == null || "N".equals(expenseReportHeader.getSheerMateFlag())){
                expenseReportHeader.setSheerMateFlagDesc("否");
            }else{
                expenseReportHeader.setSheerMateFlagDesc("是");
            }
        });
        return expenseReportHeaders;
    }

    /**
     * 报账单单据签收
     * @param expenseReportHeaderIdList 报销单头
     */
    public void expenseReportHeaderSign(List<Long> expenseReportHeaderIdList){
        Long currentUserId = OrgInformationUtil.getCurrentUserId();
        List<ExpenseReportHeader> expenseReportHeaders = baseMapper.selectBatchIds(expenseReportHeaderIdList);
        if(CollectionUtils.isNotEmpty(expenseReportHeaders)){
            for(ExpenseReportHeader expenseReportHeader : expenseReportHeaders){
                List<InvoiceLineExpence> invoiceLineExpences = invoiceLineExpenceService
                        .getInvoiceLineExpenseByReportHeaderIdAndInvoiceBagConfirmFlag(expenseReportHeader.getId(),
                                "N", false);
                if(invoiceLineExpences.size() > 0){
                    throw new BizException("单据编号为："+expenseReportHeader.getRequisitionNumber()+
                            "的单据，存在未确认的发票袋号码，不允许签收！");
                }
                List<InvoiceLineExpence> invoiceLineExpences1 = invoiceLineExpenceService
                        .getInvoiceLineExpenseByReportHeaderIdAndInvoiceBagConfirmFlag(expenseReportHeader.getId(),
                                "Y", true);
                if(invoiceLineExpences1.size() == 0){
                    throw new BizException("单据编号为："+expenseReportHeader.getRequisitionNumber()+
                            "的单据，不存在已确认的发票袋号码，不允许签收！");
                }
                expenseReportHeader.setDealUserId(currentUserId);
                expenseReportHeader.setReceiptDocumentsFlag("Y");
                baseMapper.updateById(expenseReportHeader);
            }
        }
    }

    /**
     * 报账单单据比对结果更新
     * @param expenseReportHeaderIdList 报销单头集合
     */
    public void updateExpenseReportHeaderComparisonFlag(List<Long> expenseReportHeaderIdList){
        List<ExpenseReportHeader> expenseReportHeaders = baseMapper.selectBatchIds(expenseReportHeaderIdList);
        if(CollectionUtils.isNotEmpty(expenseReportHeaders)){
            for(ExpenseReportHeader expenseReportHeader : expenseReportHeaders){
                List<InvoiceLineExpence> list = invoiceLineExpenceService.getInvoiceLineExpenseByReportHeaderIdAndInvoiceMateFlag(
                        expenseReportHeader.getId(),
                        "N"
                        );
                if(list.size() > 0){
                    throw new BizException("单据编号为："+expenseReportHeader.getRequisitionNumber()+
                            "的单据，存在未匹配的发票行，不允许更新比对结果！");
                }
                expenseReportHeader.setComparisonFlag("Y");
                baseMapper.updateById(expenseReportHeader);
            }
        }
    }

    /**
     * 自动生成报账单
     * @param expenseReportAutoCreateDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long autoCreateExpenseReport(ExpenseReportAutoCreateDTO expenseReportAutoCreateDTO){
        // 生成报账单头
        ExpenseReportType expenseReportType = expenseReportTypeService.selectById(expenseReportAutoCreateDTO.getExpenseReportTypeId());
        ExpenseReportHeaderDTO expenseReportHeaderDTO = new ExpenseReportHeaderDTO();
        List<ExpenseDimension> expenseTypeDimensions = expenseReportTypeService.getExpenseTypeDimension(expenseReportAutoCreateDTO.getExpenseReportTypeId(), null);
        expenseReportHeaderDTO.setExpenseDimensions(expenseTypeDimensions);
        expenseReportHeaderDTO.setCompanyId(OrgInformationUtil.getCurrentCompanyId());
        expenseReportHeaderDTO.setDepartmentId(OrgInformationUtil.getCurrentDepartmentId());
        expenseReportHeaderDTO.setApplicantId(OrgInformationUtil.getCurrentUserId());
        expenseReportHeaderDTO.setDocumentTypeId(expenseReportAutoCreateDTO.getExpenseReportTypeId());
        expenseReportHeaderDTO.setRequisitionDate(ZonedDateTime.now());
        // 单收款方设置收款方信息
        if(BooleanUtils.isNotTrue(expenseReportType.getMultiPayee())){
            if("EMPLOYEE".equals(expenseReportType.getPayeeType())){
                expenseReportHeaderDTO.setPayeeCategory(expenseReportType.getPayeeType());
                expenseReportHeaderDTO.setPayeeId(expenseReportHeaderDTO.getApplicantId());
                UserBankAccountCO bankAccountDTOS = organizationService.getContactPrimaryBankAccountByUserId(expenseReportHeaderDTO.getPayeeId());
                if (!ObjectUtils.isEmpty(bankAccountDTOS)) {
                    expenseReportHeaderDTO.setAccountNumber(bankAccountDTOS.getBankAccountNo());
                    expenseReportHeaderDTO.setAccountName(bankAccountDTOS.getBankAccountName());
                }
            }else if("VENDER".equals(expenseReportType.getPayeeType())){
                expenseReportHeaderDTO.setPayeeCategory(expenseReportType.getPayeeType());
            }
        }
        List<ExpenseDimension> expenseTypeDimension = expenseReportTypeService.getExpenseTypeDimension(expenseReportAutoCreateDTO.getExpenseReportTypeId(), null);
        expenseReportHeaderDTO.setExpenseDimensions(expenseTypeDimension);
        if(CollectionUtils.isNotEmpty(expenseReportAutoCreateDTO.getExpenseBookIds())){
            ExpenseBook expenseBook = expenseBookService.selectById(expenseReportAutoCreateDTO.getExpenseBookIds().get(0));
            expenseReportHeaderDTO.setCurrencyCode(expenseBook.getCurrencyCode());
            expenseReportHeaderDTO.setExchangeRate(expenseBook.getExchangeRate());
        }
        if(CollectionUtils.isNotEmpty(expenseReportAutoCreateDTO.getInvoiceDTOS())){
            InvoiceLine invoiceLine = expenseReportAutoCreateDTO.getInvoiceDTOS().get(0).getInvoiceLineList().get(0);
            expenseReportHeaderDTO.setCurrencyCode(invoiceLine.getCurrencyCode());
            expenseReportHeaderDTO.setExchangeRate(invoiceLine.getExchangeRate());
        }
        ExpenseReportHeader expenseReportHeader = saveExpenseReportHeader(expenseReportHeaderDTO,expenseReportType,true);
        // 生成费用行
        if(CollectionUtils.isNotEmpty(expenseReportAutoCreateDTO.getExpenseBookIds())){
            expenseReportLineService.saveExpenseReportLineFromBook(expenseReportHeader.getId(),expenseReportAutoCreateDTO.getExpenseBookIds());
        }
        if(CollectionUtils.isNotEmpty(expenseReportAutoCreateDTO.getInvoiceDTOS())){
            ExpenseReportInvoiceMatchResultDTO expenseBookByInvoice = createExpenseBookByInvoice(expenseReportAutoCreateDTO.getInvoiceDTOS(),true);
            expenseReportLineService.saveExpenseReportLineFromBook(expenseReportHeader, expenseBookByInvoice.getExpenseBooks());
        }
        return expenseReportHeader.getId();
    }


    /**
     * 根据发票信息创建账本
     * @param invoiceDTOS
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    //@Lock(name = SyncLockPrefix.PUBLIC_REPORT_PIPELINE,lockType = LockType.TRY_LOCK_LIST, listKey = "#expenseReportAutoCreateDTO.![invoiceDTOS].![invoiceHead].![invoiceNo + '-' + invoiceCode]")
    public ExpenseReportInvoiceMatchResultDTO createExpenseBookByInvoice(List<InvoiceDTO> invoiceDTOS, Boolean throwsException){
        ExpenseReportInvoiceMatchResultDTO dto = new ExpenseReportInvoiceMatchResultDTO(null,1001,null,invoiceDTOS,new ArrayList<>());
        for (InvoiceDTO invoiceDTO : invoiceDTOS) {
            //验重及连号校验
            try{
                invoiceHeadService.checkInvoiceCodeInvoiceNoExistsOrContinuation(invoiceDTO.getInvoiceHead().getInvoiceCode(),
                        invoiceDTO.getInvoiceHead().getInvoiceNo(),
                        true);
            } catch (BizException e){
                if(throwsException){
                    throw e;
                }
                // 发票重复
                MessageDTO message = messageService.getMessage(e.getCode(), e.getArgs());
                dto.setResultMessage(message.getKeyDescription());
                if(e.getCode().equals(RespCode.EXPENSE_REPORT_INVOICE_EXISTS)){
                    dto.setStatus(1002);
                    return dto;
                }
                if(e.getCode().equals(RespCode.EXPENSE_REPORT_INVOICE_CONTINUATION)){
                    dto.setStatus(1003);
                }
            }
        }
        // 如果校验通过
        if(1001 == dto.getStatus()){
            invoiceDTOS.stream().forEach(invoiceDTO -> {
                invoiceDTO.getInvoiceHead().setFromBook(false);
                if(StringUtils.isEmpty(invoiceDTO.getInvoiceHead().getCreatedMethod())){
                    invoiceDTO.getInvoiceHead().setCreatedMethod("BY_HAND");
                }
                //  保存发票信息
                invoiceHeadService.insertInvoice(invoiceDTO);
                // 保存账本信息
                List<ExpenseBook> expenseBookByInvoice = expenseBookService.getExpenseBookByInvoice(invoiceDTO);
                dto.getExpenseBooks().addAll(expenseBookByInvoice);
            });
        }
        return dto;
    }

    /**
     * 发票自动匹配费用类型
     * @param invoiceDTOS
     * @return
     */
    //@Lock(name = SyncLockPrefix.INVOICE_CHECK,lockType = LockType.TRY_LOCK_LIST, listKey = "#invoiceDTOS.![invoiceHead].![invoiceNo + '-' + invoiceCode]")
    public ExpenseReportInvoiceMatchResultDTO invoiceAutoMatchExpense(List<InvoiceDTO> invoiceDTOS){
        ExpenseReportInvoiceMatchResultDTO dto = new ExpenseReportInvoiceMatchResultDTO(null,1001,null,invoiceDTOS,null);
        if(CollectionUtils.isNotEmpty(invoiceDTOS)){
            for (InvoiceDTO invoiceDTO : invoiceDTOS) {
                //验重及连号校验
                try{
                    invoiceHeadService.checkInvoiceCodeInvoiceNoExistsOrContinuation(invoiceDTO.getInvoiceHead().getInvoiceCode(), invoiceDTO.getInvoiceHead().getInvoiceNo(),false);
                } catch (BizException e){
                    // 发票重复
                    MessageDTO message = messageService.getMessage(e.getCode(), e.getArgs());
                    dto.setResultMessage(message.getKeyDescription());
                    if(e.getCode().equals(RespCode.EXPENSE_REPORT_INVOICE_EXISTS)){
                        dto.setStatus(1002);
                        return dto;
                    }
                    if(e.getCode().equals(RespCode.EXPENSE_REPORT_INVOICE_CONTINUATION)){
                        dto.setStatus(1003);
                    }
                }
                List<InvoiceLine> invoiceLineList = invoiceDTO.getInvoiceLineList();
                invoiceLineList.stream().forEach(invoiceLine -> {
                    List<InvoiceExpenseTypeRules> invoiceExpenseTypeRules = invoiceExpenseTypeRulesService.selectList(new EntityWrapper<InvoiceExpenseTypeRules>()
                            .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                            .eq("set_of_books_id", OrgInformationUtil.getCurrentSetOfBookId())
                            .eq("enabled", true)
                            .and("{0} like concat('%',concat(goods_name,'%'))",new String[]{invoiceLine.getGoodsName()})
                            .and("(start_date is null or start_date <= {0}) and (end_date is null or end_date >= {1})", new ZonedDateTime[]{
                                    TypeConversionUtils.getEndTimeForDayYYMMDD(TypeConversionUtils.timeToString(ZonedDateTime.now())),
                                    TypeConversionUtils.getStartTimeForDayYYMMDD(TypeConversionUtils.timeToString(ZonedDateTime.now()))}));
                    if(invoiceExpenseTypeRules.size() == 1){
                        InvoiceExpenseTypeRules invoiceExpenseTypeRule = invoiceExpenseTypeRules.get(0);
                        ExpenseType expenseType = expenseTypeService.selectById(invoiceExpenseTypeRule.getExpenseTypeId());
                        invoiceLine.setExpenseTypeId(expenseType.getId());
                        invoiceLine.setExpenseTypeName(expenseType.getName());
                        invoiceLine.setExpenseTypeIcon(expenseType.getIconUrl());
                        invoiceLine.setMatchExpenseTypeIds(Arrays.asList());
                    }else{
                        invoiceLine.setMatchExpenseTypeIds(invoiceExpenseTypeRules.stream().map(InvoiceExpenseTypeRules::getExpenseTypeId).collect(Collectors.toList()));
                    }
                });
            }
        }
        return dto;
    }

    /**
     * 根据账本取当前用户有权限创建，且有相应费用类型的报账单类型
     * @param expenseBookIds
     * @return
     */
    public List<ExpenseReportType> getExpenseReportTypeByExpenseBooks(List<Long> expenseBookIds){
        List<ExpenseBook> expenseBooks = expenseBookService.selectBatchIds(expenseBookIds);
        if(CollectionUtils.isNotEmpty(expenseBooks)){
            ExpenseBook expenseBook = expenseBooks.get(0);
            Set<Long> collect = expenseBooks.stream().map(e -> {
                if (!e.getCurrencyCode().equals(expenseBook.getCurrencyCode())) {
                    throw new BizException(RespCode.EXPENSE_REPORT_AUTO_CREATE_CURRENCY_ERROR);
                }
                return e.getExpenseTypeId();
            }).collect(Collectors.toSet());
            List<ExpenseReportType> expenseReportTypeByExpenseTypeIds = expenseReportTypeService.getExpenseReportTypeByExpenseTypeIds(collect);
            return expenseReportTypeByExpenseTypeIds;
        }
        return Arrays.asList();
    }

    /**
     * 根据发票对应的费用类型，获取相应的报账单类型
     * @param invoiceDTOS
     * @return
     */
    public List<ExpenseReportType> getExpenseReportTypeByInvoices(List<InvoiceDTO> invoiceDTOS){
        List<Long> expenseTypeIds = new ArrayList<>();
        invoiceDTOS.stream().forEach(invoiceDTO -> {
            List<InvoiceLine> invoiceLineList = invoiceDTO.getInvoiceLineList();
            invoiceLineList.stream().forEach(invoiceLine -> {
                expenseTypeIds.add(invoiceLine.getExpenseTypeId());
            });
        });
        if(CollectionUtils.isNotEmpty(expenseTypeIds)){
            return expenseReportTypeService.getExpenseReportTypeByExpenseTypeIds(expenseTypeIds);
        }
        return Arrays.asList();
    }

    /**
     * 单据签收-单据退回
     * @param rejectType
     * @param rejectReason
     * @param expenseReportHeaderIdList
     */
    public void rejectSignReports(String rejectType, String rejectReason, List<Long> expenseReportHeaderIdList) {

        if (rejectType.equals("全部退回")) {
            //TODO 清空影像比对匹配表 单据签收中比对功能未完成 暂时无法开发

            for(Long expenseReportHeaderId : expenseReportHeaderIdList){
                //更新发票行报销记录表 发票袋号码,发票袋号码确认标志
                List<InvoiceLineExpence> invoiceLineExpences = invoiceLineExpenceService
                        .getInvoiceLineExpenseByReportHeaderId(expenseReportHeaderId);
                List<InvoiceLineExpence> list = new ArrayList<InvoiceLineExpence>();
                for(InvoiceLineExpence invoiceLineExpence : invoiceLineExpences){
                    invoiceLineExpence.setInvoiceBagNo(null);
                    invoiceLineExpence.setInvoiceBagConfirmFlag("N");
                    list.add(invoiceLineExpence);
                }
                if(list.size() > 0){
                    invoiceLineExpenceService.updateBatchById(list);
                }

                //拒绝原因
                rejectReason = rejectType + "-" + rejectReason;

                ExpenseReportHeader header = this.selectById(expenseReportHeaderId);
                if (header == null) {
                    throw new BizException(RespCode.EXPENSE_REPORT_HEADER_IS_NUTT);
                }

                //TODO 单据退回目前与工作台退回至申请人完全相同 如需状态变更 需与工作流沟通
                //单据退回 如果有预算需要释放，
                rollBackBudget(expenseReportHeaderId);
                //删除费用申请释放信息
                expenseRequisitionReleaseService.deleteExpenseRequisitionReleaseMsg(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory(), expenseReportHeaderId, null, null);
                //释放合同关联信息
                deleteContractDocumentRelations(header);
                //核销回滚
                paymentService.updateAuditChangeWriteOffStatus(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getCategory(), expenseReportHeaderId,LoginInformationUtil.getCurrentUserId(),-1);
                //删除凭证
                if (BooleanUtils.isTrue(header.getJeCreationStatus())) {
                    //accountingService.deleteExpReportGeneralLedgerJournalDataByHeaderId(header.getId());
                    header.setJeCreationStatus(false);
                    header.setJeCreationDate(null);
                }
                header.setStatus(DocumentOperationEnum.APPROVAL_REJECT.getId());
                // 删除工作流实列
                workflowClient.updateDocumentStatus(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getKey(),
                        UUID.fromString(header.getDocumentOid()), DocumentOperationEnum.AUDIT_REJECT);
                //计入工作流日志
                CommonApprovalHistoryCO approvalHistoryCO = new CommonApprovalHistoryCO();
                approvalHistoryCO.setEntityOid(UUID.fromString(header.getDocumentOid()));
                approvalHistoryCO.setEntityType(ExpenseDocumentTypeEnum.PUBLIC_REPORT.getKey());
                approvalHistoryCO.setOperatorOid(OrgInformationUtil.getCurrentUserOid());
                approvalHistoryCO.setOperationDetail(rejectReason);
                approvalHistoryCO.setOperationType(1009);
                approvalHistoryCO.setOperation(1004);
                workflowClient.saveHistory(approvalHistoryCO);
                //更新报账单 是否通过比对标志
                header.setComparisonFlag("N");
                //更新报账单 审核通过标志
                header.setAuditFlag("N");
                this.updateById(header);
            }
        }else {
            //TODO 补寄逻辑 单据签收中比对功能未完成 暂时无法开发
        }

    }
}
