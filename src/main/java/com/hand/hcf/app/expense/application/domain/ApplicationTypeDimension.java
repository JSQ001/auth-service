package com.hand.hcf.app.expense.application.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 *     申请单类型分配维度表domain
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/19
 */
@ApiModel(description = "业务规则描述")
@Data
@TableName("exp_application_type_dimension")
public class ApplicationTypeDimension extends Domain {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "类型ID")
    private Long typeId;
    /**
     * 维度ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "维度ID")
    private Long dimensionId;

    @TableField(exist = false)
    @ApiModelProperty(value = "维度名称")
    private String dimensionName;

    /**
     * 排序默认为0
     */
    @ApiModelProperty(value = "排序默认为0")
    private Integer sequence;

    /**
     * 默认值
     */
    @ApiModelProperty(value = "默认值")
    @TableField(value = "default_value", strategy = FieldStrategy.IGNORED)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long defaultValue;

    @TableField(exist = false)
    @ApiModelProperty(value = "值名称")
    private String valueName;
    /**
     * 布局位置 true -- 单据头 false -- 单据行
     */
    @ApiModelProperty(value = "布局位置 true -- 单据头 false -- 单据行")
    private Boolean headerFlag;
    /**
     * 是否必输
     */
    @ApiModelProperty(value = "是否必输")
    private Boolean requiredFlag;
}