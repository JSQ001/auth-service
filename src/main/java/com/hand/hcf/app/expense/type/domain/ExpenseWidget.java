package com.hand.hcf.app.expense.type.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */
@Data
@TableName("exp_expense_widget")
public class ExpenseWidget extends DomainI18nEnable {

    @I18nField
    private String name;

    private String fieldType;

    private Integer sequence;

    private Integer limitCount;

    private String type;
}
