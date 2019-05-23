package com.hand.hcf.app.ant.accrualExpense.persistence;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/5/21
 */

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.ant.accrualExpense.domain.AccruedExpensesHeader;
import com.hand.hcf.app.expense.report.domain.ExpenseReportHeader;
import com.hand.hcf.app.expense.report.dto.ExpenseReportHeaderDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface AccruedExpensesHeaderMapper extends BaseMapper<AccruedExpensesHeader> {

    List<ExpenseReportHeaderDTO> queryReportHeaderByids(@Param("ew") Wrapper hearderWrapper, RowBounds page);

    int getCountByCondition(@Param("ew") Wrapper wrapper);

    List<ExpenseReportHeader> getSignExpenseReports(@Param("ew") Wrapper hearderWrapper, RowBounds page);

}

