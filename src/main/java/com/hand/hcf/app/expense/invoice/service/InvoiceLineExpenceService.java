package com.hand.hcf.app.expense.invoice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineExpence;
import com.hand.hcf.app.expense.invoice.dto.InvoiceLineExpenceWebQueryDTO;
import com.hand.hcf.app.expense.invoice.persistence.InvoiceLineExpenceMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/22
 */
@Service
@AllArgsConstructor
@Transactional
public class InvoiceLineExpenceService extends BaseService<InvoiceLineExpenceMapper,InvoiceLineExpence> {
    private final InvoiceLineExpenceMapper invoiceLineExpenceMapper;

    @Autowired
    private OrganizationService organizationService;

    /**
     * 根据账本Id、发票Id获取发票行报销记录
     * @param expenseBookId
     * @param invoiceLineId
     * @return
     */
    public InvoiceLineExpence getInvoiceLineExpenceById(Long expenseBookId, Long invoiceLineId) {
        return  invoiceLineExpenceMapper.getInvoiceLineExpenceById(expenseBookId,invoiceLineId);
    }

    /**
     * 根据费用行信息获取发票费用关联关系
     * @param reportLineId
     * @return
     */
    public List<InvoiceLineExpence> getInvoiceLineExpenseByReportLineId(Long reportLineId){
        return invoiceLineExpenceMapper.selectList(new EntityWrapper<InvoiceLineExpence>().eq("exp_expense_line_id",reportLineId));
    }

    /**
     * 关联报账单详情查询
     * @param headId 发票头
     * @param expenseNum 报账单单号
     * @param expenseTypeId 报账单类型id
     * @param page
     * @return
     */
    public List<InvoiceLineExpenceWebQueryDTO> getInvoiceLineExpenceByHeadId(Long headId, String expenseNum, Long expenseTypeId, Page page) {
        Wrapper wrapper = new EntityWrapper<>()
                .like(expenseNum != null, "r.requisition_number", expenseNum)
                .eq(expenseTypeId != null, "r.document_type_id", expenseTypeId);
        List<InvoiceLineExpenceWebQueryDTO> lineExpenceDTOS = baseMapper.getInvoiceLineExpenceByHeadId(headId, wrapper, page);
        lineExpenceDTOS.stream().forEach(e -> {
            ContactCO contactCO = organizationService.getUserById(e.getApplicantId());
            e.setApplicantName(contactCO == null ? null : contactCO.getFullName());
        });
        return lineExpenceDTOS;
    }
    /**
     * 根据发票分配行查询发票行报销记录
     * @param distId
     * @return
     */
    public InvoiceLineExpence getInvoiceLineExpenseByDistId(Long distId){
        return this.selectOne(new EntityWrapper<InvoiceLineExpence>().eq("invoice_dist_id",distId));
    }

    /**
     * 根据费用头ID获取发票费用关联关系及发票信息
     * @param reportHeaderId
     * @return
     */
    public List<InvoiceLineExpenceWebQueryDTO> getInvoiceLineExpenseByReportHeaderId(Long reportHeaderId,
                                                                                      String invoiceMateFlag){
        Wrapper wrapper = new EntityWrapper<InvoiceLineExpence>()
                .eq(invoiceMateFlag != null, "ile.invoice_mate_flag", invoiceMateFlag);
        List<InvoiceLineExpenceWebQueryDTO> nvoiceLineExpenceWebQueryDTOs =
                invoiceLineExpenceMapper.getInvoiceLineExpenceByReportHeadId(wrapper, reportHeaderId);
        nvoiceLineExpenceWebQueryDTOs.stream().forEach(invoiceLineExpenceWebQueryDTO -> {
            if(invoiceLineExpenceWebQueryDTO.getInvoiceMateFlag() == null ||
                "N".equals(invoiceLineExpenceWebQueryDTO.getInvoiceMateFlag())){
                invoiceLineExpenceWebQueryDTO.setInvoiceMateFlagDesc("否");
            }else{
                invoiceLineExpenceWebQueryDTO.setInvoiceMateFlagDesc("是");
            }
            if(invoiceLineExpenceWebQueryDTO.getInvoiceBagConfirmFlag() == null ||
                    "N".equals(invoiceLineExpenceWebQueryDTO.getInvoiceBagConfirmFlag())){
                invoiceLineExpenceWebQueryDTO.setInvoiceBagConfirmFlagDesc("未确认");
            }else{
                invoiceLineExpenceWebQueryDTO.setInvoiceBagConfirmFlagDesc("已确认");
            }
        });
        return nvoiceLineExpenceWebQueryDTOs;
    }

    /**
     * 根据费用头ID及发票袋号码确认标志获取发票费用关联关系
     * @param reportHeaderId 报账单头ID
     * @param invoiceBagConfirmFlag 发票袋号码确认标志
     * @param editor 是否判断发票袋号码不为空
     * @return
     */
    public List<InvoiceLineExpence> getInvoiceLineExpenseByReportHeaderIdAndInvoiceBagConfirmFlag(Long reportHeaderId,
                                                                                                  String invoiceBagConfirmFlag,
                                                                                                  Boolean editor){
        Wrapper wrapper = new EntityWrapper<InvoiceLineExpence>()
                .eq("exp_expense_head_id",reportHeaderId)
                .eq("invoice_bag_confirm_flag", invoiceBagConfirmFlag);
        if(editor){
            wrapper.isNotNull("invoice_bag_no");
        }
        List<InvoiceLineExpence> list = invoiceLineExpenceMapper.selectList(wrapper);
        if(list.size() > 0){
            return list;
        }else{
            return new ArrayList<InvoiceLineExpence>();
        }
    }

    /**
     * 根据费用头ID及发票行匹配标志获取发票费用关联关系
     * @param reportHeaderId 报账单头ID
     * @param invoiceMateFlag 发票关联报账单行匹配标志
     * @return
     */
    public List<InvoiceLineExpence> getInvoiceLineExpenseByReportHeaderIdAndInvoiceMateFlag(Long reportHeaderId,
                                                                                             String invoiceMateFlag){
        Wrapper wrapper = new EntityWrapper<InvoiceLineExpence>()
                .eq("exp_expense_head_id",reportHeaderId)
                .eq("invoice_mate_flag", invoiceMateFlag);
        List<InvoiceLineExpence> list = invoiceLineExpenceMapper.selectList(wrapper);
        if(list.size() > 0){
            return list;
        }else{
            return new ArrayList<InvoiceLineExpence>();
        }
    }

    /**
     * 根据发票袋号码查询报账单头ID
     * @param invoiceBagNo 发票袋号码
     * @param invoiceBagConfirmFlag 发票袋号码确认标记
     * @return
     */
    public List<InvoiceLineExpence> getInvoiceLineExpenseByInvoiceBagNoGroupByExpExpenseHeadId(String invoiceBagNo,
                                                                                                String invoiceBagConfirmFlag){
        Wrapper wrapper = new EntityWrapper<InvoiceLineExpence>()
                .eq("invoice_bag_no", invoiceBagNo)
                .eq("invoice_bag_confirm_flag", invoiceBagConfirmFlag)
                .groupBy("exp_expense_head_id");
        List<InvoiceLineExpence> list = invoiceLineExpenceMapper.selectList(wrapper);
        if(list.size() > 0){
            return list;
        }else{
            return new ArrayList<InvoiceLineExpence>();
        }
    }
}
