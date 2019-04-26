package com.hand.hcf.app.payment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by cbc on 2017/11/28.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmountAndDocumentNumberDTO {

    private String currency;

    private BigDecimal totalAmount;

    private Long documentNumber;
}
