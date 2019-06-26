package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Created by 龚前军 on 2019/3/25.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrepaymentRequisitionReleaseCO {
    private Long id;

    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 账套
     */
    private Long setOfBooksId;

    /**
     * 来源单据类别 (以编码规则单据类别为准)
     * EXP_REQUISITION 申请单
     */
    private String sourceDocumentCategory;

    /**
     * 来源单据id
     */
    private Long sourceDocumentId;

    /**
     * 关联单据类别 (以编码规则单据类别为准)
     * CSH_PREPAYMENT 预付款单
     */
    private String relatedDocumentCategory;

    /**
     * 关联单据id
     */
    private Long relatedDocumentId;

    /**
     * 关联单据行id
     */
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

    private ZonedDateTime createdDate;

    private Long createdBy;
}
