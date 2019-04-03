package com.hand.hcf.app.expense.report.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.report.domain.ExpenseReportPaymentSchedule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 09:59
 * @remark
 */
public interface ExpenseReportPaymentScheduleMapper extends BaseMapper<ExpenseReportPaymentSchedule>{

    /**
     * 获取本币金额最大的计划付款行
     * @param expReportHeaderId
     * @return
     */
    List<ExpenseReportPaymentSchedule> getExpenseReportPaymentScheduleAmountMax(@Param(value = "expReportHeaderId") Long expReportHeaderId);
}
