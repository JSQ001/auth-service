package com.hand.hcf.app.expense.type.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.common.co.DimensionItemCO;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/20
 */
@Data
@TableName("exp_dimension")
public class ExpenseDimension extends Domain {

    private Integer documentType;

    /**
     * 值 id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "value", strategy = FieldStrategy.IGNORED)
    private Long value;

    @TableField(exist = false)
    private String valueName;
    /**
     * 维度名称
     */
    @TableField(exist = false)
    private String name;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimensionId;

    /**
     * dimension1Id dimension2Id
     */
    private String dimensionField;

    private Boolean headerFlag;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long headerId;
    private Integer sequence;
    /**
     * 是否必输
     */
    private Boolean requiredFlag;

    /**
     * 可选择的维度值
     */
    @TableField(exist = false)
    private List<DimensionItemCO> options;

}
