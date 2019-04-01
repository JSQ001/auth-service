package com.hand.hcf.app.workflow.brms.enums;

import lombok.AllArgsConstructor;

/**
 * Created by cuikexiang on 2017/3/15.
 */
@AllArgsConstructor
public enum RuleResult {
    REJECT(1001, "拒绝该单据成功提交"),
    WARNING(1002, "该单据可以提交，但给出提醒"),
    CALCULATE(1003, "用于计算某个值"),
    OK(1004, "OK");
     
    private int code;
    private String description;
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return "RuleResult{" + "name= '" + name() + "\'," +
            "description='" + description + '\'' +
            '}';
    }
}
