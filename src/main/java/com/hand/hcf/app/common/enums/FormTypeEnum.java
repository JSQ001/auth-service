package com.hand.hcf.app.common.enums;

/**
 * 单据大类枚举
 * @author shouting.cheng
 * @date 2019/2/11
 */
public enum FormTypeEnum {
    /**
     * 对公报账单
     */
    BULLETIN_BILL("801001", "对公报账单"),
    /**
     * 预算日记账
     */
    BUDGET_JOURNAL("801002", "预算日记账"),
    /**
     * 预付款单
     */
    PREPAYMENT("801003", "预付款单"),
    /**
     * 合同
     */
    CONTRACT("801004", "合同"),
    /**
     * 付款申请单
     */
    PAYMENT_REQUISITION("801005", "付款申请单"),
    /**
     * 费用调整单
     */
    EXPENSE_ADJUSTMENT("801006", "费用调整单"),
    /**
     * 核算工单
     */
    ACCOUNTING("801008", "核算工单"),
    /**
     * 费用申请单
     */
    EXPENSE_REQUISITION("801009", "费用申请单"),
    ;

    /**
     * 单据类型代码
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    FormTypeEnum(final String code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
