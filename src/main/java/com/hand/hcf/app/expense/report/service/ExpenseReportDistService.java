package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.co.ResponsibilityCenterCO;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.DimensionUtils;
import com.hand.hcf.app.expense.common.utils.ParameterConstant;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.common.utils.StringUtil;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLine;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineDist;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineDistService;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineService;
import com.hand.hcf.app.expense.report.domain.*;
import com.hand.hcf.app.expense.report.dto.ExpenseReportDistDTO;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportDistMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.MessageService;
import com.hand.hcf.core.util.OperationUtil;
import com.hand.hcf.core.util.PageUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 14:39
 * @remark
 */
@Service
public class ExpenseReportDistService extends BaseService<ExpenseReportDistMapper,ExpenseReportDist>{

    @Autowired
    private ExpenseReportTaxDistService expenseReportTaxDistService;

    @Autowired
    private ExpenseReportHeaderService expenseReportHeaderService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private InvoiceLineService invoiceLineService;

    @Autowired
    private InvoiceLineDistService invoiceLineDistService;

    @Autowired
    private ExpenseReportLineService expenseReportLineService;

    @Autowired
    private ExpenseReportTypeDistSettingService expenseReportTypeDistSettingService;

    @Autowired
    private MessageService messageService;

    /**
     * 根据报账单ID删除分摊行信息
     * @param headerId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExpenseReportDistByHeaderId(Long headerId){
        return delete(new EntityWrapper<ExpenseReportDist>().eq("exp_report_header_id",headerId));
    }

    /**
     * 根据分摊行ID删除分摊行信息
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExpenseReportDistById(Long id){
        //删除分摊行同时删除税金分摊行
        expenseReportTaxDistService.deleteExpenseReportTaxDistByDistId(id);
        return deleteById(id);
    }

    /**
     * 根据费用ID删除分摊行信息
     * @param lineId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExpenseReportDistByLineId(Long lineId){
        List<ExpenseReportDist> distList = selectList(new EntityWrapper<ExpenseReportDist>().eq("exp_report_line_id", lineId));
        List<Long> collect = distList.stream().map(ExpenseReportDist::getId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(collect)){
            expenseReportTaxDistService.deleteExpenseReportTaxDistByDistIds(collect);
            return deleteBatchIds(collect);
        }
        return true;
    }

    /**
     * 更新审核状态
     * @param headerId
     * @param auditFlag
     * @param auditDate
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateExpenseReportDistAduitStatusByHeaderId(Long headerId,
                                                                String auditFlag,
                                                                ZonedDateTime auditDate){
        List<ExpenseReportDist> dists = selectList(new EntityWrapper<ExpenseReportDist>().eq("exp_report_header_id", headerId));
        if(CollectionUtils.isNotEmpty(dists)){
            dists.stream().forEach(e -> {
                e.setAuditFlag(auditFlag);
                e.setAuditDate(auditDate);
            });
            return updateAllColumnBatchById(dists);
        }
        return true;
    }

    /**
     * 批量保存分摊行数据
     * @param expenseReportDistList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveExpenseReportDistBatch(List<ExpenseReportDist> expenseReportDistList,
                                           ExpenseReportLine line,
                                           String expTaxDist){
        expenseReportDistList.stream().forEach(e -> saveExpenseReportDist(e,line,expTaxDist));
    }

    /**
     * 处理分摊行及税金分摊行尾差
     * @param line
     * @param expTaxDist
     */
    public void handleTailDifference(ExpenseReportLine line, String expTaxDist){
        //分摊行尾差处理
        ExpenseReportDist expenseReportDist = checkAmount(line,expTaxDist);
        BigDecimal amountSub = OperationUtil.subtract(line.getAmount(), expenseReportDist.getAmount());
        BigDecimal fAmountSub = OperationUtil.subtract(line.getFunctionAmount(), expenseReportDist.getFunctionAmount());
        BigDecimal expenseAmountSub = OperationUtil.subtract(line.getExpenseAmount(), expenseReportDist.getNoTaxDistAmount());
        BigDecimal fExpenseAmountSub = OperationUtil.subtract(line.getExpenseFunctionAmount(), expenseReportDist.getNoTaxDistFunctionAmount());
        BigDecimal taxAmountSub = OperationUtil.subtract(line.getTaxAmount(), expenseReportDist.getTaxDistAmount());
        BigDecimal fTaxAmountSub = OperationUtil.subtract(line.getTaxFunctionAmount(), expenseReportDist.getTaxDistFunctionAmount());
        // 只要以上6个金额任意一个不为零，都需要处理尾差
        if(!amountSub.equals(BigDecimal.ZERO) || !fAmountSub.equals(BigDecimal.ZERO)
                || !expenseAmountSub.equals(BigDecimal.ZERO) || !fExpenseAmountSub.equals(BigDecimal.ZERO)
                || !taxAmountSub.equals(BigDecimal.ZERO) || !fTaxAmountSub.equals(BigDecimal.ZERO)){
            ExpenseReportDist expenseReportDistMax = selectOne(new EntityWrapper<ExpenseReportDist>()
                    .eq("exp_report_line_id", line.getId())
                    .eq("amount", expenseReportDist.getMaxAmount()));
            expenseReportDistMax.setAmount(expenseReportDistMax.getAmount().add(amountSub));
            expenseReportDistMax.setFunctionAmount(expenseReportDistMax.getFunctionAmount().add(fAmountSub));
            expenseReportDistMax.setNoTaxDistAmount(expenseReportDistMax.getNoTaxDistAmount().add(expenseAmountSub));
            expenseReportDistMax.setNoTaxDistFunctionAmount(expenseReportDistMax.getNoTaxDistFunctionAmount().add(fExpenseAmountSub));
            expenseReportDistMax.setTaxDistAmount(expenseReportDistMax.getTaxDistAmount().add(taxAmountSub));
            expenseReportDistMax.setTaxDistFunctionAmount(expenseReportDistMax.getTaxDistFunctionAmount().add(fTaxAmountSub));
            updateById(expenseReportDistMax);
            // 税额只要有尾差调整，都需要重新计算金额
            if(!(taxAmountSub.equals(BigDecimal.ZERO) && fTaxAmountSub.equals(BigDecimal.ZERO))){
                resetExpenseReportTaxDist(expenseReportDistMax,line,expTaxDist);
            }
        }
    }

    /**
     * 校验分摊行和费用行金额是否相等
     * @param line
     * @param expTaxDist
     * @return
     */
    private ExpenseReportDist checkAmount(ExpenseReportLine line, String expTaxDist){
        Wrapper<ExpenseReportDist> distWrapper = new EntityWrapper<ExpenseReportDist>().eq("exp_report_line_id", line.getId());
        distWrapper.setSqlSelect("sum(amount) amount, " +
                "sum(function_amount) functionAmount, " +
                "sum(no_tax_dist_amount) noTaxDistAmount, " +
                "sum(no_tax_dist_function_amount) noTaxDistFunctionAmount, " +
                "sum(tax_dist_amount) taxDistAmount, " +
                "sum(tax_dist_function_amount) taxDistFunctionAmount, " +
                "max(amount) maxAmount");
        ExpenseReportDist expenseReportDist = selectOne(distWrapper);
        if(expenseReportDist.getAmount() == null){
            throw new BizException(RespCode.EXPENSE_REPORT_LINE_AMOUNT_UNEQUAL_DIST_AMOUNT);
        }
        boolean amountEqual = true;
        // 按什么金额分摊，就以什么金额为标准
        // 按含税金额分摊
        if(ParameterConstant.TAX_IN.equals(expTaxDist)){
            amountEqual = line.getAmount().equals(expenseReportDist.getAmount());
            // 按不含税金额分摊
        }else if(ParameterConstant.TAX_OFF.equals(expTaxDist)){
            amountEqual = line.getExpenseAmount().equals(expenseReportDist.getNoTaxDistAmount());
        }
        if(! amountEqual){
            throw new BizException(RespCode.EXPENSE_REPORT_LINE_AMOUNT_UNEQUAL_DIST_AMOUNT);
        }
        return expenseReportDist;
    }


    /**
     * 保存分摊行数据，同时生成税金分摊行数据
     * @param expenseReportDist
     * @param line
     * @param expTaxDist        税金分摊方式
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveExpenseReportDist(ExpenseReportDist expenseReportDist,
                                         ExpenseReportLine line,
                                         String expTaxDist){
        if(expenseReportDist.getId() == null){
            if(expenseReportDist.getExpReportLineId() == null){
                expenseReportDist.setExpReportHeaderId(line.getExpReportHeaderId());
                expenseReportDist.setExpReportLineId(line.getId());
            }
            ExpenseReportHeader expenseReportHeader = expenseReportHeaderService.selectById(line.getExpReportHeaderId());
            if(expenseReportDist.getTenantId() == null){
                expenseReportDist.setTenantId(expenseReportHeader.getTenantId());
            }
            if(expenseReportDist.getSetOfBooksId() == null){
                expenseReportDist.setSetOfBooksId(expenseReportHeader.getSetOfBooksId());
            }
            ExpenseReportTypeDistSetting reportTypeDistSetting =
                    expenseReportTypeDistSettingService.selectOne(new EntityWrapper<ExpenseReportTypeDistSetting>().eq("report_type_id", expenseReportHeader.getDocumentTypeId()));
            // 公司
            if(expenseReportDist.getCompanyId() == null){
                if(BooleanUtils.isNotTrue(reportTypeDistSetting.getCompanyDistFlag())){
                    expenseReportDist.setCompanyId(expenseReportHeader.getCompanyId());
                }else{
                    if(reportTypeDistSetting.getCompanyDefaultId() == null){
                        throw new BizException(RespCode.EXPENSE_REPORT_DIST_REQUIRED_FIELD_NONE,
                                new String[]{messageService.getMessageDetailByCode(RespCode.EXPENSE_REPORT_DIST_COMPANY)});
                    }
                    expenseReportDist.setCompanyId(reportTypeDistSetting.getCompanyDefaultId());
                    if(expenseReportDist.getCompanyId() == null){
                        throw new BizException(RespCode.SYS_REQUIRED_FIELD_NONE);
                    }
                }

            }
            // 部门
            if(expenseReportDist.getDepartmentId() == null){
                if(BooleanUtils.isNotTrue(reportTypeDistSetting.getDepartmentDistFlag())){
                    expenseReportDist.setDepartmentId(expenseReportHeader.getDepartmentId());
                }else{
                    if(reportTypeDistSetting.getDepartmentDefaultId() == null){
                        throw new BizException(RespCode.EXPENSE_REPORT_DIST_REQUIRED_FIELD_NONE,
                                new String[]{messageService.getMessageDetailByCode(RespCode.EXPENSE_REPORT_DIST_DEPARTMENT)});
                    }
                    expenseReportDist.setDepartmentId(reportTypeDistSetting.getDepartmentDefaultId());
                    if(expenseReportDist.getDepartmentId() == null){
                        throw new BizException(RespCode.SYS_REQUIRED_FIELD_NONE);
                    }
                }
            }
            // 责任中心
            if(expenseReportDist.getResponsibilityCenterId() == null){
                if(BooleanUtils.isTrue(reportTypeDistSetting.getResCenterDistFlag())){
                    if("DEP_RES_CENTER".equals(reportTypeDistSetting.getResDistRange())){
                        ResponsibilityCenterCO defaultResponsibilityCenter = organizationService.getDefaultResponsibilityCenter(expenseReportDist.getCompanyId(), expenseReportDist.getDepartmentId());
                        if(defaultResponsibilityCenter != null){
                            expenseReportDist.setResponsibilityCenterId(defaultResponsibilityCenter.getId());
                        }
                    }else{
                        expenseReportDist.setResponsibilityCenterId(reportTypeDistSetting.getResDefaultId());
                    }
                }
            }

            if(expenseReportDist.getExpenseTypeId() == null){
                expenseReportDist.setExpenseTypeId(line.getExpenseTypeId());
            }
            expenseReportDist.setCurrencyCode(line.getCurrencyCode());
            expenseReportDist.setExchangeRate(line.getExchangeRate());
            expenseReportDist.setReverseFlag("N");
            expenseReportDist.setAuditFlag("N");
        }
        expenseReportDist.setFunctionAmount(OperationUtil.safeMultiply(expenseReportDist.getAmount(),expenseReportDist.getExchangeRate()));
        //税分摊额计算
        // 费用金额 = 报账金额，则表示不需要分摊税
        if(line.getAmount().equals(line.getExpenseAmount())){
            expenseReportDist.setTaxDistAmount(BigDecimal.ZERO);
            expenseReportDist.setTaxDistFunctionAmount(BigDecimal.ZERO);
            expenseReportDist.setNoTaxDistAmount(expenseReportDist.getAmount());
            expenseReportDist.setNoTaxDistFunctionAmount(expenseReportDist.getFunctionAmount());
        }else{
            // 按含税金额分摊 即分摊金额reportDistAmount = amount
            if(ParameterConstant.TAX_IN.equals(expTaxDist)){
                BigDecimal taxAmount = OperationUtil.safeDivide(OperationUtil.safeMultiply(expenseReportDist.getAmount(), line.getTaxAmount(), 8), line.getAmount());
                expenseReportDist.setTaxDistAmount(taxAmount);
                expenseReportDist.setTaxDistFunctionAmount(OperationUtil.safeMultiply(taxAmount,line.getExchangeRate()));
                expenseReportDist.setNoTaxDistAmount(OperationUtil.subtract(expenseReportDist.getAmount(),taxAmount));
                expenseReportDist.setNoTaxDistFunctionAmount(OperationUtil.subtract(expenseReportDist.getFunctionAmount(),expenseReportDist.getTaxDistFunctionAmount()));
                // 按不含税金额分摊 即分摊金额reportDistAmount = noTaxDistAmount
            }else if(ParameterConstant.TAX_OFF.equals(expTaxDist)){
                BigDecimal taxAmount = OperationUtil.safeDivide(OperationUtil.safeMultiply(expenseReportDist.getNoTaxDistAmount(), line.getTaxAmount(), 8), line.getExpenseAmount());
                expenseReportDist.setTaxDistAmount(taxAmount);
                expenseReportDist.setTaxDistFunctionAmount(OperationUtil.safeMultiply(taxAmount,line.getExchangeRate()));
                expenseReportDist.setAmount(OperationUtil.sum(expenseReportDist.getAmount(),taxAmount));
                expenseReportDist.setFunctionAmount(OperationUtil.sum(expenseReportDist.getFunctionAmount(),expenseReportDist.getTaxDistFunctionAmount()));
            }
        }
        insertOrUpdateAllColumn(expenseReportDist);
        resetExpenseReportTaxDist(expenseReportDist,line,expTaxDist);
        return true;
    }

    /**
     * 更新分摊行之后，需要重置税金分摊行
     * @param expenseReportDist
     * @param line
     * @param expTaxDist
     */
    private void resetExpenseReportTaxDist(ExpenseReportDist expenseReportDist,
                                           ExpenseReportLine line,
                                           String expTaxDist){
        // 每次把税金分摊行删除之后重新创建
        expenseReportTaxDistService.deleteExpenseReportTaxDistByDistId(expenseReportDist.getId());
        List<InvoiceLine> invoiceLines = invoiceLineService.selectInvoiceByExpenseLineId(line.getId(),"Y");
        if(CollectionUtils.isNotEmpty(invoiceLines)){
            List<Long> collect = invoiceLines.stream().map(InvoiceLine::getId).collect(Collectors.toList());
            List<InvoiceLineDist> invoiceDists = invoiceLineDistService.selectList(new EntityWrapper<InvoiceLineDist>().in("invoice_line_id", collect).orderBy("tax_amount"));
            List<ExpenseReportTaxDist> taxDists = new ArrayList();
            BigDecimal taxAmountSum = BigDecimal.ZERO;
            BigDecimal taxAmountFunctionSum = BigDecimal.ZERO;
            for(int i = 0; i < invoiceDists.size(); i ++){
                InvoiceLineDist invoiceLineDist = invoiceDists.get(i);
                ExpenseReportTaxDist expenseReportTaxDist = new ExpenseReportTaxDist();
                expenseReportTaxDist.setExpReportHeaderId(expenseReportDist.getExpReportHeaderId());
                expenseReportTaxDist.setExpReportDistId(expenseReportDist.getId());
                expenseReportTaxDist.setInvoiceDistId(invoiceLineDist.getId());
                expenseReportTaxDist.setTenantId(expenseReportDist.getTenantId());
                expenseReportTaxDist.setSetOfBooksId(expenseReportDist.getSetOfBooksId());
                expenseReportTaxDist.setCompanyId(expenseReportDist.getCompanyId());
                expenseReportTaxDist.setDepartmentId(expenseReportDist.getDepartmentId());
                expenseReportTaxDist.setExpenseTypeId(expenseReportDist.getExpenseTypeId());
                expenseReportTaxDist.setResponsibilityCenterId(expenseReportDist.getResponsibilityCenterId());
                // 按含税金额分摊
                if (ParameterConstant.TAX_IN.equals(expTaxDist)) {
                    expenseReportTaxDist.setTaxAmount(OperationUtil.safeDivide(OperationUtil.safeMultiply(expenseReportDist.getAmount(), invoiceLineDist.getTaxAmount(), 8), line.getAmount()));
                } else if (ParameterConstant.TAX_OFF.equals(expTaxDist)) {
                    expenseReportTaxDist.setTaxAmount(OperationUtil.safeDivide(OperationUtil.safeMultiply(expenseReportDist.getNoTaxDistAmount(), invoiceLineDist.getTaxAmount(), 8), line.getExpenseAmount()));
                }
                expenseReportTaxDist.setFunctionAmount(OperationUtil.safeMultiply(expenseReportTaxDist.getTaxAmount(), invoiceLineDist.getExchangeRate()));
                expenseReportTaxDist.setExchangeRate(expenseReportDist.getExchangeRate());
                expenseReportTaxDist.setCurrencyCode(expenseReportDist.getCurrencyCode());
                expenseReportTaxDist.setTaxRate(invoiceLineDist.getTaxRate());
                expenseReportTaxDist.setReverseFlag("N");
                taxAmountSum = taxAmountSum.add(expenseReportTaxDist.getTaxAmount());
                taxAmountFunctionSum = taxAmountFunctionSum.add(expenseReportTaxDist.getFunctionAmount());
                // 金额最大的处理尾差
                if(i == invoiceDists.size() - 1){
                    if(! expenseReportDist.getTaxDistAmount().equals(taxAmountSum)){
                        BigDecimal taxSub = expenseReportDist.getTaxDistAmount().subtract(taxAmountSum);
                        BigDecimal funTaxSub = expenseReportDist.getTaxDistFunctionAmount().subtract(taxAmountFunctionSum);
                        expenseReportTaxDist.setTaxAmount(expenseReportTaxDist.getTaxAmount().add(taxSub));
                        expenseReportTaxDist.setFunctionAmount(expenseReportTaxDist.getFunctionAmount().add(funTaxSub));
                    }
                }

                taxDists.add(expenseReportTaxDist);
            }
            expenseReportTaxDistService.insertBatch(taxDists);
        }
    }

    /**
     * 根据配置获取 分摊金额
     * @param expenseReportDist
     * @param expTaxDist
     * @return
     */
    private BigDecimal getReportDistAmount(ExpenseReportDist expenseReportDist,
                                           String expTaxDist,
                                           boolean isFunctionAmount){
        // 按含税金额分摊 即分摊金额reportDistAmount = amount
        if(ParameterConstant.TAX_IN.equals(expTaxDist)){
            return isFunctionAmount ? expenseReportDist.getFunctionAmount() : expenseReportDist.getAmount();
        // 按不含税金额分摊 即分摊金额reportDistAmount = noTaxDistAmount
        }else if(ParameterConstant.TAX_OFF.equals(expTaxDist)){
            return isFunctionAmount ? expenseReportDist.getNoTaxDistFunctionAmount() : expenseReportDist.getNoTaxDistAmount();
        }
        return null;
    }

    public List<ExpenseReportDistDTO> getExpenseReportDistDTOByLineId(Long lineId){
        List<ExpenseReportDist> distList = baseMapper.selectList(new EntityWrapper<ExpenseReportDist>().eq("exp_report_line_id", lineId));
        int index = 1;
        List<ExpenseReportDistDTO> list = new ArrayList<>();
        //税金分摊方式
        ExpenseReportLine expenseReportLine = expenseReportLineService.selectById(lineId);
        String expTaxDist = organizationService.getParameterValue(expenseReportLine.getCompanyId(),
                expenseReportLine.getSetOfBooksId(), ParameterConstant.EXP_TAX_DIST);
        for(ExpenseReportDist dist : distList){
            ExpenseReportDistDTO dto = new ExpenseReportDistDTO();
            BeanUtils.copyProperties(dist,dto);
            dto.setIndex(index ++);
            CompanyCO companyById = organizationService.getCompanyById(dto.getCompanyId());
            dto.setCompanyName(companyById.getName());
            DepartmentCO departmentById = organizationService.getDepartmentById(dto.getDepartmentId());
            dto.setDepartmentName(departmentById.getName());
            if(dto.getResponsibilityCenterId() != null){
                ResponsibilityCenterCO responsibilityCenterById = organizationService.getResponsibilityCenterById(dto.getResponsibilityCenterId());
                dto.setResponsibilityCenterName(responsibilityCenterById.getResponsibilityCenterName());
            }
            DimensionUtils.setDimensionName(dto,organizationService);
            list.add(dto);
        }
        return list;
    }

    /**
     *
     * @param page
     * @param documentNumber
     * @param reportDocumentNumber
     * @param companyId
     * @param unitId
     * @return
     *
     * 如果直接用费用报账单分摊行来进行关联费用申请单的话，就可以直接用。
     * 如果需要关联释放申请的这个表的话则需要重新定义sql 。
     */

    public List<ExpenseReportDistDTO> queryExpenseReportDistFromApplication(Page page, String documentNumber, String reportDocumentNumber, Long companyId, Long unitId) {
        //单据编号模糊查询
        if (!StringUtil.isNullOrEmpty(reportDocumentNumber)){
            reportDocumentNumber = "%" + reportDocumentNumber + "%";
        }
        Wrapper<ExpenseReportDist> wrapper = new EntityWrapper<ExpenseReportDist>()
                    .eq(companyId!=null,"ed.company_id",companyId)
                    .eq(unitId!=null,"ed.department_id",unitId)
                    .eq("ed.source_document_category", ExpenseDocumentTypeEnum.EXP_REQUISITION.name());
        List<ExpenseReportDistDTO>reportDistDTOList =baseMapper.queryExpenseReportDistFromApplication(page,wrapper,documentNumber,reportDocumentNumber);
        //设置dto相关属性
        for (ExpenseReportDistDTO expenseReportDistDTO : reportDistDTOList) {
            CompanyCO company = organizationService.getCompanyById(expenseReportDistDTO.getCompanyId());
            DepartmentCO department = organizationService.getDepartmentById(expenseReportDistDTO.getDepartmentId());
            expenseReportDistDTO.setCompanyName(company.getName());
            expenseReportDistDTO.setDepartmentName(department.getName());
            String auditFlag = expenseReportDistDTO.getAuditFlag();
            if (auditFlag.equals("N")){
                auditFlag = messageService.getMessageDetailByCode(RespCode.EXPENSE_REPORT_DIST_APPROVING);
            }else{
                auditFlag = messageService.getMessageDetailByCode(RespCode.EXPENSE_REPORT_DIST_APPROVED);
            }
            expenseReportDistDTO.setAuditFlag(auditFlag);
            String reverseFlag = expenseReportDistDTO.getReverseFlag();
            if (reverseFlag.equals("N")){
                reverseFlag = messageService.getMessageDetailByCode(RespCode.EXPENSE_REPORT_DIST_NOT_REVERSE);
            }else if(reverseFlag.equals("Y")){
                reverseFlag = messageService.getMessageDetailByCode(RespCode.EXPENSE_REPORT_DIST_REVERSED);
            }else {
                reverseFlag = messageService.getMessageDetailByCode(RespCode.EXPENSE_REPORT_DIST_NOT_APPROVE);
            }
            expenseReportDistDTO.setReverseFlag(reverseFlag);
        }
        return  reportDistDTOList;
    }

    /**
     * 根据报账单id获取分摊行
     */
    public List<ExpenseReportDist> getExpenseReportDistByLineId(Long lineId) {
        return baseMapper.selectList(new EntityWrapper<ExpenseReportDist>()
                .eq("exp_report_line_id", lineId));
    }

    /**
     * 根据分摊行id获取报账单行id
     * @param distId
     * @return
     */
    public Long getExpenseReportLineByDistId(Long distId){
        return this.selectById(distId).getExpReportLineId();
    }

    /**
     * 根据报账单id获取分摊行
     */
    public List<ExpenseReportDist> getExpenseReportDistByHeaderId(Long headerId) {
        return baseMapper.selectList(new EntityWrapper<ExpenseReportDist>()
                .eq("exp_report_header_id", headerId));
    }
}
