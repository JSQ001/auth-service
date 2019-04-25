package com.hand.hcf.app.payment.domain.enumeration;


/**
 * @author chengshouting
 */

public enum PaymentAssignUserEnum {
    USER_ALL("BASIS_01", "全部人员") , USER_DEPARTMENT("BASIS_02", "按部门分配"), USER_GROUP("BASIS_03", "按人员组");


    /**
     * 主键
     */
    private final String key;

    /**
     * 描述
     */
    private final String desc;

    PaymentAssignUserEnum(final String key, final String desc) {
        this.key = key;
        this.desc = desc;
    }


    public static PaymentAssignUserEnum getType(String key) {
        PaymentAssignUserEnum[] its = PaymentAssignUserEnum.values();
        for (PaymentAssignUserEnum it : its) {
            if (it.getKey().equals(key)) {
                return it;
            }
        }
        return USER_ALL;
    }

    public String getKey() {
        return this.key;
    }

    public String getDesc() {
        return this.desc;
    }
}
