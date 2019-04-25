package com.hand.hcf.app.payment.externalApi;

import com.hand.hcf.app.common.co.CashPayRequisitionTypeCO;
import com.hand.hcf.app.common.co.CashPaymentRequisitionHeaderCO;
import com.hand.hcf.app.common.co.CashPaymentRequisitionLineCO;
import com.hand.hcf.app.prepayment.implement.web.ImplementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: 调用预付款模块三方接口
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2018/12/25
 */
@Service
public class PaymentPrepaymentService {
    @Autowired
    private ImplementController prepaymentClient;

    /**
     * 根据预付款头ID查询头信息
     *
     * @param id   预付款头ID
     * @return
     */
    public CashPaymentRequisitionHeaderCO getCashPaymentRequisitionHeadById(Long id){
        return prepaymentClient.getCashPaymentRequisitionHeadById(id);
    }

    /**
     * 根据预付款行ID查询行信息
     *
     * @param id   预付款行ID
     * @return
     */
    public CashPaymentRequisitionLineCO getCashPaymentRequisitionLineById(Long id){
        return prepaymentClient.getCashPaymentRequisitionLineById(id);
    }

    /**
     * 根据预付款行ID批量查询行信息
     *
     * @param ids   预付款行ID
     * @return
     */
    public List<CashPaymentRequisitionLineCO> listCashPaymentRequisitionLineById(List<Long> ids){
        return prepaymentClient.listCashPaymentRequisitionLineById(ids);
    }

    /**
     * 根据ID查询预付款单类型
     * @param id
     * @return
     */
    public String getPrepaymentTypeByID(Long id){
        CashPayRequisitionTypeCO cashPayRequisitionTypeCO = prepaymentClient.getPaymentRequisitionTypeById(id);
        return cashPayRequisitionTypeCO.getTypeName();
    }
}
