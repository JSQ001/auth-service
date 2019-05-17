package com.hand.hcf.app.prepayment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.annotation.ExcelDomainField;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * <p>
 * 预付款单头表
 * </p>
 *
 * @author baochao.chen@hand-china.com
 * @since 2017-10-26
 */
@Data
@TableName("csh_payment_requisition_head")
public class CashPaymentRequisitionHead extends Domain {

    /**
     * 租户id
     */
    @TableField("tenant_id")

    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 公司id
     */

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("company_id")
    private Long companyId;
    @TableField(exist = false)
    private String companyName;
    /**
     * 部门id
     */

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("unit_id")
    private Long unitId;
    @TableField(exist = false)
    private String unitName;
    /**
     * 员工id
     */

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("employee_id")
    private Long employeeId;
    @TableField(exist = false)
    private String employeeName;
    /**
     * 预付款单编号
     */

    @TableField("requisition_number")
    private String requisitionNumber;
    /**
     * 申请日期
     */

    @TableField("requisition_date")
    private ZonedDateTime requisitionDate;

    @TableField(exist = false)
    private String stringRequisitionDate;
    /**
     * 预付款单类型id
     */
    @NotNull
    @TableField("payment_req_type_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentReqTypeId;

    /**
     * 说明
     */
    private String description;
    /**
     * 附件数
     */
    @TableField(value = "attachment_num", strategy = FieldStrategy.IGNORED)
    private Long attachmentNum;
    /**
     * 审批状态
     */
    private int status;
    /**
     * 审批日期
     */
    @TableField("approval_date")
    private ZonedDateTime approvalDate;
    /**
     * 审批人id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "approved_by", strategy = FieldStrategy.IGNORED)
    private Long approvedBy;
    /**
     * 审核标志
     */
    @TableField(value = "audit_flag", strategy = FieldStrategy.IGNORED)
    private Boolean auditFlag;
    /**
     * 审核日期
     */
    @TableField("audit_date")
    private ZonedDateTime auditDate;

    /**
     * 预付款金额
     */
    @TableField("advance_payment_amount")
    @ExcelDomainField(align = "right",dataFormat = "#,##0.00")
    private BigDecimal advancePaymentAmount;

    @TableField(value = "attachment_oid", strategy = FieldStrategy.IGNORED)
    private String attachmentOid;

    //以下字段工作流使用到

    /*申请人oid*/
    @TableField("application_oid")
    private String applicationOid;


    /*表单oid*/
    @TableField(value = "form_oid", strategy = FieldStrategy.IGNORED)
    private String formOid;

    /*部门oid*/
    @TableField(value = "unit_oid", strategy = FieldStrategy.IGNORED)
    private String unitOid;

    /*员工oid*/
    @TableField(value = "emp_oid", strategy = FieldStrategy.IGNORED)
    private String empOid;

    /*单据oid*/
    @TableField("document_oid")
    private String documentOid;

    /*单据类型*/
    @TableField("document_type")
    private Integer documentType;

    //审批备注
    @TableField("approval_remark")
    private String approvalRemark;

    //是否走工作流标志
    @TableField("if_workflow")
    private  Boolean ifWorkflow;

    //提交日期
    @TableField("submit_date")
    private ZonedDateTime submitDate;


    //是否从申请单创建(是：true，不是：false)
    @TableField("req_in")
    private Boolean reqIn;


    @TableField("check_by")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long checkBy;

    @TableField(exist = false)

    private String createByName;
    @TableField(exist = false)
    private String typeName;

    //核销金额（推送支付平台的单据才有）
    @TableField(exist = false)
    @ExcelDomainField(align = "right",dataFormat = "#,##0.00")
    private BigDecimal writedAmount;

    //未核销金额
    @TableField(exist = false)
    @ExcelDomainField(align = "right",dataFormat = "#,##0.00")
    private BigDecimal noWritedAmount;

    @TableField(exist = false)
    private String statusName;

    //已付金额
    @TableField(exist = false)
    @ExcelDomainField(align = "right",dataFormat = "#,##0.00")
    private BigDecimal paidAmount;


    //已退金额
    @TableField(exist = false)
    @ExcelDomainField(align = "right",dataFormat = "#,##0.00")
    private BigDecimal returnAmount;

    //本位币币种
    @TableField(exist = false)
    private String currency;

    /**
     * 关联申请id（申请单主动关联预付款单）
     */
    @TableField(value = "ref_document_id")
    private Long refDocumentId;

    /**
     * 账套ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    /**
     * 账套代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "账套代码",dataType = "String")
    private String setOfBooksCode;

    /**
     * 账套名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "账套名称",dataType = "String")
    private String setOfBooksName;

}
