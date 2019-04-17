package com.hand.hcf.app.base.codingrule.domain.enums;


import com.hand.hcf.app.core.enums.SysEnum;

/**
 * Date:2018/1/30
 * Create By:dong.liu01@hand-china.com
 */
public enum CodingRuleTypeEnum implements SysEnum {

    DOCUMENT_TYPE(2023),    //编码规则定义-单据类型
    RESET_FREQUENCE(2024),  //编码规则-重置频率
    SEGMENT_TYPE(2025);     //规则明细-段

    private int id;

    CodingRuleTypeEnum(int id) {
        this.id = id;
    }

    public static CodingRuleTypeEnum parse(int id) {
        for (CodingRuleTypeEnum batchOperationTypeEnum : CodingRuleTypeEnum.values()) {
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
