package com.hand.hcf.app.expense.invoice.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineExpence;
import org.apache.ibatis.annotations.Param;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/22
 */
public interface InvoiceLineExpenceMapper extends BaseMapper<InvoiceLineExpence>{
    /**
     * 根据账本Id、发票Id获取发票行报销记录
     * @param expenseBookId
     * @param invoiceLineId
     * @return
     */
    InvoiceLineExpence getInvoiceLineExpenceById(@Param("expenseBookId") Long expenseBookId,
                                                 @Param("invoiceLineId") Long invoiceLineId);
}
