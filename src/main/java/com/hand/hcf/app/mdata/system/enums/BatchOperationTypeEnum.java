package com.hand.hcf.app.mdata.system.enums;


import com.hand.hcf.core.enums.SysEnum;

public enum BatchOperationTypeEnum implements SysEnum {
    COMPANY(1001),
    COST_CENTER(1002),
    USER(1003),
    CONTACT_BANK_ACCOUNT(1004),
    CONTACT_CARD(1005),
    CUSTOM_ENUMERATION_ITEM(1007),
    PARTICIPANTS(1008),
    COST_CENTER_ITEM(1010),
    DEPARTMENT_POSITION_USER(1015),
    SOLR_USER_INDEX(3001),
    ES_SETOFBOOKS_INFO(3020),
    REIMBURSEMENT_BATCH(2023),
    MUTI_SETOFBOOKS_CURRENCY(2024),
    CAROUSEL_DEPLOY(3002),
    LEVEL_DEPLOY(3003),
    CUSTOM_ENUMERATION_DEPLOY(3004),
    ES_BANK_INFO(3005),
    USER_LOGIN_BIND(3006),
    MOBILE_VALIDATE(3007),
    TENANT_CODE(3008),
    ES_COMPANY_INFO(3009),
    ES_ENUMERA_INFO(3010),
    ES_DEPARTMENT_INFO(3011),
    HAND_USER_GROUP(3012),
    TENANT_CONTACT_CUSOMT_FORM(3013),
    BANK_INFO(1012);



    private int id;

    BatchOperationTypeEnum(int id) {
        this.id = id;
    }

    public static BatchOperationTypeEnum parse(int id) {
        for (BatchOperationTypeEnum batchOperationTypeEnum : BatchOperationTypeEnum.values()) {
            if (batchOperationTypeEnum.getId() == id) {
                return batchOperationTypeEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
