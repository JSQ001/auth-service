package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.annotation.InterfaceFieldAttribute;
import com.hand.hcf.app.common.enums.SceneElementFieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/3/2 15:45
 * @remark
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailCO implements Serializable {

    @InterfaceFieldAttribute(sequence = 10, elementFiled = {SceneElementFieldType.TRANSACTION_HEADER_ID,SceneElementFieldType.TRANSACTION_LINE_ID})
    private Long id;     //ID
    @InterfaceFieldAttribute(sequence = 20, elementFiled = SceneElementFieldType.DOCUMENT_NUMBER)
    private String billCode;    //支付流水号
    @InterfaceFieldAttribute(sequence = 30)
    private String customerBatchNo;    //支付批次号
    @InterfaceFieldAttribute(sequence = 40)
    private String paymentFileName;    //支付文件名
    @InterfaceFieldAttribute(sequence = 50, elementFiled = SceneElementFieldType.TENANT_ID)
    private Long tenantId;       //租户
    @InterfaceFieldAttribute(sequence = 60, elementFiled = SceneElementFieldType.SET_OF_BOOKS_ID)
    private Long setOfBooksId;       //账套
    @InterfaceFieldAttribute(sequence = 70)
    private Long documentCompanyId;        //单据公司ID
    @InterfaceFieldAttribute(sequence = 80)
    private Long paymentCompanyId;         //付款人公司ID
    @InterfaceFieldAttribute(sequence = 90)
    private Long draweeCompanyId;         //付款开户公司ID
    @InterfaceFieldAttribute(sequence = 100)
    private String draweeAccountNumber;          //付款方银行账号
    @InterfaceFieldAttribute(sequence = 110)
    private Long employeeId;         //申请人ID
    @InterfaceFieldAttribute(sequence = 120)
    private String documentCategory;        //业务大类
    @InterfaceFieldAttribute(sequence = 130)
    private String documentNumber;          //付款单据编号
    @InterfaceFieldAttribute(sequence = 140)
    private String remark;          //备注
    @InterfaceFieldAttribute(sequence = 150)
    private String paymentMethodCategory;          //付款方式大类
    @InterfaceFieldAttribute(sequence = 160)
    private String paymentTypeId;          //付款方式ID
    @InterfaceFieldAttribute(sequence = 170)
    private ZonedDateTime payDate;        //支付日期
    @InterfaceFieldAttribute(sequence = 180)
    private String payPeriod;         //支付期间
    @InterfaceFieldAttribute(sequence = 190)
    private String paymentStatus;        //付款状态
    @InterfaceFieldAttribute(sequence = 200)
    private String refundStatus;         //退票状态
    @InterfaceFieldAttribute(sequence = 210)
    private String paymentReturnStatus;         //退款状态
    @InterfaceFieldAttribute(sequence = 220)
    private String partnerCategory;           //收款方类型
    @InterfaceFieldAttribute(sequence = 230)
    private Long partnerId;           //收款方ID
    @InterfaceFieldAttribute(sequence = 240)
    private String partnerCode;         //收款方代码
    @InterfaceFieldAttribute(sequence = 250, elementFiled = SceneElementFieldType.CURRENCY_CODE)
    private String currency;           //币种
    @InterfaceFieldAttribute(sequence = 260)
    private Double doucmentExchangeRate;         //付款单据汇率
    @InterfaceFieldAttribute(sequence = 270)
    private Double exchangeRate;         //支付汇率
    @InterfaceFieldAttribute(sequence = 280)
    private BigDecimal amount;          //支付金额
    @InterfaceFieldAttribute(sequence = 290)
    private Long cshTransactionClassId;        //现金事务分类ID
    @InterfaceFieldAttribute(sequence = 300)
    private Long cashFlowItemId;          //现金流量项ID
    @InterfaceFieldAttribute(sequence = 360)
    private ZonedDateTime accountDate;          //账务日期
    @InterfaceFieldAttribute(sequence = 370)
    private String accountPeriod;       //账务期间

    // 20180525新增字段
    @InterfaceFieldAttribute(sequence = 95)
    private String draweeAccountName;    //付款方银行户名
    @InterfaceFieldAttribute(sequence = 133)
    private ZonedDateTime documentDate;      //付款单据日期
    private Long documentLineId;         //付款单据付款行ID
    private Long contractHeaderId;       //关联合同ID
    private Long sourceDataId;           //来源通用支付数据ID
    @InterfaceFieldAttribute(sequence = 137)
    private String sourceBillCode;       //原支付流水号
    @InterfaceFieldAttribute(sequence = 195)
    private String operationType;        //操作类型
    @InterfaceFieldAttribute(sequence = 215)
    private String reservedStatus;         //反冲状态
    @InterfaceFieldAttribute(sequence = 243)
    private String partnerAccountNumber;     //收款方银行账号
    @InterfaceFieldAttribute(sequence = 247)
    private String partnerAccountName;       //收款方户名
    @InterfaceFieldAttribute(sequence = 310)
    private String attribute1;          //备用字段1
    @InterfaceFieldAttribute(sequence = 320)
    private String attribute2;          //备用字段2
    @InterfaceFieldAttribute(sequence = 330)
    private String attribute3;          //备用字段3
    @InterfaceFieldAttribute(sequence = 340)
    private String attribute4;          //备用字段4
    @InterfaceFieldAttribute(sequence = 350)
    private String attribute5;          //备用字段5
}