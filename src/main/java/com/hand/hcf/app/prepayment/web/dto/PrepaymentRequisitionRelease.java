package com.hand.hcf.app.prepayment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Created by 刘亮 on 2018/5/10.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrepaymentRequisitionRelease {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 租户
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    /**
     * 账套
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    /**
     * 来源单据类别 (以编码规则单据类别为准)
     * EXP_REQUISITION 申请单
     */
    private String sourceDocumentCategory;

    /**
     * 来源单据id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sourceDocumentId;

    /**
     * 关联单据类别 (以编码规则单据类别为准)
     * CSH_PREPAYMENT 预付款单
     */
    private String relatedDocumentCategory;

    /**
     * 关联单据id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long relatedDocumentId;

    /**
     * 关联单据行id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long relatedDocumentLineId;

    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 汇率
     */
    private Double exchangeRate;

    /**
     * 金额
     */
    private BigDecimal amount;

    public Double AmountToDouble(){
        return amount != null ? amount.doubleValue() : 0D;
    }

    /**
     * 本币金额
     */
    private BigDecimal functionalAmount;

    /**
     * 状态 N:未生效;Y:已生效
     * 现在先定为单据提交时生成释放数据，所以全部数据都是生效的
     */
    private String status;

    private ZonedDateTime lastModifiedDate;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastModifiedBy;

    private ZonedDateTime createdDate;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long createdBy;
}
