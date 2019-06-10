package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Created by kai.zhang on 2017-10-30.
 */
@ApiModel(description = "借款/预付款核销实体类")
@Data
@TableName("csh_write_off")
public class CashWriteOff extends Domain {

    @ApiModelProperty(value = "支付明细ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("csh_transaction_detail_id")
    @NotNull
    private Long cshTransactionDetailId;             //支付明细ID

    @ApiModelProperty(value = "核销金额")
    @TableField("write_off_amount")
    @NotNull
    private BigDecimal writeOffAmount;           //核销金额

    @ApiModelProperty(value = "单据类型")
    @NotNull
    @TableField("document_type")
    private String documentType;                //单据类型

    @ApiModelProperty(value = "核销单据头id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("document_header_id")
    @NotNull
    private Long documentHeaderId;            //核销单据头id

    @ApiModelProperty(value = "核销单据行id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("document_line_id")
    @NotNull
    private Long documentLineId;            //核销单据行id

    @ApiModelProperty(value = "核销日期")
    @TableField(value = "write_off_date")
    private ZonedDateTime writeOffDate;           //核销日期

    @ApiModelProperty(value = "期间")
    @TableField("period_name")
    private String periodName;            //期间

    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("tenant_id")
    private Long tenantId;                //租户id

    @ApiModelProperty(value = "核销状态")
    @TableField("status")
    private String status;                //核销状态:N未生效;P已生效;Y:已审核 | 核销反冲状态:N拒绝;P已提交;Y:已审核

    @ApiModelProperty(value = "操作类型")
    @TableField("operation_type")
    private String operationType;        //操作类型(核销:WRITE_OFF；核销反冲:WRITE_OFF_RESERVED)

    @ApiModelProperty(value = "账套ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("set_of_books_id")
    private Long setOfBooksId;           //账套ID

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;               //备注

    @ApiModelProperty(value = "审核意见")
    @TableField("approval_opinions")
    private String approvalOpinions;     //审核意见

    @ApiModelProperty(value = "审核人ID")
    @TableField("approval_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long approvalId;             //审核人ID

    @ApiModelProperty(value = "核销单据编号")
    @TableField("document_number")
    private String documentNumber;       //核销单据编号

    @ApiModelProperty(value = "核销单据申请人")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("document_applicant_id")
    private Long documentApplicantId;            //核销单据申请人

    @ApiModelProperty(value = "单据创建日期")
    @TableField("document_created_date")
    private ZonedDateTime documentCreatedDate;       //单据创建日期

    @ApiModelProperty(value = "附件OID")
    @TableField("attachment_oid")
    private String attachmentOid;                //附件OID, ','为分隔符

    @ApiModelProperty(value = "来源核销ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("source_write_off_id")
    private Long sourceWriteOffId;             //来源核销ID

    @ApiModelProperty(value = "是否生成凭证")
    @TableField("is_account")
    private String isAccount;        //是否生成凭证 (仅作为核销反冲依据，核销数据是否生成凭证，主要以报销单信息为准)

}
