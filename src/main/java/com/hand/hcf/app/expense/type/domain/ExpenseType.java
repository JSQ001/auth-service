package com.hand.hcf.app.expense.type.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.common.utils.SqlConditionExpanse;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exp_expense_type")
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseType extends DomainI18nEnable {

    /**
     * 名称
     */
    @I18nField
    @TableField(value = "name", condition = SqlConditionExpanse.LIKE)
    private String name;

    /*
    * 费用大类名称
    * */
    @TableField(exist = false)
    private String categoryName;

    private String iconName;
    /**
     * 代码
     */
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    @TableField(value = "code", condition = SqlConditionExpanse.LIKE)
    private String code;
    /**
     * 图标url
     */
    private String iconUrl;
    /**
     * 租户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 账套
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;
    /**
     * 排序号
     */
    private Integer sequence = 0;
    /**
     * 所属大类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = RespCode.SYS_FIELD_IS_NULL)
    private Long typeCategoryId;
    /**
     * 类型 0-申请类型 1-费用类型
     */
    private Integer typeFlag;
    /**
     * 金额录入模式 false-总金额 true-单价*数量
     */
    private Boolean entryMode;
    /**
     * 附件
     */
    @TableField(value = "attachment_flag", strategy = FieldStrategy.IGNORED)
    private Integer attachmentFlag;
    /**
     * 申请类型ID，费用类型才有该字段
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "source_type_id", strategy = FieldStrategy.IGNORED)
    private Long sourceTypeId;

    //关联申请模式
    @TableField("application_model")
    private String applicationModel;

    @TableField(exist = false)
    private String applicationModelName;

    //对比符号
    @TableField("contrast_sign")
    private String contrastSign;

    //金额条件
    @TableField("amount")
    private BigDecimal amount;



    @TableField(exist = false)
    private String sourceTypeName;

    /**
     *  单位 当录入金额为 单价*数量 时有用
     */
    private String priceUnit;

    @TableField(exist = false)
    private String typeCategoryName;

    @TableField(exist = false)
    private String setOfBooksName;

    @TableField(exist = false)
    private String budgetItemName;


    //控件信息
    @TableField(exist = false)
    private List<ExpenseFieldDTO> fields;

    /**
     * 差旅类型代码
     */
    @TableField(value = "travel_type_code", strategy = FieldStrategy.IGNORED)
    private String travelTypeCode;
}
