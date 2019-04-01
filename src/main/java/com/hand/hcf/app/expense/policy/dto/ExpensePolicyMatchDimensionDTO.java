package com.hand.hcf.app.expense.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 费用政策匹配维度DTO
 * @author shouting.cheng
 * @date 2019/2/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpensePolicyMatchDimensionDTO {

    /**
     * 费用政策类型标识（0是申请单，1是报账单）
     */
    private Integer expenseTypeFlag;
    /**
     * 费用类型ID
     */
    private Long expenseTypeId;
    /**
     * 申请人ID
     */
    private Long applicationId;
    /**
     * 币种
     */
    private String currencyCode;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 费用政策动态字段
     */
    List<DynamicFieldDTO> dynamicFields;
}
