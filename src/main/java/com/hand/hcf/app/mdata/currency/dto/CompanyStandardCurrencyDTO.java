package com.hand.hcf.app.mdata.currency.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by fanfuqiang 2018/11/20
 */
@Data
public class CompanyStandardCurrencyDTO {

    private UUID companyCurrencyOid;

    @NotNull
    private String baseCurrency;

    @NotNull
    private String baseCurrencyName;

    @NotNull
    private String currency;

    @NotNull
    private String currencyName;

    @NotNull
    private Double rate;

    private ZonedDateTime applyDate;

    private UUID companyOid;

    private boolean enable = true;

    private boolean basic;

    private ZonedDateTime lastUpdatedDate = ZonedDateTime.now();

    private Double amount;

    private Double baseAmount;
}
