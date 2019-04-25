package com.hand.hcf.app.payment.web.adapter;


import com.hand.hcf.app.payment.domain.CashFlowItem;
import com.hand.hcf.app.payment.web.dto.EsCashFlowItemDTO;
import org.springframework.stereotype.Component;

/**
 * Created by zhaozhu on 2018/5/29.
 */
@Component
public class CashFlowItemAdapter {
    public static EsCashFlowItemDTO cashFlowItem2EsCashFlowItemDTO(CashFlowItem cashFlowItem){
        EsCashFlowItemDTO esCashFlowItemDTO = new EsCashFlowItemDTO();
        esCashFlowItemDTO.setId(cashFlowItem.getId());
        esCashFlowItemDTO.setSetOfBookId(cashFlowItem.getSetOfBookId());
        esCashFlowItemDTO.setFlowCode(cashFlowItem.getFlowCode());
        esCashFlowItemDTO.setDescription(cashFlowItem.getDescription());
        esCashFlowItemDTO.setEnabled(cashFlowItem.getEnabled());
        esCashFlowItemDTO.setDeleted(cashFlowItem.getDeleted());
        return esCashFlowItemDTO;
    }
}

