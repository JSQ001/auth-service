package com.hand.hcf.app.workflow.workflow.enums;

import com.hand.hcf.core.enums.SysEnum;

public enum WorkflowSettingType implements SysEnum {
    NO_CONDITION(0, "无条件"), HAS_CONDITION(1, "有条件"), OWNER_CONDITION(2, "自审批");

    private Integer id;
    private String des;

    WorkflowSettingType(Integer id, String des) {
        this.id = id;
        this.des = des;
    }

    public static WorkflowSettingType parse(Integer id) {
        for (WorkflowSettingType e : WorkflowSettingType.values()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }


}
