package com.hand.hcf.app.mdata.contact.enums;

import com.hand.hcf.app.core.enums.SysEnum;

public enum PhoneType implements SysEnum {
    MOBILE_PHONE(1001), LAND_PHONE(1002);

    private Integer id;

    PhoneType(Integer id) {
        this.id = id;
    }

    public static PhoneType parse(Integer id) {
        for (PhoneType phoneType : PhoneType.values()) {
            if (phoneType.getId().equals(id)) {
                return phoneType;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
