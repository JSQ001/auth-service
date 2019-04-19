package com.hand.hcf.app.expense.input.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @description
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/2/28 14:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("exp_input_tax_header")
public class ExpInputTaxHeader extends Domain {

    /**
     * 租户ID
     */
    @NotNull
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 账套ID
     */
    @NotNull
    @TableField("set_of_books_id")
    private Long setOfBooksId;

    /**
     * 单据编号
     */
    @TableField("document_number")
    private String documentNumber;

    /**
     * 员工
     */
    @NotNull
    @TableField("applicant_id")
    private Long applicantId;

    /**
     * 公司
     */
    @NotNull
    @TableField("company_id")

    private Long companyId;

    /**
     * 部门
     */
    @NotNull
    @TableField("department_id")
    private Long departmentId;

    /**
     * 业务日期
     */
    @NotNull
    @TableField("transfer_date")
    private ZonedDateTime transferDate;

    /**
     * 业务大类
     */
    @NotNull
    @TableField("transfer_type")
    private String transferType;

    /**
     * 计算比例
     */
    @NotNull
    @TableField("transfer_proportion")
    private Long transferProportion;

    /**
     * 用途类型
     */
    @NotNull
    @TableField("use_type")
    private String useType;

    /**
     * 币种
     */
    @NotNull
    @TableField("currency_code")
    private String currencyCode;

    /**
     * 汇率
     */
    @NotNull
    @TableField("rate")
    private Long rate;

    /**
     * 基数金额
     */
    @NotNull
    @TableField("base_amount")

    private BigDecimal baseAmount;

    /**
     * 基数本币金额
     */
    @NotNull
    @TableField("base_function_amount")

    private BigDecimal baseFunctionAmount;

    /**
     * 金额
     */
    @NotNull
    @TableField("amount")

    private BigDecimal amount;

    /**
     * 本币金额
     */
    @NotNull
    @TableField("function_amount")
    private BigDecimal functionAmount;


    /**
     * 备注
     */
    @TableField("description")
    private String description;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 审核状态
     */
    @TableField("audit_status")
    private String auditStatus;


    /**
     * 反冲状态
     */
    @TableField("reverse_flag")
    private String reverseFlag;

    /**
     * 关联附件的OID 用 , 分割
     */
    @TableField(value = "attachment_oid", strategy = FieldStrategy.IGNORED)
    private String attachmentOid;

    /**
     * 单据OID
     */
    @TableField("document_oid")
    private String documentOid;

    /**
     * 审核日期
     */
    @TableField("audit_date")
    private ZonedDateTime auditDate;

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
