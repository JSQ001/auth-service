package com.hand.hcf.app.expense.accrual.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualTypeAssignUserGroup;
import com.hand.hcf.app.expense.accrual.persistence.ExpenseAccrualTypeAssignUserGroupMapper;
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
public class ExpenseAccrualTypeAssignUserGroupService extends
        ServiceImpl<ExpenseAccrualTypeAssignUserGroupMapper,ExpenseAccrualTypeAssignUserGroup> {

    /**
     * 批量新增 费用预提单类型关联人员组
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseAccrualTypeAssignUserGroup> createExpenseAccrualTypeAssignUserGroupBatch(Long typeId,
                                                                                                List<ExpenseAccrualTypeAssignUserGroup> list){
        this.delete(new EntityWrapper<ExpenseAccrualTypeAssignUserGroup>().eq("exp_accrual_type_id", typeId));
        list.stream().forEach(expenseAccrualTypeAssignUserGroup -> {
            expenseAccrualTypeAssignUserGroup.setId(null);
            expenseAccrualTypeAssignUserGroup.setExpAccrualTypeId(typeId);
        });
        this.insertBatch(list);
        return list;
    }
}
