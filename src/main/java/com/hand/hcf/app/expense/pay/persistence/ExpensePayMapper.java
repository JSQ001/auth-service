package com.hand.hcf.app.expense.pay.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.income.domain.ExpenseIncomeAssign;
import com.hand.hcf.app.expense.income.domain.ExpenseTypeAssignDepartment;
import com.hand.hcf.app.expense.pay.domain.ExpensePayAssign;
import com.hand.hcf.app.expense.pay.domain.ExpensePayAssignDepartment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/6
 */
public interface ExpensePayMapper extends BaseMapper<ExpensePayAssign> {

    /**
     *  获取部门信息
     * @param reportTypeId 单号
     * @return
     */
    List<ExpensePayAssignDepartment> queryPayDepartmentInfo(@Param("reportTypeId") Long reportTypeId);

    List<ExpensePayAssignDepartment> queryPayDepartmentFilter(
            @Param("reportTypeId") Long reportTypeId,
            @Param("departmentCode") String departmentCode,
            @Param("name") String name,
            @Param("departmentFrom") String departmentFrom,
            @Param("departmentTo") String departmentTo
    );

    int distributionDepartment(ExpensePayAssignDepartment dto);

    int changeDepartmentStatus(ExpensePayAssignDepartment dto);

}
