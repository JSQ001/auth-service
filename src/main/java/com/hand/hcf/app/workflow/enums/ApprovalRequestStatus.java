package com.hand.hcf.app.workflow.enums;

import com.hand.hcf.app.core.enums.SysEnum;

public enum ApprovalRequestStatus implements SysEnum {
    VAILD(1), INVAILD(0);
    private Integer id;

    ApprovalRequestStatus(Integer id) {
        this.id = id;
    }

    public static ApprovalRequestStatus parse(Integer id) {
        for (ApprovalRequestStatus currencyCode : ApprovalRequestStatus.values()) {
            if (currencyCode.getId().equals(id)) {
                return currencyCode;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

}
