package com.hand.hcf.app.expense.application.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/13 11:23
 * @remark 申请释放信息
 */
@Data
@TableName("exp_prepayment_req_release")
public class ExpenseRequisitionReqRelease extends Domain{

    /**
     * 租户
     */
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 账套
     */
    @TableField("set_of_books_id")
    private Long setOfBooksId;

    /**
     * 来源单据类别
     * EXP_REQUISITION 申请单
     */
    @TableField("source_doc_category")
    private String sourceDocumentCategory;

    /**
     * 来源单据id
     */
    @TableField("source_doc_id")
    private Long sourceDocumentId;

    /**
     * 关联单据类别
     * PUBLIC_REPORT 预付款单
     */
    @TableField("related_doc_category")
    private String relatedDocumentCategory;

    /**
     * 关联单据id
     */
    @TableField("related_doc_id")
    private Long relatedDocumentId;

    /**
     * 关联单据行id
     */
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

    /**
     * 本币金额
     */
    @TableField("functional_amount")
    private BigDecimal functionalAmount;

    /**
     * 状态
     */
    @TableField("status")
    private  Integer status;
}
