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
 * @description: 费用预提单关联维度表
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/15
 */
@ApiModel(description = "费用预提单类型关联维度表")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "exp_accrual_dimension")
public class ExpenseAccrualDimension extends Domain {

    /**
     费用预提单类型ID 
     */
    @ApiModelProperty(value = "费用预提单类型ID")
    @TableField(value = "exp_accrual_type_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expAccrualTypeId;

    /**
     维度ID 
     */
    @ApiModelProperty(value = "维度ID")
    @TableField(value = "dimension_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimensionId;

    @TableField(exist = false)
    @ApiModelProperty(value = "维度名称")
    private String dimensionName;
    /**
     排序 
     */
    @ApiModelProperty(value = "排序")
    @TableField(value = "sequence")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer sequence;

    /**
     默认值 
     */
    @ApiModelProperty(value = "默认值")
    @TableField(value = "default_value")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long defaultValue;

    @TableField(exist = false)
    @ApiModelProperty(value = "值名称")
    private String valueName;
    /**
     布局位置 
     */
    @ApiModelProperty(value = "布局位置")
    @TableField(value = "header_flag")
    private Boolean headerFlag;

    /**
     是否必输 
     */
    @ApiModelProperty(value = "是否必输")
    @TableField(value = "required_flag")
    private Boolean requiredFlag;
}
