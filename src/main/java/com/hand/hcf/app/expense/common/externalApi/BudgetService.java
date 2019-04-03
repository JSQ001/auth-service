package com.hand.hcf.app.expense.common.externalApi;

import com.hand.hcf.app.common.co.BudgetItemCO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/24
 */
@Service
public class BudgetService {
    //jiu.zhao 预算
    /*@Autowired
    private BudgetClient budgetClient;
    @Autowired
    private BudgetInterface budgetInterface;

    public BudgetCheckReturnCO saveBudgetCheck(BudgetCheckMessageCO param) {
        return budgetClient.saveBudgetCheck(param);
    }

    public void updateBudgetRollback(List<BudgetReverseRollbackCO> message) {
        budgetClient.updateBudgetRollback(message);
    }

    public BudgetItemCO getBudgetItemByExpenseTypeId(Long sourceTypeId) {
        return budgetInterface.getBudgetItemByExpenseTypeId(sourceTypeId);
    }

    public List<BudgetItemCO> listBudgetItemByExpenseTypeIds(List<Long> ids) {
        List<BudgetItemCO> result = budgetInterface.listBudgetItemByExpenseTypeIds(ids);
        if (null == result){
            return new ArrayList<>();
        }
        return result;
    }*/
}
