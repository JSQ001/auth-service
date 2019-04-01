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
 * @Auther: chenzhipeng
 * @Date: 2019/3/26 22:46
 * 对公报账税金分摊行(分摊税行)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseReportTaxDistCO {

    /**
     * 分摊税行ID
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 0, display = false, elementFiled = SceneElementFieldType.TRANSACTION_DIST_ID)
    private Long id;
    /**
     * 分摊行ID
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 10, display = false)
    private Long distId;
    /**
     * 报账单行id
     */
    @NotNull
    private Long lineId;
    /**
     * 报账单头ID
     */
    @NotNull
    private Long headerId;
    /**
     * 发票分配ID
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 20, display = false)
    private Long invoiceDistId;
    /**
     * 发票行ID
     */
    @NotNull
    private Long invoiceLineId;
    /**
     * 发票ID
     */
    @NotNull
    private Long invoiceHeaderId;
    /**
     * 公司id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 30)
    private Long companyId;
    /**
     * 部门id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 40)
    private Long unitId;
    @InterfaceFieldAttribute(sequence = 50)
    private Long resCenterId;                //责任中心id
    @NotNull
    @InterfaceFieldAttribute(sequence = 60)
    private Long expenseTypeId;     //费用类型ID
    @NotNull
    @InterfaceFieldAttribute(sequence = 70, elementFiled = SceneElementFieldType.CURRENCY_CODE)
    private String currencyCode;               //币种CODE
    @InterfaceFieldAttribute(sequence = 80)
    private BigDecimal taxAmount;                          //税额分摊额
    @InterfaceFieldAttribute(sequence = 90)
    private BigDecimal taxFunctionAmount;                          //税本币分摊金额
    @InterfaceFieldAttribute(sequence = 100, dimension = true)
    private Long dimension1Id;                  //维度相关
    @InterfaceFieldAttribute(sequence = 110, dimension = true)
    private Long dimension2Id;
    @InterfaceFieldAttribute(sequence = 120, dimension = true)
    private Long dimension3Id;
    @InterfaceFieldAttribute(sequence = 130, dimension = true)
    private Long dimension4Id;
    @InterfaceFieldAttribute(sequence = 140, dimension = true)
    private Long dimension5Id;
    @InterfaceFieldAttribute(sequence = 150, dimension = true)
    private Long dimension6Id;
    @InterfaceFieldAttribute(sequence = 160, dimension = true)
    private Long dimension7Id;
    @InterfaceFieldAttribute(sequence = 170, dimension = true)
    private Long dimension8Id;
    @InterfaceFieldAttribute(sequence = 180, dimension = true)
    private Long dimension9Id;
    @InterfaceFieldAttribute(sequence = 190, dimension = true)
    private Long dimension10Id;
    @InterfaceFieldAttribute(sequence = 200, dimension = true)
    private Long dimension11Id;
    @InterfaceFieldAttribute(sequence = 210, dimension = true)
    private Long dimension12Id;
    @InterfaceFieldAttribute(sequence = 220, dimension = true)
    private Long dimension13Id;
    @InterfaceFieldAttribute(sequence = 230, dimension = true)
    private Long dimension14Id;
    @InterfaceFieldAttribute(sequence = 240, dimension = true)
    private Long dimension15Id;
    @InterfaceFieldAttribute(sequence = 250, dimension = true)
    private Long dimension16Id;
    @InterfaceFieldAttribute(sequence = 260, dimension = true)
    private Long dimension17Id;
    @InterfaceFieldAttribute(sequence = 270, dimension = true)
    private Long dimension18Id;
    @InterfaceFieldAttribute(sequence = 280, dimension = true)
    private Long dimension19Id;
    @InterfaceFieldAttribute(sequence = 290, dimension = true)
    private Long dimension20Id;

}
