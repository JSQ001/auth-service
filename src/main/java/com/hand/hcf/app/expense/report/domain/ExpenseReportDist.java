package com.hand.hcf.app.expense.report.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.app.expense.common.domain.DimensionDomain;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 11:43
 * @remark 对公报账分摊行
 */
@Data
@TableName("exp_report_dist")
public class ExpenseReportDist extends DimensionDomain {

    /**
     * 对公报账头ID
     */
    @TableField(value = "exp_report_header_id")
    private Long expReportHeaderId;

    /**
     * 对公报账行ID
     */
    @TableField(value = "exp_report_line_id")
    private Long expReportLineId;

    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 账套ID
     */
    @TableField(value = "set_of_books_id")
    private Long setOfBooksId;

    /**
     * 公司ID
     */
    @TableField(value = "company_id")
    private Long companyId;

    /**
     * 费用类型ID
     */
    @TableField(value = "expense_type_id")
    private Long expenseTypeId;

    /**
     * 部门ID
     */
    @TableField(value = "department_id")
    private Long departmentId;

    /**
     * 责任中心ID
     */
    @TableField(value = "responsibility_center_id")
    private Long responsibilityCenterId;

    /**
     * 分摊含税总金额
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
     * 分摊不含税金额
     */
    @NotNull
    @TableField(value = "no_tax_dist_amount")
    private BigDecimal noTaxDistAmount;

    /**
     * 分摊不含税本币金额
     */
    @TableField(value = "no_tax_dist_function_amount")
    private BigDecimal noTaxDistFunctionAmount;

    /**
     * 汇率
     */
    @TableField(value = "exchange_rate")
    private BigDecimal exchangeRate;

    /**
     * 币种
     */
    @TableField(value = "currency_code")
    private String currencyCode;

    /**
     * 税分摊额
     */
    @NotNull
    @TableField(value = "tax_dist_amount")
    private BigDecimal taxDistAmount;

    /**
     * 税本币分摊金额
     */
    @TableField(value = "tax_dist_function_amount")
    private BigDecimal taxDistFunctionAmount;

    /**
     * 反冲标志(未反冲：N；反冲提交未审批：P；已反冲：Y)
     */
    @TableField(value = "reverse_flag")
    private String reverseFlag;

    /**
     * 来源单据类型
     * EXP_REQUISITION 申请单
     */
    @TableField(value = "source_document_category")
    private String sourceDocumentCategory;

    /**
     * 来源单据id
     */
    @TableField(value = "source_document_id")
    private Long sourceDocumentId;

    /**
     * 来源单据分摊行id
     */
    @TableField(value = "source_document_dist_id")
    private Long sourceDocumentDistId;

    /**
     * 视同销售与进项转出标志（视同销售：FOR_SALE;全额转出：ALL_TRANSFER；部分转出：PART_ TRANSFER）
     */
    @TableField(value = "input_tax_flag")
    private String inputTaxFlag;

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

    /**
     * 分摊含税总金额(费用行对应最大值，尾差处理在该数据上)
     */
    @JsonIgnore
    @TableField(exist = false)
    private BigDecimal maxAmount;

}
