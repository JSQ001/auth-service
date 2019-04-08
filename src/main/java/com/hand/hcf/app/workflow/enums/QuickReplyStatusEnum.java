package com.hand.hcf.app.workflow.enums;

import com.hand.hcf.core.enums.SysEnum;

/**
 * Created By 魏建 ON 2017/12/25.
 */
public enum QuickReplyStatusEnum implements SysEnum {
    NORMAL(1000), INVALID(1001);
    private Integer id;
    QuickReplyStatusEnum(Integer id) {
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
