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
import java.time.ZonedDateTime;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/3/26 16:13
 * 报销单行信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseReportLineCO implements Serializable {
    @NotNull
    @InterfaceFieldAttribute(sequence = 0, display = false, elementFiled = SceneElementFieldType.TRANSACTION_LINE_ID)
    private Long id;
    @NotNull
    @InterfaceFieldAttribute(sequence = 10, display = false,elementFiled = SceneElementFieldType.TRANSACTION_HEADER_ID)
    private Long headerId;        //报账单id
    @NotNull
    @InterfaceFieldAttribute(sequence = 20)
    private Long companyId;         //公司id
    @NotNull
    @InterfaceFieldAttribute(sequence = 30)
    private Long expenseTypeId;     //费用类型ID
    @NotNull
    @InterfaceFieldAttribute(sequence = 40)
    private ZonedDateTime expenseDate;  //费用日期
    @InterfaceFieldAttribute(sequence = 50)
    private Integer quantity;      //数量
    @InterfaceFieldAttribute(sequence = 60)
    private BigDecimal price;       //单价
    @InterfaceFieldAttribute(sequence = 70)
    private String uom;             //单位
    @InterfaceFieldAttribute(sequence = 80, elementFiled = SceneElementFieldType.CURRENCY_CODE)
    @NotNull
    private String currencyCode;    //币种CODE
    @InterfaceFieldAttribute(sequence = 90)
    @NotNull
    private Double rate;            //汇率
    @InterfaceFieldAttribute(sequence = 100)
    private BigDecimal amount;      //报账金额
    @InterfaceFieldAttribute(sequence = 110)
    private BigDecimal functionAmount;//报账本币金额
    @InterfaceFieldAttribute(sequence = 120)
    private BigDecimal expenseAmount;//费用金额
    @InterfaceFieldAttribute(sequence = 130)
    private BigDecimal expenseFunctionAmount;//费用本币金额
    @InterfaceFieldAttribute(sequence = 140)
    private BigDecimal taxAmount;           //税额
    @InterfaceFieldAttribute(sequence = 150)
    private BigDecimal taxFunctionAmount;   //本币税额
    @InterfaceFieldAttribute(sequence = 160)
    private String installmentDeductionFlag;//分期抵扣标志
    @InterfaceFieldAttribute(sequence = 170)
    private String inputTaxFlag;            //进项税业务标志
    @InterfaceFieldAttribute(sequence = 180)
    private String useType;                 //用途类型
    @InterfaceFieldAttribute(sequence = 190)
    private String description;             //费用说明
}
