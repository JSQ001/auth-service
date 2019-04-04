package com.hand.hcf.app.expense.travel.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationTypeAssignType;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author shouting.cheng
 * @date 2019/3/4
 */
public interface TravelApplicationTypeAssignTypeMapper extends BaseMapper<TravelApplicationTypeAssignType> {
    /**
     * 查询分配的申请类型
     * @param applicationTypeId
     * @param categoryId
     * @param expenseTypeName
     * @param language
     * @param rowBounds
     * @return
     */
    List<ExpenseTypeWebDTO> queryExpenseTypeByApplicationTypeId(@Param("applicationTypeId") Long applicationTypeId,
                                                                @Param("categoryId") Long categoryId,
                                                                @Param("expenseTypeName") String expenseTypeName,
                                                                @Param("language") String language,
                                                                RowBounds rowBounds);



    /**
     * 查询当前账套下的申请类型
     * @param setOfBooksId
     * @param categoryId
     * @param expenseTypeName
     * @param language
     * @param rowBounds
     * @return
     */
    List<ExpenseTypeWebDTO> queryAllExpenseBySetOfBooksId(@Param("setOfBooksId") Long setOfBooksId,
                                                          @Param("id") Long id,
                                                          @Param("categoryId") Long categoryId,
                                                          @Param("expenseTypeName") String expenseTypeName,
                                                          @Param("typeFlag")Integer typeFlag,
                                                          @Param("language") String language,
                                                          RowBounds rowBounds);

}
