package com.hand.hcf.app.expense.book.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.common.co.CurrencyRateCO;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.OperationUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.book.domain.ExpenseBook;
import com.hand.hcf.app.expense.book.persistence.ExpenseBookMapper;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLine;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineDist;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineExpence;
import com.hand.hcf.app.expense.invoice.dto.InvoiceDTO;
import com.hand.hcf.app.expense.invoice.service.InvoiceHeadService;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineDistService;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineExpenceService;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineService;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeService;
import com.hand.hcf.app.expense.type.domain.ExpenseDocumentField;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.domain.enums.FieldType;
import com.hand.hcf.app.expense.type.service.ExpenseDocumentFieldService;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.app.expense.type.web.dto.OptionDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/2/21 14:16
 * @version: 1.0.0
 */
@Service
public class ExpenseBookService extends BaseService<ExpenseBookMapper,ExpenseBook> {

    @Autowired
    private ExpenseTypeService expenseTypeService;

    @Autowired
    private InvoiceLineDistService invoiceLineDistService;

    @Autowired
    private InvoiceHeadService invoiceHeadService;

    @Autowired
    private InvoiceLineService invoiceLineService;

    @Autowired
    private InvoiceLineExpenceService invoiceLineExpenceService;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ExpenseDocumentFieldService documentFieldService;

    @Autowired
    private ExpenseReportTypeService expenseReportTypeService;

    /**
     *  新建费用（我的账本）
     * @param expenseBook
     * @return
     */
    @Transactional
    public ExpenseBook insertExpenseBook(ExpenseBook expenseBook) {
        //判断费用类型
        ExpenseType expenseType = expenseTypeService.selectById(expenseBook.getExpenseTypeId());
        if (expenseType == null){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        setExpenseBookInfo(expenseBook);
        this.insert(expenseBook);
        //保存控件
        if (!CollectionUtils.isEmpty(expenseBook.getFields())) {
            saveField(expenseBook, expenseBook.getFields(), expenseType, true);
        }
        //保存手工录入的发票与发票行报销记录表
        if(!CollectionUtils.isEmpty(expenseBook.getInvoiceHead())){
            saveInvoice(expenseBook,true);
        }
        return expenseBook;
    }

    /**
     * 保存手工录入的发票与发票行报销记录表
     * @param expenseBook
     */
    @Transactional
    public void saveInvoice(ExpenseBook expenseBook,Boolean isNew){
        if(!isNew) {
            //编辑时先删除 发票行报销记录表
            invoiceLineExpenceService.delete(
                    new EntityWrapper<InvoiceLineExpence>()
                            .eq("expense_book_id", expenseBook.getId()));
        }
        expenseBook.getInvoiceHead().stream().forEach(invoiceHead -> {
            if(invoiceHead.getId() == null) {
                if (invoiceHead.getFromBook()) {
                    //保存发票头行
                    InvoiceDTO invoiceDTO = InvoiceDTO
                            .builder()
                            .invoiceHead(invoiceHead)
                            .invoiceLineList(invoiceHead.getInvoiceLineList())
                            .build();
                    invoiceHeadService.insertInvoice(invoiceDTO);
//                    invoiceHead.setInvoiceLineList(invoiceDTO.getInvoiceLineList());
                }
            }
            List<Long> lineId = invoiceHead.getInvoiceLineList().stream().map(InvoiceLine::getId).collect(Collectors.toList());
            //保存账本与发票行报销记录关联
            saveInvoiceLineExpence(lineId,expenseBook.getId());
        });
    }

    /**
     * 设置账本基础信息
     * @param expenseBook 账本
     */
    private void setExpenseBookInfo(ExpenseBook expenseBook) {
        Long setOfBookId = OrgInformationUtil.getCurrentSetOfBookId();
        //获取账套下默认币种
        SetOfBooksInfoCO setOfBooksInfoCO = organizationService.getSetOfBooksById(setOfBookId);
        String baseCurrencyCode = setOfBooksInfoCO.getFunctionalCurrencyCode();
        // 设置汇率
        if (baseCurrencyCode.equals(expenseBook.getCurrencyCode())) {
            expenseBook.setExchangeRate(BigDecimal.valueOf(1));
        }else {
            CurrencyRateCO cny = organizationService.getForeignCurrencyByCode(baseCurrencyCode,
                    expenseBook.getCurrencyCode(),
                    setOfBookId);
            expenseBook.setExchangeRate(BigDecimal.valueOf(cny.getRate()));
        }
        expenseBook.setFunctionalAmount(TypeConversionUtils.roundHalfUp(expenseBook.getAmount().multiply(expenseBook.getExchangeRate())));
    }

    /**
     * 账本关联保存 发票行报销记录表
     * @param invoiceLineIdList 发票行ID
     * @param expenseBookId 账本ID
     */
    private void saveInvoiceLineExpence(List<Long> invoiceLineIdList,
                                        Long expenseBookId) {

        invoiceLineIdList.stream().forEach(invoiceLineId -> {
            InvoiceLine invoiceLine = invoiceLineService.selectById(invoiceLineId);
            if(invoiceLine == null){
                throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
            }
            InvoiceHead invoiceHead = invoiceHeadService.selectById(invoiceLine.getInvoiceHeadId());
            if(invoiceHead == null) {
                throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
            }
            //保存关联我的账本
            Long invoiceLineDistId = invoiceLineDistService.selectOne(
                    new EntityWrapper<InvoiceLineDist>()
                            .eq("invoice_line_id", invoiceLineId)).getId();
            InvoiceLineExpence lineExpence = InvoiceLineExpence.builder()
                    .tenantId(OrgInformationUtil.getCurrentTenantId())
                    .setOfBooksId(OrgInformationUtil.getCurrentSetOfBookId())
                    .expenseBookInvoiceMethod(invoiceHead.getFromBook() ? "BY_HAND" : "FROM_INVOICE")
                    .invoiceDistId(invoiceLineDistId)
                    .expenseBookId(expenseBookId)
                    .invoiceNo(invoiceHead.getInvoiceNo())
                    .invoiceCode(invoiceHead.getInvoiceCode())
                    .detailAmount(invoiceLine.getDetailAmount())
                    .taxRate(invoiceLine.getTaxRate())
                    .taxAmount(invoiceLine.getTaxAmount())
                    .status("N")
                    .build();
            List<InvoiceLineExpence> invoiceLineExpences = invoiceLineExpenceService.selectList(new EntityWrapper<InvoiceLineExpence>()
                    .eq(invoiceLineDistId != null, "invoice_dist_id", invoiceLineDistId));
            if(invoiceLineExpences.size()>0){
                throw new BizException(RespCode.INVOICE_LINE_IS_ALLOTED);
            }
            invoiceLineExpenceService.insert(lineExpence);
        });
    }

    /**
     * 保存控件
     * @param expenseBook
     * @param fields
     * @param expenseType
     * @param isNew
     */
    private void saveField(ExpenseBook expenseBook,
                           List<ExpenseFieldDTO> fields,
                           ExpenseType expenseType,
                           boolean isNew) {
        if(!isNew){
            // 编辑时先删除
            documentFieldService.delete(new EntityWrapper<ExpenseDocumentField>()
                    .eq("header_id",expenseBook.getId())
                    .eq("line_id", expenseBook.getId())
                    .eq("document_type", ExpenseDocumentTypeEnum.ACCOUNT_BOOK));

        }
        List<ExpenseDocumentField> documentFields = adaptExpenseFields(fields, expenseBook, expenseType);
        documentFieldService.insertBatch(documentFields);
    }

    private List<ExpenseDocumentField> adaptExpenseFields(List<ExpenseFieldDTO> fields,
                                                          ExpenseBook expenseBook,
                                                          ExpenseType expenseType){
        List<ExpenseDocumentField> documentFields = fields.stream().map(e -> {
            ExpenseDocumentField field = ExpenseDocumentField.builder()
                    .commonField(e.getCommonField())
                    .fieldDataType(e.getFieldDataType())
                    .fieldOid(e.getFieldOid())
                    .customEnumerationOid(e.getCustomEnumerationOid())
                    .defaultValueConfigurable(e.getDefaultValueConfigurable())
                    .defaultValueKey(e.getDefaultValueKey())
                    .defaultValueMode(e.getDefaultValueMode())
                    .documentType(ExpenseDocumentTypeEnum.ACCOUNT_BOOK)
                    .editable(e.getEditable())
                    .expenseTypeId(expenseType.getId())
                    .headerId(expenseBook.getId())
                    .lineId(expenseBook.getId())
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

    /**
     * 根据条件查询我的账本信息
     * @param expenseTypeId 费用类型ID
     * @param dateFrom 发生日期从
     * @param dateTo 发生日期至于
     * @param amountFrom 金额从
     * @param amountTo 金额至
     * @param currencyCode 币种
     * @param remarks 备注
     * @param expenseReportTypeId 报账单类型ID
     * @param queryPage
     * @return
     */
    public List<ExpenseBook> pageExpenseBookByCond(Long expenseTypeId,
                                                   String dateFrom,
                                                   String dateTo,
                                                   BigDecimal amountFrom,
                                                   BigDecimal amountTo,
                                                   String currencyCode,
                                                   String remarks,
                                                   Long expenseReportTypeId,
                                                   Page queryPage) {
        List<Long> expenseTypeIds = null;
        if(expenseReportTypeId != null){
            List<ExpenseType> expenseReportTypeExpenseType = expenseReportTypeService.getExpenseReportTypeExpenseType(expenseReportTypeId, null, null);
            expenseTypeIds = expenseReportTypeExpenseType.stream().map(ExpenseType::getId).collect(Collectors.toList());
        }
        Wrapper wrapper =  new EntityWrapper<ExpenseBook>()
                .eq(expenseTypeId != null,"expense_type_id",expenseTypeId)
                .ge(StringUtils.isNotEmpty(dateFrom),"expense_date", TypeConversionUtils.getStartTimeForDayYYMMDD(dateFrom))
                .le(StringUtils.isNotEmpty(dateTo),"expense_date", TypeConversionUtils.getEndTimeForDayYYMMDD(dateTo))
                .ge(amountFrom != null, "amount",amountFrom)
                .le(amountTo != null, "amount",amountTo)
                .eq(StringUtils.isNotEmpty(currencyCode),"currency_code",currencyCode)
                .like(StringUtils.isNotEmpty(remarks),"remarks",remarks)
                .in(com.baomidou.mybatisplus.toolkit.CollectionUtils.isNotEmpty(expenseTypeIds),"expense_type_id",expenseTypeIds)
                .eq("created_by",OrgInformationUtil.getCurrentUserId())
                .orderBy("expense_date");
        List<ExpenseBook> expenseBooks = baseMapper.pageExpenseBookByCond(queryPage,wrapper);
        expenseBooks.stream().forEach(s->{
            ExpenseType expenseType = expenseTypeService.selectById(s.getExpenseTypeId());
            if(expenseType != null){
                s.setExpenseTypeName(expenseType.getName());
            }
            //设置发票和费用体系
            setInvoiceAndDocumentField(s);
            //设置附件信息
            if (StringUtils.isNotEmpty(s.getAttachmentOid())){
                String[] strings = s.getAttachmentOid().split(",");
                List<String> attachmentOidList = Arrays.asList(strings);
                List<AttachmentCO> attachments = organizationService.listAttachmentsByOids(attachmentOidList);
                s.setAttachments(attachments);
            }
        });
        return expenseBooks;
    }

    /**
     *
     * @param ids
     * @return
     */
    public List<ExpenseBook> selectExpenseBookByIds(List<Long> ids){
        List<ExpenseBook> expenseBooks = selectBatchIds(ids);
        expenseBooks.stream().forEach(s -> {
            //设置发票和费用体系
            setInvoiceAndDocumentField(s);
        });
        return expenseBooks;
    }

    /**
     * 设置发票和费用体系
     * @param expenseBook
     */
    private void setInvoiceAndDocumentField(ExpenseBook expenseBook){
        //获取发票信息
        List<InvoiceHead> headList = baseMapper.getInvoiceByBookId(expenseBook.getId());
        if(!CollectionUtils.isEmpty(headList)){
            headList.stream().forEach(head -> {
                head.setInvoiceLineList(invoiceLineService.selectList(
                        new EntityWrapper<InvoiceLine>()
                                .eq("invoice_head_id",head.getId())));
            });
            expenseBook.setInvoiceHead(headList);
        }
        //设置控件信息
        List<ExpenseDocumentField> fields = documentFieldService.selectList(
                new EntityWrapper<ExpenseDocumentField>()
                        .eq("header_id",expenseBook.getId())
                        .eq("line_id", expenseBook.getId())
                        .eq("document_type", ExpenseDocumentTypeEnum.ACCOUNT_BOOK));
        expenseBook.setFields(adaptExpenseDocumentField(fields));
    }

    private List<ExpenseFieldDTO> adaptExpenseDocumentField(List<ExpenseDocumentField> fields) {
        if (CollectionUtils.isEmpty(fields)){
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
     * 更新我的账单
     * @param expenseBook
     * @return
     */
    @Transactional
    public ExpenseBook updateExpenseBook(ExpenseBook expenseBook) {
        Long setOfBookId = OrgInformationUtil.getCurrentSetOfBookId();
        //判断费用类型
        ExpenseType expenseType = expenseTypeService.selectById(expenseBook.getExpenseTypeId());
        if (expenseType == null){
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        setExpenseBookInfo(expenseBook);
        this.updateById(expenseBook);
        //保存控件
        if (!CollectionUtils.isEmpty(expenseBook.getFields())) {
            saveField(expenseBook, expenseBook.getFields(), expenseType, false);
        }
        //保存手工录入的发票与发票行报销记录表
        if(!CollectionUtils.isEmpty(expenseBook.getInvoiceHead())){
            saveInvoice(expenseBook,false);
        }
        return expenseBook;
    }

    @Transactional
    public Boolean deleteExpenseBook(Long expenseBookId, Long invoiceHeadId, Long invoiceLineId) {
        if(expenseBookId != null){
            InvoiceLineExpence invoiceLineExpence = invoiceLineExpenceService.getInvoiceLineExpenceById(expenseBookId, invoiceLineId);
            if (invoiceLineExpence == null) {
                throw new BizException("发票行报销记录不存在");
            }
            // 解除与账本关联
            invoiceLineExpenceService.delete(
                    new EntityWrapper<InvoiceLineExpence>()
                            .eq("expense_book_id", expenseBookId)
                            .eq("invoice_dist_id", invoiceLineExpence.getInvoiceDistId()));
            //从票夹导入
            if(!"BY_HAND".equals(invoiceLineExpence.getExpenseBookInvoiceMethod())){
                return true;
            }
        }
        invoiceLineDistService.delete(
                new EntityWrapper<InvoiceLineDist>()
                        .eq("invoice_line_id",invoiceLineId));
        invoiceLineService.deleteById(invoiceLineId);
        //判断当前发票没有行就删除发票头
        List<InvoiceLine> invoiceLines =invoiceLineService.selectList(
                new EntityWrapper<InvoiceLine>()
                        .eq("invoice_head_id",invoiceHeadId));
        if(CollectionUtils.isEmpty(invoiceLines)){
            invoiceHeadService.deleteById(invoiceHeadId);
        }
        return true;
    }

    /**
     * 拷贝账本信息
     * @param id
     * @param amount 账本金额
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ExpenseBook copyExpenseBookById(Long id, BigDecimal amount){
        //拷贝账本信息
        ExpenseBook expenseBook = selectById(id);
        expenseBook.setId(null);
        expenseBook.setAmount(amount);
        expenseBook.setFunctionalAmount(OperationUtil.safeMultiply(amount,expenseBook.getExchangeRate()));
        insert(expenseBook);
        //设置控件信息
        List<ExpenseDocumentField> fields = documentFieldService.selectList(
                new EntityWrapper<ExpenseDocumentField>()
                        .eq("header_id",id)
                        .eq("line_id", id)
                        .eq("document_type", ExpenseDocumentTypeEnum.ACCOUNT_BOOK));
        if(com.baomidou.mybatisplus.toolkit.CollectionUtils.isNotEmpty(fields)){
            fields.stream().forEach(field -> {
                field.setId(null);
                field.setHeaderId(expenseBook.getId());
                field.setLineId(expenseBook.getId());
            });
            documentFieldService.insertBatch(fields);
        }
        return expenseBook;
    }

}
