package com.hand.hcf.app.expense.input.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/3/1 14:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpInputTaxSumAmountDTO {

    /**
     * 基数金额
     */
    private BigDecimal baseAmount;

    /**
     * 基数本币金额
     */
    private BigDecimal baseFunctionAmount;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 本币金额
     */
    private BigDecimal functionAmount;

}
