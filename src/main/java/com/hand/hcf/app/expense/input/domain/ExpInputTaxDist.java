package com.hand.hcf.app.expense.input.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

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
@TableName("exp_input_tax_dist")
public class ExpInputTaxDist extends Domain {
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
     * 单据行ID
     */
    @NotNull
    @TableField("input_tax_line_id")
    private Long inputTaxLineId;

    /**
     * 分摊行id
     */
    @NotNull
    @TableField("exp_report_dist_id")
    private Long expReportDistId;


    /**
     * 用途类型
     */
    @NotNull
    @TableField("use_type")
    private String useType;


    /**
     * 计算比例
     */
    @NotNull
    @TableField("transfer_proportion")
    private BigDecimal transferProportion;

    /**
     * 公司
     */
    @NotNull
    @TableField("company_id")
    private Long CompanyId;

    /**
     * 部门
     */
    @NotNull
    @TableField("department_id")

    private Long departmentId;
    /**
     * 责任中心
     */
    @NotNull
    @TableField("responsibility_center_id")
    private Long responsibilityCenterId;

    /**
     * 维度1
     */
    @NotNull
    @TableField("dimension1_id")
    private Long dimension1Id;

    /**
     * 维度2
     */
    @NotNull
    @TableField("dimension2_id")
    private Long dimension2Id;
    /**
     * 维度3
     */
    @NotNull
    @TableField("dimension3_id")
    private Long dimension3Id;
    /**
     * 维度4
     */
    @NotNull
    @TableField("dimension4_id")
    private Long dimension4Id;
    /**
     * 维度5
     */
    @NotNull
    @TableField("dimension5_id")
    private Long dimension5Id;
    /**
     * 维度6
     */
    @NotNull
    @TableField("dimension6_id")
    private Long dimension6Id;
    /**
     * 维度7
     */
    @NotNull
    @TableField("dimension7_id")
    private Long dimension7Id;
    /**
     * 维度8
     */
    @NotNull
    @TableField("dimension8_id")
    private Long dimension8Id;
    /**
     * 维度9
     */
    @NotNull
    @TableField("dimension9_id")
    private Long dimension9Id;
    /**
     * 维度10
     */
    @NotNull
    @TableField("dimension10_id")
    private Long dimension10Id;
    /**
     * 维度11
     */
    @NotNull
    @TableField("dimension11_id")
    private Long dimension11Id;
    /**
     * 维度12
     */
    @NotNull
    @TableField("dimension12_id")
    private Long dimension12Id;
    /**
     * 维度13
     */
    @NotNull
    @TableField("dimension13_id")
    private Long dimension13Id;
    /**
     * 维度14
     */
    @NotNull
    @TableField("dimension14_id")
    private Long dimension14Id;
    /**
     * 维度15
     */
    @NotNull
    @TableField("dimension15_id")
    private Long dimension15Id;
    /**
     * 维度16
     */
    @NotNull
    @TableField("dimension16_id")
    private Long dimension16Id;
    /**
     * 维度17
     */
    @NotNull
    @TableField("dimension17_id")
    private Long dimension17Id;

    /**
     * 维度18
     */
    @NotNull
    @TableField("dimension18_id")
    private Long dimension18Id;
    /**
     * 维度19
     */
    @NotNull
    @TableField("dimension19_id")
    private Long dimension19Id;
    /**
     * 维度20
     */
    @NotNull
    @TableField("dimension20_id")
    private Long dimension20Id;
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
     * 费用类型ID
     */
    @TableField(exist = false)
    private  Long expenseTypeId;

}
