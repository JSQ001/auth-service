package com.hand.hcf.app.common.enums;


import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;

public enum CashPayRequisitionTypeTypeEnum implements IEnum {
    //不关联
    BASIS_00(0),
    //全部申请单类型
    BASIS_01(1),
    //部分申请类型
    BASIS_02(2);

    private int id;
    CashPayRequisitionTypeTypeEnum(Integer id){
        this.id = id;
    }

    public static CashPayRequisitionTypeTypeEnum parse(Integer id) {
        for (CashPayRequisitionTypeTypeEnum cashPayRequisitionTypeTypeEnum : CashPayRequisitionTypeTypeEnum.values()) {
            if (cashPayRequisitionTypeTypeEnum.getId() == id) {
                return cashPayRequisitionTypeTypeEnum;
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
