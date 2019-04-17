package com.hand.hcf.app.base.system.enums;

import com.hand.hcf.app.core.enums.SysEnum;

/**
 * Created by qi.yang on 17/1/9.
 */
public enum DeviceStatusEnum implements SysEnum {
    NOMAL(1001),//正常
    UNVALIDATED(1002),//未验证
    DELETE(1003);//删除

    private Integer id;

    DeviceStatusEnum(Integer id) {
        this.id = id;
    }

    public static DeviceStatusEnum parse(Integer id) {
        for (DeviceStatusEnum rejectTypeEnum : DeviceStatusEnum.values()) {
            if (rejectTypeEnum.getId().equals(id)) {
                return rejectTypeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
