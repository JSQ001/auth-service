package com.hand.hcf.app.expense.common.externalApi;

import com.hand.hcf.app.common.co.CashPayRequisitionTypeCO;
import com.hand.hcf.app.common.co.CashPayRequisitionTypeSummaryCO;
import com.hand.hcf.app.prepayment.implement.web.ImplementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 *  调用预付款模块API
 * </p>
 *
 * @Author: 龚前军
 * @Date: 2019/3/20
 */
@Service
public class PrepaymentService {
    @Autowired
    ImplementController prepaymentClient;

    /**
     * 根据ID查询预付款单类型
     * @param id
     * @return
     */
    public CashPayRequisitionTypeCO getPaymentRequisitionTypeById(Long id){
        return prepaymentClient.getPaymentRequisitionTypeById(id);
    }

    public CashPayRequisitionTypeSummaryCO getCashPayRequisitionTypeById(Long id) {
        return prepaymentClient.getCashPayRequisitionTypeById(id);
    }

}
