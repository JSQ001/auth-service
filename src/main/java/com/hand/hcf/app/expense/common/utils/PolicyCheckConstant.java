package com.hand.hcf.app.expense.common.utils;

/**
 * @author shouting.cheng
 * @date 2019/3/6
 */
public class PolicyCheckConstant {


    /**
     * 控制维度
     */
    public static final String CONTROL_DIMENSION_TYPE_AMOUNT = "AMOUNT"; //金额
    public static final String CONTROL_DIMENSION_TYPE_PRICE = "PRICE"; //单价
    public static final String CONTROL_DIMENSION_TYPE_QUANTITY = "QUANTITY"; //数量

    /**
     * 控制维度相关数据类型
     */
    public static final String VALUE_TYPE_LONG = "LONG";
    public static final String VALUE_TYPE_DATE = "DATE";
    public static final String VALUE_TYPE_TEXT = "TEXT";

    /**
     * 条件
     */
    public static final String JUDGEMENT_SYMBOL_LESS_THEN = "01"; //<
    public static final String JUDGEMENT_SYMBOL_LESS_EQUAL = "02"; //≤
    public static final String JUDGEMENT_SYMBOL_BELONG = "03"; //属于
    public static final String JUDGEMENT_SYMBOL_NOT_BELONG = "04"; //不属于

    /**
     * 控制策略值CODE
     */
    public static final String CONTROL_STRATEGY_CODE_PASS = "PASS"; //通过
    public static final String CONTROL_STRATEGY_CODE_WARNING = "WARNING"; //警告
    public static final String CONTROL_STRATEGY_CODE_FORBIDDEN = "FORBIDDEN"; //禁止
}
