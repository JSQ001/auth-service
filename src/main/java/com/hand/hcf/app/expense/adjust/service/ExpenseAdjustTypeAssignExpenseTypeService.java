package com.hand.hcf.app.expense.adjust.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustTypeAssignExpenseType;
import com.hand.hcf.app.expense.adjust.persistence.ExpenseAdjustTypeAssignExpenseTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@Service
public class ExpenseAdjustTypeAssignExpenseTypeService extends ServiceImpl<ExpenseAdjustTypeAssignExpenseTypeMapper,ExpenseAdjustTypeAssignExpenseType>{
    private final ExpenseAdjustTypeAssignExpenseTypeMapper expenseAdjustTypeAssignExpenseTypeMapper;

    public ExpenseAdjustTypeAssignExpenseTypeService(ExpenseAdjustTypeAssignExpenseTypeMapper expenseAdjustTypeAssignExpenseTypeMapper){
        this.expenseAdjustTypeAssignExpenseTypeMapper = expenseAdjustTypeAssignExpenseTypeMapper;
    }

    /**
     * 批量新增 费用调整单类型关联费用类型
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseAdjustTypeAssignExpenseType> createExpenseAdjustTypeAssignExpenseTypeBatch(Long typeId, List<ExpenseAdjustTypeAssignExpenseType> list){
        this.delete(new EntityWrapper<ExpenseAdjustTypeAssignExpenseType>().eq("exp_adjust_type_id", typeId));
        list.stream().forEach(expenseAdjustTypeAssignExpenseType -> {
            expenseAdjustTypeAssignExpenseType.setId(null);
            expenseAdjustTypeAssignExpenseType.setExpAdjustTypeId(typeId);
        });
        this.insertBatch(list);
        return list;
    }
}
