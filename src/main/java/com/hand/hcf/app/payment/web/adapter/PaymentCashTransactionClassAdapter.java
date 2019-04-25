package com.hand.hcf.app.payment.web.adapter;


import com.hand.hcf.app.payment.domain.CashTransactionClass;
import com.hand.hcf.app.payment.web.dto.EsCashTransactionClassDTO;
import org.springframework.stereotype.Component;

/**
 * Created by zhaozhu on 2018/5/29.
 */
@Component
public class PaymentCashTransactionClassAdapter {
    public static EsCashTransactionClassDTO cashTransactionClass2EsCashTransactionClassDTO(CashTransactionClass cashTransactionClass){
        EsCashTransactionClassDTO esCashTransactionClassDTO = new EsCashTransactionClassDTO();
        esCashTransactionClassDTO.setId(cashTransactionClass.getId());
        esCashTransactionClassDTO.setSetOfBookId(cashTransactionClass.getSetOfBookId());
        esCashTransactionClassDTO.setTypeCode(cashTransactionClass.getTypeCode());
        esCashTransactionClassDTO.setClassCode(cashTransactionClass.getClassCode());
        esCashTransactionClassDTO.setDescription(cashTransactionClass.getDescription());
        esCashTransactionClassDTO.setEnabled(cashTransactionClass.getEnabled());
        esCashTransactionClassDTO.setDeleted(cashTransactionClass.getDeleted());
        return esCashTransactionClassDTO;
    }
}

