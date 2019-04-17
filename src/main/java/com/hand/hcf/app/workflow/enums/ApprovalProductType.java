package com.hand.hcf.app.workflow.enums;

import com.hand.hcf.app.core.enums.SysEnum;

/**
 * Created by yangqi on 2016/6/14.
 */
public enum ApprovalProductType implements SysEnum {

    DomesticFlight(1),//国内航班
    InternationalFlight(2),//国际航班
    DomesticHotel(3),//国内酒店
    InternationalHotel(4),//国际酒店
    DomesticTrain(5);//国内车票

    private Integer id;

    ApprovalProductType(Integer id) {
        this.id = id;
    }

    public static ApprovalProductType parse(Integer id) {
        for (ApprovalProductType currencyCode : ApprovalProductType.values()) {
            if (currencyCode.getId().equals(id)) {
                return currencyCode;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
