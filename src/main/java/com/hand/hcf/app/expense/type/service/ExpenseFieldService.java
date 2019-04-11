package com.hand.hcf.app.expense.type.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.type.domain.ExpenseField;
import com.hand.hcf.app.expense.type.persistence.ExpenseFieldMapper;
import com.hand.hcf.core.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */
@Service
public class ExpenseFieldService extends BaseService<ExpenseFieldMapper, ExpenseField> {

    public List<ExpenseField> selectByFieldId(Long expenseTypeId) {

        return this.selectList(new EntityWrapper<ExpenseField>().eq("expense_type_id", expenseTypeId));
    }

    public ExpenseField getByOid(UUID oid){
        return this.selectOne(new EntityWrapper<ExpenseField>().eq("field_oid", oid));
    }

    public List<ExpenseField> listFieldByTypeId(Long typeId,
                                                String language){
        return baseMapper.listFieldByTypeId(typeId, language);
    }
}
