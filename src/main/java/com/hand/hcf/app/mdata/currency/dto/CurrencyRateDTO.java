package com.hand.hcf.app.mdata.currency.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by fanfuqiang 2018/11/20
 */
@Data
public class CurrencyRateDTO {

    /**
     * 币种汇率Oid
     */
    private UUID currencyRateOid;
    /**
     * 持有币种code
     */
    @NotNull
    private String baseCurrencyCode;
    /**
     * 持有货币币种名称 response
     */
    private String baseCurrencyName;
    /**
     * 兑换币种code
     */
    @NotNull
    private String currencyCode;

    /**
     * 兑换币种名称 response
     */
    private String currencyName;

    private String currencyCodeAndName;
    /**
     * 汇率
     */
    @NotNull
    private Double rate;
    /**
     * 汇率生效日期
     */
    @NotNull
    private ZonedDateTime applyDate;

    /**
     * 汇率来源【欧行ECB 、手动更新MANUAL】
     */
    @NotNull
    private String source = "MANUAL";

    /**
     * 币种汇率是否启用
     */
    @NotNull
    private Boolean enabled;

    /**
     * 是否启用自动更新
     *
     * @return
     */
    @NotNull
    private Boolean enableAutoUpdate;

    /**
     * 账套ID
     */
    @NotNull
    private Long setOfBooksId;

    /**
     * 租户ID
     */
    @NotNull
    private Long tenantId;

    /**
     * 最近操作日期--response
     */
    private ZonedDateTime lastUpdatedDate;
}
