package com.hand.hcf.app.base.user.enums;

import com.hand.hcf.app.core.enums.SysEnum;

public enum SMSTokenType implements SysEnum {
    ACTIVATE_USER(1001), CHANGE_PASSWORD(1002), RESET_PASSWORD(1003), EXPORT_DATA(1004), REGISTER_COMPANY(1005), ADD_MOBILE(1006), REGISTER_PERSONAL(1007),
    BIND_EMAIL(2001), BIND_MOBILE(2002), USER_ACTIVATE_BIND_EMAIL(1008), USER_ACTIVATE_BIND_MOBILE(1009), SET_MOBILE(1010), DINGTALK_BIND_EMAIL(2003), DINGTALK_BIND_MOBILE(2004), TRAIL(2005), DEVICE_BIND_CODE(3001), WECHAT_BIND_MOBILE(2006),
    UE_BIND_MOBILE(3100);
    private Integer id;

    SMSTokenType(Integer id) {
        this.id = id;
    }

    public static SMSTokenType parse(Integer id) {
        for (SMSTokenType sMSTokenType : SMSTokenType.values()) {
            if (sMSTokenType.getId().equals(id)) {
                return sMSTokenType;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

}
