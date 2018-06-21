package com.helioscloud.atlantis.domain.enumeration;

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

    public static EntityTypeEnum parse(Integer id) {
        for (EntityTypeEnum rejectTypeEnum : EntityTypeEnum.values()) {
            if (rejectTypeEnum.getID().equals(id)) {
                return rejectTypeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getID() {
        return this.id;
    }
}
