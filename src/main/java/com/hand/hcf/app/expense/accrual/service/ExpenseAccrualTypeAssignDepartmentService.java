package com.hand.hcf.app.expense.accrual.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualTypeAssignDepartment;
import com.hand.hcf.app.expense.accrual.persistence.ExpenseAccrualTypeAssignDepartmentMapper;
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
public class ExpenseAccrualTypeAssignDepartmentService extends
        ServiceImpl<ExpenseAccrualTypeAssignDepartmentMapper,ExpenseAccrualTypeAssignDepartment> {

    /**
     * 批量新增 费用预提单类型关联部门
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseAccrualTypeAssignDepartment> createExpenseAccrualTypeAssignDepartmentBatch(Long typeId,
                                                                                                  List<ExpenseAccrualTypeAssignDepartment> list){

        this.delete(new EntityWrapper<ExpenseAccrualTypeAssignDepartment>().eq("exp_accrual_type_id", typeId));
        list.stream().forEach(expenseAccrualTypeAssignDepartment -> {
            expenseAccrualTypeAssignDepartment.setId(null);
            expenseAccrualTypeAssignDepartment.setExpAccrualTypeId(typeId);
        });
        this.insertBatch(list);
        return list;
    }
}
