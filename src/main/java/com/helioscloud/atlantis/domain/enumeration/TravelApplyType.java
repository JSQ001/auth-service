package com.helioscloud.atlantis.domain.enumeration;

public enum TravelApplyType implements SysEnum {
    FLYBACK(1001),
    NORMAL(1002),
    INTERVIEW(1003);

    private Integer id;

    TravelApplyType(Integer id) {
        this.id = id;
    }

    public static TravelApplyType parse(Integer id) {
        for (TravelApplyType fieldType : TravelApplyType.values()) {
            if (fieldType.getID().equals(id)) {
                return fieldType;
            }
        }
        return null;
    }

    @Override
    public Integer getID() {
        return this.id;
    }
}
