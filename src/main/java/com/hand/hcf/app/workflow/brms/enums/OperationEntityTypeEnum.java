package com.hand.hcf.app.workflow.brms.enums;


import com.hand.hcf.app.core.domain.enumeration.BusinessEnum;

public enum OperationEntityTypeEnum implements BusinessEnum {
    APPROVAL_CHAIN("APPROVAL_CHAIN"),
    APPROVAL_NODE("APPROVAL_NODE"),
    APPROVAL_APPROVER("APPROVAL_APPROVER"),
    APPROVAL_DETAIL("APPROVAL_DETAIL");
    private String key;

    OperationEntityTypeEnum(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
