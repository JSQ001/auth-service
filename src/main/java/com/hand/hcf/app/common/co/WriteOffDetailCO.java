package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.annotation.InterfaceFieldAttribute;
import com.hand.hcf.app.common.enums.SceneElementFieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/4/9 17:56
 * @remark
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriteOffDetailCO {

    /**
     * 核销明细ID
     */
    @InterfaceFieldAttribute(sequence = 10, elementFiled = {SceneElementFieldType.TRANSACTION_LINE_ID})
    private Long id;
    /**
     * 租户ID
     */
    @InterfaceFieldAttribute(sequence = 20, elementFiled = SceneElementFieldType.TENANT_ID)
    private Long tenantId;
    /**
     * 账套ID
     */
    @InterfaceFieldAttribute(sequence = 30, elementFiled = SceneElementFieldType.SET_OF_BOOKS_ID)
    private Long setOfBooksId;
    /**
     * 操作类型
     */
    @InterfaceFieldAttribute(sequence = 40)
    private String operationType;
    /**
     * 被核销支付流水号ID
     */
    @InterfaceFieldAttribute(sequence = 50)
    private Long cshTransactionDetailId;
    /**
     * 被核销支付流水号
     */
    @InterfaceFieldAttribute(sequence = 60)
    private String cshTransactionDetailNumber;
    /**
     * 被核销币种
     */
    @InterfaceFieldAttribute(sequence = 70)
    private String prepaymentCurrency;
    /**
     * 被核销单据公司ID
     */
    @InterfaceFieldAttribute(sequence = 80)
    private Long prepaymentCompanyId;
    /**
     * 被核销单据部门ID
     */
    @InterfaceFieldAttribute(sequence = 90)
    private Long prepaymentUnitId;

    /**
     * 被核销单据责任中心ID
     */
    @InterfaceFieldAttribute(sequence = 95)
    private Long prepaymentResCenterId;

    /**
     * 核销付款方式大类
     */
    @InterfaceFieldAttribute(sequence = 100)
    private String prepaymentPaymentMethodCategory;
    /**
     * 核销付款方式ID
     */
    @InterfaceFieldAttribute(sequence = 110)
    private Long prepaymentPaymentTypeId;
    /**
     * 被核销现金事务分类ID
     */
    @InterfaceFieldAttribute(sequence = 120)
    private Long prepaymentTransactionClassId;
    /**
     * 被核销支付流水汇率
     */
    @InterfaceFieldAttribute(sequence = 130)
    private Double prepaymentExchangeRate;
    /**
     * 被核销收款方类型
     */
    @InterfaceFieldAttribute(sequence = 140)
    private String prepaymentPartnerCategory;
    /**
     * 被核销收款方ID
     */
    @InterfaceFieldAttribute(sequence = 150)
    private Long prepaymentPartnerId;
    /**
     * 被核销收款方代码
     */
    @InterfaceFieldAttribute(sequence = 160)
    private String prepaymentPartnerCode;
    /**
     * 核销单据类型
     */
    @InterfaceFieldAttribute(sequence = 170)
    private String documentType;
    /**
     * 核销单据币种
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 180, elementFiled = SceneElementFieldType.CURRENCY_CODE)
    private String documentCurrency;
    /**
     * 核销单据编号
     */
    @InterfaceFieldAttribute(sequence = 190, elementFiled = SceneElementFieldType.DOCUMENT_NUMBER)
    private String documentNumber;
    /**
     * 核销单据头ID
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 200, elementFiled = SceneElementFieldType.TRANSACTION_HEADER_ID)
    private Long documentHeaderId;
    /**
     * 核销单据行ID
     */
    @InterfaceFieldAttribute(sequence = 210)
    private Long documentLineId;
    /**
     * 核销单据公司ID
     */
    @InterfaceFieldAttribute(sequence = 220)
    private Long documentCompanyId;
    /**
     * 核销单据部门ID
     */
    @InterfaceFieldAttribute(sequence = 230)
    private Long documentUnitId;

    /**
     * 核销单据责任中心ID
     */
    @InterfaceFieldAttribute(sequence = 235)
    private Long documentResCenterId;

    /**
     * 核销单据汇率
     */
    @InterfaceFieldAttribute(sequence = 240)
    private Double documentExchangeRate;
    /**
     * 核销单据付款方式大类
     */
    @InterfaceFieldAttribute(sequence = 250)
    private String documentPaymentMethodCategory;
    /**
     * 核销单据付款方式ID
     */
//    @InterfaceFieldAttribute(sequence = 260)
//    private Long documentPaymentTypeId;
    /**
     * 核销单据单据现金分类ID
     */
    @InterfaceFieldAttribute(sequence = 270)
    private Long documentTransactionClassId;
    /**
     * 核销收款方类型
     */
    @InterfaceFieldAttribute(sequence = 280)
    private String documentPartnerCategory;
    /**
     * 核销收款方ID
     */
    @InterfaceFieldAttribute(sequence = 290)
    private Long documentPartnerId;
    /**
     * 核销收款方代码
     */
    @InterfaceFieldAttribute(sequence = 300)
    private String documentPartnerCode;
    /**
     * 核销金额
     */
    @InterfaceFieldAttribute(sequence = 310)
    private BigDecimal amount;
    /**
     * 备注
     */
    @InterfaceFieldAttribute(sequence = 320)
    private String remark;
    /**
     * 核销日期
     */
    @InterfaceFieldAttribute(sequence = 330)
    private ZonedDateTime writeOffDate;
    /**
     * 核销期间
     */
    @InterfaceFieldAttribute(sequence = 340)
    private String writeOffPeriod;
    /**
     * 账务日期
     */
    @InterfaceFieldAttribute(sequence = 350)
    private ZonedDateTime accountDate;
    /**
     * 账务期间
     */
    @InterfaceFieldAttribute(sequence = 360)
    private String accountPeriod;
}
