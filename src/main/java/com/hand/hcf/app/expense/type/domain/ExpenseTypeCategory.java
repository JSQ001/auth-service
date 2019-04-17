package com.hand.hcf.app.expense.type.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 *     费用大类
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/5
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exp_expense_type_category")
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseTypeCategory extends DomainI18nEnable {

    @I18nField
    @NotNull(message = "名称字段不允许为空")
    private String name;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "账套ID不允许为空")
    private Long setOfBooksId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    /**
     * 序号
     */
    private Integer sequence = 0;

    /**
     * 差旅类标识（true差旅类，false非差旅类）
     */
    private Boolean travelTypeFlag;

    @TableField(exist = false)
    private List<ExpenseType> expenseTypes;

    public ExpenseTypeCategory(Long setOfBooksId){
        this.setOfBooksId = setOfBooksId;
        this.sequence = null;
        this.enabled = true;
    }

}
