package com.hand.hcf.app.workflow.workflow.enums;

import com.hand.hcf.core.enums.SysEnum;

public enum ApprovalChainStatusEnum implements SysEnum {
    NORMAL(1000), INVALID(1001) ,CHANGED(1003);
    private Integer id;

    ApprovalChainStatusEnum(Integer id) {
        this.id = id;
    }

    public static ApprovalChainStatusEnum parse(Integer id) {
        for (ApprovalChainStatusEnum applicationTypeEnum : ApprovalChainStatusEnum.values()) {
            if (applicationTypeEnum.getId().equals(id)) {
                return applicationTypeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
