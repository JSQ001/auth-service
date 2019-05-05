package com.hand.hcf.app.expense.invoice.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineExpence;
import com.hand.hcf.app.expense.invoice.dto.InvoiceLineExpenceWebQueryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    List<InvoiceLineExpenceWebQueryDTO> getInvoiceLineExpenceByHeadId(@Param("headId") Long headId,
                                                                      @Param("ew") Wrapper wrapper,
                                                                      Page page);

    List<InvoiceLineExpenceWebQueryDTO> getInvoiceLineExpenceByReportHeadId(@Param("ew") Wrapper hearderWrapper,
                                                                            @Param("reportHeadId") Long reportHeadId);
}
