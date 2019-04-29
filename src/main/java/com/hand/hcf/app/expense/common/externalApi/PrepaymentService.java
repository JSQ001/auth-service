package com.hand.hcf.app.expense.common.externalApi;

import com.hand.hcf.app.common.co.CashPayRequisitionTypeCO;
import com.hand.hcf.app.common.co.CashPayRequisitionTypeSummaryCO;
import com.hand.hcf.app.common.co.CashPaymentRequisitionHeaderCO;
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

    /**
     * 根据单据头ID校验预付款单是否可以提交
     * @param requisitionHeaderId
     * @return
     */
    public Boolean checkCashPaymentRequisitionBeforeSubmit(Long requisitionHeaderId){
        //return prepaymentClient.checkCashPaymentRequisitionBeforeSubmit(requisitionHeaderId);
        //jiu.zhao TODO
        return true;
    }

    /**
     * 更新预付款单单据状态
     * @param requisitionHeaderId
     * @param status
     * @return
     */
    public Boolean updateCashPaymentRequisitionStatus(Long requisitionHeaderId, Integer status){
        return prepaymentClient.updateCashPaymentRequisitionStatus(requisitionHeaderId, status);
    }

    /**
     * 根据申请单头ID获取关联的预付款单头
     * @param applicationHeadId
     * @return
     */
    public CashPaymentRequisitionHeaderCO getCashPaymentRequisitionHeaderByApplicationHeaderId(Long applicationHeadId){
        return prepaymentClient.getCashPaymentRequisitionHeaderByApplicationHeaderId(applicationHeadId);
    }
}
