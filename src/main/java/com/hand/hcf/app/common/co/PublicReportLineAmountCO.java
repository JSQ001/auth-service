package com.hand.hcf.app.common.co;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 报账单行已付金额和已退款金额
 * @Date: Created in 15:13 2018/5/31
 * @Modified by
 */
@Data
public class PublicReportLineAmountCO {

    private BigDecimal returnAmount; // 已退款金额
    private BigDecimal paidAmount; // 已支付金额
    private Long documentLineId; // 报账单行ID
}
