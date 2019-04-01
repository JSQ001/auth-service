package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.annotation.InterfaceFieldAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/3/26 16:37
 * 发票头
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseReportInvoiceHeaderCO {

    @NotNull
    @InterfaceFieldAttribute(sequence = 0, display = false)
    private Long id;                        //发票ID
    @NotNull
    @InterfaceFieldAttribute(sequence = 10)
    private Long invoiceTypeId;             //发票类型
    @NotNull
    @InterfaceFieldAttribute(sequence = 20)
    private Long tenantId;                  //租户id
    @NotNull
    @InterfaceFieldAttribute(sequence = 30)
    private Long setOfBooksId;                //账套id
    @NotNull
    @InterfaceFieldAttribute(sequence = 40)
    private ZonedDateTime invoiceDate;      //开票日期
    @NotNull
    @InterfaceFieldAttribute(sequence = 50)
    private String invoiceNo;               //发票号码
    @NotNull
    @InterfaceFieldAttribute(sequence = 60)
    private String invoiceCode;             //发票代码
    @InterfaceFieldAttribute(sequence = 70)
    private String machineNo;               //设备编号
    @InterfaceFieldAttribute(sequence = 80)
    private String checkCode;               //校验码(后6位)
    @NotNull
    @InterfaceFieldAttribute(sequence = 90)
    private BigDecimal totalAmount;         //价税合计
    @NotNull
    @InterfaceFieldAttribute(sequence = 100)
    private BigDecimal taxTotalAmount;      //税额合计
    @NotNull
    @InterfaceFieldAttribute(sequence = 110)
    private String currencyCode;            //币种
    @NotNull
    @InterfaceFieldAttribute(sequence = 120)
    private BigDecimal exchangeRate;        //汇率
    @InterfaceFieldAttribute(sequence = 130)
    private String remark;                  //发票说明
    @InterfaceFieldAttribute(sequence = 140)
    private String buyerName;               //购方名称
    @InterfaceFieldAttribute(sequence = 150)
    private String buyerTaxNo;              //购方纳税人识别号
    @InterfaceFieldAttribute(sequence = 160)
    private String buyerAddPh;              //购方地址/电话
    @InterfaceFieldAttribute(sequence = 170)
    private String buyerAccount;            //购方开户行/账号
    @InterfaceFieldAttribute(sequence = 180)
    private String salerName;               //销方名称
    @InterfaceFieldAttribute(sequence = 190)
    private String salerTaxNo;              //销方纳税人识别号
    @InterfaceFieldAttribute(sequence = 200)
    private String salerAddPh;              //销方地址/电话
    @InterfaceFieldAttribute(sequence = 210)
    private String salerAccount;            //销方开户行/账号
    @InterfaceFieldAttribute(sequence = 220)
    private Boolean cancelFlag;             //作废标志
    @InterfaceFieldAttribute(sequence = 230)
    private Boolean redInvoiceFlag;         //红票标志
    @InterfaceFieldAttribute(sequence = 240)
    private Boolean checkResult;            //验真状态

}
