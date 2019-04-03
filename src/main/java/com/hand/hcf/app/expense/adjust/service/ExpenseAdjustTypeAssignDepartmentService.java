package com.hand.hcf.app.expense.adjust.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustTypeAssignDepartment;
import com.hand.hcf.app.expense.adjust.persistence.ExpenseAdjustTypeAssignDepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@Service
public class ExpenseAdjustTypeAssignDepartmentService extends ServiceImpl<ExpenseAdjustTypeAssignDepartmentMapper,ExpenseAdjustTypeAssignDepartment> {
    @Autowired
    private   ExpenseAdjustTypeAssignDepartmentMapper expenseAdjustTypeAssignDepartmentMapper;

//    public ExpenseAdjustTypeAssignDepartmentService(ExpenseAdjustTypeAssignDepartmentMapper expenseAdjustTypeAssignDepartmentMapper){
//        this.expenseAdjustTypeAssignDepartmentMapper = expenseAdjustTypeAssignDepartmentMapper;
//    }

    /**
     * 批量新增 费用调整单类型关联部门
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseAdjustTypeAssignDepartment> createExpenseAdjustTypeAssignDepartmentBatch(Long typeId, List<ExpenseAdjustTypeAssignDepartment> list){

        this.delete(new EntityWrapper<ExpenseAdjustTypeAssignDepartment>().eq("exp_adjust_type_id", typeId));
        list.stream().forEach(expenseAdjustTypeAssignDepartment -> {
            expenseAdjustTypeAssignDepartment.setId(null);
            expenseAdjustTypeAssignDepartment.setExpAdjustTypeId(typeId);
        });
        this.insertBatch(list);
        return list;
    }
}
