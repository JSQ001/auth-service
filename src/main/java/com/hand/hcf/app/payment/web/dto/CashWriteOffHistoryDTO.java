package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/7/12 11:29
 * @remark 预付款核销记录明细
 */
@Data
public class CashWriteOffHistoryDTO {

    /**
     * ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 支付明细ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionDetailId;
    /**
     * 核销金额
     */
    private BigDecimal writeOffAmount;
    /**
     * 单据类型
     */
    private String documentType;
    /**
     * 核销单据头id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentHeaderId;
    /**
     * 核销单据行id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentLineId;
    /**
     * 核销日期
     */
    private ZonedDateTime writeOffDate;
    /**
     * 租户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 核销状态:N未生效;P已生效;Y:已审核 | 核销反冲状态:N拒绝;P已提交;Y:已审核
     */
    private String status;
    /**
     * 核销状态描述
     */
    private String statusDescription;
    /**
     * 操作类型(核销:WRITE_OFF；核销反冲:WRITE_OFF_RESERVED)
     */
    private String operationType;
    /**
     * 账套ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 对公报账头ID
     */
    private Long expReportHeaderId;
    /**
     * 核销单据编号
     */
    private String documentNumber;
    /**
     * 核销单据行序号
     */
    private Integer documentLineNumber;
    /**
     * 核销单据申请人ID
     */
    private Long documentApplicantId;
    /**
     * 核销单据申请人名称
     */
    private String documentApplicantName;
    /**
     * 币种
     */
    private String currency;
    /**
     * 单据类型
     */
    private String documentFormName;
    /**
     * 申请日期
     */
    private ZonedDateTime requisitionDate;
    /**
     * 金额
     */
    private BigDecimal amount;
}
