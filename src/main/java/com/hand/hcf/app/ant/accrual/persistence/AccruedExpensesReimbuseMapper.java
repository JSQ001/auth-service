package com.hand.hcf.app.ant.accrual.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.ant.accrual.domain.AccruedReimburse;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 预提报销单mapper
 * @version: 1.0
 * @author: dazhuang.xie@hand-china.com
 * @date: 2019/6/18
 */
@Component
public interface AccruedExpensesReimbuseMapper extends BaseMapper<ExpenseAccrualType> {
    /*
    * 获取预提数据
    * @param map 查询参数
    * */
    List<AccruedReimburse> queryAccruedExpensesReimbuse(AccruedReimburse accruedReimburse);
}
