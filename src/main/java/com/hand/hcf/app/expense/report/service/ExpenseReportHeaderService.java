package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.expense.application.domain.ExpenseRequisitionRelease;
import com.hand.hcf.app.expense.application.service.ExpenseRequisitionReleaseService;
import com.hand.hcf.app.expense.common.domain.enums.DocumentTypeEnum;
import com.hand.hcf.app.expense.common.dto.BudgetCheckResultDTO;
import com.hand.hcf.app.expense.common.externalApi.ContractService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.externalApi.PaymentService;
import com.hand.hcf.app.expense.common.externalApi.WorkBenchService;
import com.hand.hcf.app.expense.common.service.CommonService;
import com.hand.hcf.app.expense.common.utils.DimensionUtils;
import com.hand.hcf.app.expense.common.utils.ParameterConstant;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.common.utils.SyncLockPrefix;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLine;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineDist;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineExpence;
import com.hand.hcf.app.expense.invoice.service.InvoiceHeadService;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineDistService;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineExpenceService;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineService;
import com.hand.hcf.app.expense.report.domain.*;
import com.hand.hcf.app.expense.report.dto.ExpenseReportHeaderDTO;
import com.hand.hcf.app.expense.report.dto.ExpenseReportTypeDTO;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportHeaderMapper;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.domain.ExpenseDocumentField;
import com.hand.hcf.app.expense.type.service.ExpenseDimensionService;
import com.hand.hcf.app.expense.type.service.ExpenseDocumentFieldService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.client.com.CompanyCO;
import com.hand.hcf.app.mdata.client.contact.ContactCO;
import com.hand.hcf.app.mdata.client.currency.CurrencyRateCO;
import com.hand.hcf.app.mdata.client.department.DepartmentCO;
import com.hand.hcf.app.mdata.client.period.PeriodCO;
import com.hand.hcf.app.mdata.client.supplier.dto.VendorInfoCO;
import com.hand.hcf.app.mdata.client.workflow.WorkflowClient;
import com.hand.hcf.app.mdata.client.workflow.dto.ApprovalDocumentCO;
import com.hand.hcf.app.mdata.client.workflow.dto.ApprovalFormCO;
import com.hand.hcf.app.mdata.client.workflow.dto.ApprovalResultCO;
import com.hand.hcf.app.mdata.client.workflow.dto.WorkFlowDocumentRefCO;
import com.hand.hcf.app.mdata.client.workflow.enums.DocumentOperationEnum;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;
import com.hand.hcf.app.workflow.workflow.dto.ApprovalDocumentCO;
import com.hand.hcf.app.workflow.workflow.dto.ApprovalResultCO;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.redisLock.annotations.LockedObject;
import com.hand.hcf.core.redisLock.annotations.SyncLock;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.DateUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 14:39
 * @remark
 */
@Service
public class ExpenseReportHeaderService extends BaseService<ExpenseReportHeaderMapper,ExpenseReportHeader>{

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

    @Value("${spring.application.name:}")
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

    //jiu.zhao 核算
    /*@Autowired
    private AccountingClient accountingClient;*/

    /**
     * 保存费用类型
     * @param expenseReportHeaderDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @SyncLock(lockPrefix = SyncLockPrefix.PUBLIC_REPORT)
    public ExpenseReportHeader saveExpenseReportHeader(ExpenseReportHeaderDTO expenseReportHeaderDTO){
        ExpenseReportType expenseReportType = expenseReportTypeService.selectById(expenseReportHeaderDTO.getDocumentTypeId());
        if (!expenseReportType.getMultiPayee()) {
            if (expenseReportHeaderDTO.getAccountNumber() == null || expenseReportHeaderDTO.getAccountName() == null
                    || expenseReportHeaderDTO.getPayeeId() == null || expenseReportHeaderDTO.getPayeeCategory() == null){
                throw new BizException(RespCode.EXPENSE_REPORT_ACCOUNT_INFO_IS_NOT_COMPLETE);
            }
        }
        if (BooleanUtils.isTrue(expenseReportType.getAssociateContract()) && BooleanUtils.isTrue(expenseReportType.getContractRequired())) {
            if (expenseReportHeaderDTO.getContractHeaderId() == null) {
                throw new BizException(RespCode.EXPENSE_REPORT_CONTRACT_IS_NULL);
            }
        }
        ExpenseReportHeader expenseReportHeader = new ExpenseReportHeader();
        //设置默认值
        if(expenseReportHeaderDTO.getId() == null){
            expenseReportHeaderDTO.setTenantId(
                    expenseReportHeaderDTO.getTenantId() != null ? expenseReportHeaderDTO.getTenantId() : OrgInformationUtil.getCurrentTenantId());
            expenseReportHeaderDTO.setSetOfBooksId(
                    expenseReportHeaderDTO.getSetOfBooksId() != null ? expenseReportHeaderDTO.getSetOfBooksId() : OrgInformationUtil.getCurrentSetOfBookId());
            expenseReportHeaderDTO.setRequisitionNumber(
                    commonService.getCoding(DocumentTypeEnum.PUBLIC_REPORT.getCategory(), expenseReportHeaderDTO.getCompanyId(), null));
            expenseReportHeaderDTO.setTotalAmount(BigDecimal.ZERO);
            expenseReportHeaderDTO.setFunctionalAmount(BigDecimal.ZERO);
            expenseReportHeaderDTO.setRequisitionDate(
                    expenseReportHeaderDTO.getRequisitionDate() == null ? ZonedDateTime.now() : expenseReportHeaderDTO.getRequisitionDate());
            expenseReportHeaderDTO.setStatus(DocumentOperationEnum.GENERATE.getId());
            expenseReportHeaderDTO.setAuditFlag("N");
            expenseReportHeaderDTO.setDocumentOid(UUID.randomUUID().toString());
        }else{
            //校验状态
            checkDocumentStatus(0, expenseReportHeaderDTO.getStatus());
        }
        // 更新计划付款行信息
        if(expenseReportHeaderDTO.getId() != null){
            updateScheduleLine(selectById(expenseReportHeaderDTO.getId()),expenseReportHeaderDTO);
        }
        BeanUtils.copyProperties(expenseReportHeaderDTO,expenseReportHeader);
        insertOrUpdate(expenseReportHeader);
        // 维护维度布局
        List<ExpenseDimension> expenseDimensions = expenseReportHeaderDTO.getExpenseDimensions();
        if(expenseDimensions != null) {
            expenseDimensions.stream().forEach(e -> {
                e.setHeaderId(expenseReportHeader.getId());
                e.setDocumentType(DocumentTypeEnum.PUBLIC_REPORT.getKey());
            });
            expenseDimensionService.insertOrUpdateBatch(expenseDimensions);
        }
        return expenseReportHeader;
    }

    /**
     * 动态修改计划付款行信息
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
                    (!oldExpenseReportHeader.getPayeeCategory().equals(expenseReportDTO.getPayeeCategory())
                    || !oldExpenseReportHeader.getPayeeId().equals(expenseReportDTO.getPayeeId()));
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
     * @param headerId
     */
    @Transactional(rollbackFor = Exception.class)
    @SyncLock(lockPrefix = SyncLockPrefix.PUBLIC_REPORT)
    public void deleteExpenseReportHeaderById(@LockedObject Long headerId){
        ExpenseReportHeader expenseReportHeader = selectById(headerId);
        if(expenseReportHeader != null){
            // 判断单据状态 非编辑中、撤回、拒绝的单据，都不能删除
            if(!(expenseReportHeader.getStatus().equals(DocumentOperationEnum.GENERATE.getId())
                || expenseReportHeader.getStatus().equals(DocumentOperationEnum.WITHDRAW.getId())
                    || expenseReportHeader.getStatus().equals(DocumentOperationEnum.APPROVAL_REJECT.getId())
                    || expenseReportHeader.getStatus().equals(DocumentOperationEnum.CANCEL.getId()))){
                throw new BizException(RespCode.EXPENSE_REPORT_CANNOT_DELETED,new String[]{expenseReportHeader.getRequisitionNumber()});
            }
            // 删除field
            documentFieldService.delete(new EntityWrapper<ExpenseDocumentField>()
                    .eq("header_id", headerId)
                    .eq("document_type", DocumentTypeEnum.PUBLIC_REPORT.getKey()));
            // 删除维度
            expenseDimensionService.delete(new EntityWrapper<ExpenseDimension>()
                    .eq("header_id", headerId)
                    .eq("document_type", DocumentTypeEnum.PUBLIC_REPORT.getKey()));
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
        }
    }

    /**
     * 获取单据头信息
     * @param headerId
     * @return
     */
    public ExpenseReportHeaderDTO getExpenseReportById(Long headerId){
        ExpenseReportHeader expenseReportHeader = selectById(headerId);
        ExpenseReportHeaderDTO expenseReportHeaderDTO = new ExpenseReportHeaderDTO();
        BeanUtils.copyProperties(expenseReportHeader,expenseReportHeaderDTO);
        List<ExpenseDimension> expenseDimensions =
                expenseDimensionService.listDimensionByHeaderIdAndType(headerId, DocumentTypeEnum.PUBLIC_REPORT.getKey(), null);
        expenseReportHeaderDTO.setExpenseDimensions(expenseDimensions);
        // 公司信息
        CompanyCO companyById = organizationService.getCompanyById(expenseReportHeaderDTO.getCompanyId());
        expenseReportHeaderDTO.setCompanyCode(companyById.getCompanyCode());
        expenseReportHeaderDTO.setCompanyName(companyById.getName());
        // 部门信息
        DepartmentCO departmentById = organizationService.getDepartmentById(expenseReportHeaderDTO.getDepartmentId());
        expenseReportHeaderDTO.setDepartmentCode(departmentById.getDepartmentCode());
        expenseReportHeaderDTO.setDepartmentName(departmentById.getName());
        // 申请人信息
        ContactCO userById = organizationService.getUserById(expenseReportHeaderDTO.getApplicantId());
        expenseReportHeaderDTO.setApplicantCode(userById.getEmployeeCode());
        expenseReportHeaderDTO.setApplicantName(userById.getFullName());
        // 单据类型
        ExpenseReportType expenseReportType = expenseReportTypeService.selectById(expenseReportHeaderDTO.getDocumentTypeId());
        expenseReportHeaderDTO.setDocumentTypeName(expenseReportType.getReportTypeName());
        expenseReportHeaderDTO.setFormId(expenseReportType.getFormId());
        ApprovalFormCO approvalFormById = organizationService.getApprovalFormById(expenseReportType.getFormId());
        expenseReportHeaderDTO.setFormOid(approvalFormById.getFormOid());
        // 创建人信息
        if(expenseReportHeaderDTO.getApplicantId().equals(expenseReportHeaderDTO.getCreatedBy())){
            expenseReportHeaderDTO.setCreatedCode(userById.getEmployeeCode());
            expenseReportHeaderDTO.setCreatedName(userById.getFullName());
        }else{
            userById = organizationService.getUserById(expenseReportHeaderDTO.getCreatedBy());
            expenseReportHeaderDTO.setCreatedCode(userById.getEmployeeCode());
            expenseReportHeaderDTO.setCreatedName(userById.getFullName());
        }
        // 合同信息
        //jiu.zhao 合同
        /*if(expenseReportHeaderDTO.getContractHeaderId() != null){
            List<ContractHeaderCO> contractHeaderCOS = contractService.listContractHeadersByIds(Arrays.asList(expenseReportHeaderDTO.getContractHeaderId()));
            if(CollectionUtils.isNotEmpty(contractHeaderCOS)){
                expenseReportHeaderDTO.setContractNumber(contractHeaderCOS.get(0).getContractNumber());

        }*/
        // 收款方信息
        if(expenseReportHeaderDTO.getPayeeCategory() != null ) {
            if (expenseReportHeaderDTO.getPayeeCategory().equals("EMPLOYEE")) {
                if (expenseReportHeaderDTO.getPayeeId() != null) {
                    ContactCO contactCO = organizationService.getUserById(expenseReportHeaderDTO.getPayeeId());
                    expenseReportHeaderDTO.setPayCode(contactCO.getEmployeeCode());
                    expenseReportHeaderDTO.setPayName(contactCO.getFullName());
                }
            } else if (expenseReportHeaderDTO.getPayeeCategory().equals("VENDER")) {
                if (expenseReportHeaderDTO.getPayeeId() != null) {
                    VendorInfoCO info = organizationService.getOneVendorInfoById(expenseReportHeaderDTO.getPayeeId());
                    expenseReportHeaderDTO.setPayCode(info.getVenderCode());
                    expenseReportHeaderDTO.setPayName(info.getVenNickname());
                }
            }
        }
        // 币种
        CurrencyRateCO currencyRate = organizationService.getForeignCurrencyByCode(null, expenseReportHeaderDTO.getCurrencyCode(), expenseReportHeaderDTO.getSetOfBooksId());
        expenseReportHeaderDTO.setCurrencyName(currencyRate.getCurrencyName());
        //税金分摊方式
        String expTaxDist = organizationService.getParameterValue(expenseReportHeaderDTO.getCompanyId(),
                expenseReportHeaderDTO.getSetOfBooksId(), ParameterConstant.EXP_TAX_DIST);
        expenseReportHeaderDTO.setExpTaxDist(expTaxDist);
        return expenseReportHeaderDTO;
    }

    /**
     * 我的报账单
     * @param documentTypeId
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param applicantId
     * @param status
     * @param currencyCode
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
                                                   Integer status,
                                                   String currencyCode,
                                                   BigDecimal amountFrom,
                                                   BigDecimal amountTo,
                                                   String remark,
                                                   String requisitionNumber,
                                                   Page page){
        Long currentUserId = OrgInformationUtil.getCurrentUserId();
        List<ExpenseReportHeader> expenseReportHeaders = baseMapper.selectPage(page, new EntityWrapper<ExpenseReportHeader>()
                .eq("created_by", currentUserId)
                .eq(documentTypeId != null, "document_type_id", documentTypeId)
                .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                .eq(applicantId != null, "applicant_id", applicantId)
                .eq(status != null, "status", status)
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(currencyCode), "currency_code", currencyCode)
                .ge(amountFrom != null, "total_amount", amountFrom)
                .le(amountTo != null, "total_amount", amountTo)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(remark), "description", remark)
                .like(org.apache.commons.lang3.StringUtils.isNotEmpty(requisitionNumber), "requisition_number", requisitionNumber)
                .orderBy("requisition_number",false)
        );
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
        });
        return expenseReportHeaders;
    }

    /**
     * 调用预算模块预算校验接口
     * @param expenseReportHeader 报账单头信息
     * @return
     */
    public BudgetCheckResultDTO checkBudget(ExpenseReportHeader expenseReportHeader,
                                            boolean ignoreWarningFlag){
        BudgetCheckMessageCO param = new BudgetCheckMessageCO();
        List<BudgetReserveCO> budgetReserveDtoList;
        List<ExpenseReportDist> expenseReportDistList = expenseReportDistService.selectList(new EntityWrapper<ExpenseReportDist>().eq("exp_report_header_id", expenseReportHeader.getId()));
        budgetReserveDtoList = expenseReportDistToBudgetReserveCO(expenseReportHeader,expenseReportDistList);
        param.setTenantId(expenseReportHeader.getTenantId());
        param.setSetOfBooksId(expenseReportHeader.getSetOfBooksId());
        param.setBudgetReserveDtoList(budgetReserveDtoList);
        param.setIgnoreWarningFlag(ignoreWarningFlag ? "Y" : "N");
        param.setIncludeReleaseFlag("Y");
        return commonService.checkBudget(param);
    }

    /**
     * expenseReportDist转换为BudgetReserveCO
     * @param expenseReportDistList
     * @return
     */
    private List<BudgetReserveCO> expenseReportDistToBudgetReserveCO(ExpenseReportHeader expenseReportHeader,List<ExpenseReportDist> expenseReportDistList){
        List<BudgetReserveCO> budgetReserveCOList = new ArrayList<>();
        for (ExpenseReportDist expenseReportDist : expenseReportDistList) {
            ExpenseReportLine expenseReportLine = expenseReportLineService.selectById(expenseReportDist.getExpReportLineId());
            BudgetReserveCO budgetReserveCO = new BudgetReserveCO();
            budgetReserveCO.setCompanyId(expenseReportDist.getCompanyId());
            budgetReserveCO.setCompanyCode(organizationService.getCompanyById(expenseReportDist.getCompanyId()).getCompanyCode());
            budgetReserveCO.setBusinessType(DocumentTypeEnum.PUBLIC_REPORT.getCategory());
            budgetReserveCO.setReserveFlag("U");
            budgetReserveCO.setStatus("N");
            budgetReserveCO.setManualFlag("N");
            budgetReserveCO.setDocumentId(expenseReportDist.getExpReportHeaderId());
            budgetReserveCO.setDocumentLineId(expenseReportDist.getId());
            budgetReserveCO.setCurrency(expenseReportDist.getCurrencyCode());
            budgetReserveCO.setExchangeRate(expenseReportDist.getExchangeRate().doubleValue());
            budgetReserveCO.setAmount(expenseReportDist.getAmount());
            budgetReserveCO.setFunctionalAmount(expenseReportDist.getFunctionAmount());
            budgetReserveCO.setQuantity(expenseReportLine.getQuantity() == null ? 1 : expenseReportLine.getQuantity());
            budgetReserveCO.setUom(expenseReportLine.getUom());
            budgetReserveCO.setUnitId(expenseReportDist.getDepartmentId());
            budgetReserveCO.setUnitCode(organizationService.getDepartmentById(expenseReportDist.getDepartmentId()).getDepartmentCode());
            budgetReserveCO.setEmployeeId(expenseReportHeader.getApplicantId());
            budgetReserveCO.setEmployeeCode(organizationService.getUserById(expenseReportHeader.getApplicantId()).getEmployeeCode());
            budgetReserveCO.setCreatedBy(expenseReportDist.getCreatedBy());
            DimensionUtils.setDimensionMessage(expenseReportDist,budgetReserveCO,organizationService,true,true,false);
            budgetReserveCO.setDocumentItemSourceType("EXPENSE_TYPE");
            budgetReserveCO.setDocumentItemSourceId(expenseReportDist.getExpenseTypeId());
            // 申请单信息
            List<ExpenseRequisitionRelease> expenseRequisitionReleaseByRelatedDocumentMsg = expenseRequisitionReleaseService.getExpenseRequisitionReleaseByRelatedDocumentMsg(DocumentTypeEnum.PUBLIC_REPORT.getCategory(),
                    expenseReportDist.getExpReportHeaderId(),
                    null,
                    expenseReportDist.getId());
            if(CollectionUtils.isNotEmpty(expenseRequisitionReleaseByRelatedDocumentMsg)){
                BudgetReportRequisitionReleaseCO budgetReportRequisitionReleaseCO = expenseRequisitionReleaseByRelatedDocumentMsg.stream().map(e -> {
                    BudgetReportRequisitionReleaseCO releaseCO = new BudgetReportRequisitionReleaseCO();
                    releaseCO.setReleaseBusinessType(DocumentTypeEnum.EXP_REQUISITION.getCategory());
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
    }

    /**
     * 费用对接工作台
     * @param expenseReportHeader
     */
    public void pushToWorkBranchByHeaderId(ExpenseReportHeader expenseReportHeader){
        if(expenseReportHeader == null){
            throw new BizException(RespCode.EXPENSE_REPORT_HEADER_IS_NUTT);
        }
        List<ExpenseReportLine> reportLineList = expenseReportLineService.selectList(new EntityWrapper<ExpenseReportLine>()
                .eq("exp_report_header_id", expenseReportHeader.getId()));
        BusinessDataCO businessDataCO = new BusinessDataCO();
        businessDataCO.setOtherLines(null);
        businessDataCO.setTenantId(expenseReportHeader.getTenantId());
        businessDataCO.setSobId(expenseReportHeader.getSetOfBooksId());
        businessDataCO.setBusinessTypeCode("EXPENSE_TYPE");
        businessDataCO.setDocumentId(expenseReportHeader.getId());
        businessDataCO.setDocumentNumber(expenseReportHeader.getRequisitionNumber());
        businessDataCO.setCompanyId(expenseReportHeader.getCompanyId());
        CompanyCO company = organizationService.getCompanyById(expenseReportHeader.getCompanyId());
        if(company != null){
            businessDataCO.setCompanyCode(company.getCompanyCode());
        }
        businessDataCO.setDepartmentId(expenseReportHeader.getDepartmentId());
        DepartmentCO department = organizationService.getDepartmentById(expenseReportHeader.getDepartmentId());
        if(department != null){
            businessDataCO.setDepartmentCode(department.getDepartmentCode());
        }
        businessDataCO.setEmployeeId(expenseReportHeader.getApplicantId());
        businessDataCO.setCurrencyCode(expenseReportHeader.getCurrencyCode());
        businessDataCO.setExchangeRate(expenseReportHeader.getExchangeRate());
        businessDataCO.setAmount(expenseReportHeader.getTotalAmount());
        businessDataCO.setFunctionalAmount(expenseReportHeader.getFunctionalAmount());
        businessDataCO.setDocumentTypeId(expenseReportHeader.getDocumentTypeId());
        businessDataCO.setDocumentCategory(DocumentTypeEnum.PUBLIC_REPORT.getCategory());
        workBenchService.pushReportDataToWorkBranch(businessDataCO);
    }

    /**
     * 费用对接支付
     * @param expenseReportHeader
     */
    public void saveDataToPayment(ExpenseReportHeader expenseReportHeader){
        if(expenseReportHeader == null){
            throw new BizException(RespCode.EXPENSE_REPORT_HEADER_IS_NUTT);
        }
        List<ExpenseReportPaymentSchedule> expenseReportPaymentSchedules = expenseReportPaymentScheduleService.selectList(new EntityWrapper<ExpenseReportPaymentSchedule>()
                .eq("exp_report_header_id", expenseReportHeader.getId()).orderBy("created_date"));
        if(CollectionUtils.isEmpty(expenseReportPaymentSchedules)){
            return;
        }
        List<CashTransactionDataCreateCO> result = new ArrayList<>();
        expenseReportPaymentSchedules.forEach(paymentSchedule -> {
            CashTransactionDataCreateCO co = new CashTransactionDataCreateCO();
            co.setTenantId(paymentSchedule.getTenantId());
            co.setDocumentCategory(DocumentTypeEnum.PUBLIC_REPORT.getCategory());
            co.setDocumentHeaderId(paymentSchedule.getExpReportHeaderId());
            co.setDocumentTypeId(expenseReportHeader.getDocumentTypeId());
            co.setDocumentNumber(expenseReportHeader.getRequisitionNumber());
            co.setEmployeeId(paymentSchedule.getApplicantId());
            ContactCO contact = organizationService.getUserById(expenseReportHeader.getApplicantId());
            if(contact != null){
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
            if(paymentSchedule.getPayeeCategory() != null ) {
                if (paymentSchedule.getPayeeCategory().equals("EMPLOYEE")) {
                    ContactCO user = organizationService.getUserById(paymentSchedule.getPayeeId());
                    if(user != null) {
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
            co.setRemark(paymentSchedule.getDescription());
            co.setFrozenFlag("Y".equals(paymentSchedule.getFrozenFlag()) ? true: false);
            //单据OID
            co.setEntityOid(expenseReportHeader.getDocumentOid());
            //实体类型
            co.setEntityType(DocumentTypeEnum.PUBLIC_REPORT.getKey());
            co.setRequisitionPaymentDate(paymentSchedule.getPaymentScheduleDate());
            result.add(co);
        });
        paymentService.saveDataToPayment(result);
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
            // 审核
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
     * 报账单提交
     * @param workFlowDocumentRef   工作流信息
     * @param ignoreWarningFlag     是否忽略警告标志
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    @SyncLock(lockPrefix = SyncLockPrefix.PUBLIC_REPORT)
    public BudgetCheckResultDTO submit(@LockedObject("documentId") WorkFlowDocumentRefCO workFlowDocumentRef,
                       Boolean ignoreWarningFlag) {

        ExpenseReportHeader expenseReportHeader = selectById(workFlowDocumentRef.getDocumentId());
        if(expenseReportHeader == null){
            throw new BizException(RespCode.EXPENSE_REPORT_HEADER_IS_NUTT);
        }
        //校验状态
        checkDocumentStatus(DocumentOperationEnum.APPROVAL.getId(), expenseReportHeader.getStatus());
        expenseReportHeader.setStatus(DocumentOperationEnum.APPROVAL.getId());
        ExpenseReportTypeDTO expenseReportType = expenseReportTypeService.getExpenseReportType(expenseReportHeader.getDocumentTypeId(), expenseReportHeader.getId());
        //校验预算
        BudgetCheckResultDTO budgetCheckResultDTO;
        if (expenseReportType.getBudgetFlag()) {
            // 只有当启用预算
            budgetCheckResultDTO = checkBudget(
                    expenseReportHeader, ignoreWarningFlag);
        } else {
            budgetCheckResultDTO = BudgetCheckResultDTO.ok();
        }
        if (budgetCheckResultDTO.getPassFlag()) {
            sendWorkFlow(expenseReportHeader, expenseReportType, workFlowDocumentRef);
        } else {
            //校验失败，回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return budgetCheckResultDTO;
    }

    /**
     * 推送至工作流
     * @param expenseReportHeader
     * @param expenseReportType
     * @param workFlowDocumentRef
     */
    private void sendWorkFlow(ExpenseReportHeader expenseReportHeader,
                             ExpenseReportTypeDTO expenseReportType,
                             WorkFlowDocumentRefCO workFlowDocumentRef){
        if(expenseReportType == null){
            return;
        }
        DepartmentCO departmentCO = organizationService.getDepartmentById(expenseReportHeader.getDepartmentId());
        UUID unitOid = null;
        if(departmentCO != null){
            unitOid = departmentCO.getDepartmentOid();
        }

        CompanyCO companyCO = organizationService.getCompanyById(expenseReportHeader.getCompanyId());
        Long companyId = null;
        if(companyCO != null){
            companyId = companyCO.getId();
        }

        ContactCO contactCO = organizationService.getUserById(expenseReportHeader.getApplicantId());
        UUID applicantOid = null;
        if(contactCO != null){
            applicantOid = UUID.fromString(contactCO.getUserOid());
        }

        ApprovalFormCO approvalFormCO = organizationService.getApprovalFormById(expenseReportType.getFormId());
        UUID formOid = null;
        if (approvalFormCO != null) {
            formOid =  approvalFormCO.getFormOid();
        }

        // 设置提交的参数
        ApprovalDocumentCO submitData = new ApprovalDocumentCO();
        submitData.setDocumentId(expenseReportHeader.getId()); // 单据id
        submitData.setDocumentOid(UUID.fromString(expenseReportHeader.getDocumentOid())); // 单据oid
        submitData.setDocumentNumber(expenseReportHeader.getRequisitionNumber()); // 单据编号
        submitData.setDocumentName(null); // 单据名称
        submitData.setDocumentCategory(DocumentTypeEnum.PUBLIC_REPORT.getKey()); // 单据类别
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
        submitData.setDestinationService(applicationName); // 注册到Eureka中的名称

        //调用工作流的三方接口进行提交
        ApprovalResultCO submitResult = workflowClient.submitWorkflow(submitData);

        if (Boolean.TRUE.equals(submitResult.getSuccess())){
            Integer approvalStatus = submitResult.getStatus();

            if (DocumentOperationEnum.APPROVAL.getId().equals(approvalStatus)) {
                //提交成功，更新单据的状态
                updateById(expenseReportHeader);
            } else {
                updateDocumentStatus(expenseReportHeader.getId(), approvalStatus, "");
            }
        } else {
            throw new BizException(submitResult.getError());
        }
    }

    /**
     * @param headerId     申请单头ID
     * @param status       状态
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    @SyncLock(lockPrefix = SyncLockPrefix.PUBLIC_REPORT)
    public void updateDocumentStatus(@LockedObject Long headerId, Integer status, String approvalText) {
        ExpenseReportHeader header = this.selectById(headerId);
        if (header == null) {
            throw new BizException(RespCode.EXPENSE_REPORT_HEADER_IS_NUTT);
        }
        header.setStatus(status);
        if (DocumentOperationEnum.WITHDRAW.getId().equals(status) || DocumentOperationEnum.APPROVAL_REJECT.getId().equals(status)) {
            //审批拒绝  撤回 如果有预算需要释放，
            rollBackBudget(headerId);
        }
        // 审批通过，报账单进入共享池
        if(DocumentOperationEnum.APPROVAL_PASS.getId().equals(status)){
            pushToWorkBranchByHeaderId(header);
        }
        // 复核通过，报账单发送支付平台
        if(DocumentOperationEnum.AUDIT_PASS.getId().equals(status)){
            saveDataToPayment(header);
            header.setStatus(DocumentOperationEnum.APPROVAL_PASS.getId());
            header.setAuditFlag("Y");
            header.setAuditDate(ZonedDateTime.now());
        }
        // 复核通过，报账单发送支付平台
        if(DocumentOperationEnum.AUDIT_REJECT.getId().equals(status)){
            //审批拒绝  撤回 如果有预算需要释放，
            rollBackBudget(headerId);
            header.setStatus(DocumentOperationEnum.APPROVAL_REJECT.getId());
        }
        // 保存
        this.updateById(header);
    }

    private void rollBackBudget(Long id) {
        List<ExpenseReportDist> distList = expenseReportDistService.selectList(new EntityWrapper<ExpenseReportDist>().eq("exp_report_header_id", id));
        List<BudgetReverseRollbackCO> rollbackDTOList = distList.stream().map(e -> {
            BudgetReverseRollbackCO rollbackDTO = new BudgetReverseRollbackCO();
            rollbackDTO.setBusinessType(DocumentTypeEnum.PUBLIC_REPORT.getCategory());
            rollbackDTO.setDocumentId(id);
            rollbackDTO.setDocumentLineId(e.getId());
            return rollbackDTO;
        }).collect(Collectors.toList());
        commonService.rollbackBudget(rollbackDTOList);
    }

    public String saveInitializeExpReportGeneralLedgerJournalLine(Long reportHeaderId){
        //报销单头
        ExpenseReportHeader reportHeader = this.selectById(reportHeaderId);
        //报销单行
        List<ExpenseReportLine> reportLines = expenseReportLineService.getExpenseReportLinesByHeaderId(reportHeaderId);
        //分摊行
        List<ExpenseReportDist> expenseReportDists = new ArrayList<>();
//        //发票行
//        List<InvoiceLine> rerpotInvoiceLines = new ArrayList<>();
//        //发票分配行
//        List<InvoiceLineDist> expenseInvoiceLineDists = new ArrayList<>();
        //发票行报销记录表
        List<InvoiceLineExpence> invoiceLineExpences = invoiceLineExpenceService.selectList(new EntityWrapper<InvoiceLineExpence>()
                .eq("exp_expense_head_id", reportHeaderId));

        List<InvoiceLineExpence> lineExpences = invoiceLineExpenceService.selectList(new EntityWrapper<InvoiceLineExpence>().eq("exp_expense_head_id", reportHeaderId));
        //分配行id
        List<Long> invoiceDistIds = lineExpences.stream().map(InvoiceLineExpence::getInvoiceDistId).collect(Collectors.toList());
        //发票分配行
        List<InvoiceLineDist> invoiceLineDists = invoiceLineDistService.selectBatchIds(invoiceDistIds);
        //发票行id
        List<Long> invoiceLineIds = invoiceLineDists.stream().map(InvoiceLineDist::getInvoiceLineId).collect(Collectors.toList());
        //发票行
        List<InvoiceLine> invoiceLines = invoiceLineService.selectBatchIds(invoiceLineIds);

        reportLines.forEach(reportLine -> {
            //分摊行
            List<ExpenseReportDist> reportDists = expenseReportDistService.getExpenseReportDistByLineId(reportLine.getId());
            if(CollectionUtils.isNotEmpty(reportDists)){
                expenseReportDists.addAll(reportDists);
            }
        });
        //发票头
        List<Long> expenseReportHeaderIds = invoiceLines.stream()
                .map(InvoiceLine::getInvoiceHeadId)
                .distinct().collect(Collectors.toList());
        List<InvoiceHead> invoiceHeaders = invoiceHeadService.selectBatchIds(expenseReportHeaderIds);
        //分摊税行
        List<ExpenseReportTaxDist> reportTaxDists = new ArrayList<>();
        expenseReportDists.forEach(expenseReportDist -> {
            ExpenseReportTaxDist reportTaxDist = expenseReportTaxDistService.selectOne(new EntityWrapper<ExpenseReportTaxDist>()
                    .eq("exp_report_dist_id", expenseReportDist.getId()));
            if(reportTaxDist != null){
                reportTaxDists.add(reportTaxDist);
            }
        });
        //计划付款行
        List<ExpenseReportPaymentSchedule> expenseReportSchedules = expenseReportPaymentScheduleService.getExpensePaymentScheduleByReportHeaderId(reportHeaderId);
        //转换
        ExpenseReportHeaderCO expenseReportHeader = reportHeaderToReportHeaderCO(reportHeader);
        List<ExpenseReportLineCO> expenseReportLineCOS = reportLineToReportLineCO(reportLines);
        List<ExpenseReportInvoiceHeaderCO> expenseReportInvoiceHeaderCOS = invoiceHeaderToInvoiceHeaderCO(invoiceHeaders);
        List<ExpenseReportInvoiceLineCO> expenseReportInvoiceLineCOS = invoiceLineToInvoiceLineCO(invoiceLines);
        List<ExpenseReportInvoiceLineDistCO> expenseReportInvoiceLineDistCOS = invoiceLineDistToInvoiceLineDistCO(invoiceLineDists);
        List<ExpenseReportDistCO> expenseReportDistCOS = reportDistToReportDistCO(expenseReportDists);
        List<ExpenseReportTaxDistCO> expenseReportTaxDistCOS = reportTaxDistToReportTaxDistCO(reportTaxDists);
        List<ExpenseReportScheduleCO> expenseReportScheduleCOS = reportScheduleToReportScheduleCO(expenseReportSchedules);

        ExpenseReportCO reportCO = new ExpenseReportCO();
        reportCO.setExpenseReportHeader(expenseReportHeader);
        reportCO.setExpenseReportLines(expenseReportLineCOS);
        reportCO.setExpenseReportInvoiceHeaders(expenseReportInvoiceHeaderCOS);
        reportCO.setExpenseReportInvoiceLines(expenseReportInvoiceLineCOS);
        reportCO.setExpenseReportInvoiceLineDists(expenseReportInvoiceLineDistCOS);
        reportCO.setExpenseReportDists(expenseReportDistCOS);
        reportCO.setExpenseReportTaxDistS(expenseReportTaxDistCOS);
        reportCO.setExpenseReportSchedules(expenseReportScheduleCOS);

        return accountingClient.saveInitializeExpReportGeneralLedgerJournalLine(reportCO);
    }

    private ExpenseReportHeaderCO reportHeaderToReportHeaderCO(ExpenseReportHeader reportHeader){
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
                .accountDate(null)
                .accountPeriod(null).build();
    }

    private List<ExpenseReportLineCO> reportLineToReportLineCO(List<ExpenseReportLine> reportLines){
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

    private List<ExpenseReportInvoiceHeaderCO> invoiceHeaderToInvoiceHeaderCO(List<InvoiceHead> invoiceHeads){
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

    private List<ExpenseReportInvoiceLineCO> invoiceLineToInvoiceLineCO (List<InvoiceLine> invoiceLines){
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

    private List<ExpenseReportInvoiceLineDistCO> invoiceLineDistToInvoiceLineDistCO (List<InvoiceLineDist> invoiceLineDists){
        List<ExpenseReportInvoiceLineDistCO> expenseReportInvoiceLineDists = new ArrayList<>();
        invoiceLineDists.forEach(invoiceLineDist -> {
            ExpenseReportInvoiceLineDistCO invoiceLineDistCO = ExpenseReportInvoiceLineDistCO.builder()
                    .id(invoiceLineDist.getId())
                    .invoiceLineId(invoiceLineDist.getInvoiceLineId())
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

    private List<ExpenseReportDistCO> reportDistToReportDistCO (List<ExpenseReportDist> reportDists){
        String dimension = "dimension1Id";
        List<ExpenseReportDistCO> expenseReportDists = new ArrayList<>();
        reportDists.forEach(reportDist -> {
            ExpenseReportDistCO expenseReportDist = mapperFacade.map(reportDist, ExpenseReportDistCO.class);
            expenseReportDist.setLineId(reportDist.getExpReportLineId());
            expenseReportDist.setHeaderId(reportDist.getExpReportHeaderId());
            expenseReportDist.setUnitId(reportDist.getDepartmentId());
            expenseReportDist.setResCenterId(reportDist.getResponsibilityCenterId());
            expenseReportDist.setShareTaxAmount(reportDist.getTaxDistAmount().toString());
//            expenseReportDist.setFunctionTaxAmount();
//            expenseReportDist.setShareNotTaxAmount();
//            expenseReportDist.setShareNotFunctionTaxAmount();
//            expenseReportDist.setTaxContribution();
            expenseReportDist.setTaxFunctionShareAmount(reportDist.getTaxDistFunctionAmount().toString());
            expenseReportDists.add(expenseReportDist);
        });
        return expenseReportDists;
    }

    private List<ExpenseReportTaxDistCO> reportTaxDistToReportTaxDistCO (List<ExpenseReportTaxDist> reportTaxDists){
        List<ExpenseReportTaxDistCO> expenseReportTaxDistS = new ArrayList<>();
        if(CollectionUtils.isEmpty(reportTaxDists)){
            new ArrayList<>();
        }
        reportTaxDists.forEach(reportTaxDist -> {
            ExpenseReportTaxDistCO reportTaxDistCO = mapperFacade.map(reportTaxDist, ExpenseReportTaxDistCO.class);
//            Long reportLineByDistId = expenseReportDistService.getExpenseReportLineByDistId(reportTaxDist.getExpReportDistId());
            reportTaxDistCO.setDistId(reportTaxDist.getId());
//            reportTaxDistCO.setInvoiceDistId();
            reportTaxDistCO.setUnitId(reportTaxDist.getDepartmentId());
            reportTaxDistCO.setResCenterId(reportTaxDist.getResponsibilityCenterId());
            reportTaxDistCO.setTaxContribution(reportTaxDist.getTaxAmount().toString());
            //todo
            reportTaxDistCO.setTaxFunctionShareAmount("");
            expenseReportTaxDistS.add(reportTaxDistCO);
        });
        return expenseReportTaxDistS;
    }

    private List<ExpenseReportScheduleCO> reportScheduleToReportScheduleCO (List<ExpenseReportPaymentSchedule> reportSchedules){
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
                    .frozenResult("Y".equals(reportSchedule.getFrozenFlag()))
                    .build();
            if("EMPLOYEE".equals(reportScheduleCO.getPayeeCategory())){
                ContactCO user = organizationService.getUserById(reportScheduleCO.getPayeeId());
                if(user != null){
                    reportScheduleCO.setPayeeCode(user.getEmployeeCode());
                }
            }else{
                VendorInfoCO vendorInfo = organizationService.getOneVendorInfoById(reportScheduleCO.getPayeeId());
                if(vendorInfo != null){
                    reportScheduleCO.setPayeeCode(vendorInfo.getVenderCode());
                }
            }
            expenseReportSchedules.add(reportScheduleCO);
        });
        return expenseReportSchedules;
    }

}
