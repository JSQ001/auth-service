package com.hand.hcf.app.prepayment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 刘亮 on 2017/12/19.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandardCurrency  {

    private Long id;

    private String base;

    private String baseName;

    private String baseEnglishName;


    private String otherCurrency;


    private String otherCurrencyName;


    private String otherCurrencyEnglishName;

    private Double rate;

    private Double bocHuiIn;//现汇买入价

    private Double bocChaoIn;//现钞买入价

    private Double bocHuiOut;//现汇卖出价

    private Double bocChaoOut;//现钞卖出价

    private Double bocMidPrice;//中间价




}
