package com.hand.hcf.app.expense.adjust.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustTypeAssignDimension;
import com.hand.hcf.app.expense.adjust.persistence.ExpenseAdjustTypeAssignDimensionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@Service
public class ExpenseAdjustTypeAssignDimensionService extends ServiceImpl<ExpenseAdjustTypeAssignDimensionMapper,ExpenseAdjustTypeAssignDimension> {
    @Autowired
    private  ExpenseAdjustTypeAssignDimensionMapper expenseAdjustTypeAssignDimensionMapper;

    /**
     * 批量新增 费用调整单类型关联维度
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseAdjustTypeAssignDimension> createExpenseAdjustTypeAssignDimensionBatch(Long typeId, List<ExpenseAdjustTypeAssignDimension> list){
        this.delete(new EntityWrapper<ExpenseAdjustTypeAssignDimension>().eq("exp_adjust_type_id", typeId));
        list.stream().forEach(expenseAdjustTypeAssignDimension -> {
            expenseAdjustTypeAssignDimension.setId(null);
            expenseAdjustTypeAssignDimension.setExpAdjustTypeId(typeId);
        });
        this.insertBatch(list);
        return list;
    }

}
