package com.hand.hcf.app.workflow.enums;

import com.hand.hcf.core.enums.SysEnum;

public enum WorkflowSettingDefaultRulesEnum implements SysEnum {
    USER(1001), // 选人
    DEPARTMENT(1002) // 部门
    ;

    private Integer id;

    WorkflowSettingDefaultRulesEnum(Integer id) {
        this.id = id;
    }

    public static WorkflowSettingDefaultRulesEnum parse(Integer id) {
        for (WorkflowSettingDefaultRulesEnum workflowSettingDefaultRulesEnum : WorkflowSettingDefaultRulesEnum.values()) {
            if (workflowSettingDefaultRulesEnum.getId().equals(id)) {
                return workflowSettingDefaultRulesEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
