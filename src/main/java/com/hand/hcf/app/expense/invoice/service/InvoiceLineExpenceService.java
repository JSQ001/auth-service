package com.hand.hcf.app.expense.invoice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineExpence;
import com.hand.hcf.app.expense.invoice.persistence.InvoiceLineExpenceMapper;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
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
public class InvoiceLineExpenceService extends BaseService<InvoiceLineExpenceMapper,InvoiceLineExpence>{
    private final InvoiceLineExpenceMapper invoiceLineExpenceMapper;

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

}
