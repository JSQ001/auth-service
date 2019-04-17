package com.hand.hcf.app.expense.policy.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogic;
import lombok.Data;

/**
 * @description: 公司关联费用政策表
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/1/29 10:46
 */
@Data
@TableName(value = "exp_policy_related_company")
public class ExpensePolicyRelatedCompany extends DomainLogic {
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
     * 公司ID
     */
    private Long companyId;
    /**
     * 公司名称
     */
    @TableField(exist = false)
    private String companyName;
}
