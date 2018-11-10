package com.hand.hcf.app.base.domain.enumeration;

/**
 * /**
 * 类实体类型枚举
 * Author: elvis.xu
 * since: 2016-07-29
 */
public enum EntityTypeEnum implements SysEnum {
    APPLICATION(1001), // 申请
    EXPENSE_REPORT(1002), // 报销单
    COMPANY(1003),
    USER(1004),
    USER_GROUP(1005),
    DEPARTMENT(1006),
    COST_CENTER(1007),
    REPAYMENT(1008),//还款单
    INVOICE(1009),
    CONTACT(1010),
    SYSTEM(2001),
    TENANT(2003),    // 租户
    BOOK_TASK(2002), //订票任务  TravelOperationRecord
    BUDGET_JOURNAL(801002),//预算日记账
    CONTRACT(801004) ,//合同单据
    PREPAYMENT(801003);//预付款单
    private Integer id;

    EntityTypeEnum(Integer id) {
        this.id = id;
    }

    public static EntityTypeEnum parse(Integer id) {
        for (EntityTypeEnum rejectTypeEnum : EntityTypeEnum.values()) {
            if (rejectTypeEnum.getID().equals(id)) {
                return rejectTypeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getID() {
        return this.id;
    }
}
