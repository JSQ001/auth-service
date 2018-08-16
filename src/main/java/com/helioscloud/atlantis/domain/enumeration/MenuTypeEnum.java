package com.helioscloud.atlantis.domain.enumeration;

/**
 * 菜单类型的枚举
 */
public enum MenuTypeEnum implements SysEnum {
    //功能 菜单
    FUNCTION(1000),
    //目录
    DIRECTORY(1001),
    //组件
    COMPONENT(1002);

    private Integer id;

    MenuTypeEnum(Integer id) {
        this.id = id;
    }

    public static MenuTypeEnum parse(Integer id) {
        for (MenuTypeEnum typeEnum : MenuTypeEnum.values()) {
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
