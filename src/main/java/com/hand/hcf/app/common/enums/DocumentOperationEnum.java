package com.hand.hcf.app.common.enums;


import com.hand.hcf.core.enums.SysEnum;

/**
 * @description: 单据操作状态
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2018/1/24 14:43
 */
public enum DocumentOperationEnum implements SysEnum {
    GENERATE(1001)//编辑中
    , APPROVAL(1002)//审批中
    , WITHDRAW(1003) // 撤回
    , APPROVAL_PASS(1004) // 审批通过
    , APPROVAL_REJECT(1005) // 审批驳回
    ;

    private Integer id;

    DocumentOperationEnum(Integer id) {
        this.id = id;
    }

    public static DocumentOperationEnum parse(Integer id) {
        for (DocumentOperationEnum fieldType : DocumentOperationEnum.values()) {
            if (fieldType.getId().equals(id)) {
                return fieldType;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

}
