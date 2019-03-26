package com.hand.hcf.app.mdata.currency.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by fanfuqiang 2018/11/20
 */
@Data
@TableName("sys_currency_i18n")
public class CurrencyI18n extends Domain {

    /**
     * 币种code
     */
    @TableField("currency_code")
    @NotNull
    private String currencyCode;
    /**
     * 币种名称
     */
    @TableField("currency_name")
    @NotNull
    private String currencyName;
    /**
     * 语言
     */
    private String language;

}
