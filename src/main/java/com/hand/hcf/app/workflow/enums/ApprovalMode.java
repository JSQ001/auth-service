package com.hand.hcf.app.workflow.enums;

import com.hand.hcf.core.enums.SysEnum;

/**
 * Created by lichao on 2016/7/27.
 * 审批模式
 */
public enum ApprovalMode implements SysEnum {
    COST_CENTER(1001),//成本中心
    DEPARTMENT(1002),//部门
    USER_PICK(1003),//选人审批
    MIXED(1004),//混合
    CUSTOM(1005),//自定义
    YING_FU_USER_PICK(1006);//英孚选人模式
    private Integer id;

    ApprovalMode(Integer id) {
        this.id = id;
    }

    public static ApprovalMode parse(Integer id) {
        for (ApprovalMode mode : ApprovalMode.values()) {
            if (mode.getId().equals(id)) {
                return mode;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
