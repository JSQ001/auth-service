package com.hand.hcf.app.workflow.enums;

import org.apache.commons.lang3.StringUtils;

public enum FilterTypeRuleEnum {

    /*
        1   对比审批历史
        2   对比当前审批链
        4   不过滤(不跳过)
        10  无规则
    */
    FILTER_TYPE_RULE_HISTORY_CHAIN(1),
    FILTER_TYPE_RULE_CURRENT_CHAIN(2),
    FILTER_TYPE_RULE_NOT_FILTER(4),
    FILTER_TYPE_RULE_NULL(10);

    private Integer value;

    FilterTypeRuleEnum(Integer value) {
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
            for (FilterTypeRuleEnum e : values()) {
                if (Integer.valueOf(str).equals(e.getValue())) {
                    return e.getValue();
                }
            }
        }
        //特殊说明:业务上要求缺省值为 —— 不跳过,所以理论上不会存在10无规则的情况,除非改库
        return FILTER_TYPE_RULE_NOT_FILTER.getValue();
    }
}
