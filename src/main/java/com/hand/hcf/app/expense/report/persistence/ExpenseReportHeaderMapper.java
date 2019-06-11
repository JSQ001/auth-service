package com.hand.hcf.app.expense.report.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.expense.report.domain.ExpenseReportHeader;
import com.hand.hcf.app.expense.report.dto.ExpenseReportHeaderDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 09:59
 * @remark
 */
public interface ExpenseReportHeaderMapper extends BaseMapper<ExpenseReportHeader>{
    List<ExpenseReportHeaderDTO> queryReportHeaderByids(@Param("ew") Wrapper hearderWrapper, RowBounds page);

    int getCountByCondition(@Param("ew") Wrapper wrapper);

    List<ExpenseReportHeader> getSignExpenseReports(@Param("ew") Wrapper hearderWrapper, RowBounds page);
}
