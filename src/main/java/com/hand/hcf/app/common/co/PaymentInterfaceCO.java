package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.annotation.InterfaceDataStructure;
import com.hand.hcf.app.common.annotation.InterfaceTransactionType;
import com.hand.hcf.app.common.enums.SourceTransactionType;
import com.hand.hcf.app.common.enums.SourceTransactionTypeDataStructure;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * Created by kai.zhang on 2017-12-25.
 * 对公报账单
 */
@Data
@InterfaceTransactionType(SourceTransactionType.CSH_PAYMENT)
public class PaymentInterfaceCO extends AccountingBaseCO implements Serializable {
    //支付明细
    @Valid
    @InterfaceDataStructure(sequence = 1, type = SourceTransactionTypeDataStructure.PAYMENT_DETAIL)
    private List<PaymentDetailCO> paymentDetails;
}
