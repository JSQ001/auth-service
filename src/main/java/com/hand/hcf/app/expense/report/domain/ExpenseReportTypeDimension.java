package com.hand.hcf.app.expense.report.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @description: 报账单类型关联维度表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("exp_report_type_dimension")
public class ExpenseReportTypeDimension extends Domain {
    //报账单类型ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("report_type_id")
    private Long reportTypeId;

    //维度ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("dimension_id")
    private Long dimensionId;

    //是否必输
    @NotNull
    @TableField("must_enter")
    private Boolean mustEnter;

    //默认维值ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("default_value_id")
    private Long defaultValueId;

    //布局位置 (单据头HEADER，分摊行DIST_LINE)
    @NotNull
    @TableField("position")
    private String position;

    //优先级
    @NotNull
    @TableField("sequence_number")
    private Integer sequenceNumber;



    //维度名称
    @TableField(exist = false)
    private String dimensionName;

    //默认维值名称
    @TableField(exist = false)
    private String defaultValueName;
}
