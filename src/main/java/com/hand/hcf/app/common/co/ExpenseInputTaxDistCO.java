package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.annotation.InterfaceFieldAttribute;
import com.hand.hcf.app.common.enums.SceneElementFieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


/**
 * @Auther: zhu.zhao
 * @Date: 2019/04/14 09:08
 * 进项税单分摊
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseInputTaxDistCO {

    /**
     * 主键id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 0, display = false, elementFiled = SceneElementFieldType.TRANSACTION_DIST_ID)
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
     * 单据行id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 30, display = false, elementFiled = SceneElementFieldType.TRANSACTION_LINE_ID)
    private Long lineId;

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
     * 责任中心id
     */
    @InterfaceFieldAttribute(sequence = 80)
    private Long resCenterId;

    /**
     * 币种
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 90, elementFiled = SceneElementFieldType.CURRENCY_CODE)
    private String currencyCode;

    /**
     * 汇率
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 100)
    private Double rate;

    /**
     * 基数金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 110)
    private BigDecimal baseAmount;

    /**
     * 基数本币金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 120)
    private BigDecimal baseFunctionAmount;

    /**
     * 金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 130)
    private BigDecimal amount;

    /**
     * 维度相关
     */
    @InterfaceFieldAttribute(sequence = 140, dimension = true)
    private Long dimension1Id;
    @InterfaceFieldAttribute(sequence = 150, dimension = true)
    private Long dimension2Id;
    @InterfaceFieldAttribute(sequence = 160, dimension = true)
    private Long dimension3Id;
    @InterfaceFieldAttribute(sequence = 170, dimension = true)
    private Long dimension4Id;
    @InterfaceFieldAttribute(sequence = 180, dimension = true)
    private Long dimension5Id;
    @InterfaceFieldAttribute(sequence = 190, dimension = true)
    private Long dimension6Id;
    @InterfaceFieldAttribute(sequence = 200, dimension = true)
    private Long dimension7Id;
    @InterfaceFieldAttribute(sequence = 210, dimension = true)
    private Long dimension8Id;
    @InterfaceFieldAttribute(sequence = 220, dimension = true)
    private Long dimension9Id;
    @InterfaceFieldAttribute(sequence = 230, dimension = true)
    private Long dimension10Id;
    @InterfaceFieldAttribute(sequence = 240, dimension = true)
    private Long dimension11Id;
    @InterfaceFieldAttribute(sequence = 250, dimension = true)
    private Long dimension12Id;
    @InterfaceFieldAttribute(sequence = 260, dimension = true)
    private Long dimension13Id;
    @InterfaceFieldAttribute(sequence = 270, dimension = true)
    private Long dimension14Id;
    @InterfaceFieldAttribute(sequence = 280, dimension = true)
    private Long dimension15Id;
    @InterfaceFieldAttribute(sequence = 290, dimension = true)
    private Long dimension16Id;
    @InterfaceFieldAttribute(sequence = 300, dimension = true)
    private Long dimension17Id;
    @InterfaceFieldAttribute(sequence = 310, dimension = true)
    private Long dimension18Id;
    @InterfaceFieldAttribute(sequence = 320, dimension = true)
    private Long dimension19Id;
    @InterfaceFieldAttribute(sequence = 330, dimension = true)
    private Long dimension20Id;

    /**
     * 本币金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 340)
    private BigDecimal functionAmount;

    /**
     * 状态
     */
    @InterfaceFieldAttribute(sequence = 350)
    private String status;

    /**
     * 进项税单头id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 360, display = false, elementFiled = SceneElementFieldType.TRANSACTION_HEADER_ID)
    private Long headerId;
}
