package com.hand.hcf.app.expense.adjust.web.dto;

import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustType;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/6
 */
@Data
public class ExpenseAdjustTypeWebDTO extends ExpenseAdjustType {
    private List<ExpenseDimension> dimensions;
}
