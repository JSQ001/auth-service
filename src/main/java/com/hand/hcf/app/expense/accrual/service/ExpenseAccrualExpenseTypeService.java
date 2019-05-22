package com.hand.hcf.app.expense.accrual.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualExpenseType;
import com.hand.hcf.app.expense.accrual.persistence.ExpenseAccrualExpenseTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/15
 */
@Service
public class ExpenseAccrualExpenseTypeService extends
        ServiceImpl<ExpenseAccrualExpenseTypeMapper,ExpenseAccrualExpenseType> {

    /**
     * 批量新增 费用预提单类型关联费用类型
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseAccrualExpenseType> createExpenseAccrualExpenseTypeBatch(Long typeId,
                                                                                List<ExpenseAccrualExpenseType> list){
        this.delete(new EntityWrapper<ExpenseAccrualExpenseType>().eq("exp_accrual_type_id", typeId));
        list.stream().forEach(ExpenseAccrualExpenseType -> {
            ExpenseAccrualExpenseType.setId(null);
            ExpenseAccrualExpenseType.setExpAccrualTypeId(typeId);
        });
        this.insertBatch(list);
        return list;
    }
}
