/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.hand.hcf.app.base.system.enums;

import com.hand.hcf.core.domain.enumeration.BusinessEnum;

public enum OperationTypeEnum implements BusinessEnum {
    ADD("ADD"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    IMPORT("IMPORT"),
    EXPORT("EXPORT"),
    ENABLE("ENABLE"),
    DISABLE("DISABLE"),
    INVITE("INVITE");
    private String key;

    OperationTypeEnum(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
