package com.hand.hcf.app.expense.report.dto;

import com.hand.hcf.app.common.co.CashTransactionClassCO;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeDistSetting;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import lombok.Data;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/7 16:20
 * @remark
 */
@Data
public class ExpenseReportTypeDTO extends ExpenseReportType {

    /**
     * 单据维度布局
     */
    private List<ExpenseDimension> expenseDimensions;

    /**
     * 部分付款用途
     */
    private List<CashTransactionClassCO> cashTransactionClasses;

    /**
     * 分摊配置
     */
    private ExpenseReportTypeDistSetting expenseReportTypeDistSetting;

}
