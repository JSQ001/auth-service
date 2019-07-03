package com.hand.hcf.app.expense.type.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/5/31
 */
@Data
@TableName("exp_expense_type_expand")
@AllArgsConstructor
@NoArgsConstructor

public class ExpenseTypeExpandField extends Domain {

    @TableField(value = "expense_type_id")
    private Long expenseTypeId;

    /**
     * 字段代码
     */
    @TableField(value = "field_code")
    private String fieldCode;

    /**
     * 字段名称
     */
    @TableField(value = "field_name")
    private String fieldName;
}

