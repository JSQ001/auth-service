package com.hand.hcf.app.expense.common.utils;

/**
 * <p>
 *  参数指定涉及到的常量
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/2/28
 */
public class ParameterConstant {

    /**
     * 单据关闭时 预算释放的期间 的参数代码
     */
    public final static String BGT_CLOSED_PERIOD = "BGT_CLOSED_PERIOD";

    /**
     * 当前期间
     */
    public final static String CURRENT_PERIOD = "CURRENT_PERIOD";

    /**
     * 预算占用期间
     */
    public static final String BGT_OCCUPY_DATE = "BGT_OCCUPY_DATE";
    /**
     * 预算占用期间 - 单据提交期间
     */
    public static final String SUBMIT_DATE = "SUBMIT_DATE";
    /**
     * 预算占用期间 - 费用发生期间
     */
    public static final String EXPENSE_DATE = "EXPENSE_DATE";

    /**
     * 分税率核算控制 Y/N
     */
    public static final String EXP_TAX_RATE_ACCOUNT = "EXP_TAX_RATE_ACCOUNT";

    /**
     * 税金分摊方式 TAX_IN/TAX_OFF
     */
    public static final String EXP_TAX_DIST = "EXP_TAX_DIST";

    /**
     * 按含税金额分摊
     */
    public static final String TAX_IN = "TAX_IN";

    /**
     * 按不含税金额分摊
     */
    public static final String TAX_OFF = "TAX_OFF";
}
