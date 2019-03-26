package com.hand.hcf.app.common.co;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class CurrencyRateCO {
    /**
     * 币种汇率Oid
     */
    private UUID currencyRateOid;
    /**
     *持有币种code
     */
    private String baseCurrencyCode;
    /**
     * 持有货币币种名称 response
     */
    private String baseCurrencyName;
    /**
     * 兑换币种code
     */
    private String currencyCode;

    /**
     * 兑换币种名称 response
     */
    private String currencyName;
    /**
     * 汇率
     */
    private Double rate;
    /**
     * 汇率生效日期
     */
    private ZonedDateTime applyDate;

    /**
     *汇率来源【欧行ECB 、手动更新MANUAL】
     */
    private String source = "MANUAL";

    /**
     * 币种汇率是否启用
     */
    private Boolean enabled;

    /**
     * 是否启用自动更新
     * @return
     */
    private Boolean enableAutoUpdate;

    /**
     * 账套ID
     */
    private Long setOfBooksId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 最近操作日期--response
     */
    private ZonedDateTime lastUpdatedDate;
}
