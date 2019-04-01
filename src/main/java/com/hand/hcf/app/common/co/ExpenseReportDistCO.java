package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.annotation.InterfaceFieldAttribute;
import com.hand.hcf.app.common.enums.SceneElementFieldType;
import com.hand.hcf.app.common.message.ModuleMessageCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


/**
 * @Auther: chenzhipeng
 * @Date: 2019/3/26 17:25
 * 报销单分配行(分摊行)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseReportDistCO {
    @NotNull
    @InterfaceFieldAttribute(sequence = 0, msgCode = ModuleMessageCode.EXP_REPORT_DIST_ID, elementFiled = SceneElementFieldType.TRANSACTION_DIST_ID)
    private Long id;            //分摊行id
    @NotNull
    @InterfaceFieldAttribute(sequence = 10,display = false, elementFiled = SceneElementFieldType.TRANSACTION_LINE_ID)
    private Long lineId;         //报账单行id
    @NotNull
    private Long headerId;             //报账单头ID
    @NotNull
    @InterfaceFieldAttribute(sequence = 20, msgCode = ModuleMessageCode.EXP_REPORT_DIST_COMPANY_ID)
    private Long companyId;             //公司id
    @NotNull
    @InterfaceFieldAttribute(sequence = 30, msgCode = ModuleMessageCode.EXP_REPORT_DIST_UNIT_ID)
    private Long unitId;                //部门id
    @InterfaceFieldAttribute(sequence = 40, msgCode = ModuleMessageCode.EXP_REPORT_DIST_RES_CENTER_ID)
    private Long resCenterId;                //责任中心id
    @NotNull
    @InterfaceFieldAttribute(sequence = 60, msgCode = ModuleMessageCode.EXP_REPORT_DIST_EXPENSE_TYPE_ID)
    private Long expenseTypeId;                 //费用类型id
    @NotNull
    @InterfaceFieldAttribute(sequence = 70, msgCode = ModuleMessageCode.EXP_REPORT_DIST_CURRENCY_CODE, elementFiled = SceneElementFieldType.CURRENCY_CODE)
    private String currencyCode;                //币种
    @InterfaceFieldAttribute(sequence = 80)
    private BigDecimal amount;                          //分摊含税金额
    @InterfaceFieldAttribute(sequence = 90)
    private BigDecimal functionAmount;                          //本币含税金额
    @InterfaceFieldAttribute(sequence = 100)
    private BigDecimal noTaxAmount;                          //分摊不含税金额
    @InterfaceFieldAttribute(sequence = 110)
    private BigDecimal noTaxFunctionAmount;                          //分摊不含税本币金额
    @InterfaceFieldAttribute(sequence = 120)
    private BigDecimal taxAmount;                          //税额分摊额
    @InterfaceFieldAttribute(sequence = 130)
    private BigDecimal taxFunctionAmount;                          //税本币分摊金额
    @InterfaceFieldAttribute(sequence = 140, dimension = true)
    private Long dimension1Id;                  //维度相关
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
}
