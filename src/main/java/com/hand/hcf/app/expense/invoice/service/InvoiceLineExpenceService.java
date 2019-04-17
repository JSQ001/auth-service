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

}
