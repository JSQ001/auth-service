package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by 龚前军 on 2019/3/25.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationAmountCO {

    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 金额
     */
    private BigDecimal amount;

    public Double AmountToDouble(){
        return amount != null ? amount.doubleValue() : 0D;
    }

    /**
     * 本币金额
     */
    private BigDecimal relatedAmount;

    public Double RelatedAmountToDouble(){
        return relatedAmount != null ? relatedAmount.doubleValue() : 0D;
    }

}
