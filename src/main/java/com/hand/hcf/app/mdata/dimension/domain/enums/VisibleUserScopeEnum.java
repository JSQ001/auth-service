package com.hand.hcf.app.mdata.dimension.domain.enums;

import com.hand.hcf.core.enums.SysEnum;

/**
 * 可见人员范围枚举
 */
public enum VisibleUserScopeEnum implements SysEnum {
    ALL(1001),//全部
    DEPARTMENT(1002),//部门
    USER_GROUP(1003),//人员组
    USER(1004)//人员

    ;
    private Integer id;

    VisibleUserScopeEnum(Integer id) {
        this.id = id;
    }

    public static VisibleUserScopeEnum parse(Integer id) {
        for (VisibleUserScopeEnum visibleUserScopeEnum : VisibleUserScopeEnum.values()) {
            if (visibleUserScopeEnum.getId().equals(id)) {
                return visibleUserScopeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
