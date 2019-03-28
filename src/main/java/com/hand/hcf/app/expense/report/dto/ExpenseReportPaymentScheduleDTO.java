package com.hand.hcf.app.expense.report.dto;

import com.hand.hcf.app.apply.contract.dto.ContractHeaderLineCO;
import com.hand.hcf.app.apply.payment.dto.CashWriteOffCO;
import com.hand.hcf.app.apply.payment.dto.PublicReportLineAmountCO;
import com.hand.hcf.app.expense.report.domain.ExpenseReportPaymentSchedule;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/20 22:07
 * @remark
 */
@Data
public class ExpenseReportPaymentScheduleDTO extends ExpenseReportPaymentSchedule {

    /**
     * 收款对象code
     */
    private String payeeCode;
    /**
     * 收款对象名称
     */
    private String payeeName;
    /**
     * 收款对象类别名称
     */
    private String payeeCategoryName;
    /**
     * 合同资金计划行可关联金额
     */
    private BigDecimal contractLineAmount;
    /**
     * 付款方式名称
     */
    private String paymentMethodName;
    /**
     * 现金事务分类名称
     */
    private String cshTransactionClassName;
    /**
     * 付款信息
     */
    private PublicReportLineAmountCO paidInfo ;
    /**
     * 核销信息
     */
    private List<CashWriteOffCO> cashWriteOffMessage;

    /**
     * 合同头行详细信息
     */
    private ContractHeaderLineCO contractHeaderLineMessage;

    /**
     * 序号
     */
    private Integer index;
}
