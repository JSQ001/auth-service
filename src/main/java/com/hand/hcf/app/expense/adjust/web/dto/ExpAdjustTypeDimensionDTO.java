package com.hand.hcf.app.expense.adjust.web.dto;

import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustType;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import lombok.Data;

import java.util.List;

/**
 * @description: 单据类型关联维度DTO
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/4/10 23:08
 */
@Data
public class ExpAdjustTypeDimensionDTO extends ExpenseAdjustType {
    private List<ExpenseDimension> dimensions;
}
