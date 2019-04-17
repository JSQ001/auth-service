package com.hand.hcf.app.expense.report.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 14:10
 * @remark
 */
@Data
@TableName("exp_report_payment_schedule")
public class ExpenseReportPaymentSchedule extends Domain {

    /**
     * 对公报账头ID
     */
    @NotNull
    @TableField(value = "exp_report_header_id")
    private Long expReportHeaderId;

    /**
     * 租户ID
     */
    @NotNull
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 账套ID
     */
    @NotNull
    @TableField(value = "set_of_books_id")
    private Long setOfBooksId;

    /**
     * 公司ID
     */
    @NotNull
    @TableField(value = "company_id")
    private Long companyId;

    /**
     * 部门ID
     */
    @TableField(value = "department_id")
    private Long departmentId;

    /**
     * 申请人ID
     */
    @NotNull
    @TableField(value = "applicant_id")
    private Long applicantId;

    /**
     * 汇率
     */
    @NotNull
    @TableField(value = "exchange_rate")
    private BigDecimal exchangeRate;

    /**
     * 币种
     */
    @NotNull
    @TableField(value = "currency_code")
    private String currencyCode;

    /**
     * 金额
     */
    @NotNull
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 本币金额
     */
    @TableField(value = "function_amount")
    private BigDecimal functionAmount;

    /**
     * 备注
     */
    @TableField(value = "description")
    private String description;

    /**
     * 付款方式类型（线上：ONLINE_PAYMENT；线下：OFFLINE_PAYMENT；落地文件：EBANK_PAYMENT)
     */
    @NotNull
    @TableField(value = "payment_method")
    private String paymentMethod;

    /**
     * 付款用途ID
     */
    @NotNull
    @TableField(value = "csh_transaction_class_id")
    private Long cshTransactionClassId;

    /**
     * 现金流ID
     */
    @TableField(value = "cash_flow_item_id")
    private Long cashFlowItemId;

    /**
     * 计划付款日期
     */
    @NotNull
    @TableField(value = "payment_schedule_date")
    private ZonedDateTime paymentScheduleDate;

    /**
     * 关联合同资金计划ID
     */
    @TableField(value = "con_payment_schedule_line_id")
    private Long conPaymentScheduleLineId;

    /**
     * 收款方类型（员工：EMPLOYEE；供应商：VENDER）
     */
    @NotNull
    @TableField(value = "payee_category")
    private String payeeCategory;

    /**
     * 收款方id
     */
    @NotNull
    @TableField(value = "payee_id")
    private Long payeeId;

    /**
     * 收款方账户
     */
    @NotNull
    @TableField(value = "account_number")
    private String accountNumber;

    /**
     * 收款方户名
     */
    @NotNull
    @TableField(value = "account_name")
    private String accountName;

    /**
     * 冻结状态（Y/N）
     */
    @TableField(value = "frozen_flag")
    private String frozenFlag;

    /**
     * 核销金额
     */
    @TableField(value = "write_off_amount")
    private BigDecimal writeOffAmount;

    /**
     * 审核状态
     */
    @TableField(value = "audit_flag")
    private String auditFlag;

    /**
     * 审核日期
     */
    @TableField(value = "audit_date")
    private ZonedDateTime auditDate;

}
