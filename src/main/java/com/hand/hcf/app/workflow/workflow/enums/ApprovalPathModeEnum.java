package com.hand.hcf.app.workflow.workflow.enums;


import com.hand.hcf.core.enums.SysEnum;

public enum ApprovalPathModeEnum implements SysEnum {
    FULL(1001), NEXT_ONE(1002);
    private Integer id;

    ApprovalPathModeEnum(Integer id) {
        this.id = id;
    }

    public static ApprovalPathModeEnum parse(Integer id) {
        for (ApprovalPathModeEnum applicationTypeEnum : ApprovalPathModeEnum.values()) {
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
