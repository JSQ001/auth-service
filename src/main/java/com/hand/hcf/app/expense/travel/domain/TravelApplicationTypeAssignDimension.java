package com.hand.hcf.app.expense.travel.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

/**
 * 差旅申请单类型关联维度
 * @author shouting.cheng
 * @date 2019/3/4
 */
@Data
@TableName("exp_travel_app_type_ass_dim")
public class TravelApplicationTypeAssignDimension extends Domain {
    /**
     * 差旅申请单类型ID
     */
    private Long typeId;
    /**
     * 维度ID
     */
    private Long dimensionId;
    /**
     * 维度名称
     */
    @TableField(exist = false)
    private String dimensionName;
    /**
     * 默认维值ID
     */
    private Long defaultValue;
    /**
     * 维值名称
     */
    @TableField(exist = false)
    private String valueName;
    /**
     * 是否必输（true是，false否）
     */
    private Boolean requiredFlag;
    /**
     * 布局位置（true单据头，false申请行）
     */
    private Boolean headerFlag;
    /**
     *  优先级
     */
    private Integer sequence;
}
