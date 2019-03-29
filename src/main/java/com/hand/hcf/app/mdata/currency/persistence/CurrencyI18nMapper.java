package com.hand.hcf.app.mdata.currency.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author mawei
 * @since 2018-03-16
 */
public interface CurrencyI18nMapper extends BaseMapper<CurrencyI18n> {
    /**
     * 查询账套X未新建的除本位币之外的其他币种
     *
     * @param setOfBooksId
     * @param baseCurrencyCode
     * @param language
     * @return
     * @Param tenantId
     */
    List<CurrencyI18n> selectSefOfBooksNotCreatedCurrency(@Param("tenantId") Long tenantId,
                                                          @Param("setOfBooksId") Long setOfBooksId,
                                                          @Param("baseCurrencyCode") String baseCurrencyCode,
                                                          @Param("language") String language);


    List<CurrencyI18n> getOneOtherCurrencyByBaseCurrencyAndName(
            @Param("tenantId") Long tenantId,
            @Param("baseCurrency") String baseCurrency,
            @Param("otherCurrency") String otherCurrency,
            @Param("otherCurrencyName") String otherCurrencyName,
            @Param("language") String language,
            Pagination page

    );
}
