package com.hand.hcf.app.mdata.currency.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by fanfuqiang 2018/11/20
 */
@TableName("sys_currency_rate")
@Data
public class CurrencyRate extends Domain {

    /**
     * 生效汇率Oid
     */
    @TableField("currency_rate_oid")
    private UUID currencyRateOid;
    /**
     * 持有币种code
     */
    @TableField("base_currency_code")
    private String baseCurrencyCode;
    /**
     * 兑换币种code
     */
    @TableField("currency_code")
    private String currencyCode;
    /**
     * 持有币种A对兑换币种B的汇率
     */
    private Double rate;
    /**
     * 生效日期
     */
    @TableField("apply_date")
    private ZonedDateTime applyDate;
    /**
     * 汇率来源【欧行ECB 、手动更新MANUAL】
     */
    private String source;
    /**
     * 账套ID
     */
    @TableField("set_of_books_id")
    private Long setOfBooksId;
    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

}
