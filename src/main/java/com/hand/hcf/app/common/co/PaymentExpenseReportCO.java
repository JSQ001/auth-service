package com.hand.hcf.app.common.co;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: bin.xie
 * @Description: 报账单修改支付数据金额类
 * @Date: Created in 16:24 2018/5/10
 * @Modified by
 */
@Data
public class PaymentExpenseReportCO {
    private Long id;
    private BigDecimal amount;
    private Long userId;
}
