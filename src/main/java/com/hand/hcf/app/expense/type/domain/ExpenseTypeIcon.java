package com.hand.hcf.app.expense.type.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */
@Data
@TableName("exp_expense_type_icon")
public class ExpenseTypeIcon implements Serializable {

    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("expense_type_icon_oid")
    private String expenseTypeIconOid;

    @TableField("attachment_id")
    private Long attachmentId;

    @TableField("icon_name")
    private String iconName;

    @TableField("icon_url")
    private String iconURL;

    private boolean deleted = false;

    private boolean enabled = true;

    @TableField( "sequence")
    private Integer sequence;

    @TableField("string_1")
    private String string1;

    @TableField("string_2")
    private String string2;
}
