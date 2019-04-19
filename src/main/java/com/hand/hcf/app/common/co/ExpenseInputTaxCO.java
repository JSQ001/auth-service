package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.annotation.InterfaceDataStructure;
import com.hand.hcf.app.common.annotation.InterfaceTransactionType;
import com.hand.hcf.app.common.enums.SourceTransactionType;
import com.hand.hcf.app.common.enums.SourceTransactionTypeDataStructure;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * @Auther: zhu.zhao
 * @Date: 2019/04/14 09:08
 */
@Data
@InterfaceTransactionType(SourceTransactionType.EXP_INPUT_TAX)
public class ExpenseInputTaxCO extends AccountingBaseCO implements Serializable {
    /**
     * 进项税单头信息
     */
    @Valid
    @InterfaceDataStructure(sequence = 1,
            type = SourceTransactionTypeDataStructure.HEADER)
    private ExpenseInputTaxHeaderCO expenseInputTaxHeader;
    /**
     * 进项税单行信息
     */
    @Valid
    @InterfaceDataStructure(sequence = 2, type = SourceTransactionTypeDataStructure.LINE
            , relateDataSource = {SourceTransactionTypeDataStructure.HEADER}
            , relateField = {"headerId"})
    private List<ExpenseInputTaxLineCO> expenseReportLines;
    /**
     * 进项税单分摊行信息
     */
    @Valid
    @InterfaceDataStructure(sequence = 3, type = SourceTransactionTypeDataStructure.DIST
            , relateDataSource = {SourceTransactionTypeDataStructure.LINE,SourceTransactionTypeDataStructure.HEADER}
            , relateField = {"lineId","headerId"})
    private List<ExpenseInputTaxDistCO> expenseReportDists;
}
