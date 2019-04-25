package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/5/31 17:08
 * @remark 核销反冲 预付款信息
 */
@Data
public class CashWriteOffReservePrepaymentRequisitionDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 预付单头id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentRequisitionHeaderId;
    /**
     * 关联申请id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long refDocumentId;
    /**
     * 租户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 公司id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    /**
     * 收款方类型
     */
    private String partnerCategory;
    /**
     * 收款方id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long partnerId;
    /**
     * 收款方代码
     */
    private String partnerCode;
    /**
     * 计划付款日期
     */
    private ZonedDateTime requisitionPaymentDate;
    /**
     * 付款方式类型
     */
    private String paymentMethodCategory;
    /**
     * 现金事务分类id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionClassId;
    /**
     * 现金流量项id
     */
    private Long cashFlowId;
    /**
     * 现金流量项代码
     */
    private String cashFlowCode;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 币种
     */
    private String currency;
    /**
     * 汇率
     */
    private Double exchangeRate;
    /**
     * 本位币金额
     */
    private BigDecimal functionAmount;
    /**
     * 描述
     */
    private String description;

    /**
     *  合同编号
     */
    private String contractNumber;

    /**
     * 合同头id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractId;
    /**
     * 收款方名称
     */
    private String partnerName;
    /**
     * 创建时间
     */
    private ZonedDateTime createdDate;
    /**
     * 创建人ID
     */
    private Long createdBy;
    /**
     * 最后更新时间
     */
    private ZonedDateTime lastUpdatedDate;
    /**
     * 最后更新人ID
     */
    private Long lastUpdatedBy;
}
