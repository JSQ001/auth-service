package com.hand.hcf.app.expense.type.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.type.domain.ExpenseField;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */
public interface ExpenseFieldMapper extends BaseMapper<ExpenseField> {


    /**
     * 根据类型ID查询控件
     * @param typeId
     * @param language
     * @return
     */
    List<ExpenseField> listFieldByTypeId(@Param("typeId") Long typeId,
                                         @Param("language") String language);
}
