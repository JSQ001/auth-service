package com.hand.hcf.app.expense.input.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.DataAuthorityMetaHandler;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.security.domain.PrincipalLite;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.dto.BudgetCheckResultDTO;
import com.hand.hcf.app.expense.common.externalApi.AccountingService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.service.CommonService;
import com.hand.hcf.app.expense.common.utils.DimensionUtils;
import com.hand.hcf.app.expense.common.utils.ParameterConstant;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxDist;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxHeader;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxLine;
import com.hand.hcf.app.expense.input.dto.ExpInputTaxHeaderDTO;
import com.hand.hcf.app.expense.input.dto.ExpInputTaxLineDTO;
import com.hand.hcf.app.expense.input.persistence.ExpInputTaxHeaderMapper;
import com.hand.hcf.app.expense.input.persistence.ExpInputTaxLineMapper;
import com.hand.hcf.app.expense.type.domain.enums.DocumentOperationEnum;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.data.DataAuthMetaRealization;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
@Service
public class ExpInputTaxHeaderService extends BaseService<ExpInputTaxHeaderMapper, ExpInputTaxHeader> {
    @Autowired
    private ExpInputTaxHeaderMapper expInputTaxHeaderMapper;

    @Autowired
    private ExpInputTaxLineMapper expInputTaxLineMapper;

    @Autowired
    private ExpInputTaxLineService expInputTaxLineService;
    @Autowired
    private ExpInputTaxDistService expInputTaxDistService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ExcelExportService excelExportService;
    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private CommonService commonService;

    @Autowired
    private AccountingService accountingService;
    @Autowired
    private DataAuthMetaRealization dataAuthorityMetaHandler;


    public Page<ExpInputTaxHeaderDTO> queryHeader(Long applicantId,Long setOfBooksId, String transferType, String useType,
                                                  String transferDateFrom, String transferDateTo,
                                                  String status, BigDecimal amountFrom, BigDecimal amountTo,
                                                  String description, String documentNumber, Long companyId, Long departmentId,
                                                  boolean dataAuthFlag, Page page) {
        String dataAuthLabel = null;
        if(dataAuthFlag){
            Map<String,String> map = new HashMap<>();
            map.put(DataAuthorityUtil.TABLE_NAME, "exp_input_tax_header");
            map.put(DataAuthorityUtil.SOB_COLUMN, "set_of_books_id");
            map.put(DataAuthorityUtil.COMPANY_COLUMN, "company_id");
            map.put(DataAuthorityUtil.UNIT_COLUMN, "department_id");
            map.put(DataAuthorityUtil.EMPLOYEE_COLUMN, "applicant_id");
            dataAuthLabel = DataAuthorityUtil.getDataAuthLabel(map);
        }
        Wrapper wrapper = new EntityWrapper<ExpInputTaxHeader>()
                .eq(applicantId != null,"applicant_id", applicantId)
                .eq(transferType != null, "transfer_type", transferType)
                .eq(setOfBooksId !=null,"set_of_books_id",setOfBooksId)
                .eq(useType != null, "use_type", useType)
                .eq(status != null, "status", status)
                .like(description != null, "description", description)
                .ge(transferDateFrom != null, "transfer_date", TypeConversionUtils.getStartTimeForDayYYMMDD(transferDateFrom))
                .le(transferDateTo != null, "transfer_date", TypeConversionUtils.getEndTimeForDayYYMMDD(transferDateTo))
                .ge(amountFrom != null, "amount", amountFrom)
                .le(amountTo != null, "amount", amountTo)
                .like(documentNumber != null, "document_number", documentNumber)
                .eq(companyId != null, "company_id", companyId)
                .eq(departmentId != null, "department_id", departmentId)
                .and(!StringUtils.isEmpty(dataAuthLabel), dataAuthLabel)
                .orderBy("document_number",false);
        if (!dataAuthorityMetaHandler.checkEnabledDataAuthority()) {
            wrapper = wrapper.eq(applicantId == null, "applicant_id", OrgInformationUtil.getCurrentUserId());
        }
        List<ExpInputTaxHeader> expInputTaxHeaders = expInputTaxHeaderMapper.selectPage(page, wrapper);
        List<ExpInputTaxHeaderDTO> headers = mapperFacade.mapAsList(expInputTaxHeaders, ExpInputTaxHeaderDTO.class);
        for (ExpInputTaxHeaderDTO expInputTaxHeaderDTO : headers) {
            // 返回账套code、账套name
            SetOfBooksInfoCO setOfBooksInfoCOById = organizationService.getSetOfBooksInfoCOById(expInputTaxHeaderDTO.getSetOfBooksId(), false);
            if (setOfBooksInfoCOById != null) {
                expInputTaxHeaderDTO.setSetOfBooksCode(setOfBooksInfoCOById.getSetOfBooksCode());
                expInputTaxHeaderDTO.setSetOfBooksName(setOfBooksInfoCOById.getSetOfBooksName());
            }
        }
        //设置 名称（公司，部门，员工）
        setDesc(headers);
        //设置 值列表的 name
        setSysCodeValue(headers);
       /* for (ExpInputTaxHeaderDTO header : headers) {
            SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("transferType", header.getTransferType());
            if (sysCodeValueCO != null) {
                header.setTransferTypeName(sysCodeValueCO.getName());
            }

            SysCodeValueCO sysCodeValueCO1 = organizationService.getSysCodeValueByCodeAndValue("useType", header.getUseType());
            if (sysCodeValueCO1 != null) {
                header.setUseTypeName(sysCodeValueCO1.getName());
            }
        }*/

        page.setRecords(headers);
        return page;
    }


    public ExpInputTaxHeaderDTO queryById(Long id) {
        ExpInputTaxHeader expInputTaxHeader = expInputTaxHeaderMapper.selectById(id);
        ExpInputTaxHeaderDTO header = mapperFacade.map(expInputTaxHeader, ExpInputTaxHeaderDTO.class);
        setAttachments(header);

        //设置描述
        header.setFullName(organizationService.getUserById(header.getApplicantId()).getFullName());
        header.setDepartmentName(organizationService.getDepartmentById(header.getDepartmentId()).getName());
        header.setCompanyName(organizationService.getCompanyById(header.getCompanyId()).getName());

        //设置 值列表的 name
        setSysCodeValue(header);
        /*SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("transferType", header.getTransferType());
        if (sysCodeValueCO != null) {
            header.setTransferTypeName(sysCodeValueCO.getName());
        }
        SysCodeValueCO sysCodeValueCO1 = organizationService.getSysCodeValueByCodeAndValue("useType", header.getUseType());
        if (sysCodeValueCO1 != null) {
            header.setUseTypeName(sysCodeValueCO1.getName());
        }*/
        return header;
    }

    @Transactional(rollbackFor = Exception.class)
    public ExpInputTaxHeader insertOrUpdateHeader(ExpInputTaxHeader expInputTaxHeader) {
        PrincipalLite user = OrgInformationUtil.getUser();
        expInputTaxHeader.setLastUpdatedBy(user.getId());
        expInputTaxHeader.setLastUpdatedDate(ZonedDateTime.now());
        if (expInputTaxHeader.getId() != null) {
            expInputTaxHeaderMapper.updateById(expInputTaxHeader);
        } else {
            //新建保存时设置默认值
            expInputTaxHeader.setTenantId(user.getTenantId());
            expInputTaxHeader.setSetOfBooksId(OrgInformationUtil.getCurrentSetOfBookId());
            expInputTaxHeader.setApplicantId(user.getId());
            expInputTaxHeader.setBaseAmount(new BigDecimal(0));
            expInputTaxHeader.setBaseFunctionAmount(new BigDecimal(0));
            expInputTaxHeader.setAmount(new BigDecimal(0));
            expInputTaxHeader.setFunctionAmount(new BigDecimal(0));
            expInputTaxHeader.setStatus(expInputTaxHeader.getStatus() != null ? expInputTaxHeader.getStatus() : "1001");
            expInputTaxHeader.setReverseFlag("N");
            expInputTaxHeader.setAuditStatus("N");
            expInputTaxHeader.setCreatedBy(user.getId());
            expInputTaxHeader.setCreatedDate(ZonedDateTime.now());
            expInputTaxHeader.setDocumentNumber(commonService.getCoding(ExpenseDocumentTypeEnum.EXP_INPUT_TAX.getCategory(), expInputTaxHeader.getCompanyId(), null));
            expInputTaxHeaderMapper.insert(expInputTaxHeader);
        }
        return expInputTaxHeader;
    }

    public void setDesc(List<ExpInputTaxHeaderDTO> headers) {
        if (!CollectionUtils.isEmpty(headers)) {
            Set<Long> companyIds = new HashSet<>();
            Set<Long> departmentIds = new HashSet<>();
            Set<Long> userIds = new HashSet<>();
            headers.stream().forEach(e -> {
                companyIds.add(e.getCompanyId());
                departmentIds.add(e.getDepartmentId());
                userIds.add(e.getApplicantId());
            });
            // 查询公司
            Map<Long, CompanyCO> companyMap = organizationService.getCompanyMapByCompanyIds(new ArrayList<>(companyIds));
            // 查询部门
            Map<Long, DepartmentCO> departmentMap = organizationService.getDepartmentMapByDepartmentIds(new ArrayList<>(departmentIds));
            // 查询员工
            Map<Long, ContactCO> usersMap = organizationService.getUserMapByUserIds(new ArrayList<>(userIds));
            headers
                    .stream()
                    .forEach(e -> {
                        if (companyMap.containsKey(e.getCompanyId())) {
                            e.setCompanyName(companyMap.get(e.getCompanyId()).getName());
                        }
                        if (departmentMap.containsKey(e.getDepartmentId())) {
                            e.setDepartmentName(departmentMap.get(e.getDepartmentId()).getName());
                        }
                        if (usersMap.containsKey(e.getApplicantId())) {
                            e.setFullName(usersMap.get(e.getApplicantId()).getFullName());
                        }
                    });
        }
    }

    /**
     * expInputTaxDistList转换为BudgetReserveCO
     *
     * @param expInputTaxDistList
     * @return
     */
    private List<BudgetReserveCO> expenseInputTaxDistToBudgetReserveCO(ExpInputTaxHeader header,
                                                                       List<ExpInputTaxDist> expInputTaxDistList) {
        List<BudgetReserveCO> budgetReserveCOList = new ArrayList<>();
        for (ExpInputTaxDist expenseInputTaxDist : expInputTaxDistList) {
            BudgetReserveCO budgetReserveCO = new BudgetReserveCO();
            budgetReserveCO.setCompanyId(expenseInputTaxDist.getCompanyId());
            budgetReserveCO.setCompanyCode(organizationService.getCompanyById(expenseInputTaxDist.getCompanyId()).getCompanyCode());
            budgetReserveCO.setBusinessType(ExpenseDocumentTypeEnum.EXP_INPUT_TAX.getCategory());
            budgetReserveCO.setReserveFlag("U");
            budgetReserveCO.setStatus("N");
            budgetReserveCO.setManualFlag("N");
            budgetReserveCO.setDocumentId(header.getId());
            budgetReserveCO.setDocumentLineId(expenseInputTaxDist.getId());
            budgetReserveCO.setCurrency(expenseInputTaxDist.getCurrencyCode());
            budgetReserveCO.setExchangeRate(expenseInputTaxDist.getRate().doubleValue());
            budgetReserveCO.setAmount(expenseInputTaxDist.getAmount());
            budgetReserveCO.setFunctionalAmount(expenseInputTaxDist.getFunctionAmount());
            budgetReserveCO.setQuantity(1);
            budgetReserveCO.setUnitId(expenseInputTaxDist.getDepartmentId());
            budgetReserveCO.setUnitCode(organizationService.getDepartmentById(expenseInputTaxDist.getDepartmentId()).getDepartmentCode());
            budgetReserveCO.setEmployeeId(header.getApplicantId());
            budgetReserveCO.setEmployeeCode(organizationService.getUserById(header.getApplicantId()).getEmployeeCode());
            if (expenseInputTaxDist.getResponsibilityCenterId() != null) {
                budgetReserveCO.setResponsibilityCenterId(expenseInputTaxDist.getResponsibilityCenterId());
                budgetReserveCO.setResponsibilityCenterCode(
                        organizationService.getResponsibilityCenterById(expenseInputTaxDist.getResponsibilityCenterId()).getResponsibilityCenterCode());
            }
            budgetReserveCO.setCreatedBy(expenseInputTaxDist.getCreatedBy());
            DimensionUtils.setDimensionMessage(expenseInputTaxDist, budgetReserveCO, organizationService, true, true, false);
            budgetReserveCO.setDocumentItemSourceType(ExpenseDocumentTypeEnum.EXP_INPUT_TAX.getCategory());
            budgetReserveCO.setDocumentItemSourceId(expenseInputTaxDist.getExpenseTypeId());
            // 期间信息
            ZonedDateTime periodDate = ZonedDateTime.now();
            String parameterValue = organizationService.getParameterValue(expenseInputTaxDist.getCompanyId(),
                    expenseInputTaxDist.getSetOfBooksId(), ParameterConstant.BGT_OCCUPY_DATE);
            if (ParameterConstant.EXPENSE_DATE.equals(parameterValue)) {
                periodDate = header.getTransferDate();
            }
            PeriodCO period = organizationService.getPeriodsByIDAndTime(expenseInputTaxDist.getSetOfBooksId(), DateUtil.ZonedDateTimeToString(periodDate));
            //会计期间名称
            budgetReserveCO.setPeriodName(period.getPeriodName());
            budgetReserveCO.setPeriodNumber(period.getPeriodNum());
            budgetReserveCO.setPeriodQuarter(period.getQuarterNum());
            budgetReserveCO.setPeriodYear(period.getPeriodYear());
            budgetReserveCOList.add(budgetReserveCO);
        }
        return budgetReserveCOList;
    }

    private void rollBackBudget(Long id) {
        List<ExpInputTaxLineDTO> lineDTOS = expInputTaxLineMapper.listLineById(id);
        List<Long> lineIds = lineDTOS.stream().map(ExpInputTaxLine::getId).collect(Collectors.toList());
        //分摊行
        List<ExpInputTaxDist> distList = expInputTaxDistService.getExpInputTaxDistByLineIds(lineIds);
        List<BudgetReverseRollbackCO> rollbackDTOList = distList.stream().map(e -> {
            BudgetReverseRollbackCO rollbackDTO = new BudgetReverseRollbackCO();
            rollbackDTO.setBusinessType(ExpenseDocumentTypeEnum.EXP_INPUT_TAX.getCategory());
            rollbackDTO.setDocumentId(id);
            rollbackDTO.setDocumentLineId(e.getId());
            return rollbackDTO;
        }).collect(Collectors.toList());
        commonService.rollbackBudget(rollbackDTOList);
    }

    /**
     * 调用预算模块预算校验接口
     *
     * @param header 进项税头信息
     * @return
     */
    public BudgetCheckResultDTO checkBudget(ExpInputTaxHeader header,
                                            Boolean ignoreWarningFlag) {

        BudgetCheckMessageCO param = new BudgetCheckMessageCO();
        List<BudgetReserveCO> budgetReserveDtoList;
        List<ExpInputTaxLineDTO> lineDTOS = expInputTaxLineMapper.listLineById(header.getId());
        List<Long> lineIds = lineDTOS.stream().map(ExpInputTaxLine::getId).collect(Collectors.toList());
        //分摊行
        List<ExpInputTaxDist> expInputTaxDistList = expInputTaxDistService.getExpInputTaxDistByLineIds(lineIds);
        budgetReserveDtoList = expenseInputTaxDistToBudgetReserveCO(header, expInputTaxDistList);
        param.setTenantId(header.getTenantId());
        param.setSetOfBooksId(header.getSetOfBooksId());
        param.setBudgetReserveDtoList(budgetReserveDtoList);
        param.setIgnoreWarningFlag(BooleanUtils.isTrue(ignoreWarningFlag) ? "Y" : "N");
        param.setIncludeReleaseFlag("Y");
        return commonService.checkBudget(param);
    }

    public BudgetCheckResultDTO checkInputTaxBudget(ExpInputTaxHeader header){
        //进项税转出占用预算
        String budgetFlag = organizationService.getParameterValue(header.getCompanyId(),
                header.getSetOfBooksId(), ParameterConstant.INPUT_TAX_TRANSFER_BUDGET);
        BudgetCheckResultDTO budgetCheckResultDTO = null;
        if (ParameterConstant.OCCUPY_CHECK.equals(budgetFlag)) {
            //占用预算并校验
            budgetCheckResultDTO = checkBudget(
                    header, Boolean.TRUE);
        } else if (ParameterConstant.CCCUPY_NO_CHECK.equals(budgetFlag)){
            //todo 占用预算不校验,暂时没有这样的方法
            budgetCheckResultDTO = checkBudget(
                    header, Boolean.TRUE);
        }else{
            budgetCheckResultDTO = BudgetCheckResultDTO.ok();
        }
        return budgetCheckResultDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStatus(Long id, int status, String desc) {
        ExpInputTaxHeader header = expInputTaxHeaderMapper.selectById(id);
        //提交时，暂时只用 status
        header.setLastUpdatedDate(ZonedDateTime.now());
        header.setLastUpdatedBy(OrgInformationUtil.getUser().getId());
        CommonApprovalHistoryCO commonApprovalHistoryCO = new CommonApprovalHistoryCO();
        commonApprovalHistoryCO.setEntityType(ExpenseDocumentTypeEnum.EXP_INPUT_TAX.getKey());
        commonApprovalHistoryCO.setOperatorOid(OrgInformationUtil.getCurrentUserOid());
        commonApprovalHistoryCO.setOperation(status);

        if (DocumentOperationEnum.APPROVAL.getId() == status) {
            if (header.getStatus().equals(DocumentOperationEnum.APPROVAL.getId().toString()) || header.getStatus().equals(DocumentOperationEnum.APPROVAL_PASS.getId().toString())) {
                return false;
            }
            //因为oid只有在提交用到，所以在第一次提交的时候生成。
            UUID docOid = null;
            if (header.getDocumentOid() == null) {
                docOid = UUID.randomUUID();
                header.setDocumentOid(docOid.toString());
            } else {
                docOid = UUID.fromString(header.getDocumentOid());
            }
            //校验可转出金额/可视同销售金额
            String result = expInputTaxLineService.checkSubmitLine(header);
            if(!("").equals(result)){
                throw new BizException(RespCode.EXPENSE_INPUT_TAX_TRANSFER_AMOUNT_NOT_ENOUGH,new Object[]{result});
            }
            //校验预算
            BudgetCheckResultDTO budgetCheckResultDTO = checkInputTaxBudget(header);
            if (budgetCheckResultDTO.getPassFlag()) {
                //尝试插入工作流历史
                commonApprovalHistoryCO.setEntityOid(docOid);
                commonApprovalHistoryCO.setOperationDetail("单据提交" + (desc != null ? ":" + desc : ""));
            }else{
                //校验失败，回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }

        } else if (DocumentOperationEnum.APPROVAL_PASS.getId() == status) {
            header.setAuditDate(ZonedDateTime.now());
            if (!header.getStatus().equals(DocumentOperationEnum.APPROVAL.getId().toString())) {
                return false;
            }
            commonApprovalHistoryCO.setEntityOid(UUID.fromString(header.getDocumentOid()));
            commonApprovalHistoryCO.setOperationDetail("单据通过" + (desc != null ? ":" + desc : ""));
        } else if (DocumentOperationEnum.APPROVAL_REJECT.getId() == status) {
            header.setAuditDate(ZonedDateTime.now());
            if (!header.getStatus().equals(DocumentOperationEnum.APPROVAL.getId().toString())) {
                return false;
            }
            //单据拒绝  如果有预算需要释放，
            rollBackBudget(id);
            commonApprovalHistoryCO.setEntityOid(UUID.fromString(header.getDocumentOid()));
            commonApprovalHistoryCO.setOperationDetail("单据拒绝" + (desc != null ? ":" + desc : ""));
        }
        header.setStatus(String.valueOf(status));
        expInputTaxHeaderMapper.updateById(header);
        organizationService.saveHistory(commonApprovalHistoryCO);
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        List<ExpInputTaxLine> expInputTaxLines = expInputTaxLineService.queryByHeaderId(id);
        for (ExpInputTaxLine line : expInputTaxLines) {
            expInputTaxDistService.deleteByLineId(line.getId());
        }
        expInputTaxLineService.deleteByHeaderId(id);
        expInputTaxHeaderMapper.deleteById(id);
        return true;
    }

    private void setAttachments(ExpInputTaxHeaderDTO dto) {
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

    public List<ExpInputTaxHeaderDTO> queryExpInputFinance(
            Page page, Long companyId, Long setOfBooksId,Long unitId,Long applyId, Long status,
            String transferType, String useType, String currencyCode,
            BigDecimal amountFrom, BigDecimal amountTo, String reverseFlag, String remark,
            ZonedDateTime creatDateFrom, ZonedDateTime creatDateTo, ZonedDateTime auditDateFrom,
            ZonedDateTime auditDateTo, Long tenantId, String documentNumber, boolean dataAuthFlag) {

        String dataAuthLabel = null;
        if(dataAuthFlag){
            Map<String,String> map = new HashMap<>();
            map.put(DataAuthorityUtil.TABLE_NAME, "exp_input_tax_header");
            map.put(DataAuthorityUtil.TABLE_ALIAS,"t");
            map.put(DataAuthorityUtil.SOB_COLUMN, "set_of_books_id");
            map.put(DataAuthorityUtil.COMPANY_COLUMN, "company_id");
            map.put(DataAuthorityUtil.UNIT_COLUMN, "department_id");
            map.put(DataAuthorityUtil.EMPLOYEE_COLUMN, "applicant_id");
            dataAuthLabel = DataAuthorityUtil.getDataAuthLabel(map);
        }
        Wrapper<ExpInputTaxHeader> wrapper = getQueryExpInputFinance(companyId, unitId,setOfBooksId, applyId, status, transferType, useType, currencyCode, amountFrom, amountTo,
                reverseFlag, remark, creatDateFrom, creatDateTo, auditDateFrom, auditDateTo, tenantId, documentNumber, dataAuthLabel);
        List<ExpInputTaxHeaderDTO> expInputFinances = baseMapper.queryExpInputFinance(page, wrapper);
        for (ExpInputTaxHeaderDTO expInputTaxHeaderDTO : expInputFinances) {
            // 返回账套code、账套name
            SetOfBooksInfoCO setOfBooksInfoCOById = organizationService.getSetOfBooksInfoCOById(expInputTaxHeaderDTO.getSetOfBooksId(), false);
            if (setOfBooksInfoCOById != null) {
                expInputTaxHeaderDTO.setSetOfBooksCode(setOfBooksInfoCOById.getSetOfBooksCode());
                expInputTaxHeaderDTO.setSetOfBooksName(setOfBooksInfoCOById.getSetOfBooksName());
            }
        }
        setDesc(expInputFinances);
        setSysCodeValue(expInputFinances);
        /*for (ExpInputTaxHeaderDTO header : expInputFinances) {
            SysCodeValueCO sysCodeValueCO = getSysCodeValueByCodeAndValue("transferType",header.getTransferType());
            if (sysCodeValueCO != null) {
                header.setTransferTypeName(sysCodeValueCO.getName());
            }

            SysCodeValueCO sysCodeValueCO1 =getSysCodeValueByCodeAndValue("useType",header.getUseType());
            if (sysCodeValueCO1 != null) {
                header.setUseTypeName(sysCodeValueCO1.getName());
            }
        }*/
        //setCompanyAndDepartmentAndEmployee1(expInputFinances);
        return expInputFinances;
    }

    //获取业务大类和用途类型值列表
    public void setSysCodeValue(List<ExpInputTaxHeaderDTO> headerDTOS) {
        if (!CollectionUtils.isEmpty(headerDTOS)) {
            headerDTOS.stream().forEach(e -> {
                setSysCodeValue(e);
            });
        }
    }

    public void setSysCodeValue(ExpInputTaxHeaderDTO headerDTO) {
        if (headerDTO != null) {
            SysCodeValueCO sysCodeValueCO = getSysCodeValueByCodeAndValue("transferType", headerDTO.getTransferType());
            if (sysCodeValueCO != null) {
                headerDTO.setTransferTypeName(sysCodeValueCO.getName());
            }

            SysCodeValueCO sysCodeValueCO1 = getSysCodeValueByCodeAndValue("useType", headerDTO.getUseType());
            if (sysCodeValueCO1 != null) {
                headerDTO.setUseTypeName(sysCodeValueCO1.getName());
            }

        }
    }


    //获取系统代码值列表
    public SysCodeValueCO getSysCodeValueByCodeAndValue(String code, String type) {
        SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue(code, type);

        return sysCodeValueCO;

    }

    public void exportFormExcel(Long companyId,
                                Long unitId,
                                Long applyId,
                                Long status,
                                String transferType,
                                String useType,
                                String currencyCode,
                                BigDecimal amountFrom,
                                BigDecimal amountTo,
                                String reverseFlag,
                                String remark,
                                ZonedDateTime creatDateFrom,
                                ZonedDateTime creatDateTo,
                                ZonedDateTime auditDateFrom,
                                ZonedDateTime auditDateTo,
                                Long tenantId,
                                String documentNumber,
                                HttpServletResponse response,
                                HttpServletRequest request,
                                ExportConfig exportConfig) throws IOException {
        //获取查询的Sql
        Wrapper<ExpInputTaxHeader> wrapper = getQueryExpInputFinance(companyId, unitId,null, applyId, status, transferType, useType, currencyCode, amountFrom, amountTo,
                reverseFlag, remark, creatDateFrom, creatDateTo, auditDateFrom, auditDateTo, tenantId, documentNumber, null);
        //获取 查询的总数
        int total = baseMapper.getCountByCondition(wrapper);
        int availProcessors = Runtime.getRuntime().availableProcessors() / 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<ExpInputTaxHeaderDTO, ExpInputTaxHeaderDTO>() {
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<ExpInputTaxHeaderDTO> queryDataByPage(Page page) {
                return ListExpInputTaxHeaderFianicen(page, wrapper);
            }

            @Override
            public ExpInputTaxHeaderDTO toDTO(ExpInputTaxHeaderDTO t) {
                t.setCreatedDateStr(t.getCreatedDate() == null ? "" : t.getCreatedDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                t.setLastUpdatedDateStr(t.getLastUpdatedDate() == null ? "" : t.getLastUpdatedDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                return t;
            }

            @Override
            public Class<ExpInputTaxHeaderDTO> getEntityClass() {
                return ExpInputTaxHeaderDTO.class;
            }
        }, availProcessors, request, response);
    }

    public List<ExpInputTaxHeaderDTO> ListExpInputTaxHeaderFianicen(Page page,
                                                                    Wrapper<ExpInputTaxHeader> wrapper) {
        List<ExpInputTaxHeaderDTO> headers = baseMapper.queryExpInputFinance(page, wrapper);
        setDesc(headers);
        setSysCodeValue(headers);
        return headers;
    }

    public Wrapper<ExpInputTaxHeader> getQueryExpInputFinance(Long companyId,
                                                              Long unitId,
                                                              Long setOfBooksId,
                                                              Long applyId,
                                                              Long status,
                                                              String transferType,
                                                              String useType,
                                                              String currencyCode,
                                                              BigDecimal amountFrom,
                                                              BigDecimal amountTo,
                                                              String reverseFlag,
                                                              String remark,
                                                              ZonedDateTime creatDateFrom,
                                                              ZonedDateTime creatDateTo,
                                                              ZonedDateTime auditDateFrom,
                                                              ZonedDateTime auditDateTo,
                                                              Long tenantId,
                                                              String documentNumber,
                                                              String dataAuthLabel) {
        Wrapper<ExpInputTaxHeader> wrapper = new EntityWrapper<ExpInputTaxHeader>()
                .eq(companyId != null, "t.company_id", companyId)
                .eq(unitId != null, "t.department_id", unitId)
                .eq(setOfBooksId !=null,"t.set_of_books_id",setOfBooksId)
                .eq(applyId != null, "t.applicant_id", applyId)
                .eq(status != null, "t.status", status)
                .eq(StringUtils.hasText(transferType), "t.transfer_type", transferType)
                .eq(StringUtils.hasText(useType), "t.use_type", useType)
                .eq(StringUtils.hasText(currencyCode), "t.currency_code", currencyCode)
                .gt(amountFrom != null, "t.amount", amountFrom)
                .lt(amountTo != null, "t.amount", amountTo)
                .eq(StringUtils.hasText(reverseFlag), "t.reverse_flag", reverseFlag)
                .like(StringUtils.hasText(remark), "t.description", remark)
                .gt(creatDateFrom != null, "t.created_date", creatDateFrom)
                .lt(creatDateTo != null, "t.created_date", creatDateTo)
                .gt(auditDateFrom != null, "t.audit_date", auditDateFrom)
                .lt(auditDateTo != null, "t.audit_date", auditDateTo)
                .eq(tenantId != null, "t.tenant_id", tenantId)
                .like(StringUtils.hasText(documentNumber), "t.document_number", documentNumber)
                .and(!StringUtils.isEmpty(dataAuthLabel), dataAuthLabel)
                .orderBy("t.document_number", false);
        return wrapper;
    }

    @Transactional(rollbackFor = Exception.class)
    //@LcnTransaction
    public String saveInitializeExpInputTaxGeneralLedgerJournalLine(Long inputTaxHeaderId, String accountingDate) {
        ExpenseInputTaxCO inputTaxCO = new ExpenseInputTaxCO();
        //报销单头
        ExpInputTaxHeader inputTaxHeader = this.selectById(inputTaxHeaderId);
        //报销单行
        List<ExpInputTaxLineDTO> lineDTOS = expInputTaxLineMapper.listLineById(inputTaxHeaderId);
        List<Long> lineIds = lineDTOS.stream().map(ExpInputTaxLine::getId).collect(Collectors.toList());
        //分摊行
        List<ExpInputTaxDist> expenseInputTaxDists = expInputTaxDistService.getExpInputTaxDistByLineIds(lineIds);

        //转换
        ExpenseInputTaxHeaderCO expenseInputTaxHeader = inputTaxHeaderToInputTaxHeaderCO(inputTaxHeader, accountingDate);
        List<ExpenseInputTaxLineCO> expenseInputTaxLineCOS = inputTaxLineToInputTaxLineCO(lineDTOS, inputTaxHeader);
        List<ExpenseInputTaxDistCO> expenseInputTaxDistCOS = inputTaxDistToInputTaxDistCO(expenseInputTaxDists, inputTaxHeader);

        inputTaxCO.setExpenseInputTaxHeader(expenseInputTaxHeader);
        inputTaxCO.setExpenseReportLines(expenseInputTaxLineCOS);
        inputTaxCO.setExpenseReportDists(expenseInputTaxDistCOS);
        //bo.liu 核算
        /*accountingService.saveInitializeExpInputTaxGeneralLedgerJournalLine(inputTaxCO);*/
        inputTaxHeader.setJeCreationStatus(true);
        inputTaxHeader.setJeCreationDate(ZonedDateTime.now());
        updateById(inputTaxHeader);
        return "SUCCESS";
    }

    private ExpenseInputTaxHeaderCO inputTaxHeaderToInputTaxHeaderCO(ExpInputTaxHeader inputTaxHeader,
                                                                     String accountingDate) {
        ZonedDateTime accountDate = TypeConversionUtils.getStartTimeForDayYYMMDD(accountingDate);
        PeriodCO period = organizationService.getPeriodsByIDAndTime(inputTaxHeader.getSetOfBooksId(), DateUtil.ZonedDateTimeToString(accountDate));
        return ExpenseInputTaxHeaderCO.builder()
                .id(inputTaxHeader.getId())
                .tenantId(inputTaxHeader.getTenantId())
                .setOfBooksId(inputTaxHeader.getSetOfBooksId())
                .currencyCode(inputTaxHeader.getCurrencyCode())
                .rate(inputTaxHeader.getRate().doubleValue())
                .documentNumber(inputTaxHeader.getDocumentNumber())
                .companyId(inputTaxHeader.getCompanyId())
                .departmentId(inputTaxHeader.getDepartmentId())
                .applicationId(inputTaxHeader.getApplicantId())
                //.formId(inputTaxHeader.getFormId())
                .description(inputTaxHeader.getDescription())
                .transferDate(inputTaxHeader.getTransferDate())
                .amount(inputTaxHeader.getAmount())
                .functionAmount(inputTaxHeader.getFunctionAmount())
                .baseAmount(inputTaxHeader.getBaseAmount())
                .baseFunctionAmount(inputTaxHeader.getBaseFunctionAmount())
                .status(inputTaxHeader.getStatus().toString())
                .accountDate(accountDate)
                .accountPeriod(period.getPeriodName()).build();
    }

    private List<ExpenseInputTaxLineCO> inputTaxLineToInputTaxLineCO(List<ExpInputTaxLineDTO> inputTaxLines, ExpInputTaxHeader expInputTaxHeader) {
        List<ExpenseInputTaxLineCO> expenseReportLines = new ArrayList<>();
        inputTaxLines.forEach(inputTaxLine -> {

            ExpenseInputTaxLineCO inputTaxLineCO = ExpenseInputTaxLineCO.builder()
                    .id(inputTaxLine.getId())
                    .headerId(expInputTaxHeader.getId())
                    .companyId(inputTaxLine.getCompanyId())
                    //.expenseTypeId(inputTaxLine.getExpenseTypeId())
                    //.quantity(inputTaxLine.getQuantity())
                    //.price(inputTaxLine.getPrice())
                    //.uom(inputTaxLine.getUom())
                    .currencyCode(inputTaxLine.getCurrencyCode())
                    .rate(inputTaxLine.getRate().doubleValue())
                    .amount(inputTaxLine.getAmount())
                    .functionAmount(inputTaxLine.getFunctionAmount())
                    .baseFunctionAmount(inputTaxLine.getBaseAmount())
                    .baseFunctionAmount(inputTaxLine.getBaseFunctionAmount())
                    //.taxAmount()
                    //.taxFunctionAmount(inputTaxLine.getTaxFunctionAmount())
                    //.installmentDeductionFlag(inputTaxLine.getInstallmentDeductionFlag())
                    //.inputTaxFlag(inputTaxLine.getInputTaxFlag())
                    .useType(inputTaxLine.getUseType())
                    .description(inputTaxLine.getDescription())
                    .build();
            expenseReportLines.add(inputTaxLineCO);
        });
        return expenseReportLines;
    }

    private List<ExpenseInputTaxDistCO> inputTaxDistToInputTaxDistCO(List<ExpInputTaxDist> inputTaxDists, ExpInputTaxHeader expInputTaxHeader) {
        List<ExpenseInputTaxDistCO> expenseInputTaxDists = new ArrayList<>();
        inputTaxDists.forEach(inputTaxDist -> {
            ExpenseInputTaxDistCO expenseInputTaxDist = ExpenseInputTaxDistCO.builder().companyId(inputTaxDist.getCompanyId())
                    .currencyCode(inputTaxDist.getCurrencyCode())
                    .dimension1Id(inputTaxDist.getDimension1Id())
                    .dimension2Id(inputTaxDist.getDimension2Id())
                    .dimension3Id(inputTaxDist.getDimension3Id())
                    .dimension4Id(inputTaxDist.getDimension4Id())
                    .dimension5Id(inputTaxDist.getDimension5Id())
                    .dimension6Id(inputTaxDist.getDimension6Id())
                    .dimension7Id(inputTaxDist.getDimension7Id())
                    .dimension8Id(inputTaxDist.getDimension8Id())
                    .dimension9Id(inputTaxDist.getDimension9Id())
                    .dimension10Id(inputTaxDist.getDimension10Id())
                    .dimension11Id(inputTaxDist.getDimension11Id())
                    .dimension12Id(inputTaxDist.getDimension12Id())
                    .dimension13Id(inputTaxDist.getDimension13Id())
                    .dimension14Id(inputTaxDist.getDimension14Id())
                    .dimension15Id(inputTaxDist.getDimension15Id())
                    .dimension16Id(inputTaxDist.getDimension16Id())
                    .dimension17Id(inputTaxDist.getDimension17Id())
                    .dimension18Id(inputTaxDist.getDimension18Id())
                    .dimension19Id(inputTaxDist.getDimension19Id())
                    .dimension20Id(inputTaxDist.getDimension20Id())
                    //.expenseTypeId(inputTaxDist.getExpenseTypeId())
                    //.taxFunctionAmount(inputTaxDist.getTaxDistFunctionAmount())
                    .id(inputTaxDist.getId())
                    .lineId(inputTaxDist.getInputTaxLineId())
                    .headerId(expInputTaxHeader.getId())
                    .resCenterId(inputTaxDist.getResponsibilityCenterId())
                    //.noTaxFunctionAmount(inputTaxDist.getNoTaxDistFunctionAmount())
                    //.noTaxAmount(inputTaxDist.getNoTaxDistAmount())
                    //.taxAmount(inputTaxDist.getTaxDistAmount())
                    .amount(inputTaxDist.getAmount())
                    .functionAmount(inputTaxDist.getFunctionAmount())
                    .baseAmount(inputTaxDist.getBaseAmount())
                    .baseFunctionAmount(inputTaxDist.getBaseFunctionAmount())
                    .departmentId(inputTaxDist.getDepartmentId()).build();
            expenseInputTaxDists.add(expenseInputTaxDist);
        });
        return expenseInputTaxDists;
    }
}
