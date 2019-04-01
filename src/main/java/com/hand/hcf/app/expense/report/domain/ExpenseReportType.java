package com.hand.hcf.app.expense.report.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
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
@TableName("exp_report_type")
public class ExpenseReportType extends DomainI18nEnable {
    //租户ID
    @NotNull
    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    //账套ID
    @NotNull
    @TableField("set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    //报账单类型代码
    @NotNull
    @TableField("report_type_code")
    private String reportTypeCode;

    //报账单类型名称
    @I18nField
    @NotNull
    @TableField("report_type_name")
    private String reportTypeName;

    //关联表单ID
    @NotNull
    @TableField("form_id")
    private Long formId;

    //关联表单类型
    @NotNull
    @TableField("form_type")
    private Integer formType;

    //是否关联全部费用类型标志(全部类型:1;部分类型:0)
    @NotNull
    @TableField("all_expense_flag")
    private Boolean allExpenseFlag;

    //预算管控标志(启用:Y;不启用:N)
    @NotNull
    @TableField("budget_flag")
    private Boolean budgetFlag;

    //关联申请单依据
    @NotNull
    @TableField("application_form_basis")
    private String applicationFormBasis;

    //关联合同标志(可关联:Y;不可关联:N)
    @NotNull
    @TableField("associate_contract")
    private Boolean associateContract;

    //合同必输标志(必输:Y;非必输:N)
    @TableField("contract_required")
    private Boolean contractRequired;

    //多收款方标志(多收款方:Y;单一收款方:N)
    @NotNull
    @TableField("multi_payee")
    private Boolean multiPayee;

    //收款方属性
    @NotNull
    @TableField("payee_type")
    private String payeeType;

    //是否全部付款用途标志(全部类型:1;部分类型:0)
    @NotNull
    @TableField("all_cash_transaction_class")
    private Boolean allCashTransactionClass;

    //付款方式类型
    @NotNull
    @TableField("payment_method")
    private String paymentMethod;

    //核销依据:是否关联相同申请单(是:Y;否:N)
    @TableField("write_off_application")
    private Boolean writeOffApplication;

    //核销依据:是否关联相同合同(是:Y;否:N)
    @TableField("write_off_contract")
    private Boolean writeOffContract;

    //适用人员("1001":全部;"1002";部门;"1003":人员组)
    @NotNull
    @TableField("apply_employee")
    private String applyEmployee;



    //账套代码
    @TableField(exist = false)
    private String setOfBooksCode;
    //账套名称
    @TableField(exist = false)
    private String setOfBooksName;
    //付款方式类型名称
    @TableField(exist = false)
    private String paymentMethodName;
    //关联表单名称
    @TableField(exist = false)
    private String formName;
}
