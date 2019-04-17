package com.hand.hcf.app.expense.type.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exp_expense_type_company")
public class ExpenseTypeAssignCompany extends Domain {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long expenseTypeId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    @TableField(exist = false)
    private String companyName;

}
