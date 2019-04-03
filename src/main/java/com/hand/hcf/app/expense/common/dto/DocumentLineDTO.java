package com.hand.hcf.app.expense.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *     单据行DTO 包含按币种合计的金额等信息
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentLineDTO<T> implements Serializable {
    private CurrencyAmountDTO currencyAmount;

    private List<T> lines;
}
