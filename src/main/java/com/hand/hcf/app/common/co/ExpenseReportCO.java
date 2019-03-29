package com.hand.hcf.app.common.co;

import com.hand.hcf.app.apply.accounting.annotation.InterfaceDataStructure;
import com.hand.hcf.app.apply.accounting.annotation.InterfaceTransactionType;
import com.hand.hcf.app.apply.accounting.dto.AccountingBaseCO;
import com.hand.hcf.app.apply.accounting.enums.SourceTransactionType;
import com.hand.hcf.app.apply.accounting.enums.SourceTransactionTypeDataStructure;
import com.hand.hcf.app.apply.accounting.message.ModuleMessageCode;
import com.hand.hcf.app.common.enums.SourceTransactionType;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * Created by kai.zhang on 2017-12-25.
 * 对公报账单
 */
@Data
@InterfaceTransactionType(SourceTransactionType.EXP_REPORT)
public class ExpenseReportCO extends AccountingBaseCO implements Serializable {
    //报销单头信息
    @Valid
    @InterfaceDataStructure(sequence = 1, type = SourceTransactionTypeDataStructure.HEADER, msgCode = ModuleMessageCode.DATA_SOURCE_EXP_REPORT_HEADER)
    private ExpenseReportHeaderCO expenseReportHeader;
    //报销单分摊行信息
    @Valid
    @InterfaceDataStructure(sequence = 2, type = SourceTransactionTypeDataStructure.DIST
            , msgCode = ModuleMessageCode.DATA_SOURCE_EXP_REPORT_DIST
            , relateDataSource = {SourceTransactionTypeDataStructure.HEADER}
            , relateField = {"headerId"})
    private List<ExpenseReportDistCO> expenseReportDists;
    //报销单发票行信息
    @Valid
    @InterfaceDataStructure(sequence = 3, type = SourceTransactionTypeDataStructure.INVOICE
            , msgCode = ModuleMessageCode.DATA_SOURCE_EXP_REPORT_INVOICE
            , relateDataSource = {SourceTransactionTypeDataStructure.HEADER}
            , relateField = {"headerId"})
    private List<ExpenseReportInvoiceCO> expenseReportInvoices;
    //报销单计划付款行信息
    @Valid
    @InterfaceDataStructure(sequence = 4, type = SourceTransactionTypeDataStructure.SCHEDULE
            , msgCode = ModuleMessageCode.DATA_SOURCE_EXP_REPORT_SCHEDULE
            , relateDataSource = {SourceTransactionTypeDataStructure.HEADER}
            , relateField = {"headerId"})
    private List<ExpenseReportScheduleCO> expenseReportSchedules;
}
