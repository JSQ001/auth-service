package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @description: 支付明细DTO
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017-12-19 10:36:31
 */

@Data
public class CashTransactionDetailWebDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;//租户id

    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;//公司id

    private String billCode;//支付流水号

    private ZonedDateTime payDate;//支付日期

    private String documentNumber;//单据编号

    private BigDecimal amount;//总金额
    private BigDecimal writeOffAmount;//未核销金额

    private String cshTransactionClassName;//现金事务分类名称
}
