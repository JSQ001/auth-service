package com.hand.hcf.app.expense.invoice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLine;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineDist;
import com.hand.hcf.app.expense.invoice.persistence.InvoiceHeadMapper;
import com.hand.hcf.app.expense.invoice.persistence.InvoiceLineMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/20
 */
@Service
public class InvoiceLineService extends BaseService<InvoiceLineMapper,InvoiceLine>{
    @Autowired
    private InvoiceLineMapper invoiceLineMapper;
    @Autowired
    private InvoiceHeadMapper invoiceHeadMapper;
    @Autowired
    private InvoiceLineDistService invoiceLineDistService;

    /**
     * 更新 发票行
     * @param invoiceLine
     * @return
     */
    @Transactional
    public InvoiceLine updateInvoiceLine(InvoiceLine invoiceLine){
        if (invoiceLine.getId() == null){
            throw new BizException(RespCode.INVOICE_LINE_ID_IS_NULL);
        }
        checkInvoiceLine(invoiceLine);
        InvoiceHead invoiceHead = invoiceHeadMapper.selectById(invoiceLine.getInvoiceHeadId());

        List<InvoiceLine> invoiceLineList = invoiceLineMapper.selectList(
                new EntityWrapper<InvoiceLine>()
                        .eq("invoice_head_id", invoiceHead.getId())
        );
        //发票行金额之和
        BigDecimal lineDetailAmountSum = BigDecimal.ZERO;
        //发票行税额之和
        BigDecimal lineTaxAmountSum = BigDecimal.ZERO;
        if (invoiceLineList.size() > 0){
            for (InvoiceLine line : invoiceLineList){
                if (!line.getId().equals(invoiceLine.getId())){
                    lineDetailAmountSum = lineDetailAmountSum.add(invoiceLine.getDetailAmount());
                    lineTaxAmountSum = lineTaxAmountSum.add(invoiceLine.getTaxAmount());
                }
            }
        }
        lineDetailAmountSum = lineDetailAmountSum.add(invoiceLine.getDetailAmount());
        lineTaxAmountSum = lineTaxAmountSum.add(invoiceLine.getTaxAmount());
        //发票行金额之和不可大于发票头金额
        if (lineDetailAmountSum.compareTo(invoiceHead.getInvoiceAmount()) == 1){
            throw new BizException(RespCode.INVOICE_LINE_DETAIL_AMOUNT_SUM_NO_MORE_THAN_HEAD_INVOICE_AMOUNT_SUM);
        }
        //发票行税额之和不可大于发票头税额
        if (lineTaxAmountSum.compareTo(invoiceHead.getTaxTotalAmount()) == 1){
            throw new BizException(RespCode.INVOICE_LINE_TAX_AMOUNT_SUM_NO_MORE_THAN_HEAD_TAX_TOTAL_AMOUNT_SUM);
        }

        invoiceLineMapper.updateById(invoiceLine);
        return invoiceLine;
    }

    /**
     * 根据id 删除 发票行
     * @param id
     */
    @Transactional
    public void deleteInvoiceLine(Long id){
        InvoiceLine invoiceLine = invoiceLineMapper.selectById(id);
        if (invoiceLine == null){
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        invoiceLineMapper.deleteById(id);
    }

    /**
     * 校验发票行
     * @param invoiceLine
     */
    public void checkInvoiceLine(InvoiceLine invoiceLine){
        if (invoiceLine.getTenantId() == null){
            throw new BizException(RespCode.INVOICE_LINE_TENANT_ID_IS_NULL);
        }
        if (invoiceLine.getSetOfBooksId() == null){
            throw new BizException(RespCode.INVOICE_LINE_SET_OF_BOOKS_ID_IS_NULL);
        }
        /*if (invoiceLine.getInvoiceHeadId() == null){
            throw new BizException(RespCode.INVOICE_LINE_INVOICE_HEAD_ID_IS_NULL);
        }*/
        if (invoiceLine.getInvoiceLineNum() == null){
            throw new BizException(RespCode.INVOICE_LINE_INVOICE_LINE_NUM_IS_NULL);
        }
        if (invoiceLine.getDetailAmount() == null){
            throw new BizException(RespCode.INVOICE_LINE_DETAIL_AMOUNT_IS_NULL);
        }
        if (invoiceLine.getTaxRate() == null || invoiceLine.getTaxRate() == ""){
            throw new BizException(RespCode.INVOICE_LINE_TAX_RATE_IS_NULL);
        }
        if (invoiceLine.getTaxAmount() == null){
            throw new BizException(RespCode.INVOICE_LINE_TAX_AMOUNT_IS_NULL);
        }
        String taxRateTemp = invoiceLine.getTaxRate().replace("%","");
        BigDecimal taxRate = new BigDecimal(taxRateTemp);
        BigDecimal temp = new BigDecimal("100");
        BigDecimal rate = taxRate.divide(temp);
        if (invoiceLine.getTaxAmount().compareTo(invoiceLine.getDetailAmount().multiply(rate)) == 1){
            throw new BizException(RespCode.INVOICE_LINE_TAX_AMOUNT_NO_MORE_THAN_DETAIL_AMOUNT_MULTIPLIED_BY_TAX_RATE);
        }
    }

    /**
     * 根据费用申请单行，查询关联的发票行
     * @param expenseLineId
     * @return
     */
    public List<InvoiceLine> selectInvoiceByExpenseLineId(Long expenseLineId){
        return baseMapper.selectInvoiceByExpenseLineId(expenseLineId);
    }

    /**
     * 根据发票分配行删除
     * 当发票行被全部删除时，删除发票头信息
     * @param distId
     */
    public void deleteInvoiceLineByInvoiceDistId(Long distId){
        InvoiceLineDist invoiceLineDist = invoiceLineDistService.selectById(distId);
        // 删除发票行相关分摊行
        invoiceLineDistService.delete(new EntityWrapper<InvoiceLineDist>().eq("invoice_line_id",invoiceLineDist.getInvoiceLineId()));
        // 删除发票行
        InvoiceLine invoiceLine = selectById(invoiceLineDist.getInvoiceLineId());
        deleteById(invoiceLineDist.getInvoiceLineId());
        int invoiceHeadCount = selectCount(new EntityWrapper<InvoiceLine>().eq("invoice_head_id", invoiceLine.getInvoiceHeadId()));
        if(invoiceHeadCount == 0){
            invoiceHeadMapper.deleteById(invoiceLine.getInvoiceHeadId());
        }
    }
}
