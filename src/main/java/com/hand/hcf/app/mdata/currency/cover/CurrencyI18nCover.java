package com.hand.hcf.app.mdata.currency.cover;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.dto.CurrencyI18nDTO;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanfuqiang 2018/11/28
 */
public class CurrencyI18nCover {

    public static List<CurrencyI18nDTO> toCurrencyI18nDTO(List<CurrencyI18n> currencyI18ns) {
        if (CollectionUtils.isEmpty(currencyI18ns)) {
            return null;
        }
        List<CurrencyI18nDTO> targets = new ArrayList<CurrencyI18nDTO>();
        currencyI18ns.stream().forEach(u -> {
            CurrencyI18nDTO dto = new CurrencyI18nDTO();
            BeanUtils.copyProperties(u, dto);
            targets.add(dto);
        });
        return targets;
    }
}
