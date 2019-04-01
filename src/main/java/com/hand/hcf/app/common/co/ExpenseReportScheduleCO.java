package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.annotation.InterfaceFieldAttribute;
import com.hand.hcf.app.common.enums.SceneElementFieldType;
import com.hand.hcf.app.common.message.ModuleMessageCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/3/26 23:00
 * 报销单计划付款行信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseReportScheduleCO {

    @NotNull
    @InterfaceFieldAttribute(sequence = 0,display = false, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_ID, elementFiled = SceneElementFieldType.TRANSACTION_LINE_ID)
    private Long id;                    //计划付款行ID
    @NotNull
    @InterfaceFieldAttribute(sequence = 10,display = false, elementFiled = SceneElementFieldType.TRANSACTION_HEADER_ID)
    private Long headerId;                //报账单头ID
    @NotNull
    @InterfaceFieldAttribute(sequence = 20, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_COMPANY_ID)
    private Long companyId;                 //公司id
    @NotNull
    @InterfaceFieldAttribute(sequence = 30)
    private Long unitId;                    //部门id
    @InterfaceFieldAttribute(sequence = 40, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_DESCRIPTION)
    private String description;             //计划付款行描述
    @NotNull
    @InterfaceFieldAttribute(sequence = 50, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_CURRENCY, elementFiled = SceneElementFieldType.CURRENCY_CODE)
    private String currency;                //币种
    @NotNull
    @InterfaceFieldAttribute(sequence = 60)
    private Double rate;                    //汇率
    @NotNull
    @InterfaceFieldAttribute(sequence = 70, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_AMOUNT)
    private BigDecimal amount;              //金额
    @NotNull
    @InterfaceFieldAttribute(sequence = 80, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_FUNCTIONAL_AMOUNT)
    private BigDecimal functionalAmount;    //本币金额
    @InterfaceFieldAttribute(sequence = 90, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_SCHEDULE_PAYMENT_DATE)
    private ZonedDateTime schedulePaymentDate;//计划付款日期
    @InterfaceFieldAttribute(sequence = 100, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_PAYMENT_METHOD)
    private String paymentMethod;           //付款方式大类
    @InterfaceFieldAttribute(sequence = 110, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_CSH_TRANSACTION_CLASS_ID)
    private Long cshTransactionClassId;     //现金事务分类id
    @InterfaceFieldAttribute(sequence = 120, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_CASH_FLOW_ITEM_ID)
    private Long cashFlowItemId;            //现金流量项id
    @InterfaceFieldAttribute(sequence = 130, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_PAYEE_CATEGORY)
    private String payeeCategory;           //收款对象类型
    @InterfaceFieldAttribute(sequence = 140, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_PAYEE_ID)
    private Long payeeId;                   //收款对象id
    @InterfaceFieldAttribute(sequence = 140, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_PAYEE_CODE)
    private String payeeCode;               //收款对象代码
    @InterfaceFieldAttribute(sequence = 160, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_ACCOUNT_NUMBER)
    private String accountNumber;           //银行账号
    @InterfaceFieldAttribute(sequence = 170, msgCode = ModuleMessageCode.EXP_REPORT_SCHEDULE_ACCOUNT_NAME)
    private String accountName;             //银行户名
    @InterfaceFieldAttribute(sequence = 180)
    private String frozenResult;           //冻结状态
}

