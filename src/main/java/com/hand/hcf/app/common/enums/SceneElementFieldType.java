package com.hand.hcf.app.common.enums;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/1/24 16:05
 * @remark 核算接口字段对应的核算要素代码
 */
public enum SceneElementFieldType {
    /**
     * 租户
     */
    TENANT_ID("tenantId"),
    /**
     * 账套
     */
    SET_OF_BOOKS_ID("setOfBooksId"),
    /**
     * 币种
     */
    CURRENCY_CODE("currencyCode"),
    /**
     * 事务编码
     */
    DOCUMENT_NUMBER("documentNumber"),
    /**
     * 事务头ID
     */
    TRANSACTION_HEADER_ID("transactionHeaderId"),
    /**
     * 事务行ID
     */
    TRANSACTION_LINE_ID("transactionLineId"),
    /**
     * 事务分配行ID
     */
    TRANSACTION_DIST_ID("transactionDistId");

    private String fieldName;

    SceneElementFieldType(String fieldName){
        this.fieldName = fieldName;
    }

    public String getFieldName(){
        return this.fieldName;
    }
}
