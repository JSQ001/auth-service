package com.hand.hcf.app.expense.type.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *     费用大类Mapper
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/5
 */
public interface ExpenseTypeCategoryMapper extends BaseMapper<ExpenseTypeCategory> {

    List<ExpenseTypeCategory> listCategoryAndType(@Param("setOfBooksId") Long setOfBooksId,
                                                  @Param("typeFlag") Integer typeFlag);
}
