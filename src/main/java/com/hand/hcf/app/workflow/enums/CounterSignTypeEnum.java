package com.hand.hcf.app.workflow.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 自选审批人加签和审批加签均采用此枚举
 * Created by caixiang on 2017/12/25.
 */
public enum CounterSignTypeEnum {
    /*
        countersignType 0 所有会签审批人通过
        countersignType 1 其中一个会签审批人通过即可
        countersignType 2 顺序审批
        countersignType 10 无规则
    */
    COUNTER_SIGN_TYPE_ALL(0),
    COUNTER_SIGN_TYPE_ANYONE(1),
    COUNTER_SIGN_TYPE_ALL_BY_ORDER(2),
    COUNTER_SIGN_TYPE_NULL(10);

    private Integer value;

    CounterSignTypeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public static Integer parse(String str) {
        if (StringUtils.isNotEmpty(str)) {
            for (CounterSignTypeEnum e : values()) {
                if (Integer.valueOf(str).equals(e.getValue())) {
                    return e.getValue();
                }
            }
        }
        return COUNTER_SIGN_TYPE_NULL.getValue();
    }
}
