package com.hand.hcf.app.expense.invoice.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineDist;
import com.hand.hcf.app.expense.invoice.dto.InvoiceLineDistDTO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/23
 */
public interface InvoiceLineDistMapper extends BaseMapper<InvoiceLineDist>{

    List<InvoiceLineDistDTO> getInvoiceRerpotDetail(@Param("tenantId") Long tenantId,
                                                    @Param("setOfBooksId") Long setOfBooksId,
                                                    @Param("invoiceTypeId") Long invoiceTypeId,
                                                    @Param("invoiceCode") String invoiceCode,
                                                    @Param("invoiceNo") String invoiceNo,
                                                    @Param("expenseNum") String expenseNum,
                                                    @Param("invoiceDateFrom") ZonedDateTime invoiceDateFrom,
                                                    @Param("invoiceDateTo") ZonedDateTime invoiceDateTo,
                                                    @Param("invoiceAmountFrom") BigDecimal invoiceAmountFrom,
                                                    @Param("invoiceAmountTo") BigDecimal invoiceAmountTo,
                                                    @Param("invoiceLineNumFrom") Integer invoiceLineNumFrom,
                                                    @Param("invoiceLineNumTo") Integer invoiceLineNumTo,
                                                    @Param("taxRate") String taxRate,
                                                    @Param("taxAmountFrom") BigDecimal taxAmountFrom,
                                                    @Param("taxAmountTo") BigDecimal taxAmountTo,
                                                    @Param("applyDateFrom") ZonedDateTime applyDateFrom,
                                                    @Param("applyDateTo") ZonedDateTime applyDateTo,
                                                    @Param("applicant") Long applicant,
                                                    @Param("documentStatus") String documentStatus,
                                                    @Param("costLineNumberFrom") Long costLineNumberFrom,
                                                    @Param("costLineNumberTo") Long costLineNumberTo,
                                                    @Param("costType") String costType,
                                                    @Param("costAmountFrom") BigDecimal costAmountFrom,
                                                    @Param("costAmountTo") BigDecimal costAmountTo,
                                                    @Param("installmentDeduction") Boolean installmentDeduction,
                                                    Page page);
}
