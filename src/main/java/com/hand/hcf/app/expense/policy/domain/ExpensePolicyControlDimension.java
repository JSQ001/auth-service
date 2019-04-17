package com.hand.hcf.app.expense.policy.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogic;
import lombok.Data;

/**
 * @description: 费用政策控制维度值表
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/1/29 10:32
 */
@Data
@TableName(value = "exp_policy_control_dimension")
public class ExpensePolicyControlDimension extends DomainLogic {
    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 账套ID
     */
    private Long setOfBooksId;
    /**
     * 政策定义表ID
     */
    private Long expExpensePolicyId;
    /**
     * value
     */
    private String value;

}
