package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @Description: 创建支付明细数据dto
 * @Date: Created in 9:58 2018/7/4
 * @Modified by
 */
@Data
public class CashTransactionDataCreateCO {

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long tenantId; // 租户id

    @NotNull
    private String documentCategory; // 业务大类

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long documentHeaderId; // 所属单据头id

    @NotNull
    private String documentNumber; // 单据编号

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId; // 申请人id

    @NotNull
    private String employeeName; // 申请人

    private ZonedDateTime requisitionDate; // 申请日期

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentLineId; // 来源单据行ID

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId; // 公司id

    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentCompanyId; // 付款机构

    @NotNull
    private BigDecimal amount; // 总金额

    @NotNull
    private String currency; // 币种

    private Double exchangeRate; // 汇率

    @NotNull
    private String partnerCategory; // 收款方类型

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long partnerId; // 收款方id

    @NotNull
    private String partnerCode; // 收款方代码

    private String partnerName; // 收款方名称

    private String accountName; // 收款方银行户名

    @NotNull
    private String accountNumber; // 收款方银行账号

    private String bankCode; // 收款方银行代码

    private String bankName; // 收款方银行名称

    private String bankBranchCode; // 收款方分行代码

    private String bankBranchName; // 收款方分行名称

    private String provinceCode; // 收款方分行所在省份代码

    private String provinceName; // 收款方分行所在省份名称

    private String cityCode; // 收款方分行所在城市代码

    private String cityName; // 收款方分行所在城市名称

    @NotNull
    private String paymentMethodCategory; // 付款方式类型
    @NotNull
    private String paymentType; // 付款方式
    @NotNull
    private String propFlag; // 账户属性

    private ZonedDateTime requisitionPaymentDate; // 计划付款日期

    @NotNull
    private String cshTransactionTypeCode; // 现金事务类型代码

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionClassId; // 现金事务分类id

    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshFlowItemId; // 现金流量项id

    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractHeaderId; // 关联合同头id

    @JsonSerialize(using = ToStringSerializer.class)
    private Long instalmentId; // 分期id

    private String remark; // 描述

    private Boolean frozenFlag; // 是否冻结

    private String attribute1;
    private String attribute2;
    private String attribute3;
    private String attribute4;
    private String attribute5;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentTypeId;//单据类型ID
    private String documentTypeName;//单据类型名称

    @JsonSerialize(using = ToStringSerializer.class)
    private Long sourceDataId;//来源通用支付信息表ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sourceHeaderId;//来源单据头ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sourceLineId;//来源单据行ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long applicationLineId;//付款单关联的申请单行ID
    @NotNull
    private String entityOid;  // 单据OID
    @NotNull
    private Integer entityType; // 实体类型
}
