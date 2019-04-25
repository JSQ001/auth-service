package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * 说明：
 *      此dto的id始终存的是通用表的id，明细表id单独有字段。使用时需要注意
 * Created by cbc on 2017/9/30.
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CashTransactionDataWebDTO extends DomainObjectDTO {


    /* 单据编号 */
    private String documentNumber;
    /* 单据头id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentHeaderId;
    /* 单据line id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentLineId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractHeaderId;
    /* 单据类型 */
    private String documentCategory;

    /* 申请人 */
    private String employeeName;

    private String employeeCode;

    /* 申请日期 */
//    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime requisitionDate;

    /* 总金额 */
    private BigDecimal amount;

    /* 可支付金额 */
    private BigDecimal payableAmount;

    /* 本次支付金额 */
    private BigDecimal currentPayAmount;

    /* 付款方式类型 */
    private String paymentMethodCategory;

    /* 收款方类型 */
    private String partnerCategory;

    /* 收款方 */
    private String partnerName;

    /* 收款方 ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long partnerId;

    /* 收款方 code*/
    private String partnerCode;

    /* 收款账号 */
    private String accountNumber;

    /*收款方户名 */
    private String accountName;

    /*版本号*/
    private Integer versionNumber;

    /*付款流水号*/
    private String billcode;

    /*付款批次号*/
    private String customerBatchNo;

    /*支付日期*/
//    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime payDate;

    /*明细表id*/
    private Long detailId;

    /* 申请人 工号*/
    private Long employeeId;

    /* 币种 */
    private String currency;


    /*  支付状态     */
    private String paymentStatus;

    /* 支付状态名称 */
    private String paymentStatusName;

    private String documentCategoryName;

    private String paymentMethodCategoryName;

    private String partnerCategoryName;

    private String partnerOid;

    private String documentTypeName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentCompanyId;

    private BigDecimal returnAmount;

    private String cshTransactionClassName;

    private BigDecimal ableReservedAmount; // 可反冲金额


    private Double exchangeRate;

    private String cshTransactionTypeCode;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionClassId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshFlowItemId;

    //是否冻结（延期）
    private Boolean frozenFlag;

    private String description; // 描述

    private BigDecimal writeOffTotalAmount;// 已核销金额

    private BigDecimal writeOffAmount;// 已核销金额

    private BigDecimal paidAmount; //已支付金额


}
