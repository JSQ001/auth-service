package com.hand.hcf.app.expense.tax.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.tax.domain.ExpenseTaxAssign;
import com.hand.hcf.app.expense.tax.domain.ExpenseTaxAssignDepartment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/6
 */
public interface ExpenseTaxMapper extends BaseMapper<ExpenseTaxAssign> {

    /**
     *  获取部门信息
     * @param reportTypeId 单号
     * @return
     */
    List<ExpenseTaxAssignDepartment> queryTaxDepartmentInfo(@Param("reportTypeId") Long reportTypeId);

    List<ExpenseTaxAssignDepartment> queryTaxDepartmentFilter(
            @Param("reportTypeId") Long reportTypeId,
            @Param("departmentCode") String departmentCode,
            @Param("name") String name,
            @Param("departmentFrom") String departmentFrom,
            @Param("departmentTo") String departmentTo
    );

    int distributionDepartment(ExpenseTaxAssignDepartment dto);

    int changeDepartmentStatus(ExpenseTaxAssignDepartment dto);

}
