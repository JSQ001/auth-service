package com.hand.hcf.app.mdata.currency.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.currency.domain.CurrencyStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author mawei
 * @since 2018-03-19
 */
public interface CurrencyStatusMapper extends BaseMapper<CurrencyStatus> {
    /**
     * 查询自动更新状态为X   币种启用状态为X的账套id、租户ID
     *
     * @param enable
     * @param enableAutoUpdate
     * @return List<Long>
     */
    List<Long> selectActiveSetOfBooksIds(@Param("enable") Boolean enable,
                                         @Param("enableAutoUpdate") Boolean enableAutoUpdate);
}
