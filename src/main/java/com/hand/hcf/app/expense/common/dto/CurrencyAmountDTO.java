package com.hand.hcf.app.expense.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *     币种金额DTO
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/27
 */
@Data
public class CurrencyAmountDTO implements Serializable {
    private String currencyCode;

    private BigDecimal amount;

    private BigDecimal functionalAmount;
}
