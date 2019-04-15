package com.hand.hcf.app.expense.adjust.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("exp_adjust_type")
public class ExpenseAdjustType extends Domain {

    //租户id
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("tenant_id")
    private Long tenantId;

    //账套id
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("set_of_books_id")
    private Long setOfBooksId;

    //费用调整单类型代码
    @TableField("exp_adjust_type_code")
    private String expAdjustTypeCode;

    //费用调整单类型名称
    @TableField("exp_adjust_type_name")
    private String expAdjustTypeName;

    //调整类型
    @TableField("adjust_type_category")
    private String adjustTypeCategory;

    //预算管控标志
    @TableField("budget_flag")
    private Boolean budgetFlag;

    //核算标志
    @TableField("account_flag")
    private Boolean accountFlag;

    //关联表单oid
    @TableField("form_oid")
    private String formOid;

    //关联表单名称
    @TableField("form_name")
    private String formName;

    //关联表单类型
    @TableField("form_type")
    private Long formType;

    //启用标志
    @TableField("enabled")
    private Boolean enabled;

    //是否全部费用类型
    @TableField("all_expense")
    private Boolean allExpense;

    //是否全部维度
    @TableField("all_dimension")
    private Boolean allDimension;

    //适用人员
    @TableField("apply_employee")
    private Integer applyEmployee;


//    //最后更新日期
//    @TableField("last_updated_date")
//    private DateTime lastUpdatedDate;
//
//    //最后更新用户ID
//    @JsonSerialize(using = ToStringSerializer.class)
//    @TableField("last_updated_by")
//    private Long lastUpdatedBy;
//
//    //创建日期
//    @TableField("created_date")
//    private DateTime createdDate;
//
//    //创建用户ID
//    @JsonSerialize(using = ToStringSerializer.class)
//    @TableField("created_by")
//    private Long createdBy;


    //账套代码
    @TableField(exist = false)
    private String setOfBooksCode;

    //账套名称
    @TableField(exist = false)
    private String setOfBooksName;
}
