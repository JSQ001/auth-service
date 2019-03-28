package com.hand.hcf.app.mdata.currency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by fanfuqiang 2018/11/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyI18nDTO {
    /**
     * 币种code
     */
    private String currencyCode;
    /**
     * 币种名称
     */
    private String currencyName;
    /**
     * 语言类型
     */
    private String language;


}
