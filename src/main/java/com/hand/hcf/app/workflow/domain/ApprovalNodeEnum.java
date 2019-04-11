package com.hand.hcf.app.workflow.domain;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/11.
 */
public enum ApprovalNodeEnum {
    SUBMIT_NODE("提交"),
    END_NODE("结束");
    private String name;
    ApprovalNodeEnum(String name) {
        this.name = name;
    }
    public static ApprovalNodeEnum parse(String name) {
        for (ApprovalNodeEnum mode : ApprovalNodeEnum.values()) {
            if (mode.getName().equals(name)) {
                return mode;
            }
        }
        return null;
    }
    public String getName() {
        return this.name;
    }
}
