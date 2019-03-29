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
 * 报销单发票行信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseReportInvoiceCO {

    @NotNull
    @InterfaceFieldAttribute(sequence = 0, display = false, elementFiled = SceneElementFieldType.TRANSACTION_LINE_ID)
    private Long id;
    @NotNull
    private Long headerId;        //报账单id
    @InterfaceFieldAttribute(sequence = 10, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_TENANT_ID)
    private Long tenantId;             //租户id
    @InterfaceFieldAttribute(sequence = 20, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_SET_OF_BOOKS_ID)
    private Long setOfBooksId;           //账套id
    @InterfaceFieldAttribute(sequence = 30, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_COMPANY_ID)
    private Long companyId;             //公司id
    @InterfaceFieldAttribute(sequence = 35)
    private Long unitId;        //部门ID
    @InterfaceFieldAttribute(sequence = 40, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_EXPENSE_SEARCH_ENTITY_ID)
    private Long expenseSearchEntityId;        //法人id
    @InterfaceFieldAttribute(sequence = 50, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_TAX_PAYER_NUMBER)
    private String taxpayerNumber;             //纳税人识别号
    @InterfaceFieldAttribute(sequence = 60, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_TAX_PAYER_NAME)
    private String taxpayerName;             //销方名称
    @InterfaceFieldAttribute(sequence = 65)
    private ZonedDateTime invoiceDate;        //发票日期
    @InterfaceFieldAttribute(sequence = 70, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_INVOICE_NUMBER)
    private String invoiceNumber;        //发票号码
    @InterfaceFieldAttribute(sequence = 80, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_INVOICE_CODE)
    private String invoiceCode;          //发票代码
    @InterfaceFieldAttribute(sequence = 90, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_INVOICE_APPLY_TYPE)
    private Integer invoiceApplyType;//开票类型(1普票,2专票)
    @InterfaceFieldAttribute(sequence = 100, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_TAX_TOTAL_AMOUNT)
    private BigDecimal taxTotalAmount;          //价税合计金额
    @InterfaceFieldAttribute(sequence = 110, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_TAX_RATE)
    private Double taxRate;                  //税率
    @InterfaceFieldAttribute(sequence = 120, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_TAX_AMOUNT)
    private BigDecimal taxAmount;                //税额
    @InterfaceFieldAttribute(sequence = 130, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_SALE_AMOUNT)
    private BigDecimal saleAmount;               //不含税金额
    @InterfaceFieldAttribute(sequence = 140, msgCode = ModuleMessageCode.EXP_REPORT_INVOICE_FIXED_ASSETS_FLAG)
    private String fixedAssetsFlag;           //不动产标志
    @InterfaceFieldAttribute(sequence = 150)
    private BigDecimal firstPeriodTaxAmount;     //第1期税额
    @InterfaceFieldAttribute(sequence = 160)
    private BigDecimal thirteenthPeriodTaxAmount;    //第13期税额
}
