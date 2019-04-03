package com.hand.hcf.app.expense.input.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @description
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/2/28 14:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("exp_input_tax_line")
public class ExpInputTaxLine extends Domain {
    /**
     * 租户ID
     */
    @NotNull
    @TableField("tenant_id")

    private Long tenantId;

    /**
     * 账套ID
     */
    @NotNull
    @TableField("set_of_books_id")

    private Long setOfBooksId;

    /**
     * 单据头ID
     */
    @NotNull
    @TableField("input_tax_header_id")
    private Long inputTaxHeaderId;

    /**
     * 报账单行ID
     */
    @NotNull
    @TableField("exp_report_line_id")

    private Long expReportLineId;


    /**
     * 用途类型
     */
    @NotNull
    @TableField("use_type")
    private String useType;


    /**
     * 计算比例
     */
    @NotNull
    @TableField("transfer_proportion")
    private Long transferProportion;

    /**
     * 公司
     */
    @NotNull
    @TableField("company_id")
    private Long CompanyId;

    /**
     * 部门
     */
    @NotNull
    @TableField("department_id")
    private Long departmentId;

    /**
     * 币种
     */
    @NotNull
    @TableField("currency_code")
    private String currencyCode;

    /**
     * 汇率
     */
    @NotNull
    @TableField("rate")
    private BigDecimal rate;

    /**
     * 基数金额
     */
    @NotNull
    @TableField("base_amount")
    private BigDecimal baseAmount;

    /**
     * 基数本币金额
     */
    @NotNull
    @TableField("base_function_amount")
    private BigDecimal baseFunctionAmount;

    /**
     * 金额
     */
    @NotNull
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 本币金额
     */
    @NotNull
    @TableField("function_amount")
    private BigDecimal functionAmount;


    /**
     * 备注
     */
    @TableField("description")
    private String description;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 审核状态
     */
    @TableField("audit_status")
    private String auditStatus;


    /**
     * 反冲状态
     */
    @TableField("reverse_flag")
    private String reverseFlag;

}
