package com.hand.hcf.app.expense.travel.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shouting.cheng
 * @date 2019/3/4
 */
public interface TravelApplicationTypeMapper extends BaseMapper<TravelApplicationType> {

    /**
     *  查询已创建的单据类型
     * @param setOfBooksId
     * @param enabled
     * @return
     */
    List<TravelApplicationType> queryCreatedType(@Param("setOfBooksId") Long setOfBooksId,
                                                 @Param("enabled") Boolean enabled);
}
