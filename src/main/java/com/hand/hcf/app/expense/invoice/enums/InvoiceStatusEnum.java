package com.hand.hcf.app.expense.invoice.enums;

import com.hand.hcf.app.core.enums.SysEnum;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description 发票状态
 * @date 2019/5/19 22:32
 * @version: 1.0.0
 */
public enum  InvoiceStatusEnum implements SysEnum {
    /**
     * 正常
     */
    NORMAL(0),
    /**
     * 失控
     */
    RUNAWAY(1),
    /**
     * 作废
     */
    INVALID(2),

    /**
     * 红冲
     */
    REDSTAMPING(3),

    /**
     * 异常
     */
    ABNORMAL(4),

    ;
    private Integer id;

    InvoiceStatusEnum(Integer id) {
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
    public static InvoiceStatusEnum parse(Integer id) {
        for (InvoiceStatusEnum invoiceStatus : InvoiceStatusEnum.values()) {
            if (invoiceStatus.getId().equals(id)) {
                return invoiceStatus;
            }
        }
        return null;
    }
}
