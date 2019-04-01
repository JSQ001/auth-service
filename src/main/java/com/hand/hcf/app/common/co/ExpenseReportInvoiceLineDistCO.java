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
 * @author: chenzhipeng
 * @Date: 2019/3/26 17:13
 * 发票分配行
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseReportInvoiceLineDistCO {

    /**
     * 发票分配行ID
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 0, display = false)
    private Long id;
    /**
     * 发票行ID
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 10, display = false)
    private Long invoiceLineId;
    /**
     * 发票ID
     */
    @NotNull
    private Long invoiceHeaderId;
    /**
     * 货物或应税劳务、服务名称
     */
    @InterfaceFieldAttribute(sequence = 20, display = false)
    private String goodsName;
    /**
     * 规格型号
     */
    @InterfaceFieldAttribute(sequence = 30, display = false)
    private String specificationModel;
    /**
     * 单位
     */
    @InterfaceFieldAttribute(sequence = 40)
    private String unit;
    /**
     * 数量
     */
    @InterfaceFieldAttribute(sequence = 50)
    private Long num;
    /**
     * 金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 60)
    private BigDecimal detailAmount;
    /**
     * 税率
     */
    @InterfaceFieldAttribute(sequence = 70)
    @NotNull
    private String taxRate;
    /**
     * 税额
     */
    @InterfaceFieldAttribute(sequence = 80)
    @NotNull
    private BigDecimal taxAmount;
    /**
     * 币种
     */
    @InterfaceFieldAttribute(sequence = 90, elementFiled = SceneElementFieldType.CURRENCY_CODE)
    private String currencyCode;
    /**
     * 汇率
     */
    @InterfaceFieldAttribute(sequence = 100)
    private BigDecimal exchangeRate;
}
