package com.hand.hcf.app.workflow.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by caixiang on 2017/12/25.
 */
@Data
@NoArgsConstructor
public class ApprovalFormPropertyRuleDTO implements Serializable{

    //表单Oid
    private UUID formOid;
    //允许自选审批人加签(提交加签)
    private Boolean enableCounterSignForSubmitter;
    //允许审批加签
    private Boolean enableCounterSign;
    //自选审批人加签规则(提交加签规则)
    private Integer counterSignRuleForSubmitter;
    //审批加签规则
    private Integer counterSignRule;
    //重复审批人过滤域规则
    private Integer filterTypeRule;
    //重复审批人过滤规则
    private Integer filterRule;


    //金额变大，开启过滤
    private boolean enableAmountFilter;
    //费用类型发生变化，开启过滤
    private boolean enableExpenseTypeFilter;
    //被代理人策略
    private Integer proxyStrategy;
    //加签选人范围
    private ApprovalAddSignScopeDTO approvalAddSignScope;

}
