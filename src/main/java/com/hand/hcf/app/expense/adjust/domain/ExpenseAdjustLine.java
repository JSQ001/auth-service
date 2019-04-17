package com.hand.hcf.app.expense.adjust.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Created by silence on 2018/3/16.
 */
@TableName(value = "exp_adjust_line")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseAdjustLine extends Domain {
    // 租户ID
    @TableField(value = "tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    // 账套ID
    @TableField(value = "set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    // 费用调整单头ID
    @TableField(value = "exp_adjust_header_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expAdjustHeaderId;

    // 单据行类型
    @TableField(value = "adjust_line_category")
    private String adjustLineCategory;

    // 公司id
    @TableField(value = "company_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    // 部门id
    @TableField(value = "unit_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long unitId;

    // 申请人id
    @TableField(value = "employee_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;

    // 申请人日期
    @TableField(value = "adjust_date")
    private ZonedDateTime adjustDate;

    // 说明
    @TableField(value = "description")
    private String description;

    // 币种
    @TableField(value = "currency_code")
    private String currencyCode;

    // 汇率
    @TableField(value = "exchange_rate")
    private BigDecimal exchangeRate;

    // 费用类型id
    @TableField(value = "expense_type_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expenseTypeId;

    // 金额
    @TableField(value = "amount")
    private BigDecimal amount;

    // 本币金额
    @TableField(value = "functional_amount")
    private BigDecimal functionalAmount;

    // 维度1
    @TableField(value = "dimension1_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension1Id;

    // 维度2
    @TableField(value = "dimension2_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension2Id;

    // 维度3
    @TableField(value = "dimension3_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension3Id;

    // 维度4
    @TableField(value = "dimension4_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension4Id;

    // 维度5
    @TableField(value = "dimension5_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension5Id;

    // 维度6
    @TableField(value = "dimension6_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension6Id;

    // 维度7
    @TableField(value = "dimension7_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension7Id;

    // 维度8
    @TableField(value = "dimension8_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension8Id;

    // 维度9
    @TableField(value = "dimension9_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension9Id;

    // 维度10
    @TableField(value = "dimension10_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension10Id;

    // 维度11
    @TableField(value = "dimension11_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension11Id;

    // 维度12
    @TableField(value = "dimension12_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension12Id;

    // 维度13
    @TableField(value = "dimension13_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension13Id;

    // 维度14
    @TableField(value = "dimension14_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension14Id;

    // 维度15
    @TableField(value = "dimension15_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension15Id;

    // 维度16
    @TableField(value = "dimension16_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension16Id;

    // 维度17
    @TableField(value = "dimension17_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension17Id;

    // 维度18
    @TableField(value = "dimension18_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension18Id;

    // 维度19
    @TableField(value = "dimension19_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension19Id;

    // 维度20
    @TableField(value = "dimension20_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension20Id;

    // 审核状态
    @TableField(value = "audit_flag")
    private String auditFlag;

    // 审核日期
    @TableField(value = "audit_date")
    private ZonedDateTime auditDate;

    // 源单据行id
    @TableField(value = "source_adjust_line_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sourceAdjustLineId;

    // 创建凭证标志
    @TableField(value = "je_creation_status")
    private String jeCreationStatus;

    // 创建凭证日期
    @TableField(value = "je_creation_date")
    private ZonedDateTime jeCreationDate;
    /**
     * 增加附件
     */
    @TableField("attachment_oid")
    private String attachmentOid;
}
