package com.hand.hcf.app.mdata.currency.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainEnable;
import lombok.Data;


/**
 * Created by fanfuqiang 2018/11/20
 */
@TableName("sys_currency_status")
@Data
public class CurrencyStatus extends DomainEnable {

    /**
     * 币种code
     */
    @TableField("currency_code")
    private String currencyCode;

    /**
    /**
     * 是否启用自动更新
     */
    @TableField("enable_auto_update")
    private Boolean enableAutoUpdate;

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
