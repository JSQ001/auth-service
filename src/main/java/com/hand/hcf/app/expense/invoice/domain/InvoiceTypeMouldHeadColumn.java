package com.hand.hcf.app.expense.invoice.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description 发票类型模板头 domain 类
 * @date 2019/1/16 16:09
 * @version: 1.0.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("inv_type_mould_head_col")
public class InvoiceTypeMouldHeadColumn extends Domain {

    //租户id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    //发票类型ID
    @TableField("invoice_type_id")
    private Long invoiceTypeId;

    //开票日期（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("invoice_date")
    private String invoiceDate;

    //发票号码（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("invoice_no")
    private String invoiceNo;

    //发票代码（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("invoice_code")
    private String invoiceCode;

    //设备编号（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("machine_no")
    private String machineNo;

    //校验码（后6位）（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("check_code")
    private String checkCode;

    //币种（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("currency_code")
    private String currencyCode;

    //价税合计（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("total_amount")
    private String totalAmount;

    //金额合计（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("invoice_amount")
    private String invoiceAmount;

    //税额合计（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("tax_total_amount")
    private String taxTotalAmount;

    //备注（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    private String remark;

    //购方名称（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("buyer_name")
    private String buyerName;

    //购方纳税人识别号（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("buyer_tax_no")
    private String buyerTaxNo;

    //购方地址/电话（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("buyer_add_ph")
    private String buyerAddPh;

    //购方开户行/账号（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("buyer_account")
    private String buyerAccount;

    //销方名称（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("saler_name")
    private String salerName;


    //销方纳税人识别号（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("saler_tax_no")
    private String salerTaxNo;


    //销方地址/电话（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("saler_add_ph")
    private String salerAddPh;

    //销方开户行/账号（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    @TableField("saler_account")
    private String salerAccount;

}

