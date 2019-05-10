package com.hand.hcf.app.workflow.approval.enums;

import lombok.AllArgsConstructor;

/**
 * 加签审批顺序
 *
 * @author polus
 */
@AllArgsConstructor
public enum ApprovalOrderEnum {
    //按加签顺序审批
    ORDER(0),
    //平行
    PARALLEL(1);

    private Integer value;


    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public static Integer parse(Integer value) {
        for (ApprovalOrderEnum e : values()) {
            if (e.getValue().equals(value)) {
                return e.getValue();
            }
        }

        return ORDER.getValue();
    }
}
