package com.hand.hcf.app.payment.web.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.common.co.AttachmentCO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/5/31 17:07
 * @remark 核销反冲信息
 */
@Data
public class CashWriteOffReserveDTO {
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
     * 期间
     */
    private String periodName;            
    /**
     * 租户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;                
    /**
     * 核销状态:N未生效;P已生效;Y:已核算 | 核销反冲状态:N拒绝;P已提交;Y:已审核
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
     * 审核意见
     */
    private String approvalOpinions;     
    /**
     * 审核人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long approvalId;
    /**
     * 审核人名称
     */
    private String approvalName;
    /**
     * 核销单据编号
     */
    private String documentNumber;       
    /**
     * 核销单据申请人ID
     */
    private Long documentApplicantId;
    /**
     * 核销单据申请人名称
     */
    private String documentApplicantName;
    /**
     * 单据创建日期
     */
    private ZonedDateTime documentCreatedDate;     
    /**
     * 附件OID, ','为分隔符
     */
    private String attachmentOid;
    /**
     * 被核销单据编号(预付款单)
     */
    private String sourceDocumentNumber;
    /**
     * 被核销单据行ID(预付款单)
     */
    private Long sourceDocumentLineId;
    /**
     * 支付流水号
     */
    private String billCode;
    /**
     * 本次反冲金额 - 默认为可反冲金额
     */
    private BigDecimal reversedAmount;
    /**
     * 对公报账相关信息
     */
    private CashWriteOffReserveExpReportDTO cashWriteOffReserveExpReport;
    /**
     * 预付款相关信息
     */
    private CashWriteOffReservePrepaymentRequisitionDTO cashWriteOffReservePrepaymentRequisition;
    /**
     * 来源核销ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sourceWriteOffId;
    /**
     * 币种
     */
    private String currency;
    /**
     * 是否生成凭证
     */
    private String isAccount;
    /**
     * 创建人ID
     */
    private Long createdBy;
    /**
     * 创建人代码
     */
    private String createdCode;
    /**
     * 创建人名称
     */
    private String createdName;
    /**
     * 待反冲记录反冲历史
     */
    List<CashWriteOffReserveDTO> cashWriteOffReverseHistory;

    private List<AttachmentCO> attachments;     //附件详情
}
