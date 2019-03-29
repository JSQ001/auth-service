package com.hand.hcf.app.mdata.currency.web;

import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.service.CurrencyI18nService;
import com.hand.hcf.app.mdata.system.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/13 09:32
 */
@RestController
@RequestMapping("/api/Currency/Language")
public class CurrencyLanguageResource {

    @Autowired
    CurrencyI18nService currencyI18nService;

    @RequestMapping(value = "/getCurrencyByLanguage", method = RequestMethod.GET)
    public ResponseEntity<List<CurrencyI18n>> getCurrencyByLanguage() {
        String language= OrgInformationUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(language)) {
            language = Constants.DEFAULT_LANGUAGE;
        }
        List<CurrencyI18n> currencyLanguages = currencyI18nService.listByLanguage(language);
        return ResponseEntity.ok(currencyLanguages);
    }
}
