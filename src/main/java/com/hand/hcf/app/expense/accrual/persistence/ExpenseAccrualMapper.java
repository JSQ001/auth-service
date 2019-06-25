package com.hand.hcf.app.expense.accrual.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualAssign;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualAssignDepartment;
import com.hand.hcf.app.expense.income.domain.ExpenseIncomeAssign;
import com.hand.hcf.app.expense.income.domain.ExpenseTypeAssignDepartment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: dazhuang.xie
 * @Date: 2019/6/24
 */
public interface ExpenseAccrualMapper extends BaseMapper<ExpenseAccrualAssign> {

    /**
     *  获取部门信息
     * @param reportTypeId 单号
     * @return
     */
    List<ExpenseAccrualAssignDepartment> queryAccrualDepartmentInfo(@Param("reportTypeId") Long reportTypeId);

    List<ExpenseAccrualAssignDepartment> queryAccrualDepartmentFilter(
            @Param("reportTypeId") Long reportTypeId,
            @Param("departmentCode") String departmentCode,
            @Param("name") String name,
            @Param("departmentFrom") String departmentFrom,
            @Param("departmentTo") String departmentTo
    );

    int distributionDepartment(ExpenseAccrualAssignDepartment dto);

    int changeDepartmentStatus(ExpenseAccrualAssignDepartment dto);

}
