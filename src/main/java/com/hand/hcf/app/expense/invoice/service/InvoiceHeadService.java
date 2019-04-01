package com.hand.hcf.app.expense.invoice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.base.org.SysCodeValueCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.CurrencyRateCO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.invoice.domain.*;
import com.hand.hcf.app.expense.invoice.dto.InvoiceDTO;
import com.hand.hcf.app.expense.invoice.dto.InvoiceLineDistDTO;
import com.hand.hcf.app.expense.invoice.persistence.InvoiceHeadMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.TypeConversionUtils;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
@Service
@AllArgsConstructor
@Transactional
public class InvoiceHeadService extends BaseService<InvoiceHeadMapper,InvoiceHead> {
    private final InvoiceHeadMapper invoiceHeadMapper;

    private final InvoiceLineService invoiceLineService;

    private final InvoiceLineDistService invoiceLineDistService;

    private final InvoiceLineExpenceService invoiceLineExpenceService;
    
    private final InvoiceTypeService invoiceTypeService;

    private final OrganizationService organizationService;

    private final MapperFacade mapper;

    /**
     * 新建 发票头、行
     * @param invoiceDTO
     * @return
     */
    @Transactional
    public InvoiceDTO insertInvoice(InvoiceDTO invoiceDTO){
        //插入发票头
        InvoiceHead invoiceHead = invoiceDTO.getInvoiceHead();
        if (invoiceHead != null) {
            //校验发票头
            if (invoiceHead.getId() != null){
                throw new BizException(RespCode.INVOICE_HEAD_ID_IS_NOT_NULL);
            }
            List<CurrencyRateCO> currencyRateCOs = organizationService.listCurrencysByCode("CNY", true, invoiceHead.getSetOfBooksId());
            if (currencyRateCOs.size() > 0){
                currencyRateCOs.stream().forEach(currencyRateCO -> {
                    if (currencyRateCO.getCurrencyCode().equals(invoiceHead.getCurrencyCode())){
                        invoiceHead.setExchangeRate(new BigDecimal(currencyRateCO.getRate()));
                    }
                });

            }
            checkInvoiceHead(invoiceHead);
            invoiceHeadMapper.insert(invoiceHead);
        }

        //插入发票行
        if (invoiceDTO.getInvoiceLineList().size() > 0 ){
            List<InvoiceLine> invoiceLineList = invoiceDTO.getInvoiceLineList();
            //发票行金额之和
            BigDecimal lineDetailAmountSum = BigDecimal.ZERO;
            //发票行税额之和
            BigDecimal lineTaxAmountSum = BigDecimal.ZERO;
            //校验发票行
            for (InvoiceLine invoiceLine : invoiceLineList) {
                if (invoiceLine.getId() != null) {
                    throw new BizException(RespCode.INVOICE_LINE_ID_IS_NOT_NULL);
                }
                invoiceLineService.checkInvoiceLine(invoiceLine);

                lineDetailAmountSum = lineDetailAmountSum.add(invoiceLine.getDetailAmount());
                lineTaxAmountSum = lineTaxAmountSum.add(invoiceLine.getTaxAmount());
            }
            //发票行金额之和不可大于发票头金额合计
            if (null != invoiceHead.getInvoiceAmount()) {
                if (lineDetailAmountSum.compareTo(invoiceHead.getInvoiceAmount()) == 1) {
                    throw new BizException(RespCode.INVOICE_LINE_DETAIL_AMOUNT_SUM_NO_MORE_THAN_HEAD_INVOICE_AMOUNT_SUM);
                }
            }
            //发票行税额之和不可大于发票头税额合计
            if (null != invoiceHead.getTaxTotalAmount()) {
                if (lineTaxAmountSum.compareTo(invoiceHead.getTaxTotalAmount()) == 1) {
                    throw new BizException(RespCode.INVOICE_LINE_TAX_AMOUNT_SUM_NO_MORE_THAN_HEAD_TAX_TOTAL_AMOUNT_SUM);
                }
            }
            //执行 插入发票行、和插入发票分配行 操作
            invoiceLineList.stream().forEach(invoiceLine -> {
                // 插入发票行
                //发票行中的币种、汇率从发票头取值
                invoiceLine.setCurrencyCode(invoiceHead.getCurrencyCode());
                invoiceLine.setExchangeRate(invoiceHead.getExchangeRate());
                invoiceLine.setInvoiceHeadId(invoiceHead.getId());
                invoiceLineService.insert(invoiceLine);
                //插入发票分配行
                InvoiceLineDist invoiceLineDist = new InvoiceLineDist();
                mapper.map(invoiceLine,invoiceLineDist);
                invoiceLineDist.setInvoiceLineId(invoiceLine.getId());
                invoiceLineDist.setInvoiceNo(invoiceHead.getInvoiceNo());
                invoiceLineDist.setInvoiceCode(invoiceHead.getInvoiceCode());
                invoiceLineDistService.insert(invoiceLineDist);
            });
        }else {
            throw new BizException(RespCode.INVOICE_LINE_IS_EMPTY);
        }
        return invoiceDTO;
    }

    /**
     * 校验录入的发票代码和号码是否已经在发票头表存在
     * @param invoiceCode
     * @param invoiceNo
     * @return
     */
    public String checkInvoiceCodeInvoiceNo(String invoiceCode,String invoiceNo){
        //校验录入的发票代码和号码是否已经在发票头表存在，
        // 如果已经存在，判断是否是当前员工录入的发票，如果是则报错“此发票已经存在，请从票夹导入！”，
        // 如果是他人票夹中的发票，则报错“此发票与员工姓名-工号录入的发票重复，请更换发票！”
        List<InvoiceHead> invoiceHeadList = invoiceHeadMapper.selectList(
                new EntityWrapper<InvoiceHead>()
                        .eq(invoiceCode != null, "invoice_code", invoiceCode)
                        .eq(invoiceNo != null, "invoice_no", invoiceNo)
        );
        if (invoiceHeadList.size() > 0){
            if ( invoiceHeadList.get(0).getCreatedBy().equals(OrgInformationUtil.getCurrentUserId()) ){
                throw new BizException(RespCode.INVOICE_HEAD_EXIST_PLEASE_IMPORT_FROM_THE_TICKET_FOLDER);
            }else {
                ContactCO user = organizationService.getUserById(invoiceHeadList.get(0).getCreatedBy());

                throw new BizException(RespCode.INVOICE_HEAD_REPEAT_WITH_OTHERS_PLEASE_REPLACE_THE_INVOICE,new Object[]{user.getFullName(),user.getEmployeeCode()});
            }
        }
        return "success";
    }

    /**
     * 更新 发票头
     * @param invoiceHead
     * @return
     */
    /*@Transactional
    public InvoiceHead updateInvoiceHead(InvoiceHead invoiceHead){
        if (invoiceHead.getId() == null){
            throw new BizException(RespCode.INVOICE_HEAD_ID_IS_NULL);
        }
        checkInvoiceHead(invoiceHead);
        invoiceHeadMapper.updateById(invoiceHead);
        return invoiceHead;
    }*/

    /**
     * 根据发票id 查询发票头行信息
     * @param id
     * @return
     */
    public InvoiceDTO getInvoiceByHeadId(Long id){
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        //发票头
        InvoiceHead invoiceHead = invoiceHeadMapper.selectById(id);
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
        invoiceDTO.setInvoiceHead(invoiceHead);
        //发票行
        List<InvoiceLine> invoiceLineList = invoiceLineService.selectList(
                new EntityWrapper<InvoiceLine>().eq("invoice_head_id", id)
        );
        if (invoiceLineList.size() > 0){
            invoiceDTO.setInvoiceLineList(invoiceLineList);
        }
        return invoiceDTO;
    }

    /**
     * 根据条件分页查询 我的票夹
     * @param createdBy 用户id
     * @param invoiceTypeId 发票类型id
     * @param invoiceNo 发票号码
     * @param invoiceCode 发票代码
     * @param invoiceDateFrom 开票日期从
     * @param invoiceDateTo 开票日期至
     * @param invoiceAmountFrom 金额合计从
     * @param invoiceAmountTo 金额合计至
     * @param createdMethod 创建方式
     * @param checkResult 验真状态
     * @param reportProgress 报账进度
     * @param page
     * @return
     */
    public Page<InvoiceHead> getInvoiceHeadByCond(Long createdBy,
                                                        Long invoiceTypeId,
                                                        String invoiceNo,
                                                        String invoiceCode,
                                                        ZonedDateTime invoiceDateFrom,
                                                        ZonedDateTime invoiceDateTo,
                                                        BigDecimal invoiceAmountFrom,
                                                        BigDecimal invoiceAmountTo,
                                                        String createdMethod,
                                                        Boolean checkResult,
                                                        String reportProgress,
                                                        Page page){
        List<InvoiceHead> invoiceHeads = invoiceHeadMapper.selectPage(page,
                new EntityWrapper<InvoiceHead>()
                        .eq(createdBy != null, "created_by", createdBy)
                        .eq(invoiceTypeId != null, "invoice_type_id", invoiceTypeId)
                        .like(invoiceNo != null, "invoice_no", invoiceNo)
                        .like(invoiceCode != null, "invoice_code", invoiceCode)
                        .ge(invoiceDateFrom != null, "invoice_date", invoiceDateFrom)
                        .le(invoiceDateTo != null, "invoice_date", invoiceDateTo)
                        .ge(invoiceAmountFrom != null, "invoice_amount", invoiceAmountFrom)
                        .le(invoiceAmountTo != null, "invoice_amount", invoiceAmountTo)
                        .eq(createdMethod != null, "created_method", createdMethod)
                        .eq(checkResult != null, "check_result", checkResult)
                        .orderBy("invoice_code",true)
        );
        for (InvoiceHead invoiceHead : invoiceHeads){
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
            //设置报账进度值列表
            List<Long> lineIds = invoiceLineService.selectList(
                    new EntityWrapper<InvoiceLine>().eq("invoice_head_id",invoiceHead.getId())
            ).stream().map(InvoiceLine::getId).collect(Collectors.toList());
            if (lineIds.size() > 0){
                List<Long> distIds = invoiceLineDistService.selectList(
                        new EntityWrapper<InvoiceLineDist>().in("invoice_line_id",lineIds)
                ).stream().map(InvoiceLineDist::getId).collect(Collectors.toList());
                List<InvoiceLineExpence> expenceList = invoiceLineExpenceService.selectList(
                        new EntityWrapper<InvoiceLineExpence>().in("invoice_dist_id",distIds)
                ).stream().collect(Collectors.toList());
                if (expenceList.size() > 0){
                    if ( ( expenceList.stream().anyMatch(invoiceLineExpence -> invoiceLineExpence.getStatus().equals("N")) ||
                            expenceList.stream().anyMatch(invoiceLineExpence -> invoiceLineExpence.getStatus().equals("P")) ) &&
                            ( !expenceList.stream().anyMatch(invoiceLineExpence -> invoiceLineExpence.getStatus().equals("Y")) ||
                                    !expenceList.stream().anyMatch(invoiceLineExpence -> invoiceLineExpence.getStatus().equals("R"))) ){
                        invoiceHead.setReportProgress("NO_REPORT");
                        SysCodeValueCO sysCode = organizationService.getSysCodeValueByCodeAndValue("REPORT_PROGRESS", invoiceHead.getReportProgress());
                        if (sysCode != null){
                            invoiceHead.setReportProgressName(sysCode.getName());
                        }
                    }else if (expenceList.stream().allMatch(invoiceLineExpence -> invoiceLineExpence.getStatus().equals("Y"))){
                        invoiceHead.setReportProgress("ALL_REPORT");
                        SysCodeValueCO sysCode = organizationService.getSysCodeValueByCodeAndValue("REPORT_PROGRESS", invoiceHead.getReportProgress());
                        if (sysCode != null){
                            invoiceHead.setReportProgressName(sysCode.getName());
                        }
                    }else if ( ( expenceList.stream().anyMatch(invoiceLineExpence -> invoiceLineExpence.getStatus().equals("Y")) ) &&
                            ( expenceList.stream().anyMatch(invoiceLineExpence -> invoiceLineExpence.getStatus().equals("N")) ||
                                    expenceList.stream().anyMatch(invoiceLineExpence -> invoiceLineExpence.getStatus().equals("P")) ||
                                    expenceList.stream().anyMatch(invoiceLineExpence -> invoiceLineExpence.getStatus().equals("R")) ) ){
                        invoiceHead.setReportProgress("PART_REPORT");
                        SysCodeValueCO sysCode = organizationService.getSysCodeValueByCodeAndValue("REPORT_PROGRESS", invoiceHead.getReportProgress());
                        if (sysCode != null){
                            invoiceHead.setReportProgressName(sysCode.getName());
                        }
                    }
                }else {
                    invoiceHead.setReportProgress("NO_REPORT");
                    SysCodeValueCO sysCode = organizationService.getSysCodeValueByCodeAndValue("REPORT_PROGRESS", invoiceHead.getReportProgress());
                    if (sysCode != null){
                        invoiceHead.setReportProgressName(sysCode.getName());
                    }
                }
            }else {
                invoiceHead.setReportProgress("NO_REPORT");
                SysCodeValueCO sysCode = organizationService.getSysCodeValueByCodeAndValue("REPORT_PROGRESS", invoiceHead.getReportProgress());
                if (sysCode != null){
                    invoiceHead.setReportProgressName(sysCode.getName());
                }
            }
        }

        if(reportProgress != null){
          invoiceHeads = invoiceHeads.stream().filter(invoiceHead -> {
              return reportProgress.equals(invoiceHead.getReportProgress());
            }).collect(Collectors.toList());
        }
        page.setRecords(invoiceHeads);
        return page;
    }

    /**
     * 根据发票头id集合 批量删除 发票
     * @param headIds
     * @return
     */
    @Transactional
    public List<InvoiceHead> deleteInvoiceByIds(List<Long> headIds){
        List<InvoiceHead> list = new ArrayList<>();

        //查询出要删除的发票头对应的行
        if (headIds.size() > 0) {
            for (Long headId : headIds) {
                List<Long> lineIds = invoiceLineService.selectList(
                        new EntityWrapper<InvoiceLine>().eq("invoice_head_id",headId)
                ).stream().map(InvoiceLine::getId).collect(Collectors.toList());
                //查询出发票行id对应的分摊行id
                if (lineIds.size() > 0) {
                    for (Long lineId : lineIds) {
                        List<Long> distIds = invoiceLineDistService.selectList(
                                new EntityWrapper<InvoiceLineDist>().eq("invoice_line_id",lineId)
                        ).stream().map(InvoiceLineDist::getId).collect(Collectors.toList());
                        //查询出分摊行id对应的发票行报销记录数据
                        if (distIds.size() > 0){
                            for (Long distId : distIds){
                                if (invoiceLineExpenceService.selectList(
                                        new EntityWrapper<InvoiceLineExpence>().eq("invoice_dist_id",distId)
                                ).size() > 0){
                                    InvoiceHead head = invoiceHeadMapper.selectById(headId);
                                    list.add(head);
                                }else {
                                    invoiceLineDistService.deleteById(distId);
                                    invoiceLineService.deleteById(lineId);
                                    invoiceHeadMapper.deleteById(headId);
                                }
                            }
                        }else {
                            invoiceLineService.deleteById(lineId);
                            invoiceHeadMapper.deleteById(headId);
                        }
                    }
                }else {
                    invoiceHeadMapper.deleteById(headId);
                }
            }
        }
        return list;
    }

    /**
     * 根据发票头id 批量验真发票
     * @param headIds
     * @return
     */
    public void checkInvoice(List<Long> headIds){
        for (Long headId : headIds){
            InvoiceHead invoiceHead = invoiceHeadMapper.selectById(headId);
            if (invoiceHead == null){
                throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
            }else {
                if (invoiceHead.getCheckResult() == false){
                    invoiceHead.setCheckResult(true);
                    invoiceHeadMapper.updateById(invoiceHead);
                }
            }
        }
    }

    /**
     * 关联报账单详情查询
     * @param headId
     * @param expenseNum
     * @param expenseTypeId
     * @param page
     * @return
     */
    public Page<InvoiceLineExpence> getInvoiceLineExpenceByHeadId(Long headId,String expenseNum,Long expenseTypeId,Page page){
        Page<InvoiceLineExpence> expenceList = new Page<>();

        List<Long> lineIds = invoiceLineService.selectList(
                new EntityWrapper<InvoiceLine>()
                        .eq("invoice_head_id",headId)
        ).stream().map(InvoiceLine::getId).collect(Collectors.toList());
        if (lineIds.size() > 0){
            List<Long> distIds = invoiceLineDistService.selectList(
                    new EntityWrapper<InvoiceLineDist>().in("invoice_line_id",lineIds)
            ).stream().map(InvoiceLineDist::getId).collect(Collectors.toList());
            expenceList = invoiceLineExpenceService.selectPage(page,
                    new EntityWrapper<InvoiceLineExpence>().in("invoice_dist_id",distIds)
            );
        }
        return expenceList;
    }

    public Page<InvoiceLineDistDTO> getInvoiceLineDistByCond(Long createdBy, Long invoiceTypeId, String invoiceCode, String invoiceNo, String expenseNum,
                                                             ZonedDateTime invoiceDateFrom, ZonedDateTime invoiceDateTo,
                                                             BigDecimal invoiceAmountFrom, BigDecimal invoiceAmountTo,
                                                             Integer invoiceLineNumFrom, Integer invoiceLineNumTo,
                                                             String taxRate,
                                                             BigDecimal taxAmountFrom, BigDecimal taxAmountTo,
                                                             String createdMethod,
                                                             Boolean checkResult,
                                                             Page page){
//        Page<InvoiceLineDistDTO> result = new Page<>();
        Page distPage = invoiceLineDistService.selectPage(page,
                new EntityWrapper<InvoiceLineDist>()
                        .eq(createdBy != null, "created_by", createdBy)
                        .like(invoiceCode != null, "invoice_code", invoiceCode)
                        .like(invoiceNo != null, "invoice_no", invoiceNo)
                        .eq(taxRate != null, "tax_rate", taxRate)
                        .ge(taxAmountFrom != null, "tax_amount", taxAmountFrom)
                        .le(taxAmountTo != null, "tax_amount", taxAmountTo)
        );
        if (distPage.getRecords().size() > 0){
            List<InvoiceLineDist> distList = distPage.getRecords();

            List<InvoiceLineDistDTO> distDTOList = new ArrayList<>();

            for (InvoiceLineDist invoiceLineDist : distList){
                InvoiceLineDistDTO invoiceLineDistDTO = new InvoiceLineDistDTO();
                mapper.map(invoiceLineDist,invoiceLineDistDTO);
                InvoiceLine invoiceLine = invoiceLineService.selectById(invoiceLineDistDTO.getInvoiceLineId());
                //设置发票行号、发票行金额、发票头id
                invoiceLineDistDTO.setInvoiceLineNum(invoiceLine.getInvoiceLineNum());
                invoiceLineDistDTO.setDetailAmount(invoiceLine.getDetailAmount());
                invoiceLineDistDTO.setInvoiceHeadId(invoiceLine.getInvoiceHeadId());
                InvoiceHead invoiceHead = invoiceHeadMapper.selectById(invoiceLine.getInvoiceHeadId());
                //设置开票日期、金额合计
                invoiceLineDistDTO.setInvoiceDate(invoiceHead.getInvoiceDate());
                invoiceLineDistDTO.setInvoiceAmount(invoiceHead.getInvoiceAmount());
                //设置发票类型id、发票类型名称
                InvoiceType invoiceType = invoiceTypeService.selectById(invoiceHead.getInvoiceTypeId());
                invoiceLineDistDTO.setInvoiceTypeId(invoiceType.getId());
                invoiceLineDistDTO.setInvoiceTypeName(invoiceType.getInvoiceTypeName());

                distDTOList.add(invoiceLineDistDTO);
            }
            page.setRecords(distDTOList);
        }
        return page;
    }



    /**
     * 校验发票头
     * @param invoiceHead
     * @return
     */
    public void checkInvoiceHead(InvoiceHead invoiceHead){
        if (invoiceHead.getTenantId() == null){
            throw new BizException(RespCode.INVOICE_HEAD_TENANT_ID_IS_NULL);
        }
        if (invoiceHead.getSetOfBooksId() == null){
            throw new BizException(RespCode.INVOICE_HEAD_SET_OF_BOOKS_ID_IS_NULL);
        }
        if (invoiceHead.getInvoiceNo() == null || invoiceHead.getInvoiceNo() == ""){
            throw new BizException(RespCode.INVOICE_HEAD_INVOICE_NO_IS_NULL);
        }
        if (invoiceHead.getInvoiceCode() == null || invoiceHead.getInvoiceCode() == ""){
            throw new BizException(RespCode.INVOICE_HEAD_INVOICE_CODE_IS_NULL);
        }
        //当发票模块中没有"价税合计"字段或者有了非必输时，不需要这个校验
        /*if (invoiceHead.getTotalAmount() == null){
            throw new BizException(RespCode.INVOICE_HEAD_TOTAL_AMOUNT_IS_NULL);
        }*/
        if (invoiceHead.getCurrencyCode() == null || invoiceHead.getCurrencyCode() == ""){
            throw new BizException(RespCode.INVOICE_HEAD_CURRENCY_CODE_IS_NULL);
        }
        if (invoiceHead.getExchangeRate() == null){
            throw new BizException(RespCode.INVOICE_HEAD_EXCHANGE_RATE_IS_NULL);
        }
        /*if (invoiceHead.getCancelFlag() == null){
            throw new BizException(RespCode.INVOICE_HEAD_CANCEL_FLAG_IS_NULL);
        }
        if (invoiceHead.getRedInvoiceFlag() == null){
            throw new BizException(RespCode.INVOICE_HEAD_RED_INVOICE_FLAG_IS_NULL);
        }*/
        if (invoiceHead.getCreatedMethod() == null || invoiceHead.getCreatedMethod() == ""){
            throw new BizException(RespCode.INVOICE_HEAD_CREATED_METHOD_IS_NULL);
        }
    }
    /**
     *  根据条件查询所有发票头行
     * @param createdBy 创建人
     * @param invoiceCode 发票代码
     * @param invoiceNo 发票号码
     * @param invoiceDateFrom 开票日期从
     * @param invoiceDateTo 开票日期至
     * @param salerName 销方名称
     * @param currencyCode 币种
     * @return
     */
    public List<InvoiceHead> pageInvoiceByCond(Long createdBy,
                            String invoiceCode,
                            String invoiceNo,
                            String invoiceDateFrom,
                            String invoiceDateTo,
                            String currencyCode,
                            String salerName,
                            Page queryPage) {
        List<InvoiceHead> heads = baseMapper.selectPage(queryPage,
                new EntityWrapper<InvoiceHead>()
                        .eq(createdBy != null, "created_by", createdBy)
                        .eq(StringUtils.isNotEmpty(currencyCode),"currency_code",currencyCode)
                        .like(StringUtils.isNotEmpty(salerName),"saler_name",salerName)
                        .like(StringUtils.isNotEmpty(invoiceCode), "invoice_code", invoiceCode)
                        .like(StringUtils.isNotEmpty(invoiceNo), "invoice_no", invoiceNo)
                        .ge(StringUtils.isNotEmpty(invoiceDateFrom), "invoice_date", TypeConversionUtils.getStartTimeForDayYYMMDD(invoiceDateFrom))
                        .le(StringUtils.isNotEmpty(invoiceDateTo), "invoice_date", TypeConversionUtils.getEndTimeForDayYYMMDD(invoiceDateTo)));
        heads.stream().forEach(head->{
            //设置发票类型名称
            InvoiceType invoiceType = invoiceTypeService.selectById(head.getInvoiceTypeId());
            if (invoiceType == null){
                throw new BizException(RespCode.INVOICE_TYPE_NOT_EXIST);
            }
            head.setDeductionFlag(invoiceType.getDeductionFlag());
            head.setInvoiceTypeName(invoiceType.getInvoiceTypeName());
            //设置录入方式值列表
            SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("CREATED_METHOD", head.getCreatedMethod());
            if (sysCodeValueCO != null){
                head.setCreatedMethodName(sysCodeValueCO.getName());
            }
            //发票行
            List<InvoiceLine> invoiceLineList = invoiceLineService.selectList(
                    new EntityWrapper<InvoiceLine>().eq("invoice_head_id", head.getId())
            );
           head.setInvoiceLineList(invoiceLineList);
        });
        return heads;

    }

    /**
     * 根据发票分摊行信息删除整张发票
     * @param invoiceDistIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteInvoiceByInvoiceDistId(List<Long> invoiceDistIds){
        List<InvoiceLineDist> invoiceLineDists = invoiceLineDistService.selectBatchIds(invoiceDistIds);
        List<Long> invoiceLineIds = invoiceLineDists.stream().map(InvoiceLineDist::getInvoiceLineId).collect(Collectors.toList());
        List<InvoiceLine> invoiceLines = invoiceLineService.selectBatchIds(invoiceLineIds);
        List<Long> InvoiceHeaderIds = invoiceLines.stream().map(InvoiceLine::getInvoiceHeadId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(InvoiceHeaderIds)){
            deleteBatchIds(InvoiceHeaderIds);
        }
        if(CollectionUtils.isNotEmpty(invoiceLineIds)){
            invoiceLineService.deleteBatchIds(invoiceLineIds);
        }
        invoiceLineDistService.deleteBatchIds(invoiceDistIds);
    }
}
