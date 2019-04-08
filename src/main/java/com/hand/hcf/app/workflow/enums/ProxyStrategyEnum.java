package com.hand.hcf.app.workflow.enums;

import org.apache.commons.lang3.StringUtils;

public enum ProxyStrategyEnum {
    /*
        1  审批
        2  知会
        3  审批&知会
        10 (不审批&不知会)无规则
     */

    PROXY_STRATEGY_AUDIT(1),
    PROXY_STRATEGY_NOTIFY(2),
    PROXY_STRATEGY_ALL(3),
    PROXY_STRATEGY_NULL(10);

    private Integer value;

    ProxyStrategyEnum(Integer value) {
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
            for (ProxyStrategyEnum e : values()) {
                if (Integer.valueOf(str).equals(e.getValue())) {
                    return e.getValue();
                }
            }
        }
        return PROXY_STRATEGY_NULL.getValue();
    }
}
