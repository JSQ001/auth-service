package com.hand.hcf.app.expense.adjust.persistence;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustType;
import com.hand.hcf.app.expense.adjust.dto.ExpenseAdjustTypeDTO;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@Component
public interface ExpenseAdjustTypeMapper extends BaseMapper<ExpenseAdjustType>{

    List<ExpenseAdjustTypeDTO> selectByUser(@Param("departmentId") Long departmentId,
                                            @Param("companyId") Long companyId,
                                            @Param("setOfBooksId") Long setOfBooksId);

    List<ExpenseAdjustTypeDTO> selectByUserGroup(@Param("companyId") Long companyId,
                                                 @Param("setOfBooksId") Long setOfBooksId);

    List<ExpenseType> listAssignExpenseType(@Param("allFlag") Boolean allFlag,
                                            @Param("setOfBooksId") Long setOfBooksId,
                                            @Param("typeId") Long typeId,
                                            @Param("code") String code,
                                            @Param("name") String name,
                                            RowBounds rowBounds);
}
