package com.hand.hcf.app.mdata.supplier.enums;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/4 16:55
 */
public enum StatusEnum {

    ENABLE(1001),
    DISABLE(1002);

    private Integer id;

    StatusEnum(Integer id) {
        this.id = id;
    }

    public static StatusEnum parse(Integer id) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getId().equals(id)) {
                return statusEnum;
            }
        }
        return null;
    }

    public Integer getId() {
        return this.id;
    }

}
