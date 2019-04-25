package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 15:17 2018/4/24
 * @Modified by
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequisitionNumberCO {
    private BigDecimal amount; //原币金额

    private BigDecimal functionAmount; //本位币金额

    private Integer countNumber; //计数

    private String currencyCode; //币种代码
}
