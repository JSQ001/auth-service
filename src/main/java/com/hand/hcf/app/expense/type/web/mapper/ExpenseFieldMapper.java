package com.hand.hcf.app.expense.type.web.mapper;

import com.hand.hcf.app.expense.type.domain.ExpenseField;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import org.springframework.beans.BeanUtils;

/**
 * Created by markfredchen on 16/1/10.
 */
public class ExpenseFieldMapper {

    public static ExpenseFieldDTO expenseFieldToExpenseFieldDTO(ExpenseField expenseField) {
        if (null == expenseField) return null;
        ExpenseFieldDTO dto = new ExpenseFieldDTO();
        BeanUtils.copyProperties(expenseField,dto);
        dto.setEditable(null!=expenseField.getEditable()?expenseField.getEditable():true);
        return dto;
    }

    public static ExpenseField expenseFieldDTOToExpenseField(ExpenseFieldDTO dto) {
        if (null == dto) return null;
        ExpenseField expenseField = new ExpenseField();
        BeanUtils.copyProperties(dto,expenseField);
        expenseField.setEditable(null!=dto.getEditable()?dto.getEditable():true);
        expenseField.setI18n(dto.getI18n());
        return expenseField;
    }

}
