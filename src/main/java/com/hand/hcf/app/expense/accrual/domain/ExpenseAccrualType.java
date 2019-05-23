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
 * @description: 费用预提单类型定义
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/15
 */
@ApiModel(description = "费用预提单类型定义")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("exp_accrual_type")
public class ExpenseAccrualType extends Domain {

    //费用预提单类型代码
    @ApiModelProperty(value = "费用预提单类型代码")
    @TableField("exp_accrual_type_code")
    private String expAccrualTypeCode;

    //费用预提单类型名称
    @ApiModelProperty(value = "费用预提单类型名称")
    @TableField("exp_accrual_type_name")
    private String expAccrualTypeName;

    //账套id
    @ApiModelProperty(value = "账套id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("set_of_books_id")
    private Long setOfBooksId;

    //租户id
    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("tenant_id")
    private Long tenantId;

    //预算管控标志
    @ApiModelProperty(value = "预算管控标志")
    @TableField("budgt_flag")
    private Boolean budgtFlag;

    //关联表单id
    @ApiModelProperty(value = "关联表单id")
    @TableField("form_id")
    private Long formId;

    //适用人员
    @ApiModelProperty(value = "适用人员")
    @TableField("visible_user_scope")
    private String visibleUserScope;

    //是否全部费用类型
    @ApiModelProperty(value = "是否全部费用类型")
    @TableField("all_expense")
    private Boolean allExpense;

    //启用标志
    @ApiModelProperty(value = "启用标志")
    @TableField("enable_flag")
    private Boolean enableFlag;

    //账套代码
    @ApiModelProperty(value = "账套代码")
    @TableField(exist = false)
    private String setOfBooksCode;

    //账套名称
    @ApiModelProperty(value = "账套名称")
    @TableField(exist = false)
    private String setOfBooksName;

    //关联表单名称
    @ApiModelProperty(value = "关联表单名称")
    @TableField(exist = false)
    private String formName;
}
