package com.hand.hcf.app.ant.accrualExpense.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/15
 */
@Component
public interface AccrualExpenseTypeMapper extends BaseMapper<ExpenseAccrualType> {

    List<ExpenseType> listAssignExpenseType(@Param("allFlag") Boolean allFlag,
                                            @Param("setOfBooksId") Long setOfBooksId,
                                            @Param("typeId") Long typeId,
                                            @Param("code") String code,
                                            @Param("name") String name,
                                            RowBounds rowBounds);

    /**
     * 获取满足权限规则的单据类型 (人员组不能通过sql筛选，需要单独过滤)
     * @param departmentId
     * @param companyId
     * @param setOfBooksId
     * @return
     */
    List<ExpenseAccrualType> getCurrentUserExpenseAccrualType(@Param("departmentId") Long departmentId,
                                                            @Param("companyId") Long companyId,
                                                            @Param("setOfBooksId") Long setOfBooksId);
}
