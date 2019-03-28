package com.hand.hcf.app.expense.type.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/22
 */
public interface ExpenseDimensionMapper extends BaseMapper<ExpenseDimension> {

    /**
     * 根据单据头ID查询维度信息
     * @param headerId
     * @param documentType
     * @param headerFlag
     * @return
     */
    List<ExpenseDimension> listDimensionByHeaderIdAndType(@Param("headerId") Long headerId,
                                                          @Param("documentType") Integer documentType,
                                                          @Param("headerFlag") Boolean headerFlag);
}
