package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.payment.domain.CashDefaultFlowItem;
import com.hand.hcf.app.payment.domain.CashFlowItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 韩雪 on 2017/9/7.
 */
public interface CashDefaultFlowItemMapper extends BaseMapper<CashDefaultFlowItem>{
    List<CashFlowItem> getNotSaveFlowItem(@Param("setOfBookId") Long setOfBookId,
                                          @Param("transactionClassId") Long transactionClassId,
                                          @Param("flowCode") String flowCode,
                                          @Param("description") String description,
                                          Pagination page);

    List<CashDefaultFlowItem> getCashDefaultFlowItemByCond(@Param("defaultFlag") Boolean defaultFlag,
                                                           @Param("enabled") Boolean enabled,
                                                           @Param("transactionClassId") Long transactionClassId,
                                                           Pagination page);
}
