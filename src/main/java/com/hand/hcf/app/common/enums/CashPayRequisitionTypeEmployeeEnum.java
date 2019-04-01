package com.hand.hcf.app.common.enums;


import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;

public enum CashPayRequisitionTypeEmployeeEnum implements IEnum {
    //全部人员
    BASIS_01(1),
    //按部门添加
    BASIS_02(2),
    //按人员组添加
    BASIS_03(3);

    private int id;
    CashPayRequisitionTypeEmployeeEnum(Integer id){
        this.id = id;
    }

    public static CashPayRequisitionTypeEmployeeEnum parse(Integer id) {
        for (CashPayRequisitionTypeEmployeeEnum cashPayRequisitionTypeEmployeeEnum : CashPayRequisitionTypeEmployeeEnum.values()) {
            if (cashPayRequisitionTypeEmployeeEnum.getId() == id) {
                return cashPayRequisitionTypeEmployeeEnum;
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
