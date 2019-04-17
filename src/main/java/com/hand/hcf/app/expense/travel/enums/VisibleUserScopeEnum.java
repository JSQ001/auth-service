package com.hand.hcf.app.expense.travel.enums;

import com.hand.hcf.app.core.enums.SysEnum;

/**
 * 可见人员范围枚举
 * @author shouting.cheng
 * @date 2019/3/4
 */
public enum VisibleUserScopeEnum implements SysEnum {
    /**
     * 全部
     */
    ALL(1001),
    /**
     * 部门
     */
    DEPARTMENT(1002),
    /**
     * 人员组
     */
    USER_GROUP(1003),

    ;
    private Integer id;

    VisibleUserScopeEnum(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
