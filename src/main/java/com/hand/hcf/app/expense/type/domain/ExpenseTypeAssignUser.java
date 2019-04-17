package com.hand.hcf.app.expense.type.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *     费用/申请类型适用人员
 *     该表若无值则默认为全部人员
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/12
 */
@TableName("exp_expense_type_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpenseTypeAssignUser extends Domain {

    /**
     * 费用类型
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expenseTypeId;

    /**
     * 适用类型
     */
    private Integer applyType;

    /**
     * 适用ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userTypeId;

    @TableField(exist = false)
    private String name;
}
