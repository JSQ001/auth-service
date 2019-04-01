package com.hand.hcf.app.workflow.workflow.enums;

import com.hand.hcf.core.enums.SysEnum;

public enum WorkflowRoleType implements SysEnum {
    USER(0, "用户"), COST_CENTER_ITEM_MANAGER(1, "成本中心主管"), DEPARTMENT_MANAGER(2, "部门主管"), URL(3, "外部接口"), OWNER(4, "自审批"), USER_PICK(5, "选人审批");

    private Integer id;
    private String des;

    WorkflowRoleType(Integer id, String des) {
        this.id = id;
        this.des = des;
    }

    public static WorkflowRoleType parse(Integer id) {
        for (WorkflowRoleType e : WorkflowRoleType.values()) {
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

    public String getDes() {
        return des;
    }
}
