package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.client.attachment.AttachmentCO;
import com.hand.hcf.app.client.org.SysCodeValueCO;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.expense.book.domain.ExpenseBook;
import com.hand.hcf.app.expense.book.service.ExpenseBookService;
import com.hand.hcf.app.expense.common.domain.enums.DocumentTypeEnum;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.ParameterConstant;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.invoice.domain.*;
import com.hand.hcf.app.expense.invoice.dto.InvoiceDTO;
import com.hand.hcf.app.expense.invoice.service.*;
import com.hand.hcf.app.expense.report.domain.ExpenseReportDist;
import com.hand.hcf.app.expense.report.domain.ExpenseReportHeader;
import com.hand.hcf.app.expense.report.domain.ExpenseReportLine;
import com.hand.hcf.app.expense.report.dto.ExpenseReportDistDTO;
import com.hand.hcf.app.expense.report.dto.ExpenseReportLineDTO;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportLineMapper;
import com.hand.hcf.app.expense.type.domain.ExpenseDocumentField;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.enums.FieldType;
import com.hand.hcf.app.expense.type.service.ExpenseDocumentFieldService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.app.expense.type.web.dto.OptionDTO;
import com.hand.hcf.app.mdata.client.workflow.enums.DocumentOperationEnum;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.OperationUtil;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.util.TypeConversionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 14:39
 * @remark
 */
@Service
public class ExpenseReportLineService extends BaseService<ExpenseReportLineMapper,ExpenseReportLine>{

    @Autowired
    private ExpenseReportDistService expenseReportDistService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ExpenseDocumentFieldService documentFieldService;

    @Autowired
    private ExpenseReportHeaderService expenseReportHeaderService;

    @Autowired
    private ExpenseTypeService expenseTypeService;

    @Autowired
    private InvoiceHeadService invoiceHeadService;

    @Autowired
    private InvoiceLineService invoiceLineService;

    @Autowired
    private InvoiceLineExpenceService invoiceLineExpenceService;

    @Autowired
    private InvoiceLineDistService invoiceLineDistService;

    @Autowired
    private ExpenseReportPaymentScheduleService expenseReportPaymentScheduleService;

    @Autowired
    private ExpenseReportTypeService expenseReportTypeService;

    @Autowired
    private InvoiceTypeService invoiceTypeService;

    @Autowired
    private ExpenseBookService expenseBookService;


    /**
     * 根据报账单头ID删除费用行信息
     * @param headerId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExpenseReportLineByHeaderId(Long headerId){
        List<ExpenseReportLine> lines = selectList(new EntityWrapper<ExpenseReportLine>().eq("exp_report_header_id", headerId));
        List<Long> collect = lines.stream().map(ExpenseReportLine::getId).collect(Collectors.toList());
        //删除附件信息
        String attachmentOid = lines.stream().map(ExpenseReportLine::getAttachmentOid).collect(Collectors.joining(","));
        deleteAttachmentByAttachmentOid(attachmentOid);
        return deleteBatchIds(collect);
    }

    /**
     * 根据ID删除费用行,同时删除附带信息
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExpenseReportLineById(Long id){
        ExpenseReportLine expenseReportLine = selectById(id);
        ExpenseReportHeader expenseReportHeader = expenseReportHeaderService.selectById(expenseReportLine.getExpReportHeaderId());
        // 判断单据状态 非编辑中、撤回、拒绝的单据，都不能删除
        if(!(expenseReportHeader.getStatus().equals(DocumentOperationEnum.GENERATE.getId())
                || expenseReportHeader.getStatus().equals(DocumentOperationEnum.WITHDRAW.getId())
                || expenseReportHeader.getStatus().equals(DocumentOperationEnum.APPROVAL_REJECT.getId())
                || expenseReportHeader.getStatus().equals(DocumentOperationEnum.CANCEL.getId()))){
            throw new BizException(RespCode.EXPENSE_REPORT_CANNOT_DELETED,new String[]{expenseReportHeader.getRequisitionNumber()});
        }
        //删除费用行同时删除分摊行
        expenseReportDistService.deleteExpenseReportDistByLineId(id);

        deleteAttachmentByLineId(expenseReportLine);
        deleteInvoiceByLineId(id);
        deleteById(id);
        // 更新头金额
        updateReportHeaderAmount(expenseReportHeader);
        // 创建获取修改默认计划付款行
        expenseReportPaymentScheduleService.saveDefaultPaymentSchedule(expenseReportHeader);
        return true;
    }

    /**
     * 查询行信息
     * @param headerId
     * @param page
     * @return
     */
    public List<ExpenseReportLine> getExpenseReportLinesByHeaderId(Long headerId,Page page){
        int index = PageUtil.getPageStartIndex(page) + 1;
        List<ExpenseReportLine> reportLineList = baseMapper.selectPage(page, new EntityWrapper<ExpenseReportLine>()
                .eq("exp_report_header_id", headerId));
        for(ExpenseReportLine reportLine : reportLineList){
            reportLine.setIndex(index);
            index ++;
            setExpenseReportLineField(reportLine);
        }
        return reportLineList;
    }

    /**
     * 保存费用行
     * @param dto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExpenseReportLineDTO saveExpenseReportLine(ExpenseReportLineDTO dto){
        ExpenseReportLine line = new ExpenseReportLine();
        BeanUtils.copyProperties(dto,line);
        ExpenseReportHeader expenseReportHeader = checkExpenseLineInfoAndGetHeaderMessage(dto);
        ExpenseType expenseType = expenseTypeService.selectById(dto.getExpenseTypeId());
        setPriceAndQuantity(line,expenseType);
        boolean isNew = false;
        if(line.getId() == null){
            isNew = true;
            initExpenseReportLineMessage(expenseReportHeader,line);
        }
        //后续其他数据需要使用费用行ID，所以需要最先保存
        insertOrUpdate(line);
        // 更新费用金额
        setExpenseAmount(line);
        // 保存控件field
        saveExpenseDocumentFields(line.getId(),line.getExpReportHeaderId(),dto.getFields(),expenseType.getId(),isNew);
        // 保存发票信息
        saveInvoiceMessage(dto,line);
        // 保存分摊行信息
        saveReportDistMessage(dto,line);
        // 更新头金额
        updateReportHeaderAmount(expenseReportHeader);
        // 创建获取修改默认计划付款行
        expenseReportPaymentScheduleService.saveDefaultPaymentSchedule(expenseReportHeader);
        BeanUtils.copyProperties(line,dto);
        return dto;
    }

    /**
     * 设置费用金额
     * 费用金额=报账金额-发票类型是抵扣类发票行的税额
     * @param line
     */
    private void setExpenseAmount(ExpenseReportLine line){
        // 费用金额
        BigDecimal expenseAmount = line.getAmount();
        List<InvoiceLine> invoiceLines = invoiceLineService.selectInvoiceByExpenseLineId(line.getId());
        if(CollectionUtils.isNotEmpty(invoiceLines)){
            Map<Long, List<InvoiceLine>> collect = invoiceLines.stream().collect(Collectors.groupingBy(InvoiceLine::getInvoiceHeadId));
            List<InvoiceHead> invoiceHeads = invoiceHeadService.selectBatchIds(collect.keySet());
            // 计算费用金额
            for(InvoiceHead invoiceHead : invoiceHeads){
                InvoiceType invoiceType = invoiceTypeService.selectById(invoiceHead.getInvoiceTypeId());
                // 抵扣类
                if("Y".equals(invoiceType.getDeductionFlag())){
                    List<InvoiceLine> invoiceLinesDedution = collect.get(invoiceHead.getId());
                    BigDecimal reduce = invoiceLinesDedution.stream().map(InvoiceLine::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    expenseAmount = expenseAmount.subtract(reduce);
                }
            }
            // 后端计算金额，前端计算不准确时，已后端为主
            if(! line.getExpenseAmount().equals(expenseAmount)){
                line.setExpenseAmount(expenseAmount);
                line.setExpenseFunctionAmount(OperationUtil.safeMultiply(expenseAmount,line.getExchangeRate()));
                line.setTaxAmount(OperationUtil.subtract(line.getAmount(),line.getExpenseAmount()));
                line.setTaxFunctionAmount(OperationUtil.subtract(line.getFunctionAmount(),line.getExpenseFunctionAmount()));
                updateById(line);
            }
        }
    }

    /**
     * 查询费用行信息，分摊行信息需要分页，所以要单独查询
     * @param id
     * @return
     */
    public ExpenseReportLineDTO getExpenseReportLineById(Long id){
        ExpenseReportLine expenseReportLine = selectById(id);
        if(expenseReportLine == null){
            throw new BizException(RespCode.EXPENSE_REPORT_LINE_IS_NULL);
        }
        setExpenseReportLineField(expenseReportLine);
        ExpenseReportLineDTO expenseReportLineDTO = new ExpenseReportLineDTO();
        BeanUtils.copyProperties(expenseReportLine,expenseReportLineDTO);
        // 设置费用属性
        expenseReportLineDTO.setFields(getFields(expenseReportLine));
        // 设置发票信息
        expenseReportLineDTO.setInvoiceHeads(getInvoiceByExpenseLineId(expenseReportLine.getId()));
        return expenseReportLineDTO;
    }

    /**
     * 根据费用行ID获取关联的发票(发票行为关联的发票行)
     * @param expenseLineId
     * @return
     */
    private List<InvoiceHead> getInvoiceByExpenseLineId(Long expenseLineId){
        List<InvoiceLine> invoiceLines = invoiceLineService.selectInvoiceByExpenseLineId(expenseLineId);
        if(CollectionUtils.isNotEmpty(invoiceLines)){
            Map<Long, List<InvoiceLine>> invoiceMap = invoiceLines.stream().collect(Collectors.groupingBy(InvoiceLine::getInvoiceHeadId));
            //发票头
            List<InvoiceHead> invoiceHeads = invoiceHeadService.selectBatchIds(invoiceMap.keySet());
            invoiceHeads.stream().forEach(invoiceHead -> {
                if (invoiceHead == null){
                    throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
                }
                //设置发票类型名称
                InvoiceType invoiceType = invoiceTypeService.selectById(invoiceHead.getInvoiceTypeId());
                if (invoiceType == null){
                    throw new BizException(RespCode.INVOICE_TYPE_NOT_EXIST);
                }
                invoiceHead.setDeductionFlag(invoiceType.getDeductionFlag());
                invoiceHead.setInvoiceTypeName(invoiceType.getInvoiceTypeName());
                //设置录入方式值列表
                SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("CREATED_METHOD", invoiceHead.getCreatedMethod());
                if (sysCodeValueCO != null){
                    invoiceHead.setCreatedMethodName(sysCodeValueCO.getName());
                }
                //发票行
                invoiceHead.setInvoiceLineList(invoiceMap.get(invoiceHead.getId()));
            });
            return invoiceHeads;
        }
        return Arrays.asList();
    }

    /**
     * 获取申请类型的fields
     * @param expenseReportLine
     * @return
     */
    public List<ExpenseFieldDTO> getFields(ExpenseReportLine expenseReportLine) {
        List<ExpenseDocumentField> expenseDocumentFields = documentFieldService.selectList(
                new EntityWrapper<ExpenseDocumentField>()
                        .eq("header_id", expenseReportLine.getExpReportHeaderId())
                        .eq("line_id", expenseReportLine.getId())
                        .eq("document_type", DocumentTypeEnum.PUBLIC_REPORT)
                        .orderBy("sequence", true));
        return adaptExpenseDocumentField(expenseDocumentFields);
    }

    /**
     * 根据费用行及发票行ID删除发票行信息
     * @param invoiceId
     * @param lineId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteInvoiceByInvoiceLineId(Long invoiceId, Long lineId){
        InvoiceLineDist invoiceLineDist = invoiceLineDistService.selectOne(new EntityWrapper<InvoiceLineDist>().eq("invoice_line_id", invoiceId));
        InvoiceLineExpence invoiceLineExpence = invoiceLineExpenceService.selectOne(new EntityWrapper<InvoiceLineExpence>()
                .eq("exp_expense_line_id", lineId)
                .eq("invoice_dist_id", invoiceLineDist.getId()));
        invoiceRollback(invoiceLineExpence);
    }

    /**
     * 删除相关发票信息
     */
    private void deleteInvoiceByLineId(Long id){
        List<InvoiceLineExpence> invoiceLineExpenseByReportLineId = invoiceLineExpenceService.getInvoiceLineExpenseByReportLineId(id);
        invoiceLineExpenseByReportLineId.stream().forEach(e -> {
            if(com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(e.getReportLineInvoiceMethod())){
                invoiceRollback(e);
            }
        });
    }

    /**
     * 发票退回票夹
     * @param invoiceLineExpence
     */
    private void invoiceRollback(InvoiceLineExpence invoiceLineExpence){
        // 费用行手工录入的发票直接删除
        if(invoiceLineExpence.getReportLineInvoiceMethod().equals("BY_HAND")){
            invoiceLineExpenceService.deleteById(invoiceLineExpence.getId());
            invoiceLineService.deleteInvoiceLineByInvoiceDistId(invoiceLineExpence.getInvoiceDistId());
        }
        // 票夹导入的发票，退回票夹，即删除关联关系
        if(invoiceLineExpence.getReportLineInvoiceMethod().equals("FROM_INVOICE")){
            invoiceLineExpenceService.deleteById(invoiceLineExpence.getId());
        }
        // 从账本导入，将发票退回账本
        if(invoiceLineExpence.getReportLineInvoiceMethod().equals("FROM_EXPENSE_BOOK")){
            // 创建新的账本
            ExpenseBook expenseBook = expenseBookService.copyExpenseBookById(invoiceLineExpence.getExpenseBookId(),invoiceLineExpence.getDetailAmount());
            invoiceLineExpence.setReportLineInvoiceMethod(null);
            invoiceLineExpence.setExpExpenseHeadId(null);
            invoiceLineExpence.setExpExpenseLineId(null);
            invoiceLineExpence.setExpenseBookId(expenseBook.getId());
            invoiceLineExpenceService.updateAllColumnById(invoiceLineExpence);

        }
    }

    /**
     * 根据费用行删除附件信息
     * @param expenseReportLine
     */
    private void deleteAttachmentByLineId(ExpenseReportLine expenseReportLine){
        String attachmentOid = expenseReportLine.getAttachmentOid();
        deleteAttachmentByAttachmentOid(attachmentOid);
    }

    /**
     * 删除附件
     * @param attachmentOid
     */
    private void deleteAttachmentByAttachmentOid(String attachmentOid){
        // 判断是否存在附件
        if (StringUtils.hasText(attachmentOid)) {
            String[] strings = attachmentOid.split(",");
            organizationService.deleteAttachmentsByOids(Arrays.asList(strings));
        }
    }

    /**
     * 设置费用行基础属性
     * @param reportLine
     */
    private void setExpenseReportLineField(ExpenseReportLine reportLine){
        reportLine.setExpenseTypeName(expenseTypeService.selectById(reportLine.getExpenseTypeId()).getName());
        if(com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(reportLine.getAttachmentOid())){
            List<String> strings = Arrays.asList(reportLine.getAttachmentOid().split(","));
            reportLine.setAttachmentOidList(strings);
            List<AttachmentCO> attachmentCOS = organizationService.listAttachmentsByOids(strings);
            reportLine.setAttachments(attachmentCOS);
        }else{
            reportLine.setAttachmentOidList(Arrays.asList());
            reportLine.setAttachments(Arrays.asList());
        }
    }

    /**
     * 设置单价数量信息
     * @param line
     * @param expenseType
     */
    private void setPriceAndQuantity(ExpenseReportLine line, ExpenseType expenseType){
        // 是否是单价输入
        if (expenseType.getEntryMode()){
            line.setUom(expenseType.getPriceUnit());
            if (line.getQuantity() == null || line.getPrice() == null){
                throw new BizException(RespCode.EXPENSE_REPORT_LINE_PRICE_IS_NULL);
            }
        }else{
            line.setPrice(null);
            line.setUom(null);
            line.setQuantity(null);
        }
    }

    /**
     * 更新头金额
     * @param expenseReportHeader
     */
    private void updateReportHeaderAmount(ExpenseReportHeader expenseReportHeader){
        Wrapper<ExpenseReportLine> wrapper = new EntityWrapper<ExpenseReportLine>()
                .eq("exp_report_header_id", expenseReportHeader.getId()).groupBy("exp_report_header_id");
        wrapper.setSqlSelect("sum(amount) amount,sum(function_amount) functionAmount");
        ExpenseReportLine expenseReportLine = selectOne(wrapper);
        expenseReportHeader.setTotalAmount(expenseReportLine.getAmount());
        // 单据头本币金额由行汇总
        expenseReportHeader.setFunctionalAmount(expenseReportLine.getFunctionAmount());
        expenseReportHeaderService.updateById(expenseReportHeader);
    }

    /**
     * 保存分摊行数据
     * @param dto
     * @param line
     */
    private void saveReportDistMessage(ExpenseReportLineDTO dto, ExpenseReportLine line){
        List<ExpenseReportDistDTO> expenseReportDistList = dto.getExpenseReportDistList();
        List<ExpenseReportDist> collect = expenseReportDistList.stream().map(e -> {
            ExpenseReportDist expenseReportDist = new ExpenseReportDist();
            BeanUtils.copyProperties(e, expenseReportDist);
            return expenseReportDist;
        }).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(expenseReportDistList)){
            //税金分摊方式
            String expTaxDist = organizationService.getParameterValue(line.getCompanyId(),
                    line.getSetOfBooksId(), ParameterConstant.EXP_TAX_DIST);
            expenseReportDistService.saveExpenseReportDistBatch(collect,line,expTaxDist);
        }
    }

    /**
     * 保存发票信息
     * @param dto
     */
    private void saveInvoiceMessage(ExpenseReportLineDTO dto,ExpenseReportLine line){
        List<InvoiceHead> invoiceHeads = dto.getInvoiceHeads();
        // 根据发票信息保存发票，并存储关联关系
        if(CollectionUtils.isNotEmpty(invoiceHeads)){
            invoiceHeads.stream().forEach(invoiceHead -> {
                //发票ID为空，表示手工创建发票
                Boolean invoiceIsNew = invoiceHead.getId() == null ? true : false;
                if(invoiceHead.getId() == null){
                    InvoiceDTO invoiceDTO = new InvoiceDTO();
                    invoiceHead.setFromBook(false);
                    invoiceHead.setCreatedMethod("BY_HAND");
                    invoiceDTO.setInvoiceHead(invoiceHead);
                    invoiceDTO.setInvoiceLineList(invoiceHead.getInvoiceLineList());
                    invoiceHeadService.insertInvoice(invoiceDTO);
                }
                List<InvoiceLine> invoiceLineList = invoiceHead.getInvoiceLineList();
                invoiceLineList.stream().forEach(invoiceLine -> {
                    saveInvoiceLineExpence(invoiceLine,invoiceHead,line,invoiceIsNew);
                });
            });
        }
    }

    /**
     * 保存发票与费用的关联关系
     * @param invoiceLine   发票行
     * @param invoiceHead    发票头
     * @param line           费用行
     * @param invoiceIsNew        发票是否为新建
     */
    private void saveInvoiceLineExpence(InvoiceLine invoiceLine,
                      InvoiceHead invoiceHead,
                      ExpenseReportLine line,
                      boolean invoiceIsNew){
        List<InvoiceLineDist> invoiceDists = invoiceLineDistService.selectList(
                new EntityWrapper<InvoiceLineDist>().eq("invoice_line_id", invoiceLine.getId()));
        invoiceDists.stream().forEach(invoiceDist -> {
            InvoiceLineExpence lineExpense = invoiceLineExpenceService.selectOne(new EntityWrapper<InvoiceLineExpence>()
                    .eq("invoice_dist_id",invoiceDist.getId())
                    .eq("exp_expense_head_id",line.getExpReportHeaderId())
                    .eq("exp_expense_line_id",line.getId()));
            if(lineExpense == null){
                lineExpense = new InvoiceLineExpence();
            }
            lineExpense.setTenantId(invoiceLine.getTenantId());
            lineExpense.setSetOfBooksId(invoiceLine.getSetOfBooksId());
            lineExpense.setReportLineInvoiceMethod(invoiceIsNew ? "BY_HAND" : "FROM_INVOICE");
            lineExpense.setInvoiceCode(invoiceHead.getInvoiceCode());
            lineExpense.setExpenseBookId(line.getExpenseBookId());
            lineExpense.setInvoiceDistId(invoiceDist.getId());
            // N 新建
            lineExpense.setStatus("N");
            lineExpense.setInvoiceNo(invoiceHead.getInvoiceNo());
            lineExpense.setExpExpenseHeadId(line.getExpReportHeaderId());
            lineExpense.setExpExpenseLineId(line.getId());
            lineExpense.setDetailAmount(line.getAmount());
            lineExpense.setTaxRate(invoiceLine.getTaxRate());
            lineExpense.setTaxAmount(invoiceLine.getTaxAmount());
            invoiceLineExpenceService.insertOrUpdateAllColumn(lineExpense);
        });
    }

    private void initExpenseReportLineMessage(ExpenseReportHeader expenseReportHeader, ExpenseReportLine line){
        line.setExpReportHeaderId(expenseReportHeader.getId());
        line.setTenantId(line.getTenantId() == null ? expenseReportHeader.getTenantId() : line.getTenantId());
        line.setSetOfBooksId(line.getSetOfBooksId() == null ? expenseReportHeader.getSetOfBooksId() : line.getSetOfBooksId());
        line.setCompanyId(line.getCompanyId() == null ? expenseReportHeader.getCompanyId() : line.getCompanyId());
        line.setExchangeRate(expenseReportHeader.getExchangeRate());
        line.setCurrencyCode(expenseReportHeader.getCurrencyCode());
        line.setFunctionAmount(OperationUtil.safeMultiply(expenseReportHeader.getExchangeRate(),line.getAmount()));
        line.setExpenseFunctionAmount(OperationUtil.safeMultiply(expenseReportHeader.getExchangeRate(),line.getExpenseAmount()));
        line.setTaxAmount(OperationUtil.subtract(line.getAmount(),line.getExpenseAmount()));
        line.setTaxFunctionAmount(OperationUtil.subtract(line.getFunctionAmount(),line.getExpenseFunctionAmount()));
        line.setReverseFlag("N");
    }

    /**
     * 校验行信息，并获取单据头信息
     * @param dto
     * @return
     */
    private ExpenseReportHeader checkExpenseLineInfoAndGetHeaderMessage(ExpenseReportLineDTO dto){
        if(dto.getExpReportHeaderId() == null){
            throw new BizException(RespCode.EXPENSE_REPORT_HEADER_IS_NUTT);
        }
        ExpenseReportHeader expenseReportHeader = expenseReportHeaderService.selectById(dto.getExpReportHeaderId());

        if(expenseReportHeader == null){
            throw new BizException(RespCode.EXPENSE_REPORT_HEADER_IS_NUTT);
        }
        return expenseReportHeader;
    }

    /**
     * 保存控件field
     * @param lineId
     * @param headerId
     * @param fields
     */
    private void saveExpenseDocumentFields(Long lineId,
                                           Long headerId,
                                           List<ExpenseFieldDTO> fields,
                                           Long expenseTypeId,
                                           boolean isNew){
        if(!isNew){
            // 编辑时先删除
            documentFieldService.delete(new EntityWrapper<ExpenseDocumentField>()
                    .eq("header_id",headerId)
                    .eq("line_id", lineId)
                    .eq("document_type", DocumentTypeEnum.PUBLIC_REPORT));

        }
        if(CollectionUtils.isEmpty(fields)){
            return;
        }
        List<ExpenseDocumentField> documentFields = adaptExpenseFields(fields, lineId, headerId, expenseTypeId);
        documentFieldService.insertBatch(documentFields);
    }

    private List<ExpenseDocumentField> adaptExpenseFields(List<ExpenseFieldDTO> fields,
                                                          Long lineId,
                                                          Long headerId,
                                                          Long expenseTypeId){
        if(fields != null){
            List<ExpenseDocumentField> documentFields = fields.stream().map(e -> {
                ExpenseDocumentField field = ExpenseDocumentField.builder()
                        .commonField(e.getCommonField())
                        .fieldDataType(e.getFieldDataType())
                        .fieldOid(e.getFieldOid())
                        .customEnumerationOid(e.getCustomEnumerationOid())
                        .defaultValueConfigurable(e.getDefaultValueConfigurable())
                        .defaultValueKey(e.getDefaultValueKey())
                        .defaultValueMode(e.getDefaultValueMode())
                        .documentType(DocumentTypeEnum.PUBLIC_REPORT)
                        .editable(e.getEditable())
                        .expenseTypeId(expenseTypeId)
                        .headerId(headerId)
                        .lineId(lineId)
                        .mappedColumnId(e.getMappedColumnId())
                        .messageKey(e.getMessageKey())
                        .name(e.getName())
                        .printHide(e.getPrintHide())
                        .reportKey(e.getReportKey())
                        .required(e.getRequired())
                        .sequence(e.getSequence())
                        .showOnList(e.getShowOnList())
                        .value(e.getValue()).build();
                field.setId(null);
                field.setFieldType(e.getFieldType());
                return field;
            }).collect(Collectors.toList());
            return documentFields;
        }
        return null;
    }

    private List<ExpenseFieldDTO> adaptExpenseDocumentField(List<ExpenseDocumentField> fields){
        if (org.springframework.util.CollectionUtils.isEmpty(fields)){
            return new ArrayList<>();
        }else {
            List<ExpenseFieldDTO> expenseFieldDTOS = fields.stream().map(e -> {
                ExpenseFieldDTO fieldDTO = ExpenseFieldDTO.builder()
                        .commonField(e.getCommonField())
                        .customEnumerationOid(e.getCustomEnumerationOid())
                        .defaultValueConfigurable(e.getDefaultValueConfigurable())
                        .defaultValueKey(e.getDefaultValueKey())
                        .defaultValueMode(e.getDefaultValueMode())
                        .fieldDataType(e.getFieldDataType())
                        .fieldOid(e.getFieldOid())
                        .fieldType(e.getFieldType())
                        .mappedColumnId(e.getMappedColumnId())
                        .messageKey(e.getMessageKey())
                        .name(e.getName())
                        .id(e.getId())
                        .printHide(e.getPrintHide())
                        .reportKey(e.getReportKey())
                        .required(e.getRequired())
                        .sequence(e.getSequence())
                        .value(e.getValue())
                        .editable(e.getEditable())
                        .showOnList(e.getShowOnList())
                        .showValue(null)
                        .build();
                if (FieldType.CUSTOM_ENUMERATION.getId().equals(e.getFieldTypeId())){
                    List<SysCodeValueCO> sysCodeValueCOS = organizationService.listSysCodeValueCOByOid(e.getCustomEnumerationOid());
                    // 为值列表，则设置值列表的相关值
                    List<OptionDTO> options = sysCodeValueCOS.stream().map(OptionDTO::createOption).collect(Collectors.toList());
                    fieldDTO.setOptions(options);
                }
                return fieldDTO;
            }).collect(Collectors.toList());
            return expenseFieldDTOS;
        }
    }

    /**
     * 查询行信息 不分页
     * @param headerId
     * @return
     */
    public List<ExpenseReportLine> getExpenseReportLinesByHeaderId(Long headerId) {
        List<ExpenseReportLine> reportLineList = baseMapper.selectList(new EntityWrapper<ExpenseReportLine>()
                .eq("exp_report_header_id", headerId));
        return reportLineList;
    }

}
