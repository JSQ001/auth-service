package com.hand.hcf.app.expense.book.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.core.domain.Domain;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/2/21 14:02
 * @version: 1.0.0
 */
@ApiModel(description = "账本明细表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("exp_expense_book")
public class ExpenseBook extends Domain{

    //费用类型ID
    @ApiModelProperty(value = "费用类型ID")
    @TableField("expense_type_id")
    private Long expenseTypeId;

    //费用类型名称
    @ApiModelProperty(value = "费用类型名称")
    @TableField(exist = false)
    private String expenseTypeName;

    /**
     * 费用类型图标路径
     */
    @ApiModelProperty(value = "费用类型图标路径")
    @TableField(exist = false)
    private String expenseTypeIconUrl;

    //租户ID
    @ApiModelProperty(value = "租户ID")
    @NotNull
    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    //账套ID
    @ApiModelProperty(value = "账套ID")
    @NotNull
    @TableField("set_of_books_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;

    //发生日期
    @ApiModelProperty(value = "发生日期")
    @TableField("expense_date")
    private ZonedDateTime expenseDate;

    // 币种
    @ApiModelProperty(value = "币种")
    private String currencyCode;

    //汇率
    @ApiModelProperty(value = "汇率")
    @TableField("exchange_rate")
    private BigDecimal exchangeRate;

    //金额
    @ApiModelProperty(value = "金额")
    @TableField("amount")
    private BigDecimal amount;

    // 本位币金额
    @ApiModelProperty(value = "本位币金额")
    @TableField("functional_amount")
    private BigDecimal functionalAmount;

    //数量
    @ApiModelProperty(value = "数量")
    private Integer quantity;

    //单价
    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    //单位
    @ApiModelProperty(value = "单位")
    @TableField("price_unit")
    private String priceUnit;

    //备注
    @ApiModelProperty(value = "备注")
    private String remarks;

    //附件Oid
    @ApiModelProperty(value = "附件Oid")
    @TableField("attachment_oid")
    private String attachmentOid;

//    //发票行ID
//    @TableField(exist = false)
//    private List<Long> invoiceLineIdList;

    //发票创建方式：BY_HAND手工录入 FORM_INVOICE票夹导入
    @ApiModelProperty(value = "发票创建方式")
    @TableField(exist = false)
    private String invoiceMethod;

    //关联发票信息
    @ApiModelProperty(value = "关联发票信息")
    @TableField(exist = false)
    private List<InvoiceHead> invoiceHead;

    //关联控件信息
    @ApiModelProperty(value = "关联控件信息")
    @TableField(exist = false)
    private List<ExpenseFieldDTO> fields;

    //附件信息
    @ApiModelProperty(value = "附件信息")
    @TableField(exist = false)
    private List<AttachmentCO>  attachments;
}