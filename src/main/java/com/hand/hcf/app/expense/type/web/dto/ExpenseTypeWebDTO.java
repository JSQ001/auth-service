package com.hand.hcf.app.expense.type.web.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.app.expense.type.domain.ExpenseField;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import lombok.Data;

import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/26
 */
@Data
public class ExpenseTypeWebDTO extends ExpenseType  implements Comparable<ExpenseType>{

    @JsonIgnore
    private List<ExpenseField> fieldList;

    @JsonIgnore
    private List<Long> userGroupIds;
    @JsonIgnore
    private Boolean userGroupFlag;

    @Override
    public int compareTo(ExpenseType o) {
        return this.getCode().compareTo(o.getCode());
    }
}
