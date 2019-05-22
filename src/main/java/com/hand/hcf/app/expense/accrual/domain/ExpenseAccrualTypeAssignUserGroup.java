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
 * @description: 费用预提单关联人员组
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/15
 */
@ApiModel(description = "费用预提单关联人员组")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("exp_accrual_type_asg_u_g")
public class ExpenseAccrualTypeAssignUserGroup extends Domain {

    //费用调整单类型ID
    @ApiModelProperty(value = "费用调整单类型ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("exp_accrual_type_id")
    private Long expAccrualTypeId;

    //人员组ID
    @ApiModelProperty(value = "人员组ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("user_group_id")
    private Long userGroupId;

}
