package com.hand.hcf.app.expense.adjust.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("exp_adjust_type_assign_u_g")
public class ExpenseAdjustTypeAssignUserGroup extends Domain {


    //费用调整单类型ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("exp_adjust_type_id")
    private Long expAdjustTypeId;

    //人员组ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("user_group_id")
    private Long userGroupId;
}
