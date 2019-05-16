package com.hand.hcf.app.expense.report.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 09:59
 * @remark 对公报账头表
 */
@Data
@TableName("exp_report_header")
@ApiModel(description = "报账单头信息")
public class ExpenseReportHeader extends Domain{
    /**
     * 单据编号
     */
    @TableField(value = "requisition_number")
    @ApiModelProperty(value = "单据编号",dataType = "String")
    private String requisitionNumber;

    /**
     * 租户
     */
    @TableField(value = "tenant_id")
    @ApiModelProperty(value = "租户",dataType = "Long")
    private Long tenantId;

    /**
     * 账套
     */
    @TableField(value = "set_of_books_id")
    @ApiModelProperty(value = "账套",dataType = "Long")
    private Long setOfBooksId;

    /**
     * 公司
     */
    @NotNull
    @TableField(value = "company_id")
    @ApiModelProperty(value = "公司",dataType = "Long")
    private Long companyId;

    /**
     * 預算部门
     */
    @NotNull
    @TableField(value = "budget_dep_id")
    private Long budgetDepId;

    /**
     * 部门
     */
    @NotNull
    @TableField(value = "department_id")
    @ApiModelProperty(value = "部门",dataType = "Long")
    private Long departmentId;

    /**
     * 申请人
     */
    @NotNull
    @TableField(value = "applicant_id")
    @ApiModelProperty(value = "申请人",dataType = "Long",required = true)
    private Long applicantId;

    /**
     * 需求方
     */
    @NotNull
    @TableField(value = "demander_id")
    private Long demanderId;

    /**
     * 币种
     */
    @NotNull
    @TableField(value = "currency_code")
    @ApiModelProperty(value = "币种",dataType = "String",required = true)
    private String currencyCode;

    /**
     * 区域
     */
    @TableField(value = "area_code")
    private String areaCode;

    /**
     * 汇率
     */
    @NotNull
    @TableField(value = "exchange_rate")
    @ApiModelProperty(value = "汇率",dataType = "BigDecimal",required = true)
    private BigDecimal exchangeRate;

    /**
     * 总金额
     */
    @TableField(value = "total_amount")
    @ApiModelProperty(value = "总金额",dataType = "BigDecimal")
    private BigDecimal totalAmount;

    /**
     * 本币金额
     */
    @TableField(value = "functional_amount")
    @ApiModelProperty(value = "本币金额",dataType = "BigDecimal")
    private BigDecimal functionalAmount;

    /**
     * 备注
     */
    @TableField(value = "description")
    @ApiModelProperty(value = "备注",dataType = "String")
    private String description;

    /**
     * 附件oid
     */
    @TableField(value = "attachment_oid")
    private String attachmentOid;
    /**
     * 附件OID集合
     */
    @TableField(exist = false)
    private List<String> attachmentOidList;
    /**
     * 附件信息
     */
    @TableField(exist = false)
    private List<AttachmentCO> attachments;

    /**
     * 单据类型id
     */
    @NotNull
    @TableField(value = "document_type_id")
    @ApiModelProperty(value = "单据类型id",dataType = "Long")
    private Long documentTypeId;

    /**
     * 是否有票
     */
    @NotNull
    @TableField(value = "is_invoice")
    private String isInvoice;

    /**
     * 申请日期
     */
    @TableField(value = "requisition_date")
    @ApiModelProperty(value = "申请日期",dataType = "ZonedDateTime")
    private ZonedDateTime requisitionDate;

    /**
     * 关联合同头ID
     */
    @TableField(value = "contract_header_id")
    @ApiModelProperty(value = "关联合同头ID",dataType = "Long")
    private Long contractHeaderId;

    /**
     * 状态
     */
    @TableField(value = "status")
    @ApiModelProperty(value = "状态",dataType = "Integer")
    private Integer status;

    /**
     * 审核状态
     */
    @TableField(value = "audit_flag")
    @ApiModelProperty(value = "审核状态",dataType = "String")
    private String auditFlag;

    /**
     * 审核日期
     */
    @TableField(value = "audit_date")
    @ApiModelProperty(value = "审核日期",dataType = "String")
    private ZonedDateTime auditDate;

    /**
     * 收款方类型（员工：EMPLOYEE；供应商：VENDER）
     */
    @TableField(value = "payee_category")
    @ApiModelProperty(value = "收款方类型（员工：EMPLOYEE；供应商：VENDER）",dataType = "String")
    private String payeeCategory;

    /**
     * 收款方id
     */
    @TableField(value = "payee_id")
    @ApiModelProperty(value = "收款方id",dataType = "Long")
    private Long payeeId;

    /**
     * 收款方账户
     */
    @TableField(value = "account_number")
    @ApiModelProperty(value = "收款方账户",dataType = "String")
    private String accountNumber;

    /**
     * 收款方户名
     */
    @TableField(value = "account_name")
    @ApiModelProperty(value = "收款方户名",dataType = "String")
    private String accountName;

    /**
     * 单据OID
     */
    @TableField(value = "document_oid")
    @ApiModelProperty(value = "单据OID",dataType = "String")
    private String documentOid;

    /**
     * 预算校验返回结果
     */
    @TableField(value = "budget_check_result")
    @ApiModelProperty(value = "预算校验返回结果",dataType = "String")
    private String budgetCheckResult;

    /**
     * 创建凭证标志
     */
    @TableField("je_creation_status")
    @ApiModelProperty(value = "创建凭证标志",dataType = "Boolean")
    private Boolean jeCreationStatus;

    /**
     * 创建凭证日期
     */
    @TableField("je_creation_date")
    @ApiModelProperty(value = "创建凭证日期",dataType = "ZonedDateTime")
    private ZonedDateTime jeCreationDate;

    /**
     * 纸质单据签收标志
     */
    @TableField("receipt_documents_flag")
    @ApiModelProperty(value = "纸质单据签收标志",dataType = "String")
    private String receiptDocumentsFlag;

    /**
     * 是否完全匹配标志
     */
    @TableField("sheer_mate_flag")
    private String sheerMateFlag;

    /**
     * 处理人ID
     */
    @TableField(value = "deal_user_id")
    private Long dealUserId;
    /**
     * 是否比对通过标志
     */
    @TableField(value = "comparison_flag")
    private String comparisonFlag;

    /**
     * 预算校验返回结果描述
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "预算校验返回结果描述",dataType = "String", readOnly = true)
    private String budgetCheckResultDesc;

    /**
     * 申请人名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "申请人名称",dataType = "String", readOnly = true)
    private String applicantName;
    /**
     * 申请人编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "申请人编码",dataType = "String", readOnly = true)
    private String applicantCode;
    /**
     * 表单ID
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "表单ID",dataType = "Long", readOnly = true)
    private Long formId;
    /**
     * 表单OID
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "表单OID",dataType = "UUID", readOnly = true)
    private UUID formOid;
    /**
     * 单据类型名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "单据类型名称",dataType = "String", readOnly = true)
    private String documentTypeName;
    /**
     * 纸质单据签收标志描述
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "纸质单据签收标志描述",dataType = "String", readOnly = true)
    private String receiptDocumentsFlagDesc;
    /**
     * 是否全部匹配描述
     */
    @TableField(exist = false)
    private String sheerMateFlagDesc;

    /**
     * 处理人名称
     */
    @TableField(exist = false)
    private String dealUserIdName;
    /**
     * 是否比对通过标志描述
     */
    @TableField(exist = false)
    private String comparisonFlagDesc;
}
