package com.hand.hcf.app.ant.accrual.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @description: 预提报账单头表
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/5/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("exp_accrued_expenses_header")
public class AccruedExpensesHeader extends Domain {
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
     * 公司
     */
    @NotNull
    @TableField(value = "company_id")
    private Long companyId;

    /**
     * 预算部门
     */
    @TableField(value = "budget_dep_id")
    private Long budgetDepId;

    /**
     * 受益部门
     */
    @TableField(value = "department_id")
    private Long departmentId;

    /**
     * 区域
     */
    @TableField(value = "region_id")
    private Long regionId;

    /**
     * 创建人id
     */
    @TableField(value = "applicant_id")
    private Long applicantId;

    /**
     * 创建人名称
     */
    @TableField(exist = false)
    private String applicantName;
    /**
     * 创建人编码
     */
    @TableField(exist = false)
    private String applicantCode;


    /**
     * 责任人编码
     */
    @TableField(exist = false)
    private String demanderCode;
    /**
     * 责任方
     */
    @TableField(value = "demander_id")
    private Long demanderId;

    /**
     * 责任人名称
     */
    @TableField(exist = false)
    private String demanderName;

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
    @TableField(value = "description")
    private String description;

    /**
     * 单据类型id
     */
    @NotNull
    @TableField(value = "document_type_id")
    private Long documentTypeId;

    /**
     * 申请日期
     */
    @TableField(value = "requisition_date")
    private ZonedDateTime requisitionDate;

    /**
     * 次月是否冲销标志
     */
    @TableField(value = "release_next_month")
    private Boolean releaseNextMonth;

    /**
     * 状态
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 单据类型名称
     */
    @TableField(exist = false)
    private String documentTypeName;

    /**
     * 表单ID
     */
    @TableField(exist = false)
    private Long formId;
    /**
     * 表单OID
     */
    @TableField(exist = false)
    private UUID formOid;

    //公司名称
    @TableField(exist = false)
    private String companyName;

    //币种名称
    @TableField(exist = false)
    private String currencyName;


    /**
     * 期间从
     */
    @TableField(value = "from_date")
    private String fromDate;

    /**
     * 期间至
     */
    @TableField(value = "to_date")
    private String toDate;

    //期间范围--只有年月
    @TableField(exist = false)
    private String fromToPeriod;

    /**
     * 审核状态
     */
    @TableField(value = "audit_flag")
    private String auditFlag;

    /**
     * 审核日期
     */
    @TableField(value = "audit_date")
    private ZonedDateTime auditDate;

    /**
     * 单据OID
     */
    @TableField(value = "document_oid")
    private String documentOid;

    /**
     * 创建凭证标志
     */
    @TableField("je_creation_status")
    private Boolean jeCreationStatus;

    /**
     * 创建凭证日期
     */
    @TableField("je_creation_date")
    private ZonedDateTime jeCreationDate;

}
