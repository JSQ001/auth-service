package com.hand.hcf.app.expense.invoice.domain;

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

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @description: 发票行报销记录表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/22
 */
@ApiModel(description = "发票行报销记录")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("invoice_line_expence")
public class InvoiceLineExpence extends Domain {
    //租户ID
    @NotNull
    @ApiModelProperty(value = "租户id")
    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    //账套ID
    @NotNull
    @ApiModelProperty(value = "账套ID")
    @TableField("set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    //发票分配行ID
    @NotNull
    @ApiModelProperty(value = "发票分配行ID")
    @TableField("invoice_dist_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long invoiceDistId;

    //我的账本ID
    @ApiModelProperty(value = "我的账本ID")
    @TableField("expense_book_id")
    private Long expenseBookId;

    //发票号码
    @NotNull
    @ApiModelProperty(value = "发票号码")
    @TableField("invoice_no")
    private String invoiceNo;

    //发票代码
    @NotNull
    @ApiModelProperty(value = "发票代码")
    @TableField("invoice_code")
    private String invoiceCode;

    //金额
    @NotNull
    @ApiModelProperty(value = "金额")
    @TableField("detail_amount")
    private BigDecimal detailAmount;

    //税率
    @NotNull
    @ApiModelProperty(value = "税率")
    @TableField("tax_rate")
    private String taxRate;

    //税额
    @NotNull
    @ApiModelProperty(value = "税额")
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    //关联报账单头ID
    @ApiModelProperty(value = "关联报账单头ID")
    @TableField("exp_expense_head_id")
    private Long expExpenseHeadId;

    //关联报账单行ID
    @ApiModelProperty(value = "关联报账单行ID")
    @TableField("exp_expense_line_id")
    private Long expExpenseLineId;

    //状态
    @NotNull
    @ApiModelProperty(value = "状态")
    @TableField("status")
    private String status;

    //账本发票行录入方式：（BY_HAND手工录入、FROM_INVOICE票夹导入）
    @ApiModelProperty(value = "账本发票行录入方式：（BY_HAND手工录入、FROM_INVOICE票夹导入）")
    @TableField("expense_book_invoice_method")
    private String expenseBookInvoiceMethod;

    //费用行发票录入方式：（BY_HAND手工录入、FROM_INVOICE票夹导入、FROM_EXPENSE_BOOK账本导入）
    @ApiModelProperty(value = "费用行发票录入方式：（BY_HAND手工录入、FROM_INVOICE票夹导入、FROM_EXPENSE_BOOK账本导入）")
    @TableField("report_line_invoice_method")
    private String reportLineInvoiceMethod;

    @ApiModelProperty(value = "发票袋号码")
    @TableField("invoice_bag_no")
    private String invoiceBagNo;

    @ApiModelProperty(value = "发票袋号码确认标志")
    @TableField("invoice_bag_confirm_flag")
    private String invoiceBagConfirmFlag;

    @ApiModelProperty(value = "发票关联报账单行匹配标志")
    @TableField("invoice_mate_flag")
    private String invoiceMateFlag;

}
