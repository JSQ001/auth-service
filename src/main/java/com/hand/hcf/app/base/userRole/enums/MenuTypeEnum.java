package com.hand.hcf.app.base.userRole.enums;

import com.hand.hcf.app.core.enums.SysEnum;

/**
 * 菜单类型的枚举
 */
public enum MenuTypeEnum implements SysEnum {
    //功能 菜单
    FUNCTION(1000),
    //目录
    DIRECTORY(1001),
    //按钮
    BUTTON(1002);

    private Integer id;

    MenuTypeEnum(Integer id) {
        this.id = id;
    }

    public static MenuTypeEnum parse(Integer id) {
        for (MenuTypeEnum typeEnum : MenuTypeEnum.values()) {
            if (typeEnum.getId().equals(id)) {
                return typeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
