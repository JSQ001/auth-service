package com.hand.hcf.app.payment.web.adapter;


import com.hand.hcf.app.payment.domain.CashDefaultFlowItem;
import com.hand.hcf.app.payment.web.dto.EsCashDefaultFlowItemDTO;
import org.springframework.stereotype.Component;

/**
 * Created by zhaozhu on 2018/5/29.
 */
@Component
public class CashDefaultFlowItemAdapter {
    public static EsCashDefaultFlowItemDTO cashDefaultFlowItem2EsCashDefaultFlowItemDTO(CashDefaultFlowItem cashDefaultFlowItem){
        EsCashDefaultFlowItemDTO esCashDefaultFlowItemDTO = new EsCashDefaultFlowItemDTO();
        esCashDefaultFlowItemDTO.setId(cashDefaultFlowItem.getId());
        esCashDefaultFlowItemDTO.setTransactionClassId(cashDefaultFlowItem.getTransactionClassId());
        esCashDefaultFlowItemDTO.setCashFlowItemId(cashDefaultFlowItem.getCashFlowItemId());
        esCashDefaultFlowItemDTO.setDefaultFlag(cashDefaultFlowItem.getDefaultFlag());
        esCashDefaultFlowItemDTO.setEnabled(cashDefaultFlowItem.getEnabled());
        esCashDefaultFlowItemDTO.setDeleted(cashDefaultFlowItem.getDeleted());
        return esCashDefaultFlowItemDTO;
    }
}

