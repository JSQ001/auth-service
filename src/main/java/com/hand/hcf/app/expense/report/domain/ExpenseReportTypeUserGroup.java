package com.hand.hcf.app.expense.report.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 报账单类型关联人员组表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("exp_report_type_user_group")
public class ExpenseReportTypeUserGroup {
    //主键ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id")
    private Long id;

    //报账单类型ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("report_type_id")
    private Long reportTypeId;

    //人员组ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("user_group_id")
    private Long userGroupId;
}
