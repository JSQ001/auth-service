package com.hand.hcf.app.expense.invoice.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainEnable;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * @description: 发票费用映射规则表
 * @version: 1.0
 * @author: shuqiang.luo@hand-china.com
 * @date: 2019/4/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("invoice_expense_type_rules")
public class InvoiceExpenseTypeRules extends DomainEnable {
    /**
     * 租户ID
     */
    @NotNull
    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;


    /**
     * 账套ID
     */
    @NotNull
    @TableField("set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "账套ID")
    private Long setOfBooksId;

    /**
     * 货物、应税劳务或服务名称
     */

    @TableField("goods_name")
    @ApiModelProperty(value = "货物、应税劳务或服务名称")
    private String goodsName;

    /**
     * 费用类型ID
     */
    @NotNull
    @TableField("expense_type_id")
    @ApiModelProperty(value = "费用类型ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expenseTypeId;


    /**
     * 状态
     */
    @NotNull
    @TableField("enabled")
    @ApiModelProperty(value = "状态")
    private Boolean enabled;


    /**
     * 有效日期从
     */
    @NotNull
    @TableField("start_date")
    @ApiModelProperty(value = "有效日期从")
    private ZonedDateTime startDate;


    /**
     * 有效日期至
     */
    @TableField("end_date")
    @ApiModelProperty(value = "有效日期至")
    private ZonedDateTime endDate;


    /**
     * 备注
     */
    @TableField("description")
    @ApiModelProperty(value = "备注")
    private String description;


    /**
     * 账套类型名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "账套类型名称")
    private  String setOfBooksName;


    /**
     * 账套类型代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "账套类型代码")
    private  String setOfBooksCode;


    /**
     * 费用类型代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "费用类型代码")
    private String ExpenseTypeCode;


    /**
     * 费用类型名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "费用类型名称")
    private String expenseTypeName;


    /**
     * String格式的开始日期(给导出用)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "格式的开始日期")
    private String stringInvoiceStartDate;

    /**
     * String格式的结束日期(给导出用)
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "格式的结束日期")
    private String stringInvoiceEndDate;

}
