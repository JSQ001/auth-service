package com.hand.hcf.app.expense.policy.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogic;
import lombok.Data;

/**
 * @description: 费用政策动态字段表
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/1/29 10:32
 */
@Data
@TableName(value = "exp_policy_dynamic_field")
public class ExpensePolicyDynamicField extends DomainLogic {
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
     * FIELD_ID
     */
    private Long fieldId;
    /**
     * 动态字段类型
     */
    private String fieldType;
    /**
     * 动态字段名
     */
    @TableField(exist = false)
    private String name;
    /**
     * 值
     */
    private String value;
    /**
     * 动态字段关联属性
     */
    @TableField(exist = false)
    private ExpensePolicyFieldProperty expensePolicyFieldProperty;
}
