package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 报销单核销借款：处理核销金额
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017-12-14 10:36:08
 */

@Data
public class CashTransactionWriteOffDTO{
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cashTransactionDetailId;//支付明细ID

    private BigDecimal writeOffAmountBefore;//上次核销金额

    private BigDecimal writeOffAmountAfter;//本次核销金额

    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastUpdatedBy;//最后更新人
}
