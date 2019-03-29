package com.hand.hcf.app.common.enums;

/**
 * Created by kai.zhang on 2017-12-26.
 * 来源事务接口数据结构
 */
public enum SourceTransactionTypeDataStructure {
    HEADER,         //报销单头
    LINE,           //报销单行
    DIST,           //报销单分配行
    INVOICE,       //报销单发票行
    SCHEDULE,       //报销单计划付款行

    PAYMENT_DETAIL,        //支付明细数据

    WRITE_OFF_DETAIL,         //核销明细
}
