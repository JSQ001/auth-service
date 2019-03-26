package com.hand.hcf.app.mdata.currency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by fanfuqiang 2018/11/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyChangeLogDTO {
    private UUID changeLogOid;
    private String baseCurrencyCode;
    private String currencyCode;
    private Double rate;
    private ZonedDateTime applyDate;
    private String createdBy;
    private String lastUpdatedBy;
    private ZonedDateTime createdDate;
    private ZonedDateTime lastUpdatedDate;
}
