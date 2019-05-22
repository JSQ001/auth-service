package com.hand.hcf.app.expense.accrual.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 费用预提单关联部门
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/15
 */
@ApiModel(description = "费用预提单类型关联部门")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("exp_accrual_type_assign_dp")
public class ExpenseAccrualTypeAssignDepartment extends Domain {

    //费用预提单类型ID
    @ApiModelProperty(value = "费用预提单类型ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("exp_accrual_type_id")
    private Long expAccrualTypeId;

    //部门ID
    @ApiModelProperty(value = "部门ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("department_id")
    private Long departmentId;

}
