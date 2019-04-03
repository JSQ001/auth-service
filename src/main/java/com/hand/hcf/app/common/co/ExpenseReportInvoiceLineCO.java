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
 * @Date: 2019/3/26 16:58
 * 发票行
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseReportInvoiceLineCO {

    @NotNull
    @InterfaceFieldAttribute(sequence = 0, display = false)
    private Long id;
    @NotNull
    @InterfaceFieldAttribute(sequence = 10, display = false)
    private Long invoiceHeaderId;        //发票头id
    @NotNull
    @InterfaceFieldAttribute(sequence = 20)
    private Integer invoiceLineNum;     //发票行序号
    @InterfaceFieldAttribute(sequence = 30, display = false)
    private String goodsName;           //货物或应税劳务、服务名称
    @InterfaceFieldAttribute(sequence = 40, display = false)
    private String specificationModel;  //规格型号
    @InterfaceFieldAttribute(sequence = 50)
    private String unit;             //单位
    @InterfaceFieldAttribute(sequence = 60)
    private Long num;                 //数量
    @NotNull
    @InterfaceFieldAttribute(sequence = 70)
    private BigDecimal detailAmount;    //金额
    @NotNull
    @InterfaceFieldAttribute(sequence = 80)
    private String taxRate;             //税率
    @NotNull
    @InterfaceFieldAttribute(sequence = 90)
    private BigDecimal taxAmount;       //税额
    @InterfaceFieldAttribute(sequence = 100, elementFiled = SceneElementFieldType.CURRENCY_CODE)
    private String currencyCode;        //币种
    @InterfaceFieldAttribute(sequence = 110)
    private BigDecimal exchangeRate;    //汇率
}
