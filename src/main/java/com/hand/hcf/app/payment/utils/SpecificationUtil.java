package com.hand.hcf.app.payment.utils;


/**
 * 此util用于规范代码
 * Created by 刘亮 on 2017/9/30.
 */
public class SpecificationUtil {

    /**
     * 1.单据类型系统代码定义：
     */
    public final static  String ACP_REQUISITION = "ACP_REQUISITION"; // 付款申请单

    public final static String PUBLIC_REPORT = "PUBLIC_REPORT"; // 对公报账单

    public final static String PREPAYMENT_REQUISITION = "PREPAYMENT_REQUISITION"; // 预付款单

    /**
     * 2.付款方式类型系统代码
     */
    public final static  String ONLINE_PAYMENT = "ONLINE_PAYMENT"; // 线上

    public final static  String OFFLINE_PAYMENT = "OFFLINE_PAYMENT"; // 线下

    public final static String EBANK_PAYMENT = "EBANK_PAYMENT"; // 落地文件

    /**
     *   3.收方类型系统代码
     */
    public final static  String EMPLOYEE = "EMPLOYEE"; // 员工

    public final static  String VENDER = "VENDER"; // 供应商

    /**
     *  4.支付状态（通用支付明细表)
     */
    public final static String NOPAY = "N"; // 未支付

    public final static String PARTPAY = "P";  // 部分支付

    public final static  String FULLPAY = "T"; // 完全支付
    /** 
    * 5.支付明细状态
    */
    public final static String NEWPAY = "N"; // 新建

    public final static String TOPAY = "W"; // 待支付

    public final static String PAYING = "P"; // 支付中

    public final static String PAYSUCCESS = "S"; // 支付成功

    public final static String PAYFAILURE = "F"; // 支付失败

    public final static String REPAY = "R"; // 重新支付

    public final static String CANCE = "C"; // 取消支付

    /**
     * 6.退票状态
     */
    public final static String NOREFUND = "N"; // 未退票

    public final static String YESREFUND = "Y"; // 已退票

    /**
     * 7.退款状态
     */
    public final static String NORETURN = "1"; // 未退款

    public final static String PARTRETURN = "2"; // 部分退款

    public final static String YESRETURN = "3"; // 已退款

    /**
     * 8.支付反冲状态
     */
    public final static String NO_RESERVED = "1"; // 未反冲

    public final static String PART_RESERVED = "2"; // 部分反冲

    public final static String IS_RESERVED = "3"; // 部分反冲

    /**
     * 9.支付日志操作状态
     */
    public  final static String LOG_NEW = "NEW"; // 新增

    public final static String LOG_REPAY = "REPAY"; // 重新支付

    public final static String LOG_PEND_PAY="PEND_PAY";

    public final static String LOG_ENSURE_FAIL = "ENSURE_FAIL"; //确认失败

    public final static String LOG_ENSURE_SUCCESS = "ENSURE_SUCCESS";// 确认成功

    public final static String LOG_PAY_REFUND = "PAY_REFUND"; // 退票

    public final static String LOG_PAY_RETURN = "PAY_RETURN"; // 退款

    public final static String LOG_PAY_RESERVED = "PAY_RESERVED"; // 反冲


    /**
     * 10.支付操作类型
     */
    public final static String PAYMENT = "payment"; // 支付

    public final static String RETURN = "return";   // 退款

    public final static String REFUND = "refund";   // 退票

    public final static String RESERVED = "reserved";// 反冲

}
