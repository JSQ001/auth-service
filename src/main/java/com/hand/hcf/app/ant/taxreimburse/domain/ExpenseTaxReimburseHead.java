package com.hand.hcf.app.ant.taxreimburse.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author xu.chen02@hand-china.com
 * @create 2019/6/6 14:59
 * @remark 国内税金缴纳报账单头表
 */
@Data
@TableName("exp_tax_reimburse_head")
public class ExpenseTaxReimburseHead extends Domain{
    /**
     * 单据编号
     */
    @TableField(value = "requisition_number")
    private String requisitionNumber;

    /**
     * 租户
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 账套
     */
    @TableField(value = "set_of_books_id")
    private Long setOfBooksId;

    /**
     * 报账人公司
     */
    @NotNull
    @TableField(value = "company_id")
    private Long companyId;

    /**
     * 报账人部门
     */
    @TableField(value = "department_id")
    private Long departmentId;

    /**
     * 申请人(报账人)
     */
    @NotNull
    @TableField(value = "applicant_id")
    private Long applicantId;

    /**
     * 申请日期-报账日期
     */
    @TableField(value = "requisition_date")
    private ZonedDateTime requisitionDate;


    /**
     * 受益人公司
     */
    @NotNull
    @TableField(value = "benefited_company_id")
    private Long benefitedCompanyId;

    /**
     * 受益人部门
     */
    @NotNull
    @TableField(value = "benefited_depart_id")
    private Long benefitedDepartId;

    /**
     * 受益人部门名称
     */
    @TableField(value = "benefited_depart_name")
    private String benefitedDepartName;

    /**
     * 受益人-使用人
     */
    @NotNull
    @TableField(value = "benefited_id")
    private Long benefitedId;


    /**
     * 币种
     */
    @NotNull
    @TableField(value = "currency_code")
    private String currencyCode;

    /**
     * 汇率
     */
    @NotNull
    @TableField(value = "exchange_rate")
    private BigDecimal exchangeRate;

    /**
     * 总金额
     */
    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 本币金额
     */
    @TableField(value = "functional_amount")
    private BigDecimal functionalAmount;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 单据类型id
     */
    @NotNull
    @TableField(value = "document_type_id")
    private Long documentTypeId;

    /**
     * 单据类型名称
     */
    @NotNull
    @TableField(value = "document_type_name")
    private String documentTypeName;

    /**
     * 状态--非税金报账状态
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 审核状态
     */
    @TableField(value = "audit_flag")
    private String auditFlag;

    /**
     * 审核日期
     */
    @TableField(value = "audit_date")
    @ApiModelProperty(value = "审核日期",dataType = "String")
    private ZonedDateTime auditDate;

    /**
     * 单据OID
     */
    @TableField(value = "document_oid")
    private String documentOid;

    /**
     * 附件
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


    //以下字段为辅助字段,数据库中无具体的字段对应，用于显示在页面上
    /**
     * 申请人名称
     */
    @TableField(exist = false)
    private String applicantName;

    /**
     * 申请人编码
     */
    @TableField(exist = false)
    private String applicantCode;

    /**
     * 报账人公司名称
     */
    @TableField(exist = false)
    private String companyName;

    /**
     * 报账人部门名称
     */
    @TableField(exist = false)
    private String departmentName;

    /**
     * 受益人名称
     */
    @TableField(exist = false)
    private String benefitedName;

    /**
     * 受益人编码
     */
    @TableField(exist = false)
    private String benefitedCode;

    /**
     * 受益人公司名称
     */
    @TableField(exist = false)
    private String benefitedCompanyName;

    /**
     * 创建人名称
     */
    @TableField(exist = false)
    private String createByName;

    /**
     * 创建人编码
     */
    @TableField(exist = false)
    private String createByCode;

    //币种名称
    @TableField(exist = false)
    private String currencyName;

    /**
     * 单据类型名称
     */
    /*@TableField(exist = false)
    private String documentTypeName;*/


}
