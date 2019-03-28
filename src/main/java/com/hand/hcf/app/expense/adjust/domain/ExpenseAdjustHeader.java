package com.hand.hcf.app.expense.adjust.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Created by silence on 2018/3/16.
 */
@TableName(value = "exp_adjust_header")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseAdjustHeader extends Domain {

    // 费用调整单编号
    private String documentNumber;

    // 租户ID
    @TableField(value = "tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    // 账套ID
    @TableField(value = "set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    // 单据类型id
    @TableField(value = "exp_adjust_type_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expAdjustTypeId;

    // 公司id
    @TableField(value = "company_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "公司不允许为空！")
    private Long companyId;

    // 部门id
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "部门不允许为空！")
    private Long unitId;

    // 申请人id
    @TableField(value = "employee_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "申请人不允许为空！")
    private Long employeeId;

    // 申请人日期
    @TableField(value = "adjust_date")
    private ZonedDateTime adjustDate;

    // 说明
    @TableField(value = "description")
    @NotNull(message = "备注不允许为空！")
    private String description;

    // 币种
    @TableField(value = "currency_code")
    @NotNull
    private String currencyCode;

    // 汇率
    @TableField(value = "exchange_rate")
    private BigDecimal exchangeRate;

    // 单据状态
    @TableField(value = "status")
    private Integer status;

    // 审核状态
    @TableField(value = "audit_flag")
    private String auditFlag;

    // 审核日期
    @TableField(value = "audit_date")
    private String auditDate;

    // 创建凭证标志
    @TableField(value = "je_creation_status")
    private String jeCreationStatus;

    // 创建凭证日期
    @TableField(value = "je_creation_date")
    private ZonedDateTime jeCreationDate;

    //分摊行金额的总和
    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    // 部门oid
    private String unitOid;

    // 申请人oid
    @TableField(value = "application_oid")
    private String applicationOid;

    // 单据oid
    @TableField(value = "document_oid")
    private String documentOid;
    // 表单oid
    @TableField(value = "form_oid")
    private String formOid;

    /**
     * 是否超预算
     */
    @TableField("over_budget_flag")
    private Boolean overBudgetFlag;
    /**
     * 预算校验信息
     */
    @TableField("budget_check_message")
    private String budgetCheckMessage;

    /**
     * 增加附件
     */
    @TableField("attachment_oid")
    private String attachmentOid;

    @TableField("functional_amount")
    private BigDecimal functionalAmount; // 本位币金额

    /**
     * 调整类型 1001 -- 费用分摊 1002--费用补录
     */
    private String adjustTypeCategory;

    /**
     * 是否预算管控
     */
    private Boolean budgetFlag;

    /**
     * 是否核算
     */
    private Boolean accountFlag;
}
