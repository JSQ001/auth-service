package com.hand.hcf.app.workflow.enums;

import org.apache.commons.lang3.StringUtils;

public enum FilterRuleEnum {

    /*
        filterRule = 1  审批人过滤
        filterRule = 2  加签审批人过滤
        filterRule = 3  所有重复审批人过滤
        filterRule = 10 无规则
     */

    FILTER_RULE_APPROVER(1),
    FILTER_RULE_ADD_SIGN_APPROVER(2),
    FILTER_RULE_ALL_APPROVER(3),
    FILTER_RULE_NULL(10);

    private Integer value;

    FilterRuleEnum(Integer value) {
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
            for (FilterRuleEnum e : values()) {
                if (Integer.valueOf(str).equals(e.getValue())) {
                    return e.getValue();
                }
            }
        }
        return FILTER_RULE_NULL.getValue();
    }
}
