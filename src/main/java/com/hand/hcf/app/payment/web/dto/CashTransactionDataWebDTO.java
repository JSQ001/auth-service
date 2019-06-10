package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * 说明：
 *      此dto的id始终存的是通用表的id，明细表id单独有字段。使用时需要注意
 * Created by cbc on 2017/9/30.
 */

@ApiModel(description = "通用支付数据")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CashTransactionDataWebDTO extends DomainObjectDTO {


    /* 单据编号 */
    @ApiModelProperty(value = "单据编号")
    private String documentNumber;

    /* 单据头id */
    @ApiModelProperty(value = "单据头id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentHeaderId;

    /* 单据line id */
    @ApiModelProperty(value = "单据行id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentLineId;

    @ApiModelProperty(value = "合同头id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractHeaderId;


    /* 单据类型 */
    @ApiModelProperty(value = "单据类型")
    private String documentCategory;

    /* 申请人 */
    @ApiModelProperty(value = "申请人")
    private String employeeName;

    @ApiModelProperty(value = "申请人code")
    private String employeeCode;

    /* 申请日期 */
    @ApiModelProperty(value = "申请日期")
//    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime requisitionDate;

    /* 总金额 */
    @ApiModelProperty(value = "总金额")
    private BigDecimal amount;

    /* 可支付金额 */
    @ApiModelProperty(value = "可支付金额")
    private BigDecimal payableAmount;

    /* 本次支付金额 */
    @ApiModelProperty(value = "本次支付金额")
    private BigDecimal currentPayAmount;

    /* 付款方式类型 */
    @ApiModelProperty(value = "付款方式类型")
    private String paymentMethodCategory;

    /* 付款方式 */
    @ApiModelProperty(value = "付款方式")
    private String paymentType;

    /* 账户属性 */
    @ApiModelProperty(value = "账户属性")
    private String propFlag;

    /* 收款方类型 */
    @ApiModelProperty(value = "收款方类型")
    private String partnerCategory;

    /* 收款方 */
    @ApiModelProperty(value = "收款方")
    private String partnerName;

    /* 收款方 ID*/
    @ApiModelProperty(value = "收款方id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long partnerId;

    /* 收款方 code*/
    @ApiModelProperty(value = "收款方code")
    private String partnerCode;

    /* 收款账号 */
    @ApiModelProperty(value = "收款账号")
    private String accountNumber;

    /*收款方户名 */
    @ApiModelProperty(value = "收款方户名")
    private String accountName;

    /*版本号*/
    @ApiModelProperty(value = "版本号")
    private Integer versionNumber;

    /*付款流水号*/
    @ApiModelProperty(value = "付款流水号")
    private String billcode;

    /*付款批次号*/
    @ApiModelProperty(value = "付款批次号")
    private String customerBatchNo;

    /*支付日期*/
    @ApiModelProperty(value = "支付日期")
//    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime payDate;

    /*明细表id*/
    @ApiModelProperty(value = "明细表id")
    private Long detailId;

    /* 申请人 工号*/
    @ApiModelProperty(value = "申请人工号")
    private Long employeeId;

    /* 币种 */
    @ApiModelProperty(value = "币种")
    private String currency;


    /*  支付状态     */
    @ApiModelProperty(value = "支付状态")
    private String paymentStatus;

    /* 支付状态名称 */
    @ApiModelProperty(value = "支付状态名称")
    private String paymentStatusName;

    @ApiModelProperty(value = "支付状态")
    private String documentCategoryName;

    @ApiModelProperty(value = "支付方式")
    private String paymentMethodCategoryName;

    @ApiModelProperty(value = "付款方式名称")
    private String paymentTypeName;

    @ApiModelProperty(value = "账户属性名称")
    private String propFlagName;

    @ApiModelProperty(value = "收款方类型")
    private String partnerCategoryName;

    @ApiModelProperty(value = "收款方id")
    private String partnerOid;

    @ApiModelProperty(value = "单据类型")
    private String documentTypeName;

    @ApiModelProperty(value = "支付方id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentCompanyId;

    @ApiModelProperty(value = "回收金额")
    private BigDecimal returnAmount;

    @ApiModelProperty(value = "现金事务类")
    private String cshTransactionClassName;

    @ApiModelProperty(value = "可反冲金额")
    private BigDecimal ableReservedAmount; // 可反冲金额

    @ApiModelProperty(value = "汇率")
    private Double exchangeRate;

    @ApiModelProperty(value = "现金事务类型code")
    private String cshTransactionTypeCode;

    @ApiModelProperty(value = "现金事务分类id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionClassId;

    @ApiModelProperty(value = "现金流量项id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshFlowItemId;

    //是否冻结（延期）
    @ApiModelProperty(value = "是否冻结(延期)")
    private Boolean frozenFlag;

    @ApiModelProperty(value = "描述")
    private String description; // 描述

    @ApiModelProperty(value = "已核销总金额")
    private BigDecimal writeOffTotalAmount;// 已核销金额

    @ApiModelProperty(value = "已核销金额")
    private BigDecimal writeOffAmount;// 已核销金额

    @ApiModelProperty(value = "已支付金额")
    private BigDecimal paidAmount; //已支付金额


}