package com.hand.hcf.app.expense.common.domain.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;

/**
 * <p>
 *  单据类型枚举类
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/5Document
 */
public enum ExpenseDocumentTypeEnum implements IEnum {
    /* 单据类型枚举*/
    PUBLIC_REPORT(801001, "PUBLIC_REPORT","报账单") ,
    EXPENSE_ADJUST(801006, "ADJUST_TYPE","费用调整单"),
    EXP_REQUISITION(801009, "EXP_REQUISITION","费用申请单"),
    TRAVEL_APPLICATION(801010, "TRAVEL_APPLICATION","差旅申请单"),
    ACCOUNT_BOOK(80100101,"ACCOUNT_BOOK", "账本"),
    EXP_INPUT_TAX(9090,"EXP_INPUT_TAX", "进项税单据");


    /**
     * 主键
     */
    private final Integer key;

    private final String category;
    /**
     * 描述
     */
    private final String desc;

    ExpenseDocumentTypeEnum(final Integer key, final String category, final String desc) {
        this.key = key;
        this.desc = desc;
        this.category = category;
    }


    public static ExpenseDocumentTypeEnum getType(Integer key) {
        ExpenseDocumentTypeEnum[] its = ExpenseDocumentTypeEnum.values();
        for (ExpenseDocumentTypeEnum it : its) {
            if (it.getKey().equals(key)) {
                return it;
            }
        }
        return PUBLIC_REPORT;
    }

    public Integer getKey() {
        return this.key;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getCategory() {
        return this.category;
    }

    @Override
    public Serializable getValue() {
        return this.key;
    }}
