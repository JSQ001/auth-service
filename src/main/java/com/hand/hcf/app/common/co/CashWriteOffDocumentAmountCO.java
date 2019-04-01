package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/6/28 17:13
 * @remark 单据核销金额
 */
@Data
public class CashWriteOffDocumentAmountCO {

    /**
     * 被核销单据头ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentHeaderId;
    /**
     * 单据已核销金额
     */
    private BigDecimal writeOffAmount;
    /**
     * 单据未核销金额
     */
    private BigDecimal unWriteOffAmount;
}
