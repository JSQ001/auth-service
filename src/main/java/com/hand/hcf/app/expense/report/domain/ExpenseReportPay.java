package com.hand.hcf.app.expense.report.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @description: 报账单类型表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("exp_report_pay")
@ApiModel(description = "报账单类型")
public class ExpenseReportPay extends DomainI18nEnable {
    /**
     * 租户ID
     */
    @NotNull
    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "租户ID",dataType = "Long",required = true)
    private Long tenantId;

    /**
     * 账套ID
     */
    @NotNull
    @TableField("set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "账套ID",dataType = "Long",required = true)
    private Long setOfBooksId;

    /**
     * 报账单类型代码
     */
    @NotNull
    @TableField("report_type_code")
    @ApiModelProperty(value = "报账单类型代码",dataType = "String",required = true)
    private String reportTypeCode;

    /**
     * 报账单类型名称
     */
    @I18nField
    @NotNull
    @TableField("report_type_name")
    @ApiModelProperty(value = "报账单类型名称",dataType = "String",required = true)
    private String reportTypeName;

    /**
     * 关联表单ID
     */
    @NotNull
    @TableField("form_id")
    @ApiModelProperty(value = "关联表单ID",dataType = "Long",required = true)
    private Long formId;

    /**
     * 关联表单类型
     */
    @NotNull
    @TableField("form_type")
    @ApiModelProperty(value = "关联表单类型",dataType = "Integer",required = true)
    private Integer formType;

    /**
     * 是否关联全部费用类型标志(全部类型:1;部分类型:0)
     */
    @NotNull
    @TableField("all_expense_flag")
    @ApiModelProperty(value = "是否关联全部费用类型标志(全部类型:1;部分类型:0)",dataType = "Boolean",required = true)
    private Boolean allExpenseFlag;

    /**
     * 预算管控标志(启用:Y;不启用:N)
     */
    @NotNull
    @TableField("budget_flag")
    @ApiModelProperty(value = "预算管控标志(启用:Y;不启用:N)",dataType = "Boolean",required = true)
    private Boolean budgetFlag;

    /**
     * 关联申请单依据
     */
    @NotNull
    @TableField("application_form_basis")
    @ApiModelProperty(value = "关联申请单依据",dataType = "String",required = true)
    private String applicationFormBasis;

    /**
     * 关联合同标志(可关联:Y;不可关联:N)
     */
    @NotNull
    @TableField("associate_contract")
    @ApiModelProperty(value = "关联合同标志(可关联:Y;不可关联:N)",dataType = "Boolean",required = true)
    private Boolean associateContract;

    /**
     * 合同必输标志(必输:Y;非必输:N)
     */
    @TableField("contract_required")
    @ApiModelProperty(value = "合同必输标志(必输:Y;非必输:N)",dataType = "Boolean")
    private Boolean contractRequired;

    /**
     * 多收款方标志(多收款方:Y;单一收款方:N)
     */
    @NotNull
    @TableField("multi_payee")
    @ApiModelProperty(value = "多收款方标志(多收款方:Y;单一收款方:N)",dataType = "Boolean",required = true)
    private Boolean multiPayee;

    /**
     * 收款方属性
     */
    @NotNull
    @TableField("payee_type")
    @ApiModelProperty(value = "收款方属性",dataType = "String",required = true)
    private String payeeType;

    /**
     * 是否全部付款用途标志(全部类型:1;部分类型:0)
     */
    @NotNull
    @TableField("all_cash_transaction_class")
    @ApiModelProperty(value = "是否全部付款用途标志(全部类型:1;部分类型:0)",dataType = "Boolean",required = true)
    private Boolean allCashTransactionClass;

    /**
     * 付款方式类型(隐藏不显示)
     */
    @TableField("payment_method")
    @ApiModelProperty(value = "付款方式类型",dataType = "String",required = false)
    private String paymentMethod;

    /**
     * 付款方式(付款方式值列表：ZJ_PAYMENT_TYPE)
     */
    @NotNull
    @TableField("payment_type")
    @ApiModelProperty(value = "付款方式",dataType = "String",required = true)
    private String paymentType;

    /**
     * 核销依据:是否关联相同申请单(是:Y;否:N)
     */
    @TableField("write_off_application")
    @ApiModelProperty(value = "核销依据:是否关联相同申请单(是:Y;否:N)",dataType = "Boolean",required = false)
    private Boolean writeOffApplication;

    /**
     * 核销依据:是否关联相同合同(是:Y;否:N)
     */
    @TableField("write_off_contract")
    @ApiModelProperty(value = "核销依据:是否关联相同合同(是:Y;否:N)",dataType = "Boolean",required = false)
    private Boolean writeOffContract;

    /**
     * 适用人员("1001":全部;"1002";部门;"1003":人员组)
     */
    @NotNull
    @TableField("apply_employee")
    @ApiModelProperty(value = "适用人员(\"1001\":全部;\"1002\";部门;\"1003\":人员组)",dataType = "String",required = true)
    private String applyEmployee;

    /**
     * 账套代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "账套代码",dataType = "String")
    private String setOfBooksCode;
    /**
     * 账套名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "账套名称",dataType = "String")
    private String setOfBooksName;
    /**
     * 付款方式类型名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "付款方式类型名称",dataType = "String")
    private String paymentMethodName;
    /**
     * 付款方式名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "付款方式名称",dataType = "String")
    private String paymentTypeName;
    /**
     * 关联表单名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "关联表单名称",dataType = "String")
    private String formName;
}