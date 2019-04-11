package com.hand.hcf.app.mdata.supplier.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;

/**
 * @description:
 * @version: 1.0
 * @author: jaixing.che
 * @date: 2019/3/26
 */
public enum VendorStatusEnum implements IEnum {

    EDITOR("EDITOR"),//编辑中
    APPROVED("APPROVED"),//审批通过
    PENGDING("PENGDING"),//审批中
    PROHIBIT("PROHIBIT"),//禁用
    REFUSE("REFUSE");//审批驳回

    private String  vendorStatus;

    VendorStatusEnum(String vendorStatus){
        this.vendorStatus = vendorStatus;
    }

    @Override
    public String toString() {
        return this.vendorStatus;
    }

    public String getVendorStatus() {
        return vendorStatus;
    }

    @Override
    public Serializable getValue() {
        return this.vendorStatus;
    }
}
