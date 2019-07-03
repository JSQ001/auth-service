package com.hand.hcf.app.expense.type.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeExpandField;
import com.hand.hcf.app.expense.type.persistence.ExpenseTypeExpandFieldMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/3
 */
@Service
public class ExpenseTypeExpandFieldService extends BaseService<ExpenseTypeExpandFieldMapper, ExpenseTypeExpandField> {
    @Autowired
    ExpenseTypeExpandFieldMapper expenseTypeExpandFieldMapper;

    public ExpenseTypeExpandField saveExpandField(ExpenseTypeExpandField expandField, Long expenseTypeId) {

        if (null == expenseTypeId) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        expandField.setExpenseTypeId(expenseTypeId);
        insertOrUpdate(expandField);
        return expandField;
    }

    public List<ExpenseTypeExpandField> queryExpandField(Long expenseTypeId) {

        Wrapper wrapper = new EntityWrapper<ExpenseTypeExpandField>()
                .eq("expense_type_id", expenseTypeId);

        List<ExpenseTypeExpandField> expenseTypeExpandFields = expenseTypeExpandFieldMapper.selectList(wrapper);
        return expenseTypeExpandFields;
    }
}
