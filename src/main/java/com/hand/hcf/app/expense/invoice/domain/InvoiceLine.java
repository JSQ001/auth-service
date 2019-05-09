package com.hand.hcf.app.expense.invoice.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 发票行表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("invoice_line")
@ApiModel(description = "发票行信息")
public class InvoiceLine extends Domain {
    /**
     * 租户ID
     */
    @NotNull
    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "租户ID",dataType = "Long",required = true)
    private Long tenantId;

    /**
     * 账套ID
     */
    @NotNull
    @TableField("set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "账套ID",dataType = "Long",required = true)
    private Long setOfBooksId;

    /**
     * 发票头ID
     */
    @NotNull
    @TableField("invoice_head_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "发票头ID",dataType = "Long",required = true)
    private Long invoiceHeadId;

    /**
     * 发票行序号
     */
    @NotNull
    @TableField("invoice_line_num")
    @ApiModelProperty(value = "发票行序号",dataType = "Integer",required = true)
    private Integer invoiceLineNum;

    /**
     * 货物或应税劳务、服务名称
     */
    @TableField("goods_name")
    @ApiModelProperty(value = "货物或应税劳务、服务名称",dataType = "String")
    private String goodsName;

    /**
     * 规格型号
     */
    @TableField("specification_model")
    @ApiModelProperty(value = "规格型号",dataType = "String")
    private String specificationModel;

    /**
     * 单位
     */
    @TableField("unit")
    @ApiModelProperty(value = "单位",dataType = "String")
    private String unit;

    /**
     * 数量
     */
    @TableField("num")
    @ApiModelProperty(value = "数量",dataType = "Long")
    private Long num;

    /**
     * 单价
     */
    @TableField("unit_price")
    @ApiModelProperty(value = "单价",dataType = "BigDecimal")
    private BigDecimal unitPrice;

    /**
     * 金额
     */
    @NotNull
    @TableField("detail_amount")
    @ApiModelProperty(value = "金额",dataType = "BigDecimal",required = true)
    private BigDecimal detailAmount;

    /**
     * 税率
     */
    @NotNull
    @TableField("tax_rate")
    @ApiModelProperty(value = "税率",dataType = "String",required = true)
    private String taxRate;

    /**
     * 税额
     */
    @NotNull
    @TableField("tax_amount")
    @ApiModelProperty(value = "税额",dataType = "BigDecimal",required = true)
    private BigDecimal taxAmount;

    /**
     * 币种
     */
    @NotNull
    @TableField("currency_code")
    @ApiModelProperty(value = "币种",dataType = "BigDecimal",required = true)
    private String currencyCode;

    /**
     * 汇率
     */
    @NotNull
    @TableField("exchange_rate")
    @ApiModelProperty(value = "汇率",dataType = "BigDecimal",required = true)
    private BigDecimal exchangeRate;

    /**
     * 发票对应的费用类型ID
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发票对应的费用类型ID",dataType = "Long",readOnly = true)
    private Long expenseTypeId;

    /**
     * 发票对应的费用类型名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发票对应的费用类型名称",dataType = "String",readOnly = true)
    private String expenseTypeName;

    /**
     * 发票对应的费用类型icon
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发票对应的费用类型icon",dataType = "String",readOnly = true)
    private String expenseTypeIcon;

    /**
     * 发票匹配到的费用类型(多个)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "发票匹配到的费用类型",dataType = "List<Long>",readOnly = true)
    private List<Long> matchExpenseTypeIds;

}
