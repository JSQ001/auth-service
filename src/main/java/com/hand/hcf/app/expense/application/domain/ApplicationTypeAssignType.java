package com.hand.hcf.app.expense.application.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/8
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exp_application_type_exp_type")
public class ApplicationTypeAssignType extends Domain {

    /**
     * 申请单类型ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long applicationTypeId;

    /**
     * 申请类型ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expenseTypeId;

    /**
     * 申请类型
     */
    @TableField(exist = false)
    private String expenseTypeName;
}
