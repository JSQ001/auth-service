package com.hand.hcf.app.mdata.currency.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.dto.CurrencyRateDTO;
import com.hand.hcf.app.mdata.currency.persistence.CurrencyI18nMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.Collator;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fanfuqiang 2018/11/20
 */
@Service
public class CurrencyI18nService extends BaseService<CurrencyI18nMapper, CurrencyI18n> {


    public List<CurrencyI18n> queryByCurrencyCode(String currencyCode, String language) {
        if (StringUtils.isEmpty(currencyCode)) {
            if (null == language) {
                return this.selectList(new EntityWrapper<CurrencyI18n>().isNotNull("id").orderBy("currency_code"));
            } else {
                return this.selectList(new EntityWrapper<CurrencyI18n>().isNotNull("id").eq("language", language).orderBy("currency_code"));
            }
        }return this.selectList(new EntityWrapper<CurrencyI18n>().eq("currency_code", currencyCode).orderBy("currency_code"));
    }

    public List<CurrencyI18n> listByLanguage(String language) {

        return this.selectList(new EntityWrapper<CurrencyI18n>().eq("language", language));
    }

    public CurrencyI18n queryByCurrencyCodeAndLanguage(String currencyCode, String language) {
        language = language == null ? LanguageEnum.ZH_CN.getKey() : language;
        return this.selectOne(new EntityWrapper<CurrencyI18n>().eq("currency_code", currencyCode).eq("language", language));
    }

    public CurrencyI18n insertCurrencyI18n(CurrencyI18n currencyI18n) {
        currencyI18n.setCreatedBy(OrgInformationUtil.getCurrentUserId());
        currencyI18n.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        currencyI18n.setCreatedDate(ZonedDateTime.now());
        currencyI18n.setLastUpdatedDate(ZonedDateTime.now());
        this.insert(currencyI18n);
        return currencyI18n;
    }

    public CurrencyI18n updateByCurrencyCodeAndLanguage(CurrencyI18n currencyI18n) {
        if (StringUtils.isEmpty(currencyI18n.getCurrencyCode()) || StringUtils.isEmpty(currencyI18n.getLanguage())) {
            throw new BizException(RespCode.CURRENCY_5000, new Object[]{"currencyCode", "language"});
        }
        currencyI18n.setLastUpdatedDate(ZonedDateTime.now());
        currencyI18n.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        this.update(currencyI18n, new EntityWrapper<CurrencyI18n>().eq("currency_code", currencyI18n.getCurrencyCode()).eq("language", currencyI18n.getLanguage()));
        return currencyI18n;
    }

    public Boolean deleteByID(Long id) {
        return this.deleteById(id);
    }

    public void i18nTranslateCurrencyRateDTOs(List<CurrencyRateDTO> currencyRateDTOs, String language) {
        //currencyRateDTO为null的数据不进行翻译
        if (CollectionUtils.isEmpty(currencyRateDTOs)) {
            return;
        }
        //默认取zh_CN
        language = language == null ? LanguageEnum.ZH_CN.getKey() : language;
        Set<String> baseCurrencyCodes = currencyRateDTOs.stream().map(u -> u.getBaseCurrencyCode()).collect(Collectors.toSet());
        Set<String> currencyCodes = currencyRateDTOs.stream().map(u -> u.getCurrencyCode()).collect(Collectors.toSet());

        List<CurrencyI18n> baseCurrencyI18ns = this.selectList(new EntityWrapper<CurrencyI18n>().in("currency_code", baseCurrencyCodes).eq("language", language));
        List<CurrencyI18n> currencyI18ns = this.selectList(new EntityWrapper<CurrencyI18n>().in("currency_code", currencyCodes).eq("language", language));

        Map<String, String> baseCurrencyI18nsMap = baseCurrencyI18ns.stream().collect(Collectors.toMap(u -> u.getCurrencyCode(), u -> u.getCurrencyName()));
        Map<String, String> currencyI18nsMap = currencyI18ns.stream().collect(Collectors.toMap(u -> u.getCurrencyCode(), u -> u.getCurrencyName()));

        currencyRateDTOs.stream().forEach(
                u -> {
                    u.setBaseCurrencyName(baseCurrencyI18nsMap.get(u.getBaseCurrencyCode()));
                    u.setCurrencyName(currencyI18nsMap.get(u.getCurrencyCode()));
                }
        );
    }

    public Map<String, String> i18nTranstateByCurrencyCodes(List<String> currencyCodes, String language) {
        language = StringUtils.isEmpty(language) ? LanguageEnum.ZH_CN.getKey() : language;

        List<CurrencyI18n> currencyI18ns = this.selectList(new EntityWrapper<CurrencyI18n>().in("currency_code", currencyCodes).eq("language", language));
        Map<String, String> targetMap = new HashMap<>();
        if (CollectionUtils.isEmpty(currencyI18ns)) {
            return targetMap;
        }
        targetMap = currencyI18ns.stream().collect(Collectors.toMap(u -> u.getCurrencyCode(), u -> u.getCurrencyName()));
        return targetMap;
    }

    public List<CurrencyI18n> selectSefOfBooksNotCreatedCurrency(Long tenantId, Long setOfBooksId, String baseCurrencyCode, String language) {
        List<CurrencyI18n> currencyI18nList = baseMapper.selectSefOfBooksNotCreatedCurrency(tenantId, setOfBooksId, baseCurrencyCode, language);
        //按汉字拼音排序
        Collections.sort(currencyI18nList,(CurrencyI18n o1, CurrencyI18n o2)-> Collator.getInstance(Locale.CHINESE).compare(o1.getCurrencyName(),o2.getCurrencyName()));
        return currencyI18nList;
    }


    public List<CurrencyI18n> selectAll() {
        return this.selectAll();
    }


    public Page<CurrencyI18n> getOneOtherCurrencyByBaseCurrencyAndName(Long tenantId, String baseCurrency, String otherCurrency, String otherCurrencyName, String language, Page page) {

        List<CurrencyI18n> currencyI18ns = baseMapper.getOneOtherCurrencyByBaseCurrencyAndName(tenantId, baseCurrency, otherCurrency, otherCurrencyName, language == null ? OrgInformationUtil.getCurrentLanguage() : language, page);
        if (!CollectionUtils.isEmpty(currencyI18ns)) {
            page.setRecords(currencyI18ns);
        }
        return page;
    }
}
