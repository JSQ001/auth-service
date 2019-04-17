package com.hand.hcf.app.expense.invoice.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @description: 发票行报销记录表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("invoice_line_expence")
public class InvoiceLineExpence extends Domain {
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

    //发票分配行ID
    @NotNull
    @TableField("invoice_dist_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long invoiceDistId;

    //我的账本ID
    @TableField("expense_book_id")
    private Long expenseBookId;

    //发票号码
    @NotNull
    @TableField("invoice_no")
    private String invoiceNo;

    //发票代码
    @NotNull
    @TableField("invoice_code")
    private String invoiceCode;

    //金额
    @NotNull
    @TableField("detail_amount")
    private BigDecimal detailAmount;

    //税率
    @NotNull
    @TableField("tax_rate")
    private String taxRate;

    //税额
    @NotNull
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    //关联报账单头ID
    @TableField("exp_expense_head_id")
    private Long expExpenseHeadId;

    //关联报账单行ID
    @TableField("exp_expense_line_id")
    private Long expExpenseLineId;

    //状态
    @NotNull
    @TableField("status")
    private String status;

    //账本发票行录入方式：（BY_HAND手工录入、FROM_INVOICE票夹导入）
    @TableField("expense_book_invoice_method")
    private String expenseBookInvoiceMethod;

    //费用行发票录入方式：（BY_HAND手工录入、FROM_INVOICE票夹导入、FROM_EXPENSE_BOOK账本导入）
    @TableField("report_line_invoice_method")
    private String reportLineInvoiceMethod;

}
