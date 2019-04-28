package com.hand.hcf.app.workflow.enums;

import com.hand.hcf.app.core.enums.SysEnum;

/**
 * @author elvis.xu
 * @since 2016-07-10 17:24
 * action 执行人
 */
public enum ApprovalOperationTypeEnum implements SysEnum {
    /**
     * 自己
     */
    SELF(1001),
    /**
     * 审批人
     */
     APPROVAL(1002),
    /**
     * 财务
     */
    AUDIT(1003),
    /**
     * 开票
     */
    RECEIPT(1004),
    /**
     * 还款财务审批
     */
    REPAYMENT(1005),
    /**
     * 系统
     */
    SYSTEM(1006),
    /**
     * 订票专员
     */
    TRAVEL_BOOKER(1007),
    /**
     * 订票申请价格审核人
     */
    TRAVEL_BOOKER_PRICE_AUDITOR(1008),
    /**
     * 工作台流程日志
     */
    WORKBENCH_LOGS(1009)
    ;

    private Integer id;

    ApprovalOperationTypeEnum(Integer id) {
        this.id = id;
    }

    public static ApprovalOperationTypeEnum parse(Integer id) {
        for (ApprovalOperationTypeEnum fieldType : ApprovalOperationTypeEnum.values()) {
            if (fieldType.getId().equals(id)) {
                return fieldType;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
