package com.hand.hcf.app.common.co;

import com.hand.hcf.app.apply.accounting.annotation.InterfaceFieldAttribute;
import com.hand.hcf.app.apply.accounting.enums.SceneElementFieldType;
import com.hand.hcf.app.apply.accounting.message.ModuleMessageCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Created by kai.zhang on 2017-12-25.
 * 报销单计划付款行信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseReportScheduleCO {

    @InterfaceFieldAttribute(sequence = 10, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_ID, elementFiled = SceneElementFieldType.TRANSACTION_LINE_ID)
    @NotNull
    private Long id;
    @NotNull
    private Long headerId;                //报账单头ID
    @InterfaceFieldAttribute(sequence = 20, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_TENANT_ID)
    private Long tenantId;             //租户id
    @InterfaceFieldAttribute(sequence = 30, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_SET_OF_BOOKS_ID)
    private Long setOfBooksId;           //账套id
    /**
     * 行号
     */
    private Integer scheduleLineNumber;
    /**
     * 公司id
     */
    @InterfaceFieldAttribute(sequence = 40, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_COMPANY_ID)
    private Long companyId;
    /**
     * 描述
     */
    @InterfaceFieldAttribute(sequence = 50, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_DESCRIPTION)
    private String description;
    /**
     * 币种
     */
    @InterfaceFieldAttribute(sequence = 60, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_CURRENCY, elementFiled = SceneElementFieldType.CURRENCY_CODE)
    private String currency;
    /**
     * 汇率
     */
    @InterfaceFieldAttribute(sequence = 65)
    private Double rate;
    /**
     * 金额
     */
    @InterfaceFieldAttribute(sequence = 70, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_AMOUNT)
    private BigDecimal amount;
    /**
     * 本币金额
     */
    @InterfaceFieldAttribute(sequence = 80, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_FUNCTIONAL_AMOUNT)
    private BigDecimal functionalAmount;
    /**
     * 计划付款日期
     */
    @InterfaceFieldAttribute(sequence = 90, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_SCHEDULE_PAYMENT_DATE)
    private ZonedDateTime schedulePaymentDate;
    /**
     * 付款方式大类
     */
    @InterfaceFieldAttribute(sequence = 100, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_PAYMENT_METHOD)
    private String paymentMethod;
    /**
     * 现金事务分类id
     */
    @InterfaceFieldAttribute(sequence = 110, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_CSH_TRANSACTION_CLASS_ID)
    private Long cshTransactionClassId;
    /**
     * 现金流量项id
     */
    @InterfaceFieldAttribute(sequence = 120, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_CASH_FLOW_ITEM_ID)
    private Long cashFlowItemId;
    /**
     * 收款对象类型
     */
    @InterfaceFieldAttribute(sequence = 130, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_PAYEE_CATEGORY)
    private String payeeCategory;
    /**
     * 收款对象代码
     */
    @InterfaceFieldAttribute(sequence = 140, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_PAYEE_CODE)
    private String payeeCode;
    /**
     * 收款对象id
     */
    @InterfaceFieldAttribute(sequence = 150, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_PAYEE_ID)
    private Long payeeId;
    /**
     * 银行账号
     */
    @InterfaceFieldAttribute(sequence = 160, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_ACCOUNT_NUMBER)
    private String accountNumber;
    /**
     * 银行户名
     */
    @InterfaceFieldAttribute(sequence = 170, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_ACCOUNT_NAME)
    private String accountName;
}
