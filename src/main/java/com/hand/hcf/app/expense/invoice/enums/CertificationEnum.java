package com.hand.hcf.app.expense.invoice.enums;

import com.hand.hcf.app.core.enums.SysEnum;

/**
 * 发票认证枚举
 * @author shouting.cheng
 * @date 2019/3/4
 */
public enum CertificationEnum implements SysEnum {
    /**
     * 未认证
     */
    UNCERTIFIED(0),
    /**
     * 待认证
     */
    CERTIFICATION(1),
    /**
     * 系统认证
     */
    SYS_CERTIFICATION(2),

    /**
     * 第三方认证
     */
    THIRD_CERTIFICATION(3),

    /**
     * 认证成功
     */
     SUCCESS(4),

    /**
     * 认证失败
     */
    FAIL(5)
    ;
    private Integer id;

    CertificationEnum(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
