package com.hand.hcf.app.ant.accrual.service;

import com.hand.hcf.app.ant.accrual.domain.AccruedReimburse;
import com.hand.hcf.app.ant.accrual.persistence.AccrualExpenseTypeMapper;
import com.hand.hcf.app.ant.accrual.persistence.AccruedExpensesReimbuseMapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description: 预提报销单service
 * @version: 1.0
 * @author: dazhuang.xie@hand-china.com
 * @date: 2019/6/18
 */
@Service
@Transactional
public class AccruedExpensesReimbuseService extends BaseService<AccrualExpenseTypeMapper,ExpenseAccrualType> {

    @Autowired
    private AccruedExpensesReimbuseMapper accruedExpensesReimbuseMapper;

    public List<AccruedReimburse> getAccruedReimbuse(AccruedReimburse accruedReimburse){
        return accruedExpensesReimbuseMapper.queryAccruedExpensesReimbuse(accruedReimburse);
    }
}
