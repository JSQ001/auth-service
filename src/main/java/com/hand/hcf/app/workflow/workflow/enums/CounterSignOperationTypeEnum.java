package com.hand.hcf.app.workflow.workflow.enums;

/**
 * 加签记录保存伴随的操作枚举
 * Created by caixiang on 2017/12/28.
 */
public enum CounterSignOperationTypeEnum {

    //提交时加签
    COUNTER_SIGN_OPERATION_TYPE_CREATED_BY_SUBMIT(1),
    //审批时加签
    COUNTER_SIGN_OPERATION_TYPE_CREATED_BY_APPROVE(2),
    //审批时使用
    COUNTER_SIGN_OPERATION_TYPE_USED_BY_APPROVE(3);

    CounterSignOperationTypeEnum(Integer value) {
        this.value = value;
    }

    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
