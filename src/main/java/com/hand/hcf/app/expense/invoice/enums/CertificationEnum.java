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
    CERTIFYING(1),
    /**
     * 系统认证
     */
    SYSCERTIFYING(2),

    /**
     * 第三方认证
     */
    OTHERCERTIFYING(3),

    /**
     * 认证成功
     */
    CERTIFIED(4),

    /**
     * 认证失败
     */
    FAILEDCERTIFIED(5)
    ;
    private Integer id;

    CertificationEnum(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return this.id;
    }


    /**
     * 根据id返回类型
     * @param id
     * @return
     */
    public static CertificationEnum parse(Integer id) {
        for (CertificationEnum certificationEnum : CertificationEnum.values()) {
            if (certificationEnum.getId().equals(id)) {
                return certificationEnum;
            }
        }
        return null;
    }
}
