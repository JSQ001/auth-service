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
 * @description 发票类型模板行表 domain类
 * @date 2019/1/16 16:23
 * @version: 1.0.0
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("inv_type_mould_line_col")
public class InvoiceTypeMouldLineColumn extends Domain{
    //租户id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    //发票类型ID
    @TableField("invoice_type_id")
    private Long invoiceTypeId;

    //货物或应税劳务、服务名称（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    private String goodsName;


    //规格型号（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    private String specificationModel;

    //单位（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    private String unit;

    //数量（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    private String num;

    //单价（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    private String unitPrice;

    //金额（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    private String detailAmount;

    //税率（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    private String taxRate;

    //税额（不显示：DISABLED；可填写：ENABLED；可填且必填: REQUIRED）
    private String taxAmount;

}
