package com.helioscloud.atlantis.domain.enumeration;

/**
 * Flag类型的枚举
 */
public enum FlagEnum implements SysEnum {
    //创建  创建:1001，删除:1002
    CREATE(1001),
    //删除
    DELETE(1002);

    private Integer id;

    FlagEnum(Integer id) {
        this.id = id;
    }

    public static FlagEnum parse(Integer id) {
        for (FlagEnum typeEnum : FlagEnum.values()) {
            if (typeEnum.getID().equals(id)) {
                return typeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getID() {
        return this.id;
    }
}
