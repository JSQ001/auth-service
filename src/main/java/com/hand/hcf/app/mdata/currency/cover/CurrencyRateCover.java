package com.hand.hcf.app.mdata.currency.cover;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.currency.domain.CurrencyRate;
import com.hand.hcf.app.mdata.currency.domain.CurrencyStatus;
import com.hand.hcf.app.mdata.currency.dto.CompanyStandardCurrencyDTO;
import com.hand.hcf.app.mdata.currency.dto.CurrencyRateDTO;
import com.hand.hcf.app.mdata.currency.service.CurrencyStatusService;
import com.hand.hcf.core.component.ApplicationContextProvider;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by fanfuqiang 2018/11/20
 */
public class CurrencyRateCover {

    public static CurrencyRate dtoToCurrencyRate(CurrencyRateDTO currencyRateDTO) {
        Long currentUserId = OrgInformationUtil.getCurrentUserId();

        CurrencyRate target = new CurrencyRate();
        BeanUtils.copyProperties(currencyRateDTO, target);
        //insert
        if (null == currencyRateDTO.getCurrencyRateOid()) {
            target.setCurrencyRateOid(UUID.randomUUID());
            target.setCreatedBy(currentUserId);
            target.setCreatedDate(ZonedDateTime.now());
        }
        target.setApplyDate(currencyRateDTO.getApplyDate());
        target.setLastUpdatedBy(currentUserId);
        target.setLastUpdatedDate(ZonedDateTime.now());


        return target;
    }

    public static List<CompanyStandardCurrencyDTO> toCompanyStandardCurrencyDTO(List<CurrencyRateDTO> currencyRateDTOList) {
        if (CollectionUtils.isEmpty(currencyRateDTOList)) {
            return null;
        }
        List<CompanyStandardCurrencyDTO> targets = new ArrayList<>();
        currencyRateDTOList.stream().filter(u -> u != null).forEach(u -> {
            targets.add(toCompanyStandardCurrencyDTO(u));
        });
        return targets;
    }

    public static CompanyStandardCurrencyDTO toCompanyStandardCurrencyDTO(CurrencyRateDTO currencyRateDTO) {

        if (null == currencyRateDTO) {
            return null;
        }

        CurrencyStatusService currencyStatusService = (CurrencyStatusService) ApplicationContextProvider.getApplicationContext().getBean("currencyStatusService");

        CurrencyStatus currencyStatus = currencyStatusService.selectOne(new EntityWrapper<CurrencyStatus>().eq("tenant_id", currencyRateDTO.getTenantId()).eq("set_of_books_id", currencyRateDTO.getSetOfBooksId()).eq("currency_code", currencyRateDTO.getCurrencyCode()));

        CompanyStandardCurrencyDTO companyStandardCurrencyDTO = new CompanyStandardCurrencyDTO();
        companyStandardCurrencyDTO.setApplyDate(currencyRateDTO.getApplyDate());
        companyStandardCurrencyDTO.setBaseCurrency(currencyRateDTO.getBaseCurrencyCode());
        companyStandardCurrencyDTO.setBaseCurrencyName(currencyRateDTO.getBaseCurrencyName());
        companyStandardCurrencyDTO.setBaseCurrencyName(currencyRateDTO.getBaseCurrencyName());
        companyStandardCurrencyDTO.setCompanyCurrencyOid(currencyRateDTO.getCurrencyRateOid());
        companyStandardCurrencyDTO.setCurrency(currencyRateDTO.getCurrencyCode());
        companyStandardCurrencyDTO.setCurrencyName(currencyRateDTO.getCurrencyName());
        companyStandardCurrencyDTO.setEnable(currencyStatus.getEnabled());
        companyStandardCurrencyDTO.setLastUpdatedDate(currencyRateDTO.getLastUpdatedDate());
        companyStandardCurrencyDTO.setRate(currencyRateDTO.getRate());
        return companyStandardCurrencyDTO;

    }

    public static CurrencyRateDTO parse(CurrencyRate currencyRate) {
        if (null == currencyRate) {
            return null;
        }
        CurrencyRateDTO target = new CurrencyRateDTO();
        BeanUtils.copyProperties(currencyRate, target);
        return target;
    }
}
