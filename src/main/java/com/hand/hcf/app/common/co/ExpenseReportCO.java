package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.annotation.InterfaceDataStructure;
import com.hand.hcf.app.common.annotation.InterfaceTransactionType;
import com.hand.hcf.app.common.enums.SourceTransactionType;
import com.hand.hcf.app.common.enums.SourceTransactionTypeDataStructure;
import com.hand.hcf.app.common.message.ModuleMessageCode;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/3/26 15:08
 */
@Data
@InterfaceTransactionType(SourceTransactionType.EXP_REPORT)
public class ExpenseReportCO extends AccountingBaseCO implements Serializable {
    //报销单头信息
    @Valid
    @InterfaceDataStructure(sequence = 1,msgCode = ModuleMessageCode.DATA_SOURCE_EXP_REPORT_HEADER,
            type = SourceTransactionTypeDataStructure.HEADER)
    private ExpenseReportHeaderCO expenseReportHeader;
    //报销单行信息
    @Valid
    @InterfaceDataStructure(sequence = 2, type = SourceTransactionTypeDataStructure.LINE
            , msgCode = ModuleMessageCode.DATA_SOURCE_EXP_REPORT_LINE
            , relateDataSource = {SourceTransactionTypeDataStructure.HEADER}
            , relateField = {"headerId"})
    private List<ExpenseReportLineCO> expenseReportLines;
    //发票头信息
    @Valid
    @InterfaceDataStructure(sequence = 3, type = SourceTransactionTypeDataStructure.INVOICE_HEADER
            , msgCode = ModuleMessageCode.DATA_SOURCE_EXP_REPORT_INVOICE_HEADER)
    private List<ExpenseReportInvoiceHeaderCO> expenseReportInvoiceHeaders;
    //发票行信息
    @Valid
    @InterfaceDataStructure(sequence = 4, type = SourceTransactionTypeDataStructure.INVOICE_LINE
            , msgCode = ModuleMessageCode.DATA_SOURCE_EXP_REPORT_INVOICE_LINE
            , relateDataSource = {SourceTransactionTypeDataStructure.INVOICE_HEADER}
            , relateField = {"invoiceHeaderId"})
    private List<ExpenseReportInvoiceLineCO> expenseReportInvoiceLines;
    //发票分配行信息
    @Valid
    @InterfaceDataStructure(sequence = 5, type = SourceTransactionTypeDataStructure.INVOICE_LINE_DIST
            , msgCode = ModuleMessageCode.DATA_SOURCE_EXP_REPORT_INVOICE_LINE_DIST
            , relateDataSource = {SourceTransactionTypeDataStructure.INVOICE_LINE,SourceTransactionTypeDataStructure.INVOICE_HEADER}
            , relateField = {"invoiceLineId","invoiceHeaderId"})
    private List<ExpenseReportInvoiceLineDistCO> expenseReportInvoiceLineDists;
    //报销单分摊行信息
    @Valid
    @InterfaceDataStructure(sequence = 6, type = SourceTransactionTypeDataStructure.DIST
            , msgCode = ModuleMessageCode.DATA_SOURCE_EXP_REPORT_DIST
            , relateDataSource = {SourceTransactionTypeDataStructure.LINE,SourceTransactionTypeDataStructure.HEADER}
            , relateField = {"lineId","headerId"})
    private List<ExpenseReportDistCO> expenseReportDists;
    //报销单分摊税行信息
    @Valid
    @InterfaceDataStructure(sequence = 7, type = SourceTransactionTypeDataStructure.TAX_DIST
            , msgCode = ModuleMessageCode.DATA_SOURCE_EXP_REPORT_TAX_DIST
            , relateDataSource = {SourceTransactionTypeDataStructure.DIST,
                                    SourceTransactionTypeDataStructure.INVOICE_LINE_DIST,
                                    SourceTransactionTypeDataStructure.INVOICE_LINE,
                                    SourceTransactionTypeDataStructure.INVOICE_HEADER,
            SourceTransactionTypeDataStructure.LINE,
            SourceTransactionTypeDataStructure.HEADER}
            , relateField = {"distId","invoiceDistId","invoiceLineId","invoiceHeaderId","lineId","headerId"})
    private List<ExpenseReportTaxDistCO> expenseReportTaxDistS;
    //报销单计划付款行信息
    @Valid
    @InterfaceDataStructure(sequence = 8, type = SourceTransactionTypeDataStructure.SCHEDULE
            , msgCode = ModuleMessageCode.DATA_SOURCE_EXP_REPORT_SCHEDULE
            , relateDataSource = {SourceTransactionTypeDataStructure.HEADER}
            , relateField = {"headerId"})
    private List<ExpenseReportScheduleCO> expenseReportSchedules;

}
