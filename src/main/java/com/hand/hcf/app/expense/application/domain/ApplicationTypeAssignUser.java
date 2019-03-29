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
@TableName("exp_application_type_user")
@EqualsAndHashCode(callSuper = true)
public class ApplicationTypeAssignUser extends Domain {

    /**
     * 申请单类型ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long applicationTypeId;

    /**
     * 适用人员
     */
    private String applyType;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userTypeId;

    @TableField(exist = false)
    private String pathOrName;
}
