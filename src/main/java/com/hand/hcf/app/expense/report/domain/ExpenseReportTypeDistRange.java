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

import javax.validation.constraints.NotNull;

/**
 * @description: 报账单类型分摊范围表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/3
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("exp_report_type_dist_range")
public class ExpenseReportTypeDistRange {
    //主键ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id")
    private Long id;

    //报账单类型ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("report_type_id")
    private Long reportTypeId;

    //分摊项 (公司：COMPANY；部门：DEPARTMENT；责任中心：RESPONSIBILITY_CENTER)
    @NotNull
    @TableField("dist_dimension")
    private String distDimension;

    //分摊项ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("value_id")
    private Long valueId;
}
