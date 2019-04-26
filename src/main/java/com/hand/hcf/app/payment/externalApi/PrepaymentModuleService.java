package com.hand.hcf.app.payment.externalApi;

import org.springframework.stereotype.Component;

/**
 * 预付款模块接口
 */
@Component
public class PrepaymentModuleService {


    private static PaymentPrepaymentService prepaymentService;

    /*public PrepaymentModuleService(PrepaymentService prepaymentService) {
        this.prepaymentService = prepaymentService;
    }

    public static String getPrepaymentTypeByID(Long id) {
        CashPayRequisitionTypeDTO cashPayRequisitionType = prepaymentService.getCashPayRequisitionType(id);
        return cashPayRequisitionType.getCashPayRequisitionType().getTypeName();
    }*/

}
