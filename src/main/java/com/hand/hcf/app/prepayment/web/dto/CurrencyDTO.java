package com.hand.hcf.app.prepayment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 刘亮 on 2018/5/2.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyDTO {
    private String currency;
    private Double amount;
}
