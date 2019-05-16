package com.hand.hcf.app.workflow.approval.enums;

import com.hand.hcf.app.core.enums.SysEnum;

/**

 /**
 * 是否加签枚举
 * 类型: 0-不是加签, 1-是加签
 */
public enum IsAddSignEnum implements SysEnum {
    /**
     * 0-不是加签
     */
    SIGN_NO(0),
    /**
     * 1-是加签
     */
    SIGN_YES(1);
    private Integer id;

    IsAddSignEnum(Integer id) {
        this.id = id;
    }

    public static IsAddSignEnum parse(Integer id) {
        for (IsAddSignEnum rejectTypeEnum : IsAddSignEnum.values()) {
            if (rejectTypeEnum.getId().equals(id)) {
                return rejectTypeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
