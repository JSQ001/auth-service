package com.hand.hcf.app.expense.income.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.expense.income.domain.ExpenseIncomeAssign;
import com.hand.hcf.app.expense.income.domain.ExpenseTypeAssignDepartment;
import com.hand.hcf.app.expense.type.bo.ExpenseBO;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/6
 */
public interface ExpenseIncomeMapper extends BaseMapper<ExpenseIncomeAssign> {

    /**
     *  获取部门信息
     * @param reportTypeId 单号
     * @return
     */
    List<ExpenseTypeAssignDepartment> queryIncomeDepartmentInfo(@Param("reportTypeId") Long reportTypeId);

    List<ExpenseTypeAssignDepartment> queryIncomeDepartmentFilter(
            @Param("reportTypeId") Long reportTypeId,
            @Param("departmentCode") String departmentCode,
            @Param("name") String name,
            @Param("departmentFrom") String departmentFrom,
            @Param("departmentTo") String departmentTo
    );

    int distributionDepartment(ExpenseTypeAssignDepartment dto);

    int changeDepartmentStatus(ExpenseTypeAssignDepartment dto);

}
