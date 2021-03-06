package com.hand.hcf.app.expense.invoice.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 发票类型定义domain
 * @author shaofeng.zheng@china-hand.com
 * @create 2019/1/16 15:27
 * @version: 1.0
 * @date 2019/1/16
 */
@ApiModel(description = "发票类型表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("invoice_type")
public class InvoiceType extends DomainI18nEnable{

    //租户id
    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    // 账套ID
    @ApiModelProperty(value = "账套id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "set_of_books_id", strategy = FieldStrategy.IGNORED)
    private Long setOfBooksId;

    @TableField(exist = false)
    @ApiModelProperty(value = "账套编码")
    private String setOfBooksCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "账套名称")
    private String setOfBooksName;

    //发票类型代码
    @ApiModelProperty(value = "发票类型代码")
    @TableField("invoice_type_code")
    private String invoiceTypeCode;

    //发票类型名称
    @ApiModelProperty(value = "发票类型名称")
    @TableField("invoice_type_name")
    @I18nField
    private String invoiceTypeName;

    //抵扣标志
    @ApiModelProperty(value = "抵扣标志")
    @TableField("deduction_flag")
    private String deductionFlag;

    //创建方式（系统预置：SYS；自定义：CUSTOM）
    @ApiModelProperty(value = "创建方式（系统预置：SYS；自定义：CUSTOM）")
    @TableField("creation_method")
    private String creationMethod;

    //发票代码长度
    @ApiModelProperty(value = "发票代码长度")
    @TableField(value = "invoice_code_length",strategy = FieldStrategy.IGNORED)
    private String invoiceCodeLength;

    //发票号码长度
    @ApiModelProperty(value = "发票号码长度")
    @TableField(value = "invoice_number_length", strategy = FieldStrategy.IGNORED)
    private String invoiceNumberLength;

    //默认税率
    @ApiModelProperty(value = "默认税率")
    @TableField(value="default_tax_rate", strategy = FieldStrategy.IGNORED)
    private String defaultTaxRate;

    //接口映射值
    @ApiModelProperty(value = "接口映射值")
    @TableField("interface_mapping")
    private String interfaceMapping;

}

