package com.hand.hcf.app.expense.input.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

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
