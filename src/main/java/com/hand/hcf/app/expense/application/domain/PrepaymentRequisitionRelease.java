package com.hand.hcf.app.expense.application.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author qianjun.gong@hand-china.com
 * @create 2019/3/26
 * @remark 预付款释放申请信息
 */
@Data
@TableName("exp_prepayment_req_release")
public class PrepaymentRequisitionRelease extends Domain {
    /**
     * 租户
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 账套
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("set_of_books_id")
    private Long setOfBooksId;

    /**
     * 来源单据类别 (以编码规则单据类别为准)
     * EXP_REQUISITION 申请单
     */
    @TableField("source_doc_category")
    private String sourceDocumentCategory;

    /**
     * 来源单据id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("source_doc_id")
    private Long sourceDocumentId;

    /**
     * 关联单据类别 (以编码规则单据类别为准)
     * CSH_PREPAYMENT 预付款单
     */
    @TableField("related_doc_category")
    private String relatedDocumentCategory;

    /**
     * 关联单据id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("related_doc_id")
    private Long relatedDocumentId;

    /**
     * 关联单据行id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("related_doc_line_id")
    private Long relatedDocumentLineId;

    /**
     * 币种
     */
    @TableField("currency_code")
    private String currencyCode;

    /**
     * 汇率
     */
    @TableField("exchange_rate")
    private Double exchangeRate;

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;

    public Double AmountToDouble(){
        return amount != null ? amount.doubleValue() : 0D;
    }

    /**
     * 本币金额
     */
    @TableField("functional_amount")
    private BigDecimal functionalAmount;

    /**
     * 状态 N:未生效;Y:已生效
     * 现在先定为单据提交时生成释放数据，所以全部数据都是生效的
     */
    private String status;

}
