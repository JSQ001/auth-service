package com.hand.hcf.app.workflow.enums;

import com.hand.hcf.core.enums.SysEnum;

/**

 /**
 * 驳回类型枚举
 * Author: zhangyuxue
 * Date: 2016-07-29
 * Time:15:43
 * 驳回类型: 1000-正常, 1001-撤回, 1002-审批驳回
 */
public enum RejectTypeEnum implements SysEnum {
    /**
     * 1000-正常
     */
    NORMAL(1000),
    /**
     * 1001-撤回
     */
    WITHDRAW(1001),
    /**
     * 1002-审批驳回
     */
    APPROVAL_REJECT(1002),
    /**
     * 1003-审核驳回
     */
    AUDIT_REJECT(1003),
    /**
     * 1004-开票驳回
     */
    RECEIPT_REJECT(1004);
    private Integer id;

    RejectTypeEnum(Integer id) {
        this.id = id;
    }

    public static RejectTypeEnum parse(Integer id) {
        for (RejectTypeEnum rejectTypeEnum : RejectTypeEnum.values()) {
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
