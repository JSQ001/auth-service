package com.hand.hcf.app.workflow.enums;

import lombok.AllArgsConstructor;

/**
 * 退回审批类型
 *
 * @author polus
 */
@AllArgsConstructor
public enum BackTypeEnum {
    //从退回节点重新开始
    START(0),
    //直接调回当前节点
    CURRENT(1);

    private Integer value;


    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public static Integer parse(Integer value) {
        for (BackTypeEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e.getValue();
            }
        }

        return START.getValue();
    }
}
