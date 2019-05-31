package com.hand.hcf.app.expense.report.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.core.domain.Domain;
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
     * 付款方式类型（线上：ONLINE_PAYMENT；线下：OFFLINE_PAYMENT；落地文件：EBANK_PAYMENT）
     */
    @NotNull
    @TableField(value = "payment_method")
    private String paymentMethod;

    /**
     * 付款用途ID
     */
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
     * 收款方类型(员工：EMPLOYEE；供应商：VENDER)
     */
    @TableField(value = "payee_category")
    private String payeeCategory;

    /**
     * 收款方ID
     */
    @TableField(value = "payee_id")
    private Long payeeId;

    /**
     * 收款方账户
     */
    @TableField(value = "account_number")
    private String accountNumber;

    /**
     * 收款方户名
     */
    @TableField(value = "account_name")
    private String accountName;

    /**
     * 是否有票Y/N
     */
    @TableField(value = "is_ninvoiced")
    private String isNinvoiced;

    /**
     * 税率
     */
    @NotNull
    @TableField(value = "tax_rate")
    private String taxRate;

    /**
     * 国家
     */
    @NotNull
    @TableField(value = "country")
    private String country;

    /**
     * 地区
     */
    @NotNull
    @TableField(value = "district")
    private String district;

    /**
     * 付款方式
     */
    @TableField(value = "payment_type")
    private String paymentType;

    /**
     * 受益期开始
     */
    @NotNull
    @TableField(value = "expense_date_start")
    private ZonedDateTime expenseDateStart;

    /**
     * 受益期结束
     */
    @NotNull
    @TableField(value = "expense_date_end")
    private ZonedDateTime expenseDateEnd;

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

    /**
     * 关联申请模式
     */
    @TableField(exist = false)
    private String applicationModel;
    /**
     * 对比符号
     */
    @TableField(exist = false)
    private String contrastSign;
    /**
     * 金额条件
     */
    @TableField(exist = false)
    private BigDecimal contrastAmount;

    /**
     * 金额录入模式 false-总金额 true-单价*数量
     */
    @TableField(exist = false)
    private Boolean entryMode;
    /**
     * 费用图标
     */
    @TableField(exist = false)
    private String iconUrl;
    /**
     * 行信息是否存在发票标志
     */
    @TableField(exist = false)
    private Boolean invoiceExistsFlag;

    /**
     * 不含税金额
     */
    @TableField(exist = false)
    private BigDecimal noTaxAmount;

    /**
     * 国家名称
     */
    @TableField(exist = false)
    private String countryName;

    /**
     * 地区名称
     */
    @TableField(exist = false)
    private String districtName;

    /**
     * 收款方类型名称
     */
    @TableField(exist = false)
    private String payeeCategoryName;

    /**
     * 收款方名称
     */
    @TableField(exist = false)
    private String payeeName;

    /**
     * 付款方式名称
     */
    @TableField(exist = false)
    private String paymentTypeName;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    private String companyName;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    private String companyCode;

}
