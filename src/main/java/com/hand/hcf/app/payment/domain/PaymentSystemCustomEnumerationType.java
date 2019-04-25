package com.hand.hcf.app.payment.domain;

/**
 * @description: 系统值列表说明
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017/11/2 15:32
 */

public class PaymentSystemCustomEnumerationType {
    //预算模块值列表
    public static final String BGT_VERSION_STATUS = "2001";//版本状态
    public static final String BGT_PERIOD_STRATEGY = "2002";//编制期段
    public static final String BGT_LAYOUT_POSITION = "2003";//布局位置
    public static final String BGT_VARIATION_ATTRIBUTE = "2004";//预算项目变动属性
    public static final String BGT_STRATEGY_METHOD = "2005";//预算控制方法
    public static final String BGT_STRATEGY_TYPE = "2006";//预算控制策略类型
    public static final String BGT_STRATEGY_RANGE = "2007";//预算控制策略范围
    public static final String BGT_STRATEGY_OBJECT = "2008";//预算控制策略对象
    public static final String BGT_STRATEGY_MANNER = "2009";//预算控制策略方式
    public static final String BGT_STRATEGY_OPERATOR = "2010";//预算控制策略预算符
    public static final String BGT_STRATEGY_PERIOD = "2011";//控制期段
    public static final String BGT_RULE_PARAMETER_TYPE = "2012";//规则参数类型
    public static final String BGT_FILTRATE_METHOD = "2013";//取值方式
    public static final String BGT_CONTROL_RULE_RANGE = "2014";//取值范围
    public static final String BGT_RULE_PARAMETER_BUDGET = "2015";//规则参数类型_预算相关
    public static final String BGT_RULE_PARAMETER_ORG = "2016";//规则参数类型_组织相关
    public static final String BGT_RULE_PARAMETER_DIM = "2017";//规则参数类型_维度相关
    public static final String BGT_BUSINESS_TYPE = "2018";//预算业务类型
    public static final String BGT_QUANTITY_AMOUNT = "2019";//金额/数量
    public static final String BGT_PERIOD_SUMMARY_FLAG = "2020";//期间汇总标志
    public static final String BGT_QUARTER = "2021";//预算季度
    public static final String BGT_CONTROL_MESSAGE = "2022";//预算控制消息
    public static final String BGT_CODING_RULE_DOC_CATEGORY = "2023";//单据类别
    public static final String BGT_CODING_RULE_RESET_FREQUENCY = "2024";//重置频率
    public static final String BGT_CODING_RULE_SEGMENT = "2025";//段值
    public static final String BGT_CODING_RULE_DATE_FORMAT = "2026";//日期格式
    public static final String BGT_SOURCE_CATEGORY_OF_DOCUMENT = "2027";//单据来源类别
    public static final String BGT_BUDGET_JOURNAL_STATUS = "2028";//预算日记账状态

    //支付模块值列表
    public static final String CSH_RATE_METHOD = "2101";//汇率方法
    public static final String CSH_RATE_QUOTATION = "2102";//汇率标价方法
    public static final String CSH_BANK_TYPE = "2103";//银行类型
    public static final String CSH_TRANSACTION_TYPE = "2104";//现金交易事务类型
    public static final String CSH_PAYMENT_TYPE = "2105";//付款方式类型
    public static final String CSH_DOCUMENT_TYPE = "2106";//单据类别
    public static final String CSH_PARTNER_CATEGORY = "2107";//收款方类型
    public static final String CSH_DATA_PAYMENT_STATUS = "2108";//通用待付信息付款状态
    public static final String CSH_PAYMENT_STATUS = "2109";//付款状态
    public static final String CSH_REFUND_TYPE = "2110";//退款状态
    public static final String CSH_LOG_OPERATION_TYPE = "2111";//支付日志操作类型
    //付款申请单状态
    public static final String ACP_REQUISITION_STATUS = "2208";//付款申请单状态

    //合同模块值列表
    public static final String CON_STATUS = "2201";//CON_STATUS
    public static final String CON_CATEGORY = "2202";//CON_CATEGORY


}
