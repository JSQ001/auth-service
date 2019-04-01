package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.annotation.InterfaceFieldAttribute;
import com.hand.hcf.app.common.enums.SceneElementFieldType;
import com.hand.hcf.app.common.message.ModuleMessageCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Created by kai.zhang on 2017-12-25.
 * 报销单头信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseReportHeaderCO implements Serializable {

    @InterfaceFieldAttribute(sequence = 10, msgCode = ModuleMessageCode.EXP_REPORT_HEADER_ID, elementFiled = SceneElementFieldType.TRANSACTION_HEADER_ID)
    private Long id;
    @InterfaceFieldAttribute(sequence = 20, msgCode = ModuleMessageCode.EXP_REPORT_HEADER_BUSINESS_CODE, elementFiled = SceneElementFieldType.DOCUMENT_NUMBER)
    private String businessCode;    //编码
    @InterfaceFieldAttribute(sequence = 30, msgCode = ModuleMessageCode.EXP_REPORT_HEADER_COMPANY_ID)
    private Long companyId;     //公司id
    @InterfaceFieldAttribute(sequence = 40, msgCode = ModuleMessageCode.EXP_REPORT_HEADER_UNIT_ID)
    private Long unitId;       //部门id
    @InterfaceFieldAttribute(sequence = 50, msgCode = ModuleMessageCode.EXP_REPORT_HEADER_APPLICATION_ID)
    private Long applicationId;          //申请人id
    private BigDecimal totalAmount;          //总金额
    @InterfaceFieldAttribute(sequence = 60, msgCode = ModuleMessageCode.EXP_REPORT_HEADER_FORM_ID)
    private Long formId;                 //表单ID(单据类型ID)
    @InterfaceFieldAttribute(sequence = 70, msgCode = ModuleMessageCode.EXP_REPORT_HEADER_REMARK)
    private String remark;                  //说明
    @InterfaceFieldAttribute(sequence = 80, msgCode = ModuleMessageCode.EXP_REPORT_HEADER_TENANT_ID, elementFiled = SceneElementFieldType.TENANT_ID)
    private Long tenantId;                  //租户id
    @InterfaceFieldAttribute(sequence = 90, msgCode = ModuleMessageCode.EXP_REPORT_HEADER_SET_OF_BOOKS_ID, elementFiled = SceneElementFieldType.SET_OF_BOOKS_ID)
    private Long setOfBooksId;                //账套id
    @InterfaceFieldAttribute(sequence = 93, elementFiled = SceneElementFieldType.CURRENCY_CODE)
    private String currencyCode;               //币种
    @InterfaceFieldAttribute(sequence = 96)
    private Double rate;                       //汇率
    private BigDecimal functionalAmount;                 //本币金额
    @InterfaceFieldAttribute(sequence = 100, msgCode = ModuleMessageCode.EXP_REPORT_HEADER_REPORT_DATE)
    private ZonedDateTime reportDate;//报账日期
    @InterfaceFieldAttribute(sequence = 110, msgCode = ModuleMessageCode.EXP_REPORT_HEADER_REPORT_STATUS)
    private String reportStatus;//报账单状态
    //    @InterfaceFieldAttribute(sequence = 120,msgCode = ModuleMessageCode.EXP_REPORT_HEADER_JE_CREATION_STATUS)
//    private Boolean jeCreationStatus;//创建凭证标志
//    @InterfaceFieldAttribute(sequence = 130,msgCode = ModuleMessageCode.EXP_REPORT_HEADER_REVERSED_FLAG)
//    private String reversedFlag;//反冲标志
    @InterfaceFieldAttribute(sequence = 140)
    private ZonedDateTime accountDate;          //账务日期 单据审核的界面给的参数，不需要审核的则默认提供系统日期
    @InterfaceFieldAttribute(sequence = 150)
    private String accountPeriod;       //账务期间  单据审核的界面给的参数，不需要审核的则默认提供系统日期对应期间
}
