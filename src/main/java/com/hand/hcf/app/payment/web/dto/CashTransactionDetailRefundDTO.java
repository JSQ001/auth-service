package com.hand.hcf.app.payment.web.dto;

import com.hand.hcf.app.payment.domain.CashTransactionDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 13:05 2018/4/9
 * @Modified by
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashTransactionDetailRefundDTO {
    private CashTransactionDetail newCashTransactionDetail;
    private CashTransactionDetail oldCashTransactionDetail;
}
