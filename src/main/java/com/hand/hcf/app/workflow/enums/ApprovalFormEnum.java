package com.hand.hcf.app.workflow.enums;

import com.hand.hcf.app.core.enums.SysEnum;

/**
 *
 */
public enum ApprovalFormEnum implements SysEnum {
    CUSTOMER_FROM_COMPANY(1),//表单来源公司
    CUSTOMER_FROM_TENANT(2),//表单来源账套
    VISIBLE_COMPANY_SCOPE_ALL(1),//分配给所有公司
    VISIBLE_COMPANY_SCOPE_NOT_ALL(2)//分配给一部分公司
    ;


    private Integer id;
    ApprovalFormEnum(Integer id){
        this.id = id;
    }


    @Override
    public Integer getId() {
        return this.id;
    }
}
