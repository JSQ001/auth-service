package com.hand.hcf.app.expense.adjust.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustTypeAssignUserGroup;
import com.hand.hcf.app.expense.adjust.persistence.ExpenseAdjustTypeAssignUserGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@Service
public class ExpenseAdjustTypeAssignUserGroupService extends ServiceImpl<ExpenseAdjustTypeAssignUserGroupMapper,ExpenseAdjustTypeAssignUserGroup>{
    @Autowired
    private  ExpenseAdjustTypeAssignUserGroupMapper expenseAdjustTypeAssignUserGroupMapper;
    /**
     * 批量新增 费用调整单类型关联人员组
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseAdjustTypeAssignUserGroup> createExpenseAdjustTypeAssignUserGroupBatch(Long typeId, List<ExpenseAdjustTypeAssignUserGroup> list){
        this.delete(new EntityWrapper<ExpenseAdjustTypeAssignUserGroup>().eq("exp_adjust_type_id", typeId));
        list.stream().forEach(expenseAdjustTypeAssignUserGroup -> {
            expenseAdjustTypeAssignUserGroup.setId(null);
            expenseAdjustTypeAssignUserGroup.setExpAdjustTypeId(typeId);
        });
        this.insertBatch(list);
        return list;
    }
}
