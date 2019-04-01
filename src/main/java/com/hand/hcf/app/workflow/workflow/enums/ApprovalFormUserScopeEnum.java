package com.hand.hcf.app.workflow.workflow.enums;

import com.hand.hcf.core.enums.SysEnum;

public enum ApprovalFormUserScopeEnum implements SysEnum {
    ALL_PEOPLE(1001),//所有公司员工
    USER_GROUP(1002),//用户组
    DEPARTMENT(1003),//部门
    BY_APPLICATION(1004),//和关联的申请单一致
    ;


    private Integer id;
    ApprovalFormUserScopeEnum(Integer id){
        this.id = id;
    }

    public static ApprovalFormUserScopeEnum parse(Integer id) {
        for (ApprovalFormUserScopeEnum fieldType : ApprovalFormUserScopeEnum.values()) {
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
