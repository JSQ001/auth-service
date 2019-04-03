package com.hand.hcf.app.expense.invoice.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/20
 */
public interface InvoiceLineMapper extends BaseMapper<InvoiceLine>{

    /**
     * 根据费用申请单行，查询关联的发票头ID
     * @param expExpenseLineId
     * @return
     */
    List<InvoiceLine> selectInvoiceByExpenseLineId(@Param(value = "expExpenseLineId") Long expExpenseLineId,
                                                   @Param(value = "deductionFlag") String deductionFlag);

    List<InvoiceLine> listNotAssignInvoiceLinesByInvoiceHeadId(@Param(value = "invoiceHeadId") Long invoiceHeadId);
}
