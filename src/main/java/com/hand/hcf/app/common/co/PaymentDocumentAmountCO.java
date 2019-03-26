package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 付款单据金额类型
 * @Date: Created in 11:16 2018/7/4
 * @Modified by
 */
@Data
public class PaymentDocumentAmountCO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentLineId;

    private BigDecimal payAmount; // 已付金额

    private BigDecimal returnAmount; // 已退款金额

    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentId; // 单据头ID
}
