package com.hand.hcf.app.payment.web.dto;

import com.hand.hcf.app.payment.domain.CashTransactionDetail;
import lombok.Data;

import java.util.List;

/**
 * Created by cbc on 2017/12/25.
 */
@Data
public class RePayDTO {

    private List<CashTransactionDetail> details;

    private CashPayDTO payDTO;
}
