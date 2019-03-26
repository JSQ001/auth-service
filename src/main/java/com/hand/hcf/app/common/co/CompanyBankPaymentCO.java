package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2018/12/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyBankPaymentCO {

    private Long bankAccountId;

    private Long paymentMethodId;

    private String paymentMethodCategory;

    private String paymentMethodCode;
}
