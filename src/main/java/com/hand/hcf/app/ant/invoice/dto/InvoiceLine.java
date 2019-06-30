package com.hand.hcf.app.ant.invoice.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ApiModel(description = "蚂蚁发票行")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ant_invoice_line")
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceLine extends Domain {
    @TableId
    @JsonSerialize(
            using = ToStringSerializer.class
    )
    private Long id;

    /**
     * 发票头ID
     */
    @NotNull
    @TableField("header_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "发票头ID",dataType = "Long",required = true)
    private Long headerId;

    /**
     * 发票行号
     */
    @NotNull
    @TableField("line_number")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "发票行号",dataType = "int",required = true)
    private Long lineNumber;

    /**
     * 价税合计金额
     */
    @NotNull
    @TableField("price_tax_total_amount")
    @ApiModelProperty(value = "价税合计金额",dataType = "decimal",required = true)
    private BigDecimal priceTaxTotalAmount;

    /**
     * 税率
     */
    @NotNull
    @TableField("tax")
    @ApiModelProperty(value = "税率",dataType = "String",required = true)
    private String taxRate;

    /**
     * 不含税金额
     */
    @TableField("no_tax_amount")
    @ApiModelProperty(value = "不含税金额",dataType = "decimal",required = false)
    private BigDecimal noTaxAmount;

    /**
     * 货物或应税劳务
     */
    @TableField("goods_name")
    @ApiModelProperty(value = "货物或应税劳务",dataType = "String",required = false)
    private String goodsName;

    /**
     * 规格型号
     */
    @TableField("specification")
    @ApiModelProperty(value = "规格型号",dataType = "String",required = false)
    private String specification;

    /**
     * 单位
     */
    @TableField("unit")
    @ApiModelProperty(value = "单位",dataType = "String",required = false)
    private String unit;

    /**
     * 数量
     */
    @TableField("quantity")
    @ApiModelProperty(value = "数量",dataType = "int",required = false)
    private int quantity;

    /**
     * 单价
     */
    @TableField("price")
    @ApiModelProperty(value = "单价",dataType = "String",required = false)
    private BigDecimal price;


}
