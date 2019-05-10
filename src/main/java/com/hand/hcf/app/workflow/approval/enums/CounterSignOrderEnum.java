package com.hand.hcf.app.workflow.approval.enums;

import lombok.AllArgsConstructor;

/**
 * 加签顺序
 *
 * @author polus
 */
@AllArgsConstructor
public enum CounterSignOrderEnum {
    //节点之前
    BEFORE(0),
    //平行
    PARALLEL(1),
    //节点之后
    AFTER(2);

    private Integer value;


    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public static Integer parse(Integer value) {
        for (CounterSignOrderEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e.getValue();
            }
        }

        return BEFORE.getValue();
    }
}
