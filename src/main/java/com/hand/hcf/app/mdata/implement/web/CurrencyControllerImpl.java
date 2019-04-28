package com.hand.hcf.app.mdata.implement.web;

import com.hand.hcf.app.common.co.CurrencyRateCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.currency.dto.CurrencyRateDTO;
import com.hand.hcf.app.mdata.currency.service.CurrencyRateService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class CurrencyControllerImpl {

    @Autowired
    CurrencyRateService currencyRateService;

    @Autowired
    MapperFacade mapperFacade;

    /**
     * 通过baseCode获取币种信息
     *
     * @param code
     * @param enabled
     * @param setOfBooksId
     * @return
     */
    public List<CurrencyRateCO> listCurrencysByCode(@RequestParam(value = "code") String code,
                                                    @RequestParam(value = "enabled") Boolean enabled,
                                                    @RequestParam(value = "setOfBooksId") Long setOfBooksId) {
        String language = OrgInformationUtil.getCurrentLanguage();
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        List<CurrencyRateCO> result = new ArrayList();
        List list = currencyRateService.selectActiveBySetOfBooksIdAndTenantIdAndBaseCurrencyCode(code,setOfBooksId,tenantId,language,enabled);
        list.stream().forEach(item -> {
            CurrencyRateCO co = new CurrencyRateCO();
            mapperFacade.map(item,co);
            result.add(co);
        });
        return result;
    }

    /**
     * 通过baseCode批量获取外币币种信息
     *
     * @param code
     * @param setOfBooksId
     * @param enabled
     * @param otherCodes
     * @return
     */
    public List<CurrencyRateCO> listForeignCurrencysByCodes(@RequestParam(value = "code", required = false) String code,
                                                            @RequestParam(value = "setOfBooksId") Long setOfBooksId,
                                                            @RequestParam(value = "enabled", required = false) Boolean enabled,
                                                            @RequestBody List<String> otherCodes) {
        String language = OrgInformationUtil.getCurrentLanguage();
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        List<CurrencyRateCO> result = new ArrayList();
        List list = currencyRateService.getListCurrencyByOtherCurrencies(tenantId,code,otherCodes,language);
        list.stream().forEach(item -> {
            CurrencyRateCO co = new CurrencyRateCO();
            mapperFacade.map(item,co);
            result.add(co);
        });
        return result;
    }

    /**
     * 通过baseCode获取外币币种信息
     *
     * @param code
     * @param otherCode
     * @param setOfBooksId
     * @return
     */
    public CurrencyRateCO getForeignCurrencyByCode(@RequestParam(value = "code",required = false) String code,
                                                   @RequestParam(value = "otherCode") String otherCode,
                                                   @RequestParam(value = "setOfBooksId") Long setOfBooksId) {
        String language = OrgInformationUtil.getCurrentLanguage();
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        CurrencyRateCO co = new CurrencyRateCO();
        CurrencyRateDTO dto = currencyRateService.getCurrencyRateByOtherAndBaseCurrency(setOfBooksId,tenantId,code,otherCode,language);
        if (dto != null) {
            mapperFacade.map(dto,co);
        }
        return co;
    }

    /**
     * 通过申请人Oid获取账套下默认币种
     *
     * @param applicantOid
     * @return
     */
    public String getUserSetOfBooksBaseCurrencyByApplicatinonOid( @RequestParam(value = "applicantOid") String applicantOid) {
        return currencyRateService.getUserSetOfBooksBaseCurrency(UUID.fromString(applicantOid));
    }


}
