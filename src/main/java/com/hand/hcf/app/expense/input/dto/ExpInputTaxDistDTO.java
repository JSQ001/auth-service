package com.hand.hcf.app.expense.input.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/2/28 15:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpInputTaxDistDTO {

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 账套ID
     */

    private Long setOfBooksId;

    /**
     * 单据行ID
     */

    private Long inputTaxLineId;

    /**
     * 分摊行id
     */

    private Long expReportDistId;


    /**
     * 用途类型
     */

    private String useType;


    /**
     * 计算比例
     */

    private Long transferProportion;

    /**
     * 公司
     */

    private Long CompanyId;

    /**
     * 部门
     */

    private Long departmentId;
    /**
     * 责任中心
     */
    private Long responsibilityCenterId;


    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 汇率
     */
    @TableField("rate")
    private BigDecimal rate;

    /**
     * 基数金额
     */
    private BigDecimal baseAmount;

    /**
     * 基数本币金额
     */
    private BigDecimal baseFunctionAmount;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 本币金额
     */
    private BigDecimal functionAmount;


    /**
     * 状态
     */
    private String status;

    /**
     * 审核状态
     */
    private String auditStatus;


    /**
     * 反冲状态
     */
    private String reverseFlag;
}
