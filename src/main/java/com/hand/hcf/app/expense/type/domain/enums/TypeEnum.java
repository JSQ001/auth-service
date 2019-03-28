package com.hand.hcf.app.expense.type.domain.enums;

/**
 * <p>
 *     费用体系类型枚举
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */
public enum TypeEnum {

    APPLICATION_TYPE(0, "申请类型") , COST_TYPE(1, "费用类型");


    /**
     * 主键
     */
    private final Integer key;

    /**
     * 描述
     */
    private final String desc;

    TypeEnum(final Integer key, final String desc) {
        this.key = key;
        this.desc = desc;
    }


    public static TypeEnum getType(Integer key) {
        TypeEnum[] its = TypeEnum.values();
        for (TypeEnum it : its) {
            if (it.getKey().equals(key)) {
                return it;
            }
        }
        return APPLICATION_TYPE;
    }

    public Integer getKey() {
        return this.key;
    }

    public String getDesc() {
        return this.desc;
    }
}
