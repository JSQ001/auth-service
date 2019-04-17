package com.hand.hcf.app.base.codingrule.domain.enums;


import com.hand.hcf.app.core.domain.enumeration.BusinessEnum;

/**
 * 编码规则单据类型枚举
 * Created by liudong on 2018/2/26.
 */
public enum DocumentTypeEnum implements BusinessEnum {
    //EXP_REQUISITION("EXP_REQUISITION"), // 费用申请单
    //EXP_REPORT("EXP_REPORT"), // 费用报销单
    CSH_TRANSACTION("CSH_TRANSACTION"), // 现金交易事务
    //PAYMENT_REQUISITION("PAYMENT_REQUISITION"), // 借款申请单
    BGT_JOURNAL("BGT_JOURNAL"), // 费用申请单
    CON_CONTRACT("CON_CONTRACT"), // 合同
    ACP_REQUISITION("ACP_REQUISITION"), // 付款申请单

    //JD_REQUISITION("JD_REQUISITION"), //京东申请
    //TR_REQUISITION("TR_REQUISITION"), //差旅报销
    //TA_REQUISITION("TA_REQUISITION"), //差旅申请
    //ER_REQUISITION("ER_REQUISITION"),  //报销单
    //差旅申请单
    TRAVEL_APPLICATION("TRAVEL_APPLICATION"),
    //费用申请单
    EXPENSE_APPLICATION("EXPENSE_APPLICATION"),
    //订票申请单
    TRAVEL_BOOK_APPLICATION("TRAVEL_BOOK_APPLICATION"),
    //京东申请单
    JINDDONG_ORDER_APPLICATION("JINDDONG_ORDER_APPLICATION"),
    //借款申请
    BORROW_APPLICATION("BORROW_APPLICATION"),
    //日常报销单
    NORMAL_EXPENSE_REPORT("NORMAL_EXPENSE_REPORT"),
    //差旅报销单
    TRAVEL_EXPENSE_REPORT("TRAVEL_EXPENSE_REPORT"),
    //费用报销单
    EXPENSE_EXPENSE_REPORT("EXPENSE_EXPENSE_REPORT"),
    //京东订单报销单
    JINGDONG_ORDER_EXPENSE_REPORT("JINGDONG_ORDER_EXPENSE_REPORT"),
    //供应商
    VENDER("VENDER")
    ;

    private String key;

    DocumentTypeEnum(String key){this.key = key;}

    public static DocumentTypeEnum parse(String key) {
        for (DocumentTypeEnum fieldType : DocumentTypeEnum.values()) {
            if (fieldType.getKey().equals(key)) {
                return fieldType;
            }
        }
        return null;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
