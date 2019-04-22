package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.annotation.InterfaceFieldAttribute;
import com.hand.hcf.app.common.enums.SceneElementFieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Auther: zhu.zhao
 * @Date: 2019/04/14 09:08
 * 进项税单行
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseInputTaxLineCO implements Serializable {

    /**
     * 主键id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 0, display = false, elementFiled = SceneElementFieldType.TRANSACTION_LINE_ID)
    private Long id;

    /**
     * 租户id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 10, display = false)
    private Long tenantId;

    /**
     * 账套id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 20, display = false)
    private Long setOfBooksId;

    /**
     * 进项税单头id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 30, display = false, elementFiled = SceneElementFieldType.TRANSACTION_HEADER_ID)
    private Long headerId;

    /**
     * 用途类型
     */
    @InterfaceFieldAttribute(sequence = 40)
    private String useType;

    /**
     * 计算比例
     */
    @InterfaceFieldAttribute(sequence = 50)
    private Long transferProportion;

    /**
     * 公司id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 60)
    private Long companyId;

    /**
     * 部门id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 70)
    private Long departmentId;

    /**
     * 币种
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 80)
    private String currencyCode;

    /**
     * 汇率
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 90)
    private Double rate;

    /**
     * 基数金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 100)
    private BigDecimal baseAmount;

    /**
     * 基数本币金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 110)
    private BigDecimal baseFunctionAmount;

    /**
     * 金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 120)
    private BigDecimal amount;

    /**
     * 本币金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 130)
    private BigDecimal functionAmount;

    /**
     * 备注
     */
    @InterfaceFieldAttribute(sequence = 140)
    private String description;

    /**
     * 状态
     */
    @InterfaceFieldAttribute(sequence = 150)
    private String status;
}
