package com.hand.hcf.app.expense.report.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/22
 */
public interface ExpenseReportTypeMapper extends BaseMapper<ExpenseReportType>{

    /**
     * 获取满足权限规则的单据类型 (人员组不能通过sql筛选，需要单独过滤)
     * @param departmentId
     * @param companyId
     * @param setOfBooksId
     * @return
     */
    List<ExpenseReportType> getCurrentUserExpenseReportType(@Param("departmentId") Long departmentId,
                                                            @Param("companyId") Long companyId,
                                                            @Param("setOfBooksId") Long setOfBooksId);

    List<ExpenseReportType> getExpenseReportTypeByFormTypes(@Param("ew") Wrapper<ExpenseReportType> orderBy);
}
