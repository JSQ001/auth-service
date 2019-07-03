package com.hand.hcf.app.ant.taxreimburse.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description:
 * @date 2019/5/29 14:33
 */
@Data
public class ExpBankFlowDTO {
    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 币种代码
     */
    private String currencyCode;

    /**
     * 流水金额
     */
    private BigDecimal flowAmountSum;

}
