package com.hand.hcf.app.common.enums;

/**
 * Created by kai.zhang on 2017-12-26.
 * 需与值列表 SOURCE_TRANSACTION_TYPE 一一对应
 */
public enum SourceTransactionType {
    EXP_REPORT,                //对公报销
    EMP_EXP_REPORT,           //员工报销
    CSH_PREPAYMENT,           //预付款单
    CSH_EMP_LOAN,             //借款单
    ACP_PAYMENT,              //付款单
    CSH_PAYMENT,              //支付平台
    CSH_RECEIPT,              //收款单
    CSH_WRITE_OFF,            //核销
    EXP_REVERSE,       //对公报账反冲
}
