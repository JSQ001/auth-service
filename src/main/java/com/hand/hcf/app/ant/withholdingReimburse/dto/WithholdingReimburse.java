/**
 * <p>
 *     费用小类基类
 * </p>
 *
 * @Author: jsq
 * @Date: 2019/06/19
 */

package com.hand.hcf.app.ant.withholdingReimburse.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.Domain;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.common.utils.SqlConditionExpanse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@ApiModel(description = "费用小类")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ant_exp_withholding_expense_reimburse")
@AllArgsConstructor
@NoArgsConstructor
public class WithholdingReimburse extends Domain {
    @TableId
    @JsonSerialize(
            using = ToStringSerializer.class   //用字符串类型序列化id
    )
    private Long id;
    /**
     * 费用代码
     */
    @ApiModelProperty(value = "单据类型")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "document_type")
    private String documentType;

    /**
     * 费用名称
     */
    @ApiModelProperty(value = "名称")
    @I18nField
    @TableField(value = "name", condition = SqlConditionExpanse.LIKE)
    private String name;


    /**
     * 账套
     */
    @ApiModelProperty(value = "帐套id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "set_of_book_id")
    private Long setOfBooksId;

    @TableField(exist = false)
    @ApiModelProperty(value = "账套名称")
    private String setOfBooksName;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 费用小类id
     */
    @ApiModelProperty(value = "费用小类id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "category_type_id")
    private Long categoryTypeId;

    /**
     * 预提金额
     */
    @ApiModelProperty(value = "预提金额")
    @TableField(value = "amount")
    private Double amount;


    /**
     * 摘要
     */
    @ApiModelProperty(value = "摘要")
    @TableField(value = "comment")
    private String comment;

    /**
     * 记账期间
     */
    @ApiModelProperty(value = "记账期间")
    @TableField(value = "period")
    private String period;

    /**
     * 责任人id
     */
    @ApiModelProperty(value = "责任人id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "duty_person_id")
    private Long dutyPersonId;


    /*
     * 状态
     * */
    @ApiModelProperty(value = "预提状态")
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "status")
    private String status;


    /*
     * 次月是否冲销
     * */
    @ApiModelProperty(value = "次月是否冲销")
    @TableField(value = "auto_sterilisation")
    private boolean autoSterilisation;

    /*
     * 附件oid
     * */
    @ApiModelProperty(value = "附件oid")
    @TableField(value = "attachmentOid")
    private String attachmentOid;


}
