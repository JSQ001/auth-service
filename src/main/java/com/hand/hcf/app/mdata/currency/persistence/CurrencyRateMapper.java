package com.hand.hcf.app.mdata.currency.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.currency.domain.CurrencyRate;
import com.hand.hcf.app.mdata.currency.dto.CurrencyChangeLogDTO;
import com.hand.hcf.app.mdata.currency.dto.CurrencyRateDTO;
import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author mawei
 * @since 2018-03-19
 */
public interface CurrencyRateMapper extends BaseMapper<CurrencyRate> {
    /**
     * 汇率详情查询
     *
     * @param currencyOid
     * @return
     */
    CurrencyRateDTO selectByCurrencyOid(@Param(value = "currencyOid") UUID currencyOid);

    /**
     * 查询汇率历史信息
     *
     * @param currencyCode
     * @param setOfBooksId
     * @param tenantId
     * @param currencyRateOid
     * @param page
     * @return
     */
    List<CurrencyRateDTO> selectHistoryByCurrencyCodeAndSetOfBooksIdAndTenantId(@Param("currencyCode") String currencyCode,
                                                                                @Param("setOfBooksId") Long setOfBooksId,
                                                                                @Param("tenantId") Long tenantId,
                                                                                @Param("currencyRateOid") UUID currencyRateOid,
                                                                                @Param("startDate") ZonedDateTime startDate,
                                                                                @Param("endDate") ZonedDateTime endDate,
                                                                                @Param("baseCurrencyCode") String baseCurrencyCode,
                                                                                Page<CurrencyRateDTO> page);

    /**
     * 查询账套下的所有生效汇率
     *
     * @param baseCurrencyCode 本位币
     * @param setOfBooksId     账套ID
     * @param tenantId         租户ID
     * @param enable           是否启用(enable为null时查询所有)
     * @param page             分页
     * @return
     */
    List<CurrencyRateDTO> selectActiveBySetOfBooksIdAndTenantIdAndBaseCurrencyCode(@Param("baseCurrencyCode") String baseCurrencyCode,
                                                                                   @Param("setOfBooksId") Long setOfBooksId,
                                                                                   @Param("tenantId") Long tenantId,
                                                                                   @Param("enable") Boolean enable,
                                                                                   Page page);

    /**
     * 查询 X租户 X账套 下X持有币中的所有可用的兑换币种汇率 ---不带分页
     *
     * @param baseCurrencyCode
     * @param setOfBooksId
     * @param tenantId
     * @param enable           是否启用(enable为null时查询所有)
     * @return
     */
    List<CurrencyRateDTO> selectActiveBySetOfBooksIdAndTenantIdAndBaseCurrencyCode(@Param("baseCurrencyCode") String baseCurrencyCode,
                                                                                   @Param("setOfBooksId") Long setOfBooksId,
                                                                                   @Param("tenantId") Long tenantId,
                                                                                   @Param("enable") Boolean enable);

    /**
     * 根据生效日期查询X账套X币种对X本位币的汇率
     * <!--根据生效日期、last_updated_date排倒叙-->
     *
     * @param setOfBooksId
     * @param tenantId
     * @param baseCurrencyCode
     * @param currencyCode
     * @param enable           (enable为null查询所有)
     * @return
     */
    List<CurrencyRate> selectCurrencyRates(@Param("setOfBooksId") Long setOfBooksId,
                                           @Param("tenantId") Long tenantId,
                                           @Param("baseCurrencyCode") String baseCurrencyCode,
                                           @Param("currencyCode") String currencyCode,
                                           @Param("enable") Boolean enable
    );

    /**
     * 查询老数据历史汇率信息----老数据迁移专用方法
     *
     * @param tenantId
     * @return
     */
    List<CurrencyChangeLogDTO> selectCompanyStandardCurrencyChangeLogByTenantId(@Param("tenantId") Long tenantId);


}
