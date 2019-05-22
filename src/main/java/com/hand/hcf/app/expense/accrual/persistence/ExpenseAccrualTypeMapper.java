package com.hand.hcf.app.expense.accrual.persistence;

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
public interface ExpenseAccrualTypeMapper extends BaseMapper<ExpenseAccrualType> {

    List<ExpenseType> listAssignExpenseType(@Param("allFlag") Boolean allFlag,
                                            @Param("setOfBooksId") Long setOfBooksId,
                                            @Param("typeId") Long typeId,
                                            @Param("code") String code,
                                            @Param("name") String name,
                                            RowBounds rowBounds);
}
