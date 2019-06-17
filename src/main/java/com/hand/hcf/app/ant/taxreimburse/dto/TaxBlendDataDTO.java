package com.hand.hcf.app.ant.taxreimburse.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description:
 * @date 2019/6/4 20:17
 */
@Data
public class TaxBlendDataDTO {
    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 币种代码
     */
    private String currencyCode;

    /**
     * 申报总金额
     */
    private BigDecimal requestAmountSum;

    /**
     * 流水金额
     */
    private BigDecimal flowAmountSum;


}
