package com.hand.hcf.app.common.enums;


import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;

public enum CashPayRequisitionTypeBasisEnum implements IEnum {
    //不选择申请单依据
    BASIS_00(0),
    //预付款单头公司+头部门=申请单头公司+头部门
    BASIS_01(1),
    //预付款单头申请人=申请单头申请人
    BASIS_02(2);

    private int id;
    CashPayRequisitionTypeBasisEnum(Integer id){
        this.id = id;
    }

    public static CashPayRequisitionTypeBasisEnum parse(Integer id) {
        for (CashPayRequisitionTypeBasisEnum cashPayRequisitionTypeBasisEnum : CashPayRequisitionTypeBasisEnum.values()) {
            if (cashPayRequisitionTypeBasisEnum.getId() == id) {
                return cashPayRequisitionTypeBasisEnum;
            }
        }
        return null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public Serializable getValue() {
        return this.id;
    }
}
