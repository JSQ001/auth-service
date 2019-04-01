package com.hand.hcf.app.expense.travel.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 差旅申请单类型关联申请类型
 * @author shouting.cheng
 * @date 2019/3/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("exp_travel_app_type_ass_typ")
public class TravelApplicationTypeAssignType extends Domain {
    /**
     * 差旅申请单类型ID
     */
    private Long typeId;
    /**
     * 申请类型ID
     */
    private Long requisitionTypeId;
    /**
     * 申请类型名称
     */
    @TableField(exist = false)
    private String requisitionTypeName;
}
