package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Created by kai.zhang on 2017-10-30.
 */
@Data
@TableName("csh_write_off")
public class CashWriteOff extends Domain {
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("csh_transaction_detail_id")
    @NotNull
    private Long cshTransactionDetailId;             //支付明细ID
    @TableField("write_off_amount")
    @NotNull
    private BigDecimal writeOffAmount;           //核销金额
    @NotNull
    @TableField("document_type")
    private String documentType;                //单据类型
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("document_header_id")
    @NotNull
    private Long documentHeaderId;            //核销单据头id
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("document_line_id")
    @NotNull
    private Long documentLineId;            //核销单据行id
    @TableField(value = "write_off_date")
    private ZonedDateTime writeOffDate;           //核销日期
    @TableField("period_name")
    private String periodName;            //期间
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("tenant_id")
    private Long tenantId;                //租户id
    @TableField("status")
    private String status;                //核销状态:N未生效;P已生效;Y:已审核 | 核销反冲状态:N拒绝;P已提交;Y:已审核
    @TableField("operation_type")
    private String operationType;        //操作类型(核销:WRITE_OFF；核销反冲:WRITE_OFF_RESERVED)
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("set_of_books_id")
    private Long setOfBooksId;           //账套ID

    @TableField("remark")
    private String remark;               //备注
    @TableField("approval_opinions")
    private String approvalOpinions;     //审核意见
    @TableField("approval_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long approvalId;             //审核人ID
    @TableField("document_number")
    private String documentNumber;       //核销单据编号
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("document_applicant_id")
    private Long documentApplicantId;            //核销单据申请人
    @TableField("document_created_date")
    private ZonedDateTime documentCreatedDate;       //单据创建日期
    @TableField("attachment_oid")
    private String attachmentOid;                //附件OID, ','为分隔符
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("source_write_off_id")
    private Long sourceWriteOffId;             //来源核销ID
    @TableField("is_account")
    private String isAccount;        //是否生成凭证 (仅作为核销反冲依据，核销数据是否生成凭证，主要以报销单信息为准)

}
