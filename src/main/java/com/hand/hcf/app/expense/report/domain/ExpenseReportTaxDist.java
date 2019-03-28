package com.hand.hcf.app.expense.report.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.expense.common.domain.DimensionDomain;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 13:55
 * @remark 对公报账税金分摊行
 */
@Data
@TableName("exp_report_tax_dist")
public class ExpenseReportTaxDist extends DimensionDomain {

    /**
     * 对公报账头ID
     */
    @TableField(value = "exp_report_header_id")
    private Long expReportHeaderId;

    /**
     * 对公报账分摊行ID
     */
    @TableField(value = "exp_report_dist_id")
    private Long expReportDistId;

    /**
     * 发票分配行ID
     */
    @TableField(value = "invoice_dist_id")
    private Long invoiceDistId;

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
     * 成本中心ID
     */
    @TableField(value = "responsibility_center_id")
    private Long responsibilityCenterId;

    /**
     * 分摊税额
     */
    @TableField(value = "tax_amount")
    private BigDecimal taxAmount;

    /**
     * 本币金额
     */
    @TableField(value = "function_amount")
    private BigDecimal functionAmount;

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
     * 税率
     */
    @TableField(value = "tax_rate")
    private String taxRate;

    /**
     * 反冲标志(未反冲：N；反冲提交未审批：P；已反冲：Y)
     */
    @TableField(value = "reverse_flag")
    private String reverseFlag;
}
