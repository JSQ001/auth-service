package com.hand.hcf.app.expense.type.domain.enums;

/**
 * <p>
 *     适用人员枚举
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/16
 */
public enum  AssignUserEnum {
    /**/
    USER_ALL(101, "全部人员") , USER_DEPARTMENT(102, "按部门分配"), USER_GROUP(103, "按人员组");


    /**
     * 主键
     */
    private final Integer key;

    /**
     * 描述
     */
    private final String desc;

    AssignUserEnum(final Integer key, final String desc) {
        this.key = key;
        this.desc = desc;
    }



    public Integer getKey() {
        return this.key;
    }

    public String getDesc() {
        return this.desc;
    }
}
