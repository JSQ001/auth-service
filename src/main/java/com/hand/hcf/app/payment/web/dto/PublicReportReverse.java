package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;


/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 11:38 2018/6/27
 * @Modified by
 */
@Data
public class PublicReportReverse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentHeaderId; // 报账单ID

    private BigDecimal reserveAmount; // 可反冲金额

    private BigDecimal paidAmount;// 已付金额

    private BigDecimal writeOffTotalAmount; //已核销金额
}
