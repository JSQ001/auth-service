package com.hand.hcf.app.expense.report.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 11:32
 * @remark 对公报账行
 */
@Data
@TableName("exp_report_line")
public class ExpenseReportLine extends Domain{

    /**
     * 对公报账头ID
     */
    @TableField(value = "exp_report_header_id")
    private Long expReportHeaderId;

    /**
     * 租户
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 账套
     */
    @TableField(value = "set_of_books_id")
    private Long setOfBooksId;

    /**
     * 公司
     */
    @TableField(value = "company_id")
    private Long companyId;

    /**
     * 费用类型
     */
    @NotNull
    @TableField(value = "expense_type_id")
    private Long expenseTypeId;

    /**
     * 费用类型名称
     */
    @TableField(exist = false)
    private String expenseTypeName;

    /**
     * 费用发生日期
     */
    @NotNull
    @TableField(value = "expense_date")
    private ZonedDateTime expenseDate;

    /**
     * 数量
     */
    @TableField(value = "quantity")
    private Integer quantity;

    /**
     * 单价
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 单位
     */
    @TableField(value = "uom")
    private String uom;

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
     * 报账金额
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
     * 费用金额
     */
    @NotNull
    @TableField(value = "expense_amount")
    private BigDecimal expenseAmount;

    /**
     * 费用本币金额
     */
    @TableField(value = "expense_function_amount")
    private BigDecimal expenseFunctionAmount;

    /**
     * 税额
     */
    @TableField(value = "tax_amount")
    private BigDecimal taxAmount;

    /**
     * 本币税额
     */
    @TableField(value = "tax_function_amount")
    private BigDecimal taxFunctionAmount;

    /**
     * 分期抵扣标志（是：Y；否：N）
     */
    @TableField(value = "installment_deduction_flag")
    private String installmentDeductionFlag;

    /**
     * 视同销售与进项转出标志（视同销售：FOR_SALE;全额转出：ALL_TRANSFER；部分转出：PART_ TRANSFER）
     */
    @TableField(value = "input_tax_flag")
    private String inputTaxFlag;

    /**
     * 备注
     */
    @TableField(value = "description")
    private String description;

    /**
     * 反冲标志(未反冲：N；反冲提交未审批：P；已反冲：Y)
     */
    @TableField(value = "reverse_flag")
    private String reverseFlag;

    /**
     * 用途类型
     */
    @TableField(value = "use_type")
    private String useType;

    /**
     * 附件
     */
    @TableField(value = "attachment_oid")
    private String attachmentOid;

    /**
     * 账本ID
     */
    @TableField(value = "expense_book_id")
    private Long expenseBookId;

    /**
     * 附件OID集合
     */
    @TableField(exist = false)
    private List<String> attachmentOidList;
    /**
     * 附件信息
     */
    @TableField(exist = false)
    private List<AttachmentCO> attachments;
    /**
     * 序号
     */
    @TableField(exist = false)
    private Integer index;

}
