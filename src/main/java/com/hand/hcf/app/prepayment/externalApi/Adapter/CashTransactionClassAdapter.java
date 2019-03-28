package com.hand.hcf.app.prepayment.externalApi.Adapter;



import com.hand.hcf.app.common.co.CashDefaultFlowItemCO;
import com.hand.hcf.app.common.co.CashTransactionClassCO;
import com.hand.hcf.app.prepayment.web.dto.EsCashDefaultFlowItemDTO;
import com.hand.hcf.app.prepayment.web.dto.EsCashFlowItemDTO;
import com.hand.hcf.app.prepayment.web.dto.EsCashTransactionClassDTO;
import org.springframework.stereotype.Component;

@Component
public class CashTransactionClassAdapter {
    public static CashTransactionClassCO esCashTransactionClassDTO2CashTransactionClassDTO(EsCashTransactionClassDTO esCashTransactionClassDTO){
        CashTransactionClassCO cashTransactionClass = new CashTransactionClassCO();
        cashTransactionClass.setId(esCashTransactionClassDTO.getId());
        //第三方查询没有该字段
        //cashTransactionClass.setSetOfBookId(esCashTransactionClassDTO.getSetOfBookId());
        cashTransactionClass.setTypeCode(esCashTransactionClassDTO.getTypeCode());
        cashTransactionClass.setClassCode(esCashTransactionClassDTO.getClassCode());
        cashTransactionClass.setDescription(esCashTransactionClassDTO.getDescription());
        return cashTransactionClass;
    }
    public static CashDefaultFlowItemCO toCashDefaultFlowItemCO(EsCashTransactionClassDTO esCashTransactionClassDTO,
                                                                  EsCashDefaultFlowItemDTO esCashDefaultFlowItemDTO,
                                                                  EsCashFlowItemDTO esCashFlowItemDTO){
        CashDefaultFlowItemCO cashDefaultFlowItemDTO = new CashDefaultFlowItemCO();
        cashDefaultFlowItemDTO.setTransactionClassId(esCashTransactionClassDTO.getId());
        cashDefaultFlowItemDTO.setCashFlowItemId(esCashFlowItemDTO.getId());
        cashDefaultFlowItemDTO.setDefaultFlag(esCashDefaultFlowItemDTO.getDefaultFlag());
        cashDefaultFlowItemDTO.setTransactionClassCode(esCashTransactionClassDTO.getClassCode());
        cashDefaultFlowItemDTO.setTransactionClassName(esCashTransactionClassDTO.getDescription());
        cashDefaultFlowItemDTO.setCashFlowItemCode(esCashFlowItemDTO.getFlowCode());
        cashDefaultFlowItemDTO.setCashFlowItemName(esCashFlowItemDTO.getDescription());
        return cashDefaultFlowItemDTO;
    }


}
