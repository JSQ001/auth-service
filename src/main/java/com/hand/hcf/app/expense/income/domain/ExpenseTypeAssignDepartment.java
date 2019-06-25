package com.hand.hcf.app.expense.income.domain;

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
@TableName("exp_expense_income_department")
public class ExpenseTypeAssignDepartment extends Domain {

   @TableField(value = "id")
   private Long id;

    @TableField(value = "report_type_id")
   private Long reportTypeId;

    @TableField(value = "department_id")
    private Long departmentId;

    @TableField(exist = false)
    private String departmentName;

    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private String departmentCode;

    @TableField(exist = false)
    private String departmentOid;

    @TableField(exist = false)
    private String status;

}
